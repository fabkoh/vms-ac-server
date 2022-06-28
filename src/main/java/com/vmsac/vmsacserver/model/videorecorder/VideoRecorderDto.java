package com.vmsac.vmsacserver.model.videorecorder;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class VideoRecorderDto implements Serializable {

    private Long recorderId;

    @NotBlank(message = "Recorder name must not be blank")
    private String recorderName;

    @NotBlank(message = "Recorder serial number must not be blank")
    private String recorderSerialNumber;

    @NotBlank(message = "Recorder IP address must not be blank")
    private String recorderIpAddress;

    @NotBlank(message = "Recorder port number must not be blank")
    private Integer recorderPortNumber;

    @NotBlank(message = "Recorder username must not be blank")
    private String recorderUsername;

    @NotBlank(message = "Recorder password must not be blank")
    private String recorderPassword;

    private final LocalDateTime created;

    private Boolean deleted;

    public VideoRecorder toCreateVideoRecorder(Boolean deleted) {
        return new VideoRecorder(recorderName, recorderSerialNumber, recorderIpAddress, recorderPortNumber, recorderUsername, recorderPassword, deleted);
    }

    public VideoRecorder toUpdateVideoRecorder(Boolean deleted) {
        return new VideoRecorder(recorderId, recorderName, recorderSerialNumber, recorderIpAddress, recorderPortNumber, recorderUsername, recorderPassword, deleted);
    }
}
