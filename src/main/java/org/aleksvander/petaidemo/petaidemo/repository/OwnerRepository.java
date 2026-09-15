package org.aleksvander.petaidemo.petaidemo.repository;

import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
