package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.Controller;
import com.vmsac.vmsacserver.service.ControllerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UniconUpdater {

    private ControllerService controllerService;

    public UniconUpdater(ControllerService controllerService) {
        this.controllerService = controllerService;
    }

    @Scheduled(cron = "@monthly")
    public void updateUnicons() {
        List<Controller> controllers = controllerService.findAllNotDeleted();

        controllers.forEach(controller -> {
            try {
                controllerService.sendEntranceNameRelationship(controller.getControllerId());
                controllerService.generate(controller.getControllerId());
            }
            catch(Exception e){

            }
        });
    }
}
