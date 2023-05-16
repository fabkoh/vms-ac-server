package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.EventActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface EventActionTypeRepository extends JpaRepository<EventActionType,Long> {

    @Query(value = "select * from eventactiontype " +
            "where upper(eventactiontypename) like upper(concat('%', :name, '%'))", nativeQuery = true)
    List<EventActionType> searchByEventActionTypeName(String name);
}
