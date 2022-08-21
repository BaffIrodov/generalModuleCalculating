DROP TABLE IF EXISTS maps_calculating_queue;
CREATE TABLE IF NOT EXISTS maps_calculating_queue
(
    "id_stats_map" int8 not null,
    "calculation_time" int8,
    "processed" BOOLEAN
    );

DROP TABLE IF EXISTS player_force;
CREATE TABLE IF NOT EXISTS player_force
(
    "id" int8 not null,
    "player_id" int8,
    "player_force" float8,
    "player_stability" int8,
    "map" VARCHAR(20)
);

DROP SEQUENCE IF EXISTS "sq_player_force_id";
CREATE SEQUENCE "sq_player_force_id"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;