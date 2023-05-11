package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.repository.ControllerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IPaddressWhitelisting {

    @Autowired
    private ControllerRepository controllerRepository;

    public Boolean exisitingIPaddressVerification(String IPaddress){
        if (controllerRepository.existsByControllerIPAndDeletedFalse(IPaddress)){
            return true;
        }
        return false;
    }

    public Boolean pendingIPaddressVerification(String IPaddress){
        if (controllerRepository.existsByPendingIPAndDeletedFalse(IPaddress)){
            return true;
        }
        return false;
    }
}
