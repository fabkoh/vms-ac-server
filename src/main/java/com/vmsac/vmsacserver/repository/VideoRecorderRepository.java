package com.vmsac.vmsacserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vmsac.vmsacserver.model.videorecorder.VideoRecorder;

public interface VideoRecorderRepository extends JpaRepository<VideoRecorder, Long> {
    List<VideoRecorder> findByDeletedIsFalseOrderByCreatedDesc();

    Optional<VideoRecorder> findByRecorderIdEqualsAndDeletedIsFalse(Long recorderId);

    Optional<VideoRecorder> findByRecorderSerialNumberEqualsAndDeletedIsFalse(String recorderSerialNumber);

    boolean existsByRecorderIpAddressEqualsAndDeletedIsFalse(String recorderIpAddress);

    boolean existsByRecorderSerialNumberEqualsAndDeletedIsFalse(String recorderSerialNumber);

    boolean existsByRecorderNameEqualsAndDeletedIsFalse(String recorderName);

    boolean existsByRecorderPortNumberEqualsAndDeletedIsFalse(Integer recorderPortNumber);
}