package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")

public class DataManagementController {
    @Autowired
    private BackupService backupService;

    @GetMapping("/backup")
    public ResponseEntity<?> createBackup() {
        try {
            InputStreamResource resource = backupService.createBackup();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"backup.sql\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}