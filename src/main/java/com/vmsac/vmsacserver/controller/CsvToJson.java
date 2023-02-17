package com.vmsac.vmsacserver.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;

import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReaderBuilder;

public class CsvToJson {
    public String convert(MultipartFile file) throws IOException, CsvValidationException, JSONException {
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader);
        com.opencsv.CSVReader csvReader = csvReaderBuilder.build();

        String[] header = null;
        String[] nextLine;
        JSONArray jsonArray = new JSONArray();

        while ((nextLine = csvReader.readNext()) != null) {
            if (header == null) {
                header = nextLine;
                continue;
            }

            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < header.length; i++) {
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

                if ((jsonObject.has("personFirstName"))
                        && (jsonObject.has("personLastName"))
                        && (jsonObject.has("personMobileNumber"))) {
                    System.out.println(jsonObject + " Is valid");
                    jsonObject.put("Color", "green");
                } else {
                    jsonObject.put("Color", "red");
                }

            }
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
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
