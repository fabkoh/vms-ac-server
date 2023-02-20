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

    boolean existsByRecorderPrivateIpEqualsAndDeletedIsFalse(String recorderPrivateIp);

    boolean existsByRecorderPortNumberEqualsAndDeletedIsFalse(Integer recorderPortNumber);

    boolean existsByRecorderIWSPortEqualsAndDeletedIsFalse(Integer recorderIWSPort);
}