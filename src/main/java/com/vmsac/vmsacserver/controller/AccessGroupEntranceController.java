package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import com.vmsac.vmsacserver.service.AccessGroupEntranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api")
public class AccessGroupEntranceController {

    @Autowired
    private AccessGroupEntranceService accessGroupEntranceService;

    // return access group entrance objects
    @GetMapping("/access-group-entrance")
    public List<AccessGroupEntranceNtoNDto> getAccessGroupEntrance(@RequestParam(required = false) Long accessGroupId,
                                                                   @RequestParam(required = false) Long entranceId) {
        if (accessGroupId == null && entranceId == null) {
            return accessGroupEntranceService.findAll();
        }
        if (accessGroupId == null) {
            return accessGroupEntranceService.findAllWhereEntranceId(entranceId);
        }
        if (entranceId == null) {
            return accessGroupEntranceService.findAllWhereAccessGroupId(accessGroupId);
        }
        return accessGroupEntranceService.findAllWhereAccessGroupIdAndEntranceId(accessGroupId, entranceId);
    }

    // associates entrance with all access group in access group list
    @PostMapping("/access-group-entrance/entrance/{entranceId}")
    public ResponseEntity<?> assignEntranceIdToAccessGroupIds(@PathVariable Long entranceId,
                                                          @RequestParam List<Long> accessGroupIds) {
        try {
            accessGroupEntranceService.assignEntranceToAccessGroups(entranceId, accessGroupIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}
