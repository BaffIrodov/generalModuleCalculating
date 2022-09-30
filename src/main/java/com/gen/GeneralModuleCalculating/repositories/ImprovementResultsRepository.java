package com.gen.GeneralModuleCalculating.repositories;

import com.gen.GeneralModuleCalculating.entities.ImprovementResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ImprovementResultsRepository extends JpaRepository<ImprovementResults, Integer> {

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS improvement_results;\n" +
                    "CREATE TABLE IF NOT EXISTS improvement_results\n" +
                    "(\n" +
                    "    \"id\" int8 not null,\n" +
                    "    \"accuracy\" float8,\n" +
                    "    \"current_epoch\" int8,\n" +
                    "    \"right_count\" int8,\n" +
                    "    \"all_count\" int8,\n" +
                    "    \"full_config\" VARCHAR(4000)\n" +
                    ");\n" +
                    "\n" +
                    "DROP SEQUENCE IF EXISTS \"sq_improvement_results_id\";\n" +
                    "CREATE SEQUENCE \"sq_improvement_results_id\"\n" +
                    "    INCREMENT 1\n" +
                    "    MINVALUE  1\n" +
                    "    MAXVALUE 9223372036854775807\n" +
                    "    START 1\n" +
                    "    CACHE 1;",
            nativeQuery = true
    )
    @Transactional
    void createImprovementResultsTable();

}
