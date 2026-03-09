package com.vmsac.vmsacserver.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

/**
 * Writes a "VMS-AC Dev" saved setting into ~/.h2.server.properties so that
 * the H2 console login form is pre-filled with the correct JDBC URL and
 * username. Run once per startup; safe to run multiple times.
 */
@Component
@Profile("dev")
public class H2ConsolePreConfig implements ApplicationRunner {

    private static final String SETTING_NAME = "VMS-AC Dev";
    private static final String SETTING_VALUE =
            SETTING_NAME + "|org.h2.Driver|jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE|Admin|";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File settingsFile = new File(System.getProperty("user.home"), ".h2.server.properties");

        Properties props = new Properties();
        if (settingsFile.exists()) {
            try (InputStream is = new FileInputStream(settingsFile)) {
                props.load(is);
            }
        }

        // Skip if already added
        for (String key : props.stringPropertyNames()) {
            if (props.getProperty(key).startsWith(SETTING_NAME + "|")) {
                return;
            }
        }

        // Find next free numeric index
        int index = 0;
        while (props.containsKey(String.valueOf(index))) {
            index++;
        }

        props.setProperty(String.valueOf(index), SETTING_VALUE);

        try (OutputStream os = new FileOutputStream(settingsFile)) {
            props.store(os, "H2 Console Settings");
        }
    }
}
