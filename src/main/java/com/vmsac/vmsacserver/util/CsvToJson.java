package com.vmsac.vmsacserver.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvValidationException;
import com.vmsac.vmsacserver.service.CredentialService;
import com.vmsac.vmsacserver.service.PersonService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReaderBuilder;

@Component
public class CsvToJson {
     final
    CredentialService credentialService;
    final
    PersonService personService;

    public CsvToJson(CredentialService credentialService, PersonService personService) {
        this.credentialService = credentialService;
        this.personService = personService;
    }

    public String convert(MultipartFile file) throws IOException, CsvValidationException, JSONException {
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader);
        com.opencsv.CSVReader csvReader = csvReaderBuilder.build();

        String[] header = null;
        String[] nextLine;
        JSONArray jsonArray = new JSONArray();
        List<String> credPinsList = new ArrayList<String>();
        List<String> mobileList = new ArrayList<String>();

        while ((nextLine = csvReader.readNext()) != null) {
            if (header == null) {
                header = nextLine;
                continue;
            }

            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < header.length; i++) {
                chckHeadersForErrors(header, nextLine, jsonObject, i);
            }
            System.out.println(jsonObject);
            greenOrRed(jsonObject, credPinsList, mobileList);
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }

    private static void chckHeadersForErrors(String[] header, String[] nextLine, JSONObject jsonObject, int i) throws JSONException {
        if (header[i].trim().equalsIgnoreCase("\uFEFFFirst Name")) {
            jsonObject.put("personFirstName", nextLine[i]);
        } else if (header[i].trim().equalsIgnoreCase("Last Name")) {
            jsonObject.put("personLastName", nextLine[i]);
        } else if (header[i].trim().equalsIgnoreCase("uid")) {
            jsonObject.put("personUid", nextLine[i]);
        } else if (header[i].trim().equalsIgnoreCase("Mobile Number")) {
            jsonObject.put("personMobileNumber", nextLine[i]);
        } else if (header[i].trim().equalsIgnoreCase("Email")) {
            jsonObject.put("personEmail", nextLine[i]);
        } else if (header[i].trim().equalsIgnoreCase("Credential type")) {
            jsonObject.put("credentialType", nextLine[i]);
        } else if (header[i].trim().equalsIgnoreCase("Credential pin")) {
            jsonObject.put("credentialPin", nextLine[i]);
        } else if (header[i].trim().equalsIgnoreCase("Credential Expiry (YYYY-MM-DD HOUR-MIN-SEC)")) {
            jsonObject.put("credentialExpiry", nextLine[i]);
        } else {
            jsonObject.put(header[i].trim(), nextLine[i]);
        }
    }

    private void greenOrRed(JSONObject jsonObject, List<String> credPinsList, List<String> mobileList) throws JSONException {
//        check for valid first, last name and mobile
        if ((jsonObject.has("personFirstName") && !jsonObject.isNull("personFirstName") && !jsonObject.getString("personFirstName").isEmpty())
                && (jsonObject.has("personLastName") && !jsonObject.isNull("personLastName") && !jsonObject.getString("personLastName").isEmpty())
                && (jsonObject.has("personMobileNumber") && !jsonObject.isNull("personMobileNumber") && !jsonObject.getString("personMobileNumber").isEmpty())
        ) {
            System.out.println(jsonObject + " Is valid");
            jsonObject.put("Color", "green");
        } else {
            jsonObject.put("Color", "red");
        }

//        check for unique pin not in db
        if (!jsonObject.getString("credentialPin").isEmpty()
                && !jsonObject.getString("credentialType").isEmpty()
                && !jsonObject.getString("credentialExpiry").isEmpty()
        ) {
            System.out.println("pin check");
            String credentialPin = jsonObject.getString("credentialPin");
            String credentialType = jsonObject.getString("credentialType");
            if (!credentialType.equals("Pin")) {
                if (!credentialService.findByContainCredUid(credentialPin).isEmpty()
                        || credPinsList.contains(credentialPin)
                ) {
                    System.out.println("pin in use");
                    jsonObject.put("Color", "red");
                }
                credPinsList.add(credentialPin);
            }
        }

//        check for unique mobile not in db
        if (!jsonObject.getString("personMobileNumber").isEmpty()) {
            System.out.println("mobile check");
            String mobile = jsonObject.getString("personMobileNumber");
            if (!personService.findByPersonMobileNumber(mobile).isEmpty()
                    || mobileList.contains(mobile)
            ) {
                System.out.println("mobile in use");
                jsonObject.put("Color", "red");
            }
            mobileList.add(mobile);
        }
    }
}


//public class CsvToJson {
//    public String convert(MultipartFile file) throws IOException, CsvValidationException, JSONException {
//        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
//        CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader);
//        com.opencsv.CSVReader csvReader = csvReaderBuilder.build();
//
//        String[] header = null;
//        String[] nextLine;
//        JSONArray jsonArray = new JSONArray();
//
//        while ((nextLine = csvReader.readNext()) != null) {
//            if (header == null) {
//                header = nextLine;
//                continue;
//            }
//
//            JSONObject jsonObject = new JSONObject();
//            for (int i = 0; i < header.length; i++) {
////                if (nextLine[i] == "") {
////                    nextLine[i] = null;
////                }
//                System.out.println(header[i]);
//                if (header[i].trim().equalsIgnoreCase("\uFEFFFirst Name")) {
//                    jsonObject.put("personFirstName", nextLine[i]);
//                } else if (header[i].trim().equalsIgnoreCase("Last Name")) {
//                    jsonObject.put("personLastName", nextLine[i]);
//                } else if (header[i].trim().equalsIgnoreCase("uid")) {
//                    jsonObject.put("personUid", nextLine[i]);
//                } else if (header[i].trim().equalsIgnoreCase("Mobile Number")) {
//                    jsonObject.put("personMobileNumber", nextLine[i]);
//                } else if (header[i].trim().equalsIgnoreCase("Email")) {
//                    jsonObject.put("personEmail", nextLine[i]);
//                }  else if (header[i].trim().equalsIgnoreCase("Credential type")) {
//                    jsonObject.put("credentialType", nextLine[i]);
//                } else if (header[i].trim().equalsIgnoreCase("Credential pin")) {
//                    jsonObject.put("credentialPin", nextLine[i]);
//                } else if (header[i].trim().equalsIgnoreCase("Credential Expiry (YYYY-MM-DD HOUR-MIN-SEC)")) {
//                    jsonObject.put("credentialExpiry", nextLine[i]);
//                } else {
//                    jsonObject.put(header[i].trim(), nextLine[i]);
//                }
//
//                if ((jsonObject.has("personFirstName"))
//                        && (jsonObject.has("personLastName"))
//                        && (jsonObject.has("personMobileNumber"))) {
//                    System.out.println(jsonObject + " Is valid");
//                    jsonObject.put("Color", "green");
//                } else {
//                    jsonObject.put("Color", "red");
//                }
//
//            }
//            jsonArray.put(jsonObject);
//        }
//
//        return jsonArray.toString();
//    }
//}
