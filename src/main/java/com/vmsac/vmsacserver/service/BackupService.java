package com.vmsac.vmsacserver.service;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.core.io.InputStreamResource;
@Service
public class BackupService {

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public CompletableFuture<InputStreamResource> createBackup() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/pg_dump",
                "-U", "postgres",
                "-f", "/home/etlas/backup.sql",
                "vms_ac_db");
        pb.environment().put("PGPASSWORD", "remoteDBfortest"); // avoid password prompt
        Process p = pb.start();
        p.waitFor();

        File file = new File("/home/etlas/backup.sql");

        return CompletableFuture.supplyAsync(() -> {
            try {
                return new InputStreamResource(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, executor).thenApply(inputStreamResource -> {
            try {
                deleteBackup();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStreamResource;
        });
    }

    public void deleteBackup() throws IOException {
        Path filePath = Paths.get("/home/etlas/backup.sql");
        Files.delete(filePath);
    }
}
