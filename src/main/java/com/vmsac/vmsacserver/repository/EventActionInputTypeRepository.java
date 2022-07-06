package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.EventActionInputType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventActionInputTypeRepository extends JpaRepository<EventActionInputType, Long> {
    public List<EventActionInputType> findAllOrderByEventActionInputTypeNameAsc();
}
