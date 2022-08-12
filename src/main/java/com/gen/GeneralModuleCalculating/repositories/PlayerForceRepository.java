package com.gen.GeneralModuleCalculating.repositories;

import com.gen.GeneralModuleCalculating.entities.Errors;
import com.gen.GeneralModuleCalculating.entities.PlayerForce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerForceRepository extends JpaRepository<PlayerForce, Integer> {

}
