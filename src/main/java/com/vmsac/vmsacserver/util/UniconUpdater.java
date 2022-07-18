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
    public List<Controller> updateUnicons() {
        List<Controller> controllers = controllerService.findAllNotDeleted();
        List<Controller> errors = new ArrayList<Controller>() ;

        if (controllers.isEmpty()){
            return errors;
        }

        controllers.forEach(controller -> {
            try {

                controllerService.sendEntranceNameRelationship(controller.getControllerId());
                controllerService.generate(controller.getControllerId());
                controllerService.sendEventsManagementToController(controller);
                System.out.println("controller" + controller.getControllerId());
            }
            catch(Exception e){
                errors.add(controller);
            }
        });
        return errors;
    }
}
