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
import java.util.HashMap;
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
    private static final QImprovementResults improvementResults =
            new QImprovementResults("improvementResults");

    public void improvementByConsensus(ImprovementRequestDto requestDto) {
        List<Integer> availableStatsIdsTest = getAvailableStatsIdsTest(requestDto);
        Map<Map<Integer, List<PlayerOnMapResults>>, Map<Integer, List<PlayerForce>>> map1 = improvementTest(requestDto);
        Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere1 = map1.keySet().stream().toList().get(0);
        Map<Integer, List<PlayerForce>> playerForcesMap1 = map1.values().stream().toList().get(0);
        Map<Integer, Boolean> res1 = getRightAnswerIds(availableStatsIdsTest,
                allPlayersAnywhere1, playerForcesMap1);
        //second
        Config.oneCoeffFuncKills = 1;
        Config.twoCoeffFuncKills = 0;
        Config.threeCoeffFuncKills = 0;
        Config.fourCoeffFuncKills = 0;
        Config.sixthCoeffFuncKills = 0;

        Config.oneCoeffFuncAdr = 1;
        Config.twoCoeffFuncAdr = 0;
        Config.threeCoeffFuncAdr = 0;
        Config.fourCoeffFuncAdr = 0;

        Config.oneCoeffFuncHeadshots = 1;
        Config.twoCoeffFuncHeadshots = 0;
        Config.threeCoeffFuncHeadshots = 0;
        Config.fourCoeffFuncHeadshots = 0;

        Config.oneCoeffFuncRating20 = 1;
        Config.twoCoeffFuncRating20 = 0;
        Config.threeCoeffFuncRating20 = 0;
        Config.fourCoeffFuncRating20 = 0;
        Map<Map<Integer, List<PlayerOnMapResults>>, Map<Integer, List<PlayerForce>>> map2 = improvementTest(requestDto);
        Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere2 = map2.keySet().stream().toList().get(0);
        Map<Integer, List<PlayerForce>> playerForcesMap2 = map2.values().stream().toList().get(0);
        Map<Integer, Boolean> res2 = getRightAnswerIds(availableStatsIdsTest,
                allPlayersAnywhere2, playerForcesMap2);
        Map<Integer, Boolean> fullResult = new HashMap<>();
        res1.forEach((k,v) -> {
            if(res2.containsKey(k)) { //если в обоих случаях программа считает, что знает ответ
                if(res2.get(k) == v) //и если ответ получился одинаковым
                    fullResult.put(k, v);
            }
        });
        Integer rightAnswers = fullResult.values().stream().filter(e -> e).toList().size();
        Integer allAnswers = fullResult.size();
        System.out.println("\nПосле консенсуса вышло вот это: на " + allAnswers +
                " ответов приходится " + rightAnswers + "правильных. Процент вышел: " +
                (float) ((float) rightAnswers/ (float) allAnswers));
    }

    public Map<Map<Integer, List<PlayerOnMapResults>>, Map<Integer, List<PlayerForce>>> improvementTest(ImprovementRequestDto requestDto) {
        Map<String, Object> mapForThisImprovement = CommonUtils.invokeConfig();
        System.out.println("improvement started");
        List<Integer> availableStatsIdsTrain = getAvailableStatsIdsTrain(requestDto);
        List<Integer> availableStatsIdsTest = getAvailableStatsIdsTest(requestDto);
        List<Integer> existingPlayerIds = calculatingReader
                .getPlayerIdsWhoExistsInCalculatingMatches(availableStatsIdsTrain);
        List<PlayerForce> allPlayerForces = calculatingReader.getPlayerForceListByPlayerIds(existingPlayerIds, true);
        List<PlayerForce> newList = new ArrayList<>();
        allPlayerForces.forEach(e -> {
            newList.add(new PlayerForce(e.id, e.playerId, e.playerForce, e.playerStability, e.map));
        });

        Map<Integer, List<PlayerForce>> playerForcesMap = newList.stream().collect(Collectors.groupingBy(e -> e.playerId));
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
                if (Config.isConsiderStabilityCorrection) {
                    stabilityCalculator.calculateCorrectedStability(leftTeamForce, rightTeamForce, players.get(0).teamWinner);
                }
            }
        }
        calculateImprovementResult(availableStatsIdsTest,
                allPlayersAnywhere, playerForcesMap, mapForThisImprovement, epochs);
        Map<Map<Integer, List<PlayerOnMapResults>>, Map<Integer, List<PlayerForce>>> resultMap = new HashMap<>();
        resultMap.put(allPlayersAnywhere, playerForcesMap);
        return resultMap;
    }

    public void calculateImprovementResult(List<Integer> availableStatsIdsTest,
                                           Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere,
                                           Map<Integer, List<PlayerForce>> playerForcesMap,
                                           Map<String, Object> mapForThisImprovement, int currectEpoch) {
        Integer rightAnswers = 0;
        Integer percentRightAnswers = 0;
        Integer percentAllAnswers = 0;
        Integer constRightAnswers = 0;
        Integer constAllAnswers = 0;
        for (Integer id : availableStatsIdsTest) {
            List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
            Float leftForce = 0f;
            Float rightForce = 0f;
            // основная карта
            for (PlayerOnMapResults p : players) {
                PlayerForce force = playerForcesMap.get(p.playerId).stream().filter(r -> r.map.equals(p.playedMapString)).toList().get(0);
                if (p.team.equals("left")) {
                    leftForce += (force.playerForce * force.playerStability) / 100;
                } else {
                    rightForce += (force.playerForce * force.playerStability) / 100;
                }
            }
            // второстепенные карты
            if (Config.isConsiderActiveMaps) {
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
            if ((leftForce > rightForce && winner.equals("left")) || (rightForce > leftForce && winner.equals("right"))) {
                rightAnswers++;
            }
            if ((leftForce > rightForce * 1.5) || (rightForce > leftForce * 1.5)) percentAllAnswers++;
            if ((leftForce > rightForce * 1.5 && winner.equals("left")) || (rightForce > leftForce * 1.5 && winner.equals("right"))) {
                percentRightAnswers++;
            }
            if ((leftForce > rightForce + 100) || (rightForce > leftForce + 100)) constAllAnswers++;
            if ((leftForce > rightForce + 100 && winner.equals("left")) || (rightForce > leftForce + 100 && winner.equals("right"))) {
                constRightAnswers++;
            }
        }
        saveImprovementResult(rightAnswers, availableStatsIdsTest.size(), mapForThisImprovement, currectEpoch);
        System.out.println("Эпоха номер: " + (currectEpoch) + ". На " + availableStatsIdsTest.size() +
                " матчей приходится " + rightAnswers +
                " правильных ответов! Процент точности равен " +
                (float) rightAnswers / availableStatsIdsTest.size());
        System.out.println("(Процент) Эпоха номер: " + (currectEpoch) + ". На " + percentAllAnswers +
                " матчей приходится " + percentRightAnswers +
                " правильных ответов! Процент точности равен " +
                (float) percentRightAnswers / percentAllAnswers);
        System.out.println("(Константа) Эпоха номер: " + (currectEpoch) + ". На " + constAllAnswers +
                " матчей приходится " + constRightAnswers +
                " правильных ответов! Процент точности равен " +
                (float) constRightAnswers / constAllAnswers);
    }

    private Map<Integer, Boolean> getRightAnswerIds(List<Integer> availableStatsIdsTest,
                                            Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere,
                                            Map<Integer, List<PlayerForce>> playerForcesMap) {
        Map<Integer, Boolean> resultMap = new HashMap<>();
        for (Integer id : availableStatsIdsTest) {
            List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
            Float leftForce = 0f;
            Float rightForce = 0f;
            // основная карта
            for (PlayerOnMapResults p : players) {
                PlayerForce force = playerForcesMap.get(p.playerId).stream().filter(r -> r.map.equals(p.playedMapString)).toList().get(0);
                if (p.team.equals("left")) {
                    leftForce += (force.playerForce * force.playerStability) / 100;
                } else {
                    rightForce += (force.playerForce * force.playerStability) / 100;
                }
            }
            // второстепенные карты
            if (Config.isConsiderActiveMaps) {
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
            if ((leftForce > rightForce * 1.5) || (rightForce > leftForce * 1.5)) {
                if ((leftForce > rightForce * 1.5 && winner.equals("left")) || (rightForce > leftForce * 1.5 && winner.equals("right"))) {
                    resultMap.put(id, true);
                } else {
                    resultMap.put(id, false);
                }
            }
        }
        return resultMap;
    }

    private void saveImprovementResult(int rightAnswers, int availableStatsIdsSize, Map<String, Object> mapForThisImprovement,
                                       int currentEpoch) {
        ImprovementResults improvementResults = new ImprovementResults();
        improvementResults.accuracy = (float) rightAnswers / availableStatsIdsSize;
        improvementResults.currentEpoch = currentEpoch;
        improvementResults.rightCount = rightAnswers;
        improvementResults.allCount = availableStatsIdsSize;
        improvementResults.fullConfig = mapForThisImprovement.toString();
        improvementResultsRepository.save(improvementResults);
    }

    public List<ImprovementResults> getImprovementResultsFromDB () {
        List<ImprovementResults> results = (List<ImprovementResults>)
                queryFactory.from(improvementResults).fetch();
        return results;
    }

    private List<Integer> getAvailableStatsIdsTrain(ImprovementRequestDto requestDto) {
        Integer testPercent = requestDto.getTestDatasetPercent();
        return calculatingReader.getAvailableStatsIdsOrderedDataset(testPercent, false);
    }

    private List<Integer> getAvailableStatsIdsTest(ImprovementRequestDto requestDto) {
        Integer testPercent = requestDto.getTestDatasetPercent();
        return calculatingReader.getAvailableStatsIdsOrderedDataset(testPercent, true);
    }

}
