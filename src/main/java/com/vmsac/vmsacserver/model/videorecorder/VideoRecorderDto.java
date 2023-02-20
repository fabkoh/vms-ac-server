package com.vmsac.vmsacserver.model.videorecorder;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class VideoRecorderDto implements Serializable {

    private Long recorderId;

    @NotBlank(message = "Recorder name must not be blank")
    private String recorderName;

    @NotBlank(message = "Recorder serial number must not be blank")
    private String recorderSerialNumber;

    @NotBlank(message = "Recorder Public IP address must not be blank")
    private String recorderPublicIp;

    @NotBlank(message = "Recorder Private port number must not be blank")
    private String recorderPrivateIp;

    @NotBlank(message = "Recorder port number must not be blank")
    private Integer recorderPortNumber;

    @Column( name = "recorderiwsport", nullable = false, unique = true)
    private Integer recorderIWSPort;

    @NotBlank(message = "Recorder username must not be blank")
    private String recorderUsername;

    @NotBlank(message = "Recorder password must not be blank")
    private String recorderPassword;

    private final LocalDateTime created;

    private Boolean deleted;

    public VideoRecorder toCreateVideoRecorder(Boolean deleted) {
        return new VideoRecorder(recorderName, recorderSerialNumber, recorderPublicIp, recorderPrivateIp,
                recorderPortNumber, recorderIWSPort, recorderUsername, recorderPassword, deleted);
    }

    public VideoRecorder toUpdateVideoRecorder(Boolean deleted) {
        return new VideoRecorder(recorderId, recorderName, recorderSerialNumber,
                recorderPublicIp, recorderPrivateIp, recorderPortNumber, recorderIWSPort,
                recorderUsername, recorderPassword, deleted);
    }
}
