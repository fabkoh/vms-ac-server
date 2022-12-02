package com.vmsac.vmsacserver.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmsac.vmsacserver.model.videorecorder.VideoRecorder;
import com.vmsac.vmsacserver.repository.VideoRecorderRepository;


@Service
public class VideoRecorderService {

    @Autowired
    VideoRecorderRepository videoRecorderRepository;

    public List<VideoRecorder> findAllNotDeleted() {
        return videoRecorderRepository.findByDeletedIsFalseOrderByCreatedDesc();
    }

    public Optional<VideoRecorder> findByIdNotDeleted(Long Id){
        return videoRecorderRepository.findByRecorderIdEqualsAndDeletedIsFalse(Id);
    }

    public Optional<VideoRecorder> findBySerialNumberNotDeleted(String serialNumber) {
        return videoRecorderRepository.findByRecorderSerialNumberEqualsAndDeletedIsFalse(serialNumber);
    }

    public VideoRecorder save(VideoRecorder videoRecorder){
        return videoRecorderRepository.save(videoRecorder);
    }

    public void delete(Long id){
        VideoRecorder deleted = videoRecorderRepository.findByRecorderIdEqualsAndDeletedIsFalse(id)
                .orElseThrow(() -> new RuntimeException("Recorder with id "+ id + " does not exist"));
        deleted.setDeleted(true);

        videoRecorderRepository.save(deleted);
    }

    public Boolean nameInUse(String name){
        return videoRecorderRepository.existsByRecorderNameEqualsAndDeletedIsFalse(name);
    }

    public Boolean serialNumberInUse(String serialNumber){
        return videoRecorderRepository.existsByRecorderSerialNumberEqualsAndDeletedIsFalse(serialNumber);
    }

    public Boolean ipAddressInUse(String ipAddress){
        return videoRecorderRepository.existsByRecorderIpAddressEqualsAndDeletedIsFalse(ipAddress);
    }

    public Boolean portNumberInUse(Integer portNumber){
        return videoRecorderRepository.existsByRecorderPortNumberEqualsAndDeletedIsFalse(portNumber);
    }

    public Map<String, String> isNotValidVideoRecorderCreation(String name, String ipAddress, Integer portNumber, String serialNumber) {
        Map<String, String> errors = new HashMap<>();
        if (videoRecorderRepository.existsByRecorderNameEqualsAndDeletedIsFalse(name)) {
            errors.put("recorderName", "Recorder name " + name + " in use");
        }

        if (videoRecorderRepository.existsByRecorderSerialNumberEqualsAndDeletedIsFalse(serialNumber)) {
            errors.put("recorderSerialNumber", "Recorder serial number " + serialNumber + " in use");
        }

        if (videoRecorderRepository.existsByRecorderIpAddressEqualsAndDeletedIsFalse(ipAddress)) {
            errors.put("recorderIpAddress", "Recorder IP address " + ipAddress + " in use");
        }

        if (videoRecorderRepository.existsByRecorderPortNumberEqualsAndDeletedIsFalse(portNumber)) {
            errors.put("recorderPortNumber", "Recorder port number " + portNumber + " in use");
        }
        return errors;
    }
}
