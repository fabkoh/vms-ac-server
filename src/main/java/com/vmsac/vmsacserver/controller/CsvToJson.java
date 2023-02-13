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
                if(nextLine[i] == ""){
                    nextLine[i] = null;
                }
                jsonObject.put(header[i], nextLine[i]);
            }
            if ((jsonObject.has("\uFEFFFirst Name"))
                    && (jsonObject.has("Last Name"))
                    && (jsonObject.has("Mobile Number"))) {
                System.out.println(jsonObject + " Is valid");
                jsonObject.put("Color", "green");
            } else{
                jsonObject.put("Color", "red");
            }
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }
}
