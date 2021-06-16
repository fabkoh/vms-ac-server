package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VisitorController {

    @Autowired
    private VisitorRepository visitorRepository;

    @GetMapping("/visitors")
    List<Visitor> getVisitors(){
        return visitorRepository.findAll();
    }
}
