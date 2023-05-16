package com.vmsac.vmsacserver.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
@RestController
public class CheckIp {

    @RequestMapping("/myEndpoint")
    public String handleRequest(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        return "Request received from IP address: " + remoteAddr;
    }

}
