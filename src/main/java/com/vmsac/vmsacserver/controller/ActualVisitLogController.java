package com.vmsac.vmsacserver.controller;

import com.google.zxing.WriterException;
import com.vmsac.vmsacserver.model.ActualVisitLogs;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.repository.ActualVisitLogRepository;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import com.vmsac.vmsacserver.service.ProcessJson;
import com.vmsac.vmsacserver.service.RetrieveQrId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ActualVisitLogController {

    @Autowired
    private ActualVisitLogRepository actualVisitLogRepository;

    List<ActualVisitLogs> actualVisits = new List<ActualVisitLogs>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<ActualVisitLogs> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(ActualVisitLogs actualVisitLogs) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends ActualVisitLogs> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends ActualVisitLogs> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public ActualVisitLogs get(int index) {
            return null;
        }

        @Override
        public ActualVisitLogs set(int index, ActualVisitLogs element) {
            return null;
        }

        @Override
        public void add(int index, ActualVisitLogs element) {

        }

        @Override
        public ActualVisitLogs remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<ActualVisitLogs> listIterator() {
            return null;
        }

        @Override
        public ListIterator<ActualVisitLogs> listIterator(int index) {
            return null;
        }

        @Override
        public List<ActualVisitLogs> subList(int fromIndex, int toIndex) {
            return null;
        }
    };

    //@Autowired
    //private ProcessJson processJson;

    @GetMapping("/actualvisits")
    List<ActualVisitLogs> getActualVisits(){
        return actualVisitLogRepository.findAll();
    }

    //Storing visit log files when network is interrupted
    @GetMapping("/storevisitlogs")
    public ResponseEntity<String> insertVisits() {
        //ActualVisitLogs v2 = new ActualVisitLogs("00002", "2021-08-07", "6516516516351");
        //ActualVisitLogs v3 = new ActualVisitLogs("00003", "2021-08-07", "65165136575");
        //ActualVisitLogs v4 = new ActualVisitLogs("00004", "2021-08-07", "655555555561");
        //List<ActualVisitLogs> actualVisits = Arrays.asList(v1, v2, v3, v4);
        //actualVisitLogRepository.saveAll(actualVisits);
        processJson();
       return ResponseEntity.status(HttpStatus.OK)
               .contentType(MediaType.APPLICATION_JSON)
               .body("List stored in DB successfully");
    }

    //Storing adhoc visits, _POSTS made by RPi
    @GetMapping(path = "/storevisit/{logid}/{scanneridin}")
    private ResponseEntity<String> insertSingleVisit(
            @PathVariable("logid") String logId,
            @PathVariable("scanneridin") String scannerIdIn) throws IOException {

        ActualVisitLogs tempSingleV = new ActualVisitLogs(logId, LocalDate.now().toString(), scannerIdIn);
        actualVisits = Arrays.asList(tempSingleV);
        actualVisitLogRepository.saveAll(actualVisits);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Visit saved");

    }

    private void processJson() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("C:\\Users\\tester85\\Desktop\\actualvisits.txt"));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;

            // A JSON array. JSONObject supports java.util.List interface.
            JSONArray visitList = (JSONArray) jsonObject.get("Visits");

            // An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
            // Iterators differ from enumerations in two ways:
            // 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
            // 2. Method names have been improved.
            Iterator<JSONObject> iterator = visitList.iterator();
            for(int i = 0; i < visitList.size(); i++)
            //while (iterator.hasNext()) {
            {   System.out.println(iterator.next());
                JSONObject tempobj = (JSONObject)visitList.get(i);
                ActualVisitLogs tempV = new ActualVisitLogs(tempobj.get("logId").toString(), tempobj.get("timeIn").toString(),tempobj.get("scannerIdIn").toString());
             actualVisits = Arrays.asList(tempV);
             //actualVisits.add(tempV);
             actualVisitLogRepository.saveAll(actualVisits);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public ActualVisitLogController() {
    }



}
