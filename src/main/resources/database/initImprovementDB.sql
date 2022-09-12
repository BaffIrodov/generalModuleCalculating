DROP TABLE IF EXISTS improvement_results;
CREATE TABLE IF NOT EXISTS improvement_results
(
    "id" int8 not null,
    "accuracy" float8,
    "current_epoch" int8,
    "right_count" int8,
    "all_count" int8,
    "full_config" VARCHAR(4000)
);

DROP SEQUENCE IF EXISTS "sq_improvement_results_id";
CREATE SEQUENCE "sq_improvement_results_id"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;