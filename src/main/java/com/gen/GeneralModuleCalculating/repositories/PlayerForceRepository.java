package com.gen.GeneralModuleCalculating.repositories;

import com.gen.GeneralModuleCalculating.entities.Errors;
import com.gen.GeneralModuleCalculating.entities.PlayerForce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlayerForceRepository extends JpaRepository<PlayerForce, Integer> {

    @Modifying
    @Query(
            value = "truncate table player_force",
            nativeQuery = true
    )
    void truncatePlayerForce();

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS player_force;\n" +
                    "CREATE TABLE IF NOT EXISTS player_force\n" +
                    "(\n" +
                    "    \"id\" int8 not null,\n" +
                    "    \"player_id\" int8,\n" +
                    "    \"player_force\" float8,\n" +
                    "    \"player_stability\" int8,\n" +
                    "    \"map\" VARCHAR(20)\n" +
                    ");\n" +
                    "\n" +
                    "DROP SEQUENCE IF EXISTS \"sq_player_force_id\";\n" +
                    "CREATE SEQUENCE \"sq_player_force_id\"\n" +
                    "    INCREMENT 1\n" +
                    "    MINVALUE  1\n" +
                    "    MAXVALUE 9223372036854775807\n" +
                    "    START 1\n" +
                    "    CACHE 1;",
            nativeQuery = true
    )
    @Transactional
    void createPlayerForceTable();
}
