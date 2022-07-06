package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.EntranceEventType;
import com.vmsac.vmsacserver.model.EntranceEventTypeCreateDto;
import com.vmsac.vmsacserver.repository.EntranceEventTypeRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntranceEventTypeServie {

    @Autowired
    private EntranceEventTypeRepository typeRepository;

    public EntranceEventType createType(EntranceEventTypeCreateDto dto) {

        EntranceEventType newType = new EntranceEventType(null,
                dto.getActionTypeName(), dto.getEndTypeConfig());

        return typeRepository.save(newType);
    }
}
