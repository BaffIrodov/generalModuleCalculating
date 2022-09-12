package com.gen.GeneralModuleCalculating.services;

import com.gen.GeneralModuleCalculating.calculatingMethods.*;
import com.gen.GeneralModuleCalculating.common.CommonUtils;
import com.gen.GeneralModuleCalculating.common.MapsEnum;
import com.gen.GeneralModuleCalculating.dtos.ImprovementRequestDto;
import com.gen.GeneralModuleCalculating.entities.*;
import com.gen.GeneralModuleCalculating.readers.CalculatingReader;
import com.gen.GeneralModuleCalculating.repositories.ImprovementResultsRepository;
import com.gen.GeneralModuleCalculating.repositories.MapsCalculatingQueueRepository;
import com.gen.GeneralModuleCalculating.repositories.PlayerForceRepository;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImprovementService {

    @Autowired
    MapsCalculatingQueueRepository mapsCalculatingQueueRepository;

    @Autowired
    PlayerForceRepository playerForceRepository;

    @Autowired
    ImprovementResultsRepository improvementResultsRepository;

    @Autowired
    AdrCalculator adrCalculator;

    @Autowired
    KillsCalculator killsCalculator;

    @Autowired
    HeadshotsCalculator headshotsCalculator;

    @Autowired
    Rating20Calculator rating20Calculator;

    @Autowired
    RoundHistoryCalculator roundHistoryCalculator;

    @Autowired
    Calculator calculator;

    @Autowired
    StabilityCalculator stabilityCalculator;

    @Autowired
    CalculatingReader calculatingReader;

    @Autowired
    JPAQueryFactory queryFactory;

    private static final QPlayerOnMapResults playerOnMapResults =
            new QPlayerOnMapResults("playerOnMapResults");

    private static final QPlayerForce playerForce =
            new QPlayerForce("playerForce");
    private static final QRoundHistory roundHistory =
            new QRoundHistory("roundHistory");
    private static final QMapsCalculatingQueue mapsCalculatingQueue =
            new QMapsCalculatingQueue("mapsCalculatingQueue");

    public void improvementTest(ImprovementRequestDto requestDto) {
        Map<String, Object> mapForThisImprovement = CommonUtils.invokeConfig();
        System.out.println("improvement started");
        Integer testPercent = requestDto.getTestDatasetPercent();
        List<Integer> availableStatsIdsTrain = calculatingReader.getAvailableStatsIdsOrderedDataset(testPercent, false);
        List<Integer> availableStatsIdsTest = calculatingReader.getAvailableStatsIdsOrderedDataset(testPercent, true);
        List<Integer> existingPlayerIds = calculatingReader
                .getPlayerIdsWhoExistsInCalculatingMatches(availableStatsIdsTrain);
        List<PlayerForce> allPlayerForces = calculatingReader.getPlayerForceListByPlayerIds(existingPlayerIds, true);
        List<PlayerForce> newList = new ArrayList<>();
        allPlayerForces.forEach(e -> {
            newList.add(new PlayerForce(e.id, e.playerId, e.playerForce, e.playerStability, e.map));
        });

        Map<Integer, List<PlayerForce>> playerForcesMap = newList.stream().collect(Collectors.groupingBy(e -> e.playerId));
        long now = System.currentTimeMillis();
        Integer currectId = 0;
        Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere =
                queryFactory.from(playerOnMapResults).transform(GroupBy.groupBy(playerOnMapResults.idStatsMap).as(GroupBy.list(playerOnMapResults)));
        for (Integer id : availableStatsIdsTrain) {
            currectId++;
            Integer finalCurrectId = currectId;
            List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
            List<PlayerOnMapResults> leftTeam = players.stream().filter(e -> e.team.equals("left")).toList();
            List<PlayerOnMapResults> rightTeam = players.stream().filter(e -> e.team.equals("right")).toList();
            RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                    .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
            List<Float> forces = roundHistoryCalculator.getTeamForces(history.roundSequence, history.leftTeamIsTerroristsInFirstHalf);
            players.forEach(player -> {
                float force = calculator.calculatePlayerForce(player, forces, Config.adrMultiplier,
                        Config.killsMultiplier, Config.headshotsMultiplier, Config.ratingMultiplier, Config.historyMultiplier, Config.forceTeamMultiplier,
                        true, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap, finalCurrectId, availableStatsIdsTrain.size());
                playerForcesMap.get(player.playerId).stream()
                        .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce += force;
                //Задаю лимиты для силы
                playerForcesMap.get(player.playerId).stream()
                        .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce =
                        calculator.correctLowAndHighLimit(playerForcesMap.get(player.playerId).stream()
                                .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce);
            });
        }
        System.out.println("Первичный расчет занял: " + (System.currentTimeMillis() - now) + " мс");
        currectId = 0;
        int epochs = Config.epochsNumber;
        for (int i = 0; i < epochs; i++) {
            for (Integer id : availableStatsIdsTrain) {
                currectId++;
                Integer finalCurrectId = currectId;
                List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
                List<PlayerOnMapResults> leftTeam = players.stream().filter(e -> e.team.equals("left")).toList();
                List<PlayerOnMapResults> rightTeam = players.stream().filter(e -> e.team.equals("right")).toList();
                RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                        .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
                List<Float> forces = roundHistoryCalculator.getTeamForces(history.roundSequence, history.leftTeamIsTerroristsInFirstHalf);
                players.forEach(player -> {
                    float force = calculator.calculatePlayerForce(player, forces, Config.adrMultiplier,
                            Config.killsMultiplier, Config.headshotsMultiplier, Config.ratingMultiplier, Config.historyMultiplier, Config.forceTeamMultiplier,
                            false, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap, finalCurrectId, availableStatsIdsTrain.size());
                    playerForcesMap.get(player.playerId).stream()
                            .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce += force;
                    //Задаю лимиты для силы
                    playerForcesMap.get(player.playerId).stream()
                            .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce =
                            calculator.correctLowAndHighLimit(playerForcesMap.get(player.playerId).stream()
                                    .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce);
                });
                //подобие обратного распространения ошибки - считаем стабильность
                List<PlayerForce> leftTeamForce = new ArrayList<>();
                List<PlayerForce> rightTeamForce = new ArrayList<>();
                players.forEach(p -> {
                    if (p.team.equals("left")) {
                        leftTeamForce.add(playerForcesMap.get(p.playerId)
                                .stream().filter(e -> e.map.equals(p.playedMapString))
                                .toList().get(0));
                    } else {
                        rightTeamForce.add(playerForcesMap.get(p.playerId)
                                .stream().filter(e -> e.map.equals(p.playedMapString))
                                .toList().get(0));
                    }
                });

                stabilityCalculator.calculateCorrectedStability(leftTeamForce, rightTeamForce, players.get(0).teamWinner);
            }

            Integer rightAnswers = 0;
            Integer nonRightAnswers = 0;
            for (Integer id : availableStatsIdsTest) {
                List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
                Float leftForce = 0f;
                Float rightForce = 0f;
                // основная карта
                for (PlayerOnMapResults p: players) {
                    PlayerForce force = playerForcesMap.get(p.playerId).stream().filter(r -> r.map.equals(p.playedMapString)).toList().get(0);
                    if (p.team.equals("left")) {
                        leftForce += (force.playerForce * force.playerStability) / 100;
                    } else {
                        rightForce += (force.playerForce * force.playerStability) / 100;
                    }
                }
                // второстепенные карты
                if(Config.isConsiderActiveMaps) {
                    for (PlayerOnMapResults p : players) {
                        for (int j = 0; j < 7; j++) {
                            int currentMap = Config.activeMaps.get(j);
                            String currentMapString = MapsEnum.values()[currentMap].toString();
                            PlayerForce force = playerForcesMap.get(p.playerId).stream().filter(r -> r.map.equals(currentMapString)).toList().get(0);
                            if (p.team.equals("left")) {
                                leftForce += ((force.playerForce * force.playerStability) / 100) * 0.05f;
                            } else {
                                rightForce += ((force.playerForce * force.playerStability) / 100) * 0.05f;
                            }
                        }
                    }
                }
                String winner = players.get(0).teamWinner;
                if((leftForce > rightForce && winner.equals("left")) || (rightForce > leftForce && winner.equals("right"))) {
                    rightAnswers++;
                }
            }
            saveImprovementResult(rightAnswers, availableStatsIdsTest.size(), mapForThisImprovement, i+1);
            System.out.println("Эпоха номер: " + (i+1) + ". На " + availableStatsIdsTest.size() +
                    " матчей приходится " + rightAnswers +
                    " правильных ответов! Процент точности равен " +
                    (float) rightAnswers/availableStatsIdsTest.size());
        }
        //TODO надо сделать ограничение сил - снизу 0
        //TODO именно в процессе расчета предикта меняются кожффициенты для достижения консенсуса! Консенсус выкидывает ненадежные матчи!
        System.out.println("Вторичный расчет занял: " + (System.currentTimeMillis() - now) + " мс");
    }

    private void saveImprovementResult(int rightAnswers, int availableStatsIdsSize, Map<String, Object> mapForThisImprovement,
                                       int currentEpoch) {
        ImprovementResults improvementResults = new ImprovementResults();
        improvementResults.accuracy = (float) rightAnswers/availableStatsIdsSize;
        improvementResults.currentEpoch = currentEpoch;
        improvementResults.rightCount = rightAnswers;
        improvementResults.allCount = availableStatsIdsSize;
        improvementResults.fullConfig = mapForThisImprovement.toString();
        improvementResultsRepository.save(improvementResults);
    }
}
