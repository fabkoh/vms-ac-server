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

    private String recorderSerialNumber;

    @NotBlank(message = "Recorder Public IP address must not be blank")
    private String recorderPublicIp;

    @NotBlank(message = "Recorder Private port number must not be blank")
    private String recorderPrivateIp;

    private Integer recorderPortNumber;

    private Integer recorderIWSPort;

    @NotBlank(message = "Recorder username must not be blank")
    private String recorderUsername;

    @NotBlank(message = "Recorder password must not be blank")
    private String recorderPassword;

    @Column( name = "autoportforwarding", nullable = false)
    private boolean autoPortForwarding;

    private final LocalDateTime created;

    private Boolean deleted;

    public VideoRecorder toCreateVideoRecorder(Boolean deleted) {
        return new VideoRecorder(recorderName, recorderSerialNumber, recorderPublicIp, recorderPrivateIp,
                recorderPortNumber, recorderIWSPort, recorderUsername, recorderPassword, autoPortForwarding,deleted);
    }

    public VideoRecorder toUpdateVideoRecorder(Boolean deleted) {
        return new VideoRecorder(recorderId, recorderName, recorderSerialNumber,
                recorderPublicIp, recorderPrivateIp, recorderPortNumber, recorderIWSPort,
                recorderUsername, recorderPassword, autoPortForwarding, deleted);
    }
}
