package com.gen.GeneralModuleCalculating.repositories;

import com.gen.GeneralModuleCalculating.entities.MapsCalculatingQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapsCalculatingQueueRepository extends JpaRepository<MapsCalculatingQueue, Integer> {

}
