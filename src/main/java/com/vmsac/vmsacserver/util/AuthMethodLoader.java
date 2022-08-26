package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethodcredentialtypenton.AuthMethodCredentialTypeNtoN;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.repository.AuthMethodCredentialTypeNtoNRepository;
import com.vmsac.vmsacserver.repository.AuthMethodRepository;
import com.vmsac.vmsacserver.repository.CredTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile({"dev", "devpostgres"})
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
        if (credTypeRepository.findAll().isEmpty()) loadData();
    }

    // just change credTypes list, the generator should take care of auth method combinations
    // WARNING: DO NOT CHANGE credTypeName, the python code (events.py) relies on cred names
    private void loadData() {
        // must be in alphabetical order for the auth methods to be in sorted array order
        // ie a + b before a + c ( ['a', 'b'] < ['a', 'c'] );
        // be careful when changing the name of the Pin cred type, generateAuthMethod line 140
        // relies on the name to prevent creation of OR methods with Pin
        List<CredentialType> credTypes = credTypeRepository.saveAll( List.of(
                CredentialType.builder()
                        .credTypeName("Card")
                        .credTypeDesc("RFID Card")
                        .deleted(false)
                        .build(),
                CredentialType.builder()
                        .credTypeName("Face")
                        .credTypeDesc("Face Recognition")
                        .deleted(false)
                        .build(),
                CredentialType.builder()
                        .credTypeName("Fingerprint")
                        .credTypeDesc("Fingerprint scanner")
                        .deleted(false)
                        .build(),
                CredentialType.builder()
                        .credTypeName("Pin")
                        .credTypeDesc("Digit pin")
                        .deleted(false)
                        .build()
        ));

        generateAuthMethods(credTypes);
    }

    // create all 1 credType auth methods, then create 2 credTypes etc;
    // to do this, have an array representing the cred types
    // then generate the next permutation in lexicographical order
    // ie [1, 2, 4] for max 4 cred types, next permutation is [1, 3, 4]
    private void generateAuthMethods(List<CredentialType> credTypes) {
        if (credTypes.isEmpty()) return;
        int[] arr = new int[credTypes.size()];
        arr[0] = 1;
        ArrayList<AuthMethodCredentialTypeNtoN> acc = new ArrayList<>();
        generateAuthMethods(credTypes, arr, 1, acc);
        authMethodCredentialTypeNtoNRepository.saveAll(acc);
    }

    private void generateAuthMethods(List<CredentialType> credTypes, int[] arr, int length, List<AuthMethodCredentialTypeNtoN> acc) {
        generateAuthMethod(credTypes, arr, length, acc);
        if (credTypes.size() == length) { // last permutation
            return;
        }
        // create next permutation
        if (arr[0] == credTypes.size()-length+1) { // increase length by 1
            for (int i = 0; i <= length; ++i) {
                arr[i] = i+1;
            }
            generateAuthMethods(credTypes, arr, length + 1, acc);
            return;
        }
        // for each index i
        // the max value is credTypes.size()-length+i+1
        for(int i=length-1; i>=0; --i) {
            if(arr[i] < credTypes.size()-length+i+1) { // can increase
                arr[i] += 1;
                for(int j=i+1; j<length; ++j) {
                    arr[j] = arr[j-1]+1;
                }
                generateAuthMethods(credTypes, arr, length, acc);
                return;
            }
        }
    }

    // create auth method
    // accumulate n to n in acc for bulk creation
    private void generateAuthMethod(List<CredentialType> credTypes, int[] arr, int length, List<AuthMethodCredentialTypeNtoN> acc) {
        // create and method
        StringBuilder andSb = new StringBuilder(credTypes.get(arr[0]-1).getCredTypeName());
        for(int i=1; i<length; ++i) {
            andSb.append(" + ");
            andSb.append(credTypes.get(arr[i]-1).getCredTypeName());
        }

        AuthMethod andMethod = authMethodRepository.save(
                AuthMethod.builder()
                        .authMethodDesc(andSb.toString())
                        .authMethodCondition("AND")
                        .deleted(false)
                        .build()
        );

        for(int i=0; i<length; ++i)
            acc.add(
                    AuthMethodCredentialTypeNtoN.builder()
                            .authMethodId(andMethod.getAuthMethodId())
                            .credentialType(credTypes.get(arr[i]-1))
                            .deleted(false)
                            .build()
            );

        if(length == 1) return;

        // create orMethod
        StringBuilder orSb = new StringBuilder(credTypes.get(arr[0]-1).getCredTypeName());
        for(int i=1; i<length; ++i) {
            orSb.append(" / ");
            String credTypeName = credTypes.get(arr[i]-1).getCredTypeName();
            if(credTypeName.equals("Pin")) return; // no pin method for OR
            orSb.append(credTypes.get(arr[i]-1).getCredTypeName());
        }

        AuthMethod orMethod = authMethodRepository.save(
                AuthMethod.builder()
                        .authMethodDesc(orSb.toString())
                        .authMethodCondition("OR")
                        .deleted(false)
                        .build()
        );

        for(int i=0; i<length; ++i)
            acc.add(
                    AuthMethodCredentialTypeNtoN.builder()
                            .authMethodId(orMethod.getAuthMethodId())
                            .credentialType(credTypes.get(arr[i]-1))
                            .deleted(false)
                            .build()
            );
    }

}
