package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.EventActionOutputType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventActionOutputTypeRepository extends JpaRepository<EventActionOutputType, Long> {

    public List<EventActionOutputType> findAllByEventActionOutputNameIgnoreCaseIn(String[] names);
}
