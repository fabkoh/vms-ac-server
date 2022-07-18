package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.EventActionInputType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventActionInputTypeRepository extends JpaRepository<EventActionInputType, Long> {
    @Query("from EventActionInputType t order by t.eventActionInputName")
    public List<EventActionInputType> findAllOrderByEventActionInputNameAsc();

    public List<EventActionInputType> findAllByEventActionInputNameIgnoreCaseIn(String[] names);
}
