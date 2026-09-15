package org.aleksvander.petaidemo.petaidemo.repository;

import org.aleksvander.petaidemo.petaidemo.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPetId(Long petId);
}
