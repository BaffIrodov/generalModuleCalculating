package com.gen.GeneralModuleCalculating.repositories;

import com.gen.GeneralModuleCalculating.entities.MapsCalculatingQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MapsCalculatingQueueRepository extends JpaRepository<MapsCalculatingQueue, Integer> {

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS maps_calculating_queue;\n" +
                    "CREATE TABLE IF NOT EXISTS maps_calculating_queue\n" +
                    "(\n" +
                    "    \"id_stats_map\" int8 not null,\n" +
                    "    \"calculation_time\" int8,\n" +
                    "    \"processed\" BOOLEAN\n" +
                    "    );",
            nativeQuery = true
    )
    @Transactional
    void createMapsCalculatingQueueTable();

}
