package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import com.vmsac.vmsacserver.service.AccessGroupEntranceService;
import com.vmsac.vmsacserver.util.UniconUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api")
public class AccessGroupEntranceController {

    @Autowired
    private AccessGroupEntranceService accessGroupEntranceService;

    @Autowired
    UniconUpdater uniconUpdater;

    // return access group entrance objects
    @GetMapping("/access-group-entrance")
    public List<AccessGroupEntranceNtoNDto> getAccessGroupEntrance(@RequestParam(name="accessgroupid", required = false) Long accessGroupId,
                                                                   @RequestParam(name="entranceid", required = false) Long entranceId) {
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

    // associates entrance with all access groups in accessGroupIds
    @PostMapping("/access-group-entrance/entrance/{entranceId}")
    public ResponseEntity<?> assignAccessGroupsToEntrance(@RequestParam(name = "accessgroupids", required = false) List<Long> accessGroupIds,
                                                          @PathVariable Long entranceId) {
        try {
            accessGroupEntranceService.assignAccessGroupsToEntrance(Objects.requireNonNullElse(accessGroupIds, new ArrayList<>()), entranceId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        uniconUpdater.updateUnicons();
        return ResponseEntity.noContent().build();
    }

    // associates the access group with all entrances in entranceIds
    @PostMapping("/access-group-entrance/access-group/{accessGroupId}")
    public ResponseEntity<?> assignEntrancesToAccessGroup(@RequestParam(name = "entranceids", required = false) List<Long> entranceIds,
                                                          @PathVariable Long accessGroupId) {
        try {
            accessGroupEntranceService.assignEntrancesToAccessGroup(Objects.requireNonNullElse(entranceIds, new ArrayList<>()), accessGroupId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        uniconUpdater.updateUnicons();
        return ResponseEntity.noContent().build();
    }
}
