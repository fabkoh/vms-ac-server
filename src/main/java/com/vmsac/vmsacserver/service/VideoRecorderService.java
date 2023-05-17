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
        //boolean upnp = videoRecorder
        //if (videoRecorder) get enable UPNP
        if (videoRecorder.isAutoPortForwarding()) {
            System.out.println("checkpoint 6");
            System.out.println(videoRecorder.getRecorderPortNumber());
            String publicIP = videoRecorder.getRecorderPublicIp();
            int portNumber = 8085;
            int IWSNumber = 7681;
            if (videoRecorder.getRecorderPortNumber() == null) {
                portNumber = findFirstAvailablePort(publicIP, 8085);
            } else {
                portNumber = videoRecorder.getRecorderPortNumber();
            }
            if (videoRecorder.getRecorderIWSPort() == null) {
                IWSNumber = findFirstAvailablePort(publicIP, 7681);
            } else {
                IWSNumber = videoRecorder.getRecorderIWSPort();
            }
            try {
                openUPNPports(videoRecorder.getRecorderPrivateIp(),
                        80, portNumber);
                openUPNPports(videoRecorder.getRecorderPrivateIp(),
                        7681, IWSNumber);
            } catch (Exception e) {
                return null;
            }
            videoRecorder.setRecorderPortNumber(portNumber);
            videoRecorder.setRecorderIWSPort(IWSNumber);
        }
        return videoRecorderRepository.save(videoRecorder);

    }

    public void delete(Long id) throws Exception {
        VideoRecorder deleted = videoRecorderRepository.findByRecorderIdEqualsAndDeletedIsFalse(id)
                .orElseThrow(() -> new RuntimeException("Recorder with id " + id + " does not exist"));
        if (!(deleteUPNPports(deleted.getRecorderIWSPort()) && deleteUPNPports(deleted.getRecorderPortNumber()))) {
            throw new Exception("Unable to disable UPNP");
        }
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
                                                               String publicIP, Integer portNumber, Integer recorderIWSPort, Boolean enableUPNP) {
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

        if (enableUPNP && (!checkIfPortAvailable(publicIP, recorderIWSPort))) {
            errors.put("recorderIWSPort", "Recorder IWS port " + recorderIWSPort + " in use");
        }

        if (enableUPNP && (!checkIfPortAvailable(publicIP, portNumber))) {
            errors.put("recorderIWSPort", "Recorder port number " + portNumber + " in use");
        }
        return errors;
    }

    public Boolean openUPNPports(String privateIp, Integer internalPort, Integer publicPort) {
        String s;
        Process p;
        try {
            String command = "upnpc -a " + privateIp + " " + internalPort +
                    " " + publicPort + " tcp";
            System.out.println(command);
            p = Runtime.getRuntime().exec(command);
            System.out.println("create upnp" + p);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean deleteUPNPports(Integer port) {
        String s;
        Process p;
        try {
            String command = "upnpc -d " + port + " tcp";
            System.out.println(command);
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean checkIfPortAvailable(String publicIp, Integer port) {
        if (port == null) {
            return true;
        }
        String s;
        Process p;
        System.out.println("checkpoint 4");
        String formattedPort = String.format("%04d", port);
        System.out.println("checkpoint 5");
        try {
            String command = "telnet " + publicIp + " " + formattedPort;
            System.out.println(command);
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int findFirstAvailablePort(String publicIp, int port) {
        while (true) {
            if (checkIfPortAvailable(publicIp, port)) {
                return port;
            } else {
                if (port < 9999) {
                    port++;
                } else {
                    port = 0000;
                }
            }
        }
    }

}

