package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.credential.CreateCredentialDto;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.model.credential.EditCredentialDto;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import java.time.LocalDateTime;
import com.vmsac.vmsacserver.repository.CredTypeRepository;
import com.vmsac.vmsacserver.repository.CredentialRepository;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CredentialService} using Mockito.
 *
 * Covers {@code createCredential}:
 * <ul>
 *   <li>Non-PIN type with a globally duplicate UID (same UID + type, different person)
 *       → throws {@link RuntimeException}</li>
 *   <li>PIN type (credTypeId=4): global UID dedup check is skipped — the same PIN
 *       value may be assigned to multiple persons</li>
 *   <li>Exact duplicate (same person + type + UID already exists) → throws</li>
 *   <li>Unknown credTypeId (not found in DB) → throws</li>
 * </ul>
 *
 * Covers {@code uidInUse}:
 * <ul>
 *   <li>Returns {@code true} when a non-PIN credential with that UID exists</li>
 *   <li>Returns {@code false} when only a PIN credential (type 4) has that UID —
 *       PINs are excluded from the global UID uniqueness constraint</li>
 * </ul>
 *
 * No Spring context — pure unit tests with mocked repositories.
 */
@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {

    @InjectMocks
    private CredentialService credentialService;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CredTypeRepository credTypeRepository;

    // --- createCredential ---

    @Test
    void createCredential_nonPinType_duplicateUidGlobally_throws() {
        CreateCredentialDto dto = mockCreateDto(1L, "ABC123", 1L); // type 1 = Card
        when(credentialRepository.findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(
                any(), any(), any())).thenReturn(Optional.empty());
        // Create CredentialType mock first, then use it in thenReturn — avoids nested when() issue
        CredentialType cardType = mockCredType(1L);
        when(credTypeRepository.findById(1L)).thenReturn(Optional.of(cardType));
        // Global duplicate exists for the same UID + type, different person
        when(credentialRepository.findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(1L, "ABC123"))
                .thenReturn(Optional.of(new Credential()));

        assertThrows(RuntimeException.class, () -> credentialService.createCredential(dto));
    }

    @Test
    void createCredential_pinType_globalDuplicateAllowed() throws Exception {
        // Type 4 = PIN; the same PIN value can belong to multiple persons
        CreateCredentialDto dto = mockCreateDto(4L, "1234", 99L);
        when(credentialRepository.findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(
                any(), any(), any())).thenReturn(Optional.empty());
        CredentialType pinType = mockCredType(4L);
        when(credTypeRepository.findById(4L)).thenReturn(Optional.of(pinType));
        Person person = mock(Person.class);
        when(personRepository.findById(99L)).thenReturn(Optional.of(person));
        Credential savedCred = mockSavedCredential();
        when(credentialRepository.save(any())).thenReturn(savedCred);
        // dto.toCredential() is called inside createCredential — set it up here only where needed
        when(dto.toCredential()).thenReturn(new Credential());

        // Should NOT throw even though the global UID dedup check is skipped for PIN
        CredentialDto result = credentialService.createCredential(dto);
        assertNotNull(result);
        verify(credentialRepository, never())
                .findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(eq(4L), any());
    }

    @Test
    void createCredential_samePerson_exactDuplicate_throws() {
        // Only stubs that are actually used by this code path
        CreateCredentialDto dto = mock(CreateCredentialDto.class);
        when(dto.getCredTypeId()).thenReturn(1L);
        when(dto.getCredUid()).thenReturn("ABC123");
        when(dto.getPersonId()).thenReturn(10L);
        // Exact same (person + type + uid) already exists — throws immediately after this check
        when(credentialRepository.findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(
                1L, "ABC123", 10L)).thenReturn(Optional.of(new Credential()));

        assertThrows(RuntimeException.class, () -> credentialService.createCredential(dto));
    }

    @Test
    void createCredential_missingCredType_throws() {
        // Only stubs that are actually used by this code path
        CreateCredentialDto dto = mock(CreateCredentialDto.class);
        when(dto.getCredTypeId()).thenReturn(99L);
        when(dto.getCredUid()).thenReturn("ABC123");
        when(dto.getPersonId()).thenReturn(10L);
        when(credentialRepository.findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(
                any(), any(), any())).thenReturn(Optional.empty());
        when(credTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> credentialService.createCredential(dto));
    }

    // --- uidInUse ---

    @Test
    void uidInUse_cardUidExists_returnsTrue() {
        when(credentialRepository.findByDeletedFalseAndCredUidAndCredType_CredTypeIdNotAndCredIdNot(
                "ABC123", 4L, 0L)).thenReturn(Optional.of(new Credential()));

        assertTrue(credentialService.uidInUse("ABC123", 0L));
    }

    @Test
    void uidInUse_onlyPinWithSameUid_returnsFalse() {
        // PIN type (4) is excluded from the UID-in-use check
        when(credentialRepository.findByDeletedFalseAndCredUidAndCredType_CredTypeIdNotAndCredIdNot(
                "1234", 4L, 0L)).thenReturn(Optional.empty());

        assertFalse(credentialService.uidInUse("1234", 0L));
    }

    // --- editCredential ---

    /**
     * editCredential when the credential being edited does not exist → throws.
     */
    @Test
    void editCredential_nonExistentCredential_throws() {
        EditCredentialDto dto = new EditCredentialDto(10L, "ABC123",
                LocalDateTime.of(2099, 1, 1, 0, 0), true, false, 1L, 99L);

        when(credentialRepository.findByCredIdAndDeletedFalse(10L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> credentialService.editCredential(dto),
                "Should throw when credential does not exist");
    }

    /**
     * editCredential when the new UID is already held by a DIFFERENT non-PIN credential
     * → throws "Credential type and value repeated".
     *
     * <p>This is the UID-uniqueness-on-edit check that createCredential also does.
     * A bug here would let two persons share the same card UID after an edit.
     */
    @Test
    void editCredential_uidClashWithOtherCredential_throws() {
        long editCredId = 10L;
        EditCredentialDto dto = new EditCredentialDto(
                editCredId, "ABC123",
                LocalDateTime.of(2099, 1, 1, 0, 0), true, false, 1L, 99L);

        // Existing credential (the one being edited)
        Credential existing = mock(Credential.class);
        when(credentialRepository.findByCredIdAndDeletedFalse(editCredId))
                .thenReturn(Optional.of(existing));

        // Type lookup
        CredentialType cardType = CredentialType.builder().credTypeId(1L).build();
        when(credTypeRepository.findById(1L)).thenReturn(Optional.of(cardType));

        // Person lookup
        Person person = mock(Person.class);
        when(person.getPersonId()).thenReturn(99L);
        when(personRepository.findById(99L)).thenReturn(Optional.of(person));

        // Global UID check: a DIFFERENT credential already has this UID+type
        CredentialType conflictType = CredentialType.builder().credTypeId(1L).build(); // id != 4
        Credential conflictCred = Credential.builder()
                .credId(99L)          // different from editCredId=10
                .credUid("ABC123")
                .credType(conflictType)
                .deleted(false)
                .build();
        when(credentialRepository.findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(1L, "ABC123"))
                .thenReturn(Optional.of(conflictCred));
        when(credentialRepository.findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(
                1L, "ABC123", 99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> credentialService.editCredential(dto),
                "Should throw when new UID clashes with another credential");
    }

    /**
     * editCredential where the UID belongs to the SAME credential (self-edit, no UID change)
     * → succeeds and returns the saved DTO.
     */
    @Test
    void editCredential_sameCredentialSelfEdit_succeeds() throws Exception {
        long editCredId = 10L;
        EditCredentialDto dto = new EditCredentialDto(
                editCredId, "ABC123",
                LocalDateTime.of(2099, 1, 1, 0, 0), true, false, 1L, 99L);

        Credential existing = mock(Credential.class);
        when(credentialRepository.findByCredIdAndDeletedFalse(editCredId))
                .thenReturn(Optional.of(existing));

        CredentialType cardType = CredentialType.builder().credTypeId(1L).build();
        when(credTypeRepository.findById(1L)).thenReturn(Optional.of(cardType));

        Person person = mock(Person.class);
        when(person.getPersonId()).thenReturn(99L);
        when(personRepository.findById(99L)).thenReturn(Optional.of(person));

        // Global UID check: same credId → no collision
        CredentialType sameType = CredentialType.builder().credTypeId(1L).build();
        Credential sameCred = Credential.builder()
                .credId(editCredId)   // same credential being edited
                .credUid("ABC123")
                .credType(sameType)
                .deleted(false)
                .build();
        when(credentialRepository.findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(1L, "ABC123"))
                .thenReturn(Optional.of(sameCred));
        // Exact-copy-from-same-person also finds the same credential → no throw
        when(credentialRepository.findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(
                1L, "ABC123", 99L)).thenReturn(Optional.of(sameCred));

        Credential savedCred = mock(Credential.class);
        CredentialDto resultDto = mock(CredentialDto.class);
        when(savedCred.toDto()).thenReturn(resultDto);
        when(credentialRepository.save(any())).thenReturn(savedCred);

        CredentialDto result = credentialService.editCredential(dto);

        assertNotNull(result, "editCredential must return a non-null DTO on success");
        verify(credentialRepository).save(any());
    }

    // ---- helpers ----

    private CreateCredentialDto mockCreateDto(Long credTypeId, String uid, Long personId) {
        CreateCredentialDto dto = mock(CreateCredentialDto.class);
        when(dto.getCredTypeId()).thenReturn(credTypeId);
        when(dto.getCredUid()).thenReturn(uid);
        when(dto.getPersonId()).thenReturn(personId);
        return dto;
    }

    private CredentialType mockCredType(Long id) {
        return CredentialType.builder().credTypeId(id).build();
    }

    private Credential mockSavedCredential() {
        Credential c = mock(Credential.class);
        CredentialDto cdto = mock(CredentialDto.class);
        when(c.toDto()).thenReturn(cdto);
        return c;
    }
}
