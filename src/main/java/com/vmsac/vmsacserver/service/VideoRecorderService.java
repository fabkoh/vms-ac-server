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

    public Optional<VideoRecorder> findByIdNotDeleted(Long Id){
        return videoRecorderRepository.findByRecorderIdEqualsAndDeletedIsFalse(Id);
    }

    public Optional<VideoRecorder> findBySerialNumberNotDeleted(String serialNumber) {
        return videoRecorderRepository.findByRecorderSerialNumberEqualsAndDeletedIsFalse(serialNumber);
    }

    public VideoRecorder save(VideoRecorder videoRecorder){
        return videoRecorderRepository.save(videoRecorder);
//        if (openUPNPports(videoRecorder.getRecorderPrivateIp(),
//                80, videoRecorder.getRecorderPortNumber()) &&
//                openUPNPports(videoRecorder.getRecorderPrivateIp(),
//                        7681, videoRecorder.getRecorderIWSPort())){
//            return videoRecorderRepository.save(videoRecorder);
//        }
//        return null;
    }

    public void delete(Long id) throws Exception{
        VideoRecorder deleted = videoRecorderRepository.findByRecorderIdEqualsAndDeletedIsFalse(id)
                .orElseThrow(() -> new RuntimeException("Recorder with id "+ id + " does not exist"));
        if (!(deleteUPNPports(deleted.getRecorderIWSPort()) && deleteUPNPports(deleted.getRecorderPortNumber()))){
            throw new Exception("Unable to disable UPNP");
        }
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
        return videoRecorderRepository.existsByRecorderPrivateIpEqualsAndDeletedIsFalse(ipAddress);
    }

    public Boolean portNumberInUse(Integer portNumber){
        return videoRecorderRepository.existsByRecorderPortNumberEqualsAndDeletedIsFalse(portNumber);
    }

    public Map<String, String> isNotValidVideoRecorderCreation(String name, String privateIP,
            String publicIP, Integer portNumber, Integer recorderIWSPort, String serialNumber) {
        Map<String, String> errors = new HashMap<>();
        if (videoRecorderRepository.existsByRecorderNameEqualsAndDeletedIsFalse(name)) {
            errors.put("recorderName", "Recorder name " + name + " in use");
        }

        if (videoRecorderRepository.existsByRecorderPrivateIpEqualsAndDeletedIsFalse(privateIP)) {
            errors.put("recorderName", "Recorder private ip  " + privateIP + " in use");
        }

        if (videoRecorderRepository.existsByRecorderSerialNumberEqualsAndDeletedIsFalse(serialNumber)) {
            errors.put("recorderSerialNumber", "Recorder serial number " + serialNumber + " in use");
        }

        if (videoRecorderRepository.existsByRecorderPortNumberEqualsAndDeletedIsFalse(portNumber)) {
            errors.put("recorderPortNumber", "Recorder port number " + portNumber + " in use");
        }

        if (videoRecorderRepository.existsByRecorderIWSPortEqualsAndDeletedIsFalse(recorderIWSPort)) {
            errors.put("recorderIWSPort", "Recorder IWS port " + recorderIWSPort + " in use");
        }

        if (!checkIfPortAvailable(publicIP,recorderIWSPort)) {
            errors.put("recorderIWSPort", "Recorder IWS port " + recorderIWSPort + " in use");
        }

        if (!checkIfPortAvailable(publicIP,portNumber)) {
            errors.put("recorderIWSPort", "Recorder port number " + portNumber + " in use");
        }
        return errors;
    }

    public Boolean openUPNPports(String privateIp, Integer internalPort,Integer publicPort){
        String s;
        Process p;
        try {
            String command = "upnpc -a " + privateIp + " " + internalPort +
                    " " + publicPort + " tcp";
            System.out.println(command);
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) { return false;}
        return true;
    }

    public Boolean deleteUPNPports(Integer port){
        String s;
        Process p;
        try {
            String command ="upnpc -d " + port + " tcp";
            System.out.println(command);
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) { return false;}
        return true;
    }

    public Boolean checkIfPortAvailable(String publicIp, Integer port){
        String s;
        Process p;
        try {
            String command ="telnet " + publicIp + " " + port;
            System.out.println(command);
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) { return false;}
        return true;
    }

}

