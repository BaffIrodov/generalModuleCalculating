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
    "player_id" int8 not null,
    "player_force" float8,
    "player_stability" int8
);