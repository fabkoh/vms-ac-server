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
    public List<VideoRecorder> getVideoRecorders(){
        return videoRecorderService.findAllNotDeleted();
    }

    //returns details of an entrance
    @GetMapping("/videorecorder/{id}")
    public ResponseEntity<?> getVideoRecorder(@PathVariable("id") Long id){
        Optional<VideoRecorder> optionalVideoRecorder = videoRecorderService.findByIdNotDeleted(id);
        if(optionalVideoRecorder.isPresent()){
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
//        return new ResponseEntity<>(videoRecorderService.save(newVideoRecorder.toCreateVideoRecorder(false))
//                , HttpStatus.CREATED);

        Map<String, String> errors = videoRecorderService.isNotValidVideoRecorderCreation(
                newVideoRecorder.getRecorderName(),
                newVideoRecorder.getRecorderPrivateIp(),
                newVideoRecorder.getRecorderPublicIp(),
                newVideoRecorder.getRecorderPortNumber(),
                newVideoRecorder.getRecorderIWSPort(),
                newVideoRecorder.getRecorderSerialNumber());
        if (!errors.isEmpty()) {
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }

        VideoRecorder videoRecorder;
        try {
            videoRecorder = videoRecorderService.save(newVideoRecorder.toCreateVideoRecorder(false));
            if (videoRecorder == null){
                return new ResponseEntity<>("error opening ports, please enable UPNP",
                        HttpStatus.BAD_REQUEST);
            }
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(videoRecorder, HttpStatus.CREATED);
    }

    //Update name or description of recorder
    @PutMapping("/videorecorder")
    public ResponseEntity<?> updateVideoRecorder(@RequestBody VideoRecorderDto editVideoRecorder){
        Long tempid = editVideoRecorder.getRecorderId();
        Optional<VideoRecorder> optionalVideoRecorder = videoRecorderService.findByIdNotDeleted(tempid);

        if(optionalVideoRecorder.isEmpty()){
            Map<String, String> errors = new HashMap<>();
            errors.put("recorderId", "Recorder with ID " +
                    tempid + " does not exist");
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }

        // Validations
        String newVideoRecorderName = editVideoRecorder.getRecorderName();
        String newVideoRecorderSerialNumber = editVideoRecorder.getRecorderSerialNumber();
        String newVideoRecorderPrivateIp = editVideoRecorder.getRecorderPrivateIp();
        Integer newVideoRecorderPortNumber = editVideoRecorder.getRecorderPortNumber();

        VideoRecorder videoRecorder = optionalVideoRecorder.get();

        if (!Objects.equals(editVideoRecorder.getRecorderName(), videoRecorder.getRecorderName())){
            if(videoRecorderService.nameInUse(newVideoRecorderName)){
                Map<String, String> errors = new HashMap<>();
                errors.put("recorderName", "Recorder Name " + newVideoRecorderName + " in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        if (!Objects.equals(editVideoRecorder.getRecorderSerialNumber(), videoRecorder.getRecorderSerialNumber())){
            if(videoRecorderService.serialNumberInUse(newVideoRecorderSerialNumber)){
                Map<String, String> errors = new HashMap<>();
                errors.put("recorderSerialNumber", "Recorder Serial Number " + newVideoRecorderSerialNumber + " in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        if (!Objects.equals(editVideoRecorder.getRecorderPrivateIp(), videoRecorder.getRecorderPrivateIp())){
            if(videoRecorderService.ipAddressInUse(newVideoRecorderPrivateIp)){
                Map<String, String> errors = new HashMap<>();
                errors.put("recorderPrivateIp", "Recorder private IP Address " + newVideoRecorderPrivateIp + " in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        if (!Objects.equals(editVideoRecorder.getRecorderPortNumber(), videoRecorder.getRecorderPortNumber())){
            if(videoRecorderService.portNumberInUse(newVideoRecorderPortNumber)){
                Map<String, String> errors = new HashMap<>();
                errors.put("recorderPortNumber", "Recorder Port Number " + newVideoRecorderPortNumber + " in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        VideoRecorder updatedVideoRecorder = videoRecorderService.save(editVideoRecorder.toUpdateVideoRecorder(false));

        return new ResponseEntity<>(updatedVideoRecorder,HttpStatus.OK);
    }

    //set delete = true.
    @DeleteMapping("/videorecorder/{id}")
    public ResponseEntity<?> deleteVideoRecorder(@PathVariable("id")Long id){
        try {
            videoRecorderService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.NOT_FOUND);
        }
    }

    // testing
    @GetMapping("/videorecorder/testing")
    public ResponseEntity<?> testing(){
        try {
            String privateIp = "192.168.1.172";
            String publicIp = "118.201.255.164";
            videoRecorderService.openUPNPports(privateIp,80,8085);
            videoRecorderService.openUPNPports(privateIp,7681,7681);
            videoRecorderService.deleteUPNPports(8085);
            videoRecorderService.deleteUPNPports(7681);
            videoRecorderService.checkIfPortAvailable(publicIp,8085);
            videoRecorderService.checkIfPortAvailable(publicIp,8084);
            videoRecorderService.checkIfPortAvailable(publicIp,8083);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.NOT_FOUND);
        }
    }
}
