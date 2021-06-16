package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VisitorRepository extends JpaRepository<Visitor, Long>{
}
