package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.videorecorder.VideoRecorder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRecorderRepository extends JpaRepository<VideoRecorder, Long> {
    List<VideoRecorder> findByDeletedIsFalseOrderByCreatedDesc();

    Optional<VideoRecorder> findByRecorderIdEqualsAndDeletedIsFalse(Long recorderId);

    Optional<VideoRecorder> findByRecorderSerialNumberEqualsAndDeletedIsFalse(String recorderSerialNumber);

    boolean existsByRecorderSerialNumberEqualsAndDeletedIsFalse(String recorderSerialNumber);

    boolean existsByRecorderNameEqualsAndDeletedIsFalse(String recorderName);

    boolean existsByRecorderPrivateIpAndDeletedAndRecorderPublicIp(String recorderPrivateIp, Boolean deleted, String recorderPublicIp);

    boolean existsByRecorderPortNumberAndDeletedAndRecorderPublicIp(Integer recorderPortNumber, Boolean deleted, String recorderPublicIp);

    boolean existsByRecorderIWSPortAndDeletedAndRecorderPublicIp(Integer recorderIWSPort, Boolean deleted, String recorderPublicIp);


}