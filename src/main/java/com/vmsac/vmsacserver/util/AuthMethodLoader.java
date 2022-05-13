package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethodcredentialtypenton.AuthMethodCredentialTypeNtoN;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.repository.AuthMethodCredentialTypeNtoNRepository;
import com.vmsac.vmsacserver.repository.AuthMethodRepository;
import com.vmsac.vmsacserver.repository.CredTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthMethodLoader implements CommandLineRunner {
    private final AuthMethodRepository authMethodRepository;
    private final AuthMethodCredentialTypeNtoNRepository authMethodCredentialTypeNtoNRepository;
    private final CredTypeRepository credTypeRepository;

    public AuthMethodLoader(AuthMethodRepository authMethodRepository, AuthMethodCredentialTypeNtoNRepository authMethodCredentialTypeNtoNRepository, CredTypeRepository credTypeRepository) {
        this.authMethodRepository = authMethodRepository;
        this.authMethodCredentialTypeNtoNRepository = authMethodCredentialTypeNtoNRepository;
        this.credTypeRepository = credTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadData();
    }

    // just change credTypes list, the generator should take care of auth method combinations
    private void loadData() {
        List<CredentialType> credTypes = credTypeRepository.saveAll( List.of(
                CredentialType.builder()
                        .credTypeName("Pin")
                        .credTypeDesc("Digit pin")
                        .deleted(false)
                        .build(),
                CredentialType.builder()
                        .credTypeName("Face")
                        .credTypeDesc("Face Recognition")
                        .deleted(false)
                        .build(),
                CredentialType.builder()
                        .credTypeName("Card")
                        .credTypeDesc("RFID Card")
                        .deleted(false)
                        .build(),
                CredentialType.builder()
                        .credTypeName("Fingerprint")
                        .credTypeDesc("Fingerprint scanner")
                        .deleted(false)
                        .build()
        ));

        generateAuthMethods(credTypes);
    }

    // wrapper function so call on line 55 is clean
    private void generateAuthMethods(List<CredentialType> credTypes) {
        generateAuthMethods(credTypes, new ArrayList<>());
    }

    // do not mutate credTypes or acc
    private void generateAuthMethods(List<CredentialType> credTypes, List<CredentialType> acc) {
        // to get all permutations, for each cred type in cred types, either choose it or not
        // if cred types is empty, pass to createAuthMethods
        if (credTypes.isEmpty()) {
            createAuthMethods(acc);
            return;
        }

        List<CredentialType> newList = credTypes.subList(1, credTypes.size());
        List<CredentialType> copyList = new ArrayList<>(acc);
        copyList.add(credTypes.get(0));
        // choose the first item
        generateAuthMethods(newList, copyList);
        // do not choose the first item
        generateAuthMethods(newList, acc);
    }

    private void createAuthMethods(List<CredentialType> credTypes) {
        // if credTypes is length 0, ignore (empty auth method)
        // create AND method
        // if credTypes is length 1 or contains "Pin", do not create OR method
        if (credTypes.isEmpty()) { return; }

        AuthMethod andMethod = authMethodRepository.save(
                AuthMethod.builder()
                        .authMethodDesc( // stream all names and join with a " + " between
                                credTypes.stream().map(CredentialType::getCredTypeName).collect(Collectors.joining(" + "))
                        )
                        .authMethodCondition("AND")
                        .deleted(false)
                        .build()
        );

        authMethodCredentialTypeNtoNRepository.saveAll(
                credTypes.stream().map(
                        credType -> AuthMethodCredentialTypeNtoN.builder()
                                .authMethodId(andMethod.getAuthMethodId())
                                .credentialType(credType)
                                .deleted(false)
                                .build()
                ).collect(Collectors.toList())
        );

        if (credTypes.size() == 1 || credTypes.get(0).getCredTypeName().equals("Pin")) {
            return;
        }

        AuthMethod orMethod = authMethodRepository.save(
                AuthMethod.builder()
                        .authMethodDesc(
                                credTypes.stream().map(CredentialType::getCredTypeName).collect(Collectors.joining(" / "))
                        )
                        .authMethodCondition("OR")
                        .deleted(false)
                        .build()
        );

        authMethodCredentialTypeNtoNRepository.saveAll(
                credTypes.stream().map(
                        credType -> AuthMethodCredentialTypeNtoN.builder()
                                .authMethodId(orMethod.getAuthMethodId())
                                .credentialType(credType)
                                .deleted(false)
                                .build()
                ).collect(Collectors.toList())
        );
    }
}
