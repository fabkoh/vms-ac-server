package com.vmsac.vmsacserver.model.videorecorder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "videorecorder")
public class VideoRecorder {

    public VideoRecorder(String recorderName, String recorderSerialNumber, String recorderPublicIp,
                         String recorderPrivateIp, Integer recorderPortNumber, Integer recorderIWSPort,
                         String recorderUsername, String recorderPassword, Boolean deleted) {
        this.recorderName = recorderName;
        this.recorderSerialNumber = recorderSerialNumber;
        this.recorderPublicIp = recorderPublicIp;
        this.recorderPrivateIp = recorderPrivateIp;
        this.recorderPortNumber = recorderPortNumber;
        this.recorderIWSPort = recorderIWSPort;
        this.recorderUsername = recorderUsername;
        this.recorderPassword = recorderPassword;
        this.deleted = deleted;
        this.created = LocalDateTime.now();
    }

    public VideoRecorder(Long recorderId, String recorderName, String recorderSerialNumber,
                         String recorderPublicIp, String recorderPrivateIp, Integer recorderPortNumber,
                         Integer recorderIWSPort, String recorderUsername, String recorderPassword, Boolean deleted) {
        this.recorderId = recorderId;
        this.recorderName = recorderName;
        this.recorderSerialNumber = recorderSerialNumber;
        this.recorderPublicIp = recorderPublicIp;
        this.recorderPrivateIp = recorderPrivateIp;
        this.recorderPortNumber = recorderPortNumber;
        this.recorderIWSPort = recorderIWSPort;
        this.recorderUsername = recorderUsername;
        this.recorderPassword = recorderPassword;
        this.deleted = deleted;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recorderid", columnDefinition = "serial", nullable = false, unique = true)
    private Long recorderId;

    @Column( name ="recordername", nullable = false)
    private String recorderName;

    @Column( name = "recorderserialnumber", nullable = false, unique = true)
    private String recorderSerialNumber;

    @Column( name = "recorderpublicip", nullable = false, unique = true)
    private String recorderPublicIp;

    @Column( name = "recorderprivateip", nullable = false, unique = true)
    private String recorderPrivateIp;

    @Column( name = "recorderportnumber", nullable = false, unique = true)
    private Integer recorderPortNumber;

    @Column( name = "recorderiwsport", nullable = false, unique = true)
    private Integer recorderIWSPort;

    @Column( name = "recorderusername", nullable = false)
    private String recorderUsername;

    @Column( name = "recorderpassword", nullable = false)
    private String recorderPassword;

    @CreatedDate
    @Column( name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @JsonIgnore
    @Column( name = "deleted")
    private Boolean deleted;
}