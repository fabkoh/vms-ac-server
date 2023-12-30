package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.videorecorder.VideoRecorder;
import com.vmsac.vmsacserver.repository.VideoRecorderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VideoRecorderService {

    @Autowired
    VideoRecorderRepository videoRecorderRepository;

    public List<VideoRecorder> findAllNotDeleted() {
        return videoRecorderRepository.findByDeletedIsFalseOrderByCreatedDesc();
    }

    public Optional<VideoRecorder> findByIdNotDeleted(Long Id) {
        return videoRecorderRepository.findByRecorderIdEqualsAndDeletedIsFalse(Id);
    }

    public Optional<VideoRecorder> findBySerialNumberNotDeleted(String serialNumber) {
        return videoRecorderRepository.findByRecorderSerialNumberEqualsAndDeletedIsFalse(serialNumber);
    }


    public VideoRecorder save(VideoRecorder videoRecorder) {
        return videoRecorderRepository.save(videoRecorder);
    }

    public void delete(Long id) throws Exception {

        VideoRecorder deleted = videoRecorderRepository.findByRecorderIdEqualsAndDeletedIsFalse(id)
                .orElseThrow(() -> new RuntimeException("Recorder with id " + id + " does not exist"));
        deleted.setDeleted(true);

        videoRecorderRepository.save(deleted);
    }

    public Boolean nameInUse(String name) {
        return videoRecorderRepository.existsByRecorderNameEqualsAndDeletedIsFalse(name);
    }

    public Boolean serialNumberInUse(String serialNumber) {
        return videoRecorderRepository.existsByRecorderSerialNumberEqualsAndDeletedIsFalse(serialNumber);
    }

    public Boolean ipAddressInUse(String ipAddress, String publicIp) {
        return videoRecorderRepository.existsByRecorderPrivateIpAndDeletedAndRecorderPublicIp(ipAddress, false, publicIp);
    }

    public Boolean portNumberInUse(Integer portNumber, String publicIp) {
        return videoRecorderRepository.existsByRecorderPortNumberAndDeletedAndRecorderPublicIp(portNumber, false, publicIp);
    }

    public Boolean ISWportInUse(Integer IWSPort, String publicIp) {
        return videoRecorderRepository.existsByRecorderIWSPortAndDeletedAndRecorderPublicIp(IWSPort, false, publicIp);
    }

    public Map<String, String> isNotValidVideoRecorderCreation(String name, String privateIP,
                                                               String publicIP, Integer portNumber, Integer recorderIWSPort) {
        Map<String, String> errors = new HashMap<>();
        if (videoRecorderRepository.existsByRecorderNameEqualsAndDeletedIsFalse(name)) {
            errors.put("recorderName", "Recorder name " + name + " in use");
        }

        if (videoRecorderRepository.existsByRecorderPrivateIpAndDeletedAndRecorderPublicIp(privateIP, false, publicIP)) {
            errors.put("recorderName", "Recorder private ip  " + privateIP + " in use");
        }

        if (videoRecorderRepository.existsByRecorderPortNumberAndDeletedAndRecorderPublicIp(portNumber, false, publicIP)) {
            errors.put("recorderPortNumber", "Recorder port number " + portNumber + " in use");
        }

        if (videoRecorderRepository.existsByRecorderIWSPortAndDeletedAndRecorderPublicIp(recorderIWSPort, false, publicIP)) {
            errors.put("recorderIWSPort", "Recorder IWS port " + recorderIWSPort + " in use");
        }

        return errors;
    }
}
