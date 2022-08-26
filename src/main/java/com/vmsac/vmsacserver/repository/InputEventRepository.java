package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.InputEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InputEventRepository extends JpaRepository<InputEvent, Long> {
}
