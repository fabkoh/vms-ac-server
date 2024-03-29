package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.videorecorder.VideoRecorder;
import com.vmsac.vmsacserver.model.videorecorder.VideoRecorderDto;
import com.vmsac.vmsacserver.service.VideoRecorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class VideoRecorderController {

    @Autowired
    VideoRecorderService videoRecorderService;

    //returns all video recorders that are not deleted
    @GetMapping("/videorecorders")
    public List<VideoRecorder> getVideoRecorders() {
        return videoRecorderService.findAllNotDeleted();
    }

    //returns details of an entrance
    @GetMapping("/videorecorder/{id}")
    public ResponseEntity<?> getVideoRecorder(@PathVariable("id") Long id) {
        Optional<VideoRecorder> optionalVideoRecorder = videoRecorderService.findByIdNotDeleted(id);
        if (optionalVideoRecorder.isPresent()) {
            VideoRecorder videoRecorder = optionalVideoRecorder.get();
            return ResponseEntity.ok(videoRecorder);
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("recorderId", "Recorder with ID " +
                id + " does not exist");
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    //create a recorder
    @PostMapping("/videorecorder")
    public ResponseEntity<?> createVideoRecorder(@RequestBody VideoRecorderDto newVideoRecorder) {

        Map<String, String> errors = videoRecorderService.isNotValidVideoRecorderCreation(
                newVideoRecorder.getRecorderName(),
                newVideoRecorder.getRecorderPrivateIp(),
                newVideoRecorder.getRecorderPublicIp(),
                newVideoRecorder.getRecorderPortNumber(),
                newVideoRecorder.getRecorderIWSPort()
        );

        if (!errors.isEmpty()) {
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }

        VideoRecorder videoRecorder;
        try {
            videoRecorder = videoRecorderService.save(newVideoRecorder.toCreateVideoRecorder(false));
            if (videoRecorder == null) {
                return new ResponseEntity<>("error opening ports, please enable UPNP",
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(videoRecorder, HttpStatus.CREATED);
    }

    //Update name or description of recorder
    @PutMapping("/videorecorder")
    public ResponseEntity<?> updateVideoRecorder(@RequestBody VideoRecorderDto editVideoRecorder) {
        Long tempid = editVideoRecorder.getRecorderId();
        Optional<VideoRecorder> optionalVideoRecorder = videoRecorderService.findByIdNotDeleted(tempid);

        if (optionalVideoRecorder.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("recorderId", "Recorder with ID " +
                    tempid + " does not exist");
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }

        // Validations
        String newVideoRecorderName = editVideoRecorder.getRecorderName();
        String newVideoRecorderPrivateIp = editVideoRecorder.getRecorderPrivateIp();
        Integer newVideoRecorderPortNumber = editVideoRecorder.getRecorderPortNumber();
        Integer newVideoIWSPortNumber = editVideoRecorder.getRecorderIWSPort();
        String newVideoRecorderPublicIp = editVideoRecorder.getRecorderPublicIp();

        VideoRecorder videoRecorder = optionalVideoRecorder.get();

        if (!Objects.equals(editVideoRecorder.getRecorderName(), videoRecorder.getRecorderName())) {
            if (videoRecorderService.nameInUse(newVideoRecorderName)) {
                Map<String, String> errors = new HashMap<>();
                errors.put("recorderName", "Recorder Name " + newVideoRecorderName + " in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }

        if (!Objects.equals(editVideoRecorder.getRecorderPrivateIp(), videoRecorder.getRecorderPrivateIp())) {
            if (videoRecorderService.ipAddressInUse(newVideoRecorderPrivateIp, newVideoRecorderPublicIp)) {
                Map<String, String> errors = new HashMap<>();
                errors.put("recorderPrivateIp", "Recorder private IP Address " + newVideoRecorderPrivateIp + " in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        if (!Objects.equals(editVideoRecorder.getRecorderIWSPort(), videoRecorder.getRecorderIWSPort())) {
            if (videoRecorderService.portNumberInUse(newVideoIWSPortNumber, newVideoRecorderPublicIp) && newVideoIWSPortNumber != null) {
                Map<String, String> errors = new HashMap<>();
                errors.put("recorderIWSPortNumber", "Recorder IWS Port Number " + newVideoIWSPortNumber + " in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        if (!Objects.equals(editVideoRecorder.getRecorderPortNumber(), videoRecorder.getRecorderPortNumber())) {
            if (videoRecorderService.portNumberInUse(newVideoRecorderPortNumber, newVideoRecorderPublicIp) && newVideoRecorderPortNumber != null) {
                Map<String, String> errors = new HashMap<>();
                errors.put("recorderPublicPortNumber", "Recorder Public Port Number " + newVideoRecorderPortNumber + " in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        VideoRecorder updatedVideoRecorder = videoRecorderService.save(editVideoRecorder.toUpdateVideoRecorder(false));

        return new ResponseEntity<>(updatedVideoRecorder, HttpStatus.OK);
    }

    //set delete = true.
    @DeleteMapping("/videorecorder/{id}")
    public ResponseEntity<?> deleteVideoRecorder(@PathVariable("id") Long id) {
        try {
            videoRecorderService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.toString());
            return new ResponseEntity<>(e.toString(), HttpStatus.NOT_FOUND);
        }
    }
}
