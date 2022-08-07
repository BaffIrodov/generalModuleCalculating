DROP TABLE IF EXISTS maps_calculating_queue;
CREATE TABLE IF NOT EXISTS maps_calculating_queue
(
    "id_stats_map" int8 not null ,
    "calculation_time" int8,
    "processed" BOOLEAN
    );