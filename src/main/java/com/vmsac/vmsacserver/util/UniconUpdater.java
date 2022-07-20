package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.Controller;
import com.vmsac.vmsacserver.service.ControllerService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UniconUpdater {

    private ControllerService controllerService;

    public UniconUpdater(ControllerService controllerService) {
        this.controllerService = controllerService;
    }

    @Scheduled(cron = "@monthly")
    public List<Long> updateUnicons() {
        List<Controller> controllers = controllerService.findAllNotDeleted();
        List<Long> errors = new ArrayList<Long>() ;

        if (controllers.isEmpty()){
            return errors;
        }

        controllers.forEach(controller -> {
            try {

                controllerService.sendEntranceNameRelationship(controller.getControllerId());
                System.out.println("EntranceName Done");
                controllerService.generate(controller.getControllerId());
                System.out.println("Generate Done");
                controllerService.sendEventsManagementToController(controller);
                System.out.println("sendEventsManagementToController Done");
            }
            catch(Exception e){
                errors.add(controller.getControllerId());
            }
        });
        return errors;
    }
}
