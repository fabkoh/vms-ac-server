package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.Controller;
import com.vmsac.vmsacserver.model.ControllerConnection;
import com.vmsac.vmsacserver.service.ControllerService;
import javassist.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UniconUpdater {

    private ControllerService controllerService;

    public UniconUpdater(ControllerService controllerService) {
        this.controllerService = controllerService;
    }

    @Scheduled(cron = "@monthly")
    public Map<String, String> updateUnicons() {
        List<Controller> controllers = controllerService.findAllNotDeleted();
        Map<String, String> errors = new HashMap<>();

        if (controllers.isEmpty()){
            return errors;
        }

        controllers.forEach(controller -> {
            try {

                ControllerConnection cc = controllerService.getControllerConnectionUnicon(controller.getControllerIP());
                if (cc == null) {
                    throw new NotFoundException("controller ip address cannot be connected");
                }

                controllerService.sendEntranceNameRelationship(controller.getControllerId());
                System.out.println("EntranceName Done");
                controllerService.generate(controller.getControllerId());
                System.out.println("Generate Done");
                controllerService.sendEventsManagementToController(controller);
                System.out.println("sendEventsManagementToController Done");

                controller.setLastSync(LocalDateTime.now(ZoneId.of("GMT+08:00")));
            }
            catch(Exception e){
                errors.put(controller.getControllerName(), controller.getControllerSerialNo());
            }

            controllerService.save(controller);
        });
        return errors;
    }
}
