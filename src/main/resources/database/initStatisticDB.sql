DROP TABLE IF EXISTS statistic_result;
CREATE TABLE IF NOT EXISTS statistic_result
(
    "stats_id" int8 not null,
    "team_winner" VARCHAR(10),
    "odd" float
);