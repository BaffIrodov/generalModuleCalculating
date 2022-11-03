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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
    PressureCalculator pressureCalculator;

    @Autowired
    Calculator calculator;

    @Autowired
    StabilityCalculator stabilityCalculator;

    @Autowired
    DifferenceCalculator differenceCalculator;

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
        /*
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
         */
    }

    public void improvementByInactivePercent(ImprovementRequestDto requestDto) {
        Integer inactivePercent = requestDto.getTestDatasetPercent() * 5;
        Map<Integer, Integer> resultMap = new HashMap<>();
//        List<String> forcesLimits = new ArrayList<>();
//        List<Float> forcesLow = new ArrayList<>();
//        List<Float> forcesHigh = new ArrayList<>();
//        List<Integer> wins = new ArrayList<>();
//        List<Integer> all = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            forcesLow.add(i * 1f);
//            forcesHigh.add(i * 1f + 1f);
//            forcesLimits.add(forcesLow.get(i).toString() + " - " + forcesHigh.get(i).toString());
//            wins.add(0);
//            all.add(0);
//        }
//        forcesLow.add(20 * 1f);
//        forcesHigh.add(100000f);
//        forcesLimits.add(forcesLow.get(20).toString() + " - " + forcesHigh.get(20).toString());
//        wins.add(0);
//        all.add(0);
//        ResData resData = new ResData(forcesLimits, forcesLow, forcesHigh, wins, all);
        for (int i = 0; i <= inactivePercent; i++) {
            requestDto.setInactiveDatasetPercent(inactivePercent - i);
            resultMap.putAll(improvementTest(requestDto));
//            improvementTestHard(requestDto).merge(resData);
//            System.out.println("inactive, hard: " + i);
        }
//        resData.calculatePercent();
//        int ildld = 0;
        AtomicReference<Integer> right = new AtomicReference<>(0);
        AtomicReference<Integer> all = new AtomicReference<>(0);
        resultMap.forEach((k, v) -> {
            right.updateAndGet(v1 -> v1 + k);
            all.updateAndGet(v1 -> v1 + v);
        });
        System.out.println("!Сводный результат! На " + all.get() +
                " матчей приходится " + right.get() +
                " правильных ответов! Процент точности равен " +
                (float) right.get()/all.get());
    }

    public Map<Integer, Integer> improvementTest(ImprovementRequestDto requestDto) {
        Map<String, Object> mapForThisImprovement = CommonUtils.invokeConfig();
        System.out.println("improvement started");
        List<Integer> availableStatsIdsTrain = getAvailableStatsIdsTrain(requestDto);
        List<Integer> availableStatsIdsTest = getAvailableStatsIdsTest(requestDto);
        List<Integer> existingPlayerIds = calculatingReader
                .getPlayerIdsWhoExistsInCalculatingMatches(availableStatsIdsTrain);
        List<PlayerForce> allPlayerForces = calculatingReader.getPlayerForceListByPlayerIds(existingPlayerIds, true);
        List<PlayerForce> newList = new ArrayList<>();
        allPlayerForces.forEach(e -> {
            //для improvement нужны только дефолтные значения. Дальше расчет в памяти
            newList.add(new PlayerForce(e.id, e.playerId, Config.playerForceDefault, Config.playerStability, e.map, 0, 0));
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
            List<Float> pressures = pressureCalculator.getPressure(history.roundSequence);
//            Float leftHelpForce;
//            Float rightHelpForce;
//            if(players.get(0).teamWinner.equals("left")) {
//                leftHelpForce = forces.get(0) + pressures.get(0);
//                forces.set(0, leftHelpForce);
//            } else {
//                rightHelpForce = forces.get(1) + pressures.get(1);
//                forces.set(1, rightHelpForce);
//            }
            players.forEach(player -> {
                float force = calculator.calculatePlayerForce(player, forces, Config.adrMultiplier,
                        Config.killsMultiplier, Config.headshotsMultiplier, Config.ratingMultiplier, Config.historyMultiplier, Config.forceTeamMultiplier,
                        true, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap, finalCurrectId, availableStatsIdsTrain.size());
                PlayerForce playerForceForCalculate = playerForcesMap.get(player.playerId).stream()
                        .filter(e -> e.map.equals(player.playedMapString)).toList().get(0);
                playerForceForCalculate.playerForce += force;
                winStrikeCalculation(player, playerForceForCalculate);
                loseStrikeCalculation(player, playerForceForCalculate);
                //Задаю лимиты для силы
                playerForceForCalculate.playerForce = calculator.correctLowLimit(playerForceForCalculate.playerForce);
//                playerForceForCalculate.playerForce = calculator.correctLowAndHighLimit(playerForceForCalculate.playerForce);
            });
        }
        currectId = 0;
        int epochs = Config.epochsNumber;
        for (int i = 0; i < epochs; i++) {
//            AtomicInteger index = new AtomicInteger();
            for (Integer id : availableStatsIdsTrain) {
                currectId++;
                Integer finalCurrectId = currectId;
                List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
                List<PlayerOnMapResults> leftTeam = players.stream().filter(e -> e.team.equals("left")).toList();
                List<PlayerOnMapResults> rightTeam = players.stream().filter(e -> e.team.equals("right")).toList();
                RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                        .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
                List<Float> forces = roundHistoryCalculator.getTeamForces(history.roundSequence, history.leftTeamIsTerroristsInFirstHalf);
//                List<Float> pressures = pressureCalculator.getPressure(history.roundSequence);
//                Float leftHelpForce;
//                Float rightHelpForce;
//                if(players.get(0).teamWinner.equals("right")) {
//                    leftHelpForce = forces.get(0) + pressures.get(0);
//                    forces.set(0, leftHelpForce);
//                } else {
//                    rightHelpForce = forces.get(1) + pressures.get(1);
//                    forces.set(1, rightHelpForce);
//                }
//                int wow = i;
                players.forEach(player -> {
//                    index.getAndIncrement();
                    float force = calculator.calculatePlayerForce(player, forces, Config.adrMultiplier,
                            Config.killsMultiplier, Config.headshotsMultiplier, Config.ratingMultiplier, Config.historyMultiplier, Config.forceTeamMultiplier,
                            false, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap, finalCurrectId, availableStatsIdsTrain.size());
                    //if (!player.team.equals(player.teamWinner)) force -= 3f;
                    PlayerForce playerForceForCalculate = playerForcesMap.get(player.playerId).stream()
                            .filter(e -> e.map.equals(player.playedMapString)).toList().get(0);
                    playerForceForCalculate.playerForce += force;
                    winStrikeCalculation(player, playerForceForCalculate);
                    loseStrikeCalculation(player, playerForceForCalculate);
                    //Задаю лимиты для силы
                    playerForceForCalculate.playerForce = calculator.correctLowLimit(playerForceForCalculate.playerForce);
//                    playerForceForCalculate.playerForce = calculator.correctLowAndHighLimit(playerForceForCalculate.playerForce);


//                    if (player.playerId == 29) {
//
//                        System.out.println("Эпоха: " + wow + "| Id игры: " + id + "| Сила игрока: " +  playerForcesMap.get(player.playerId).stream()
//                                .filter(e -> e.map.equals("MIRAGE")).toList().get(0).playerForce);
//                    }
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
                if (Config.isConsiderDifferenceCorrection) {
                    differenceCalculator.calculateTeamsDifference(leftTeamForce, rightTeamForce, players.get(0).teamWinner);
                }
            }
//            float max2 = 0;
//            for (Float f : newList.stream().map(e -> e.playerForce).toList()) {
//                max2 = max2 < f? f: max2;
//            }
//            if (max2 > Config.highLimit) {
//                for (PlayerForce player : newList) {
//                    player.playerForce = (player.playerForce / max2) * Config.highLimit;
//                }
//            }
        }
        float max2 = 0;
        for (Float f : newList.stream().map(e -> e.playerForce).toList()) {
            max2 = max2 < f? f: max2;
        }
        if (max2 > Config.highLimit) {
            for (PlayerForce player : newList) {
                player.playerForce = (player.playerForce / max2) * Config.highLimit;
            }
        }
        List<PlayerForce> changed = newList.stream().filter(e -> e.playerForce != Config.playerForceDefault || e.playerStability != Config.playerStability).toList();
        Map<Integer, Integer> resultMap = calculateImprovementResult(availableStatsIdsTest,
                allPlayersAnywhere, playerForcesMap, mapForThisImprovement, epochs);
//        ResData resData = calculateImprovementResultObject(availableStatsIdsTest,
//                allPlayersAnywhere, playerForcesMap);
//        Map<Map<Integer, List<PlayerOnMapResults>>, Map<Integer, List<PlayerForce>>> resultMap = new HashMap<>();
//        resultMap.put(allPlayersAnywhere, playerForcesMap);
        return resultMap;
    }

    public ResData improvementTestHard(ImprovementRequestDto requestDto) {
        Map<String, Object> mapForThisImprovement = CommonUtils.invokeConfig();
        System.out.println("improvement started");
        List<Integer> availableStatsIdsTrain = getAvailableStatsIdsTrain(requestDto);
        List<Integer> availableStatsIdsTest = getAvailableStatsIdsTest(requestDto);
        List<Integer> existingPlayerIds = calculatingReader
                .getPlayerIdsWhoExistsInCalculatingMatches(availableStatsIdsTrain);
        List<PlayerForce> allPlayerForces = calculatingReader.getPlayerForceListByPlayerIds(existingPlayerIds, true);
        List<PlayerForce> newList = new ArrayList<>();
        allPlayerForces.forEach(e -> {
            //для improvement нужны только дефолтные значения. Дальше расчет в памяти
            newList.add(new PlayerForce(e.id, e.playerId, Config.playerForceDefault, Config.playerStability, e.map, 0, 0));
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
                PlayerForce playerForceForCalculate = playerForcesMap.get(player.playerId).stream()
                        .filter(e -> e.map.equals(player.playedMapString)).toList().get(0);
                playerForceForCalculate.playerForce += force;
                winStrikeCalculation(player, playerForceForCalculate);
                loseStrikeCalculation(player, playerForceForCalculate);
                //Задаю лимиты для силы
                playerForceForCalculate.playerForce = calculator.correctLowLimit(playerForceForCalculate.playerForce);
//                playerForceForCalculate.playerForce = calculator.correctLowAndHighLimit(playerForceForCalculate.playerForce);
            });
        }
        currectId = 0;
        int epochs = Config.epochsNumber;
        for (int i = 0; i < epochs; i++) {
//            AtomicInteger index = new AtomicInteger();
            for (Integer id : availableStatsIdsTrain) {
                currectId++;
                Integer finalCurrectId = currectId;
                List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
                List<PlayerOnMapResults> leftTeam = players.stream().filter(e -> e.team.equals("left")).toList();
                List<PlayerOnMapResults> rightTeam = players.stream().filter(e -> e.team.equals("right")).toList();
                RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                        .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
                List<Float> forces = roundHistoryCalculator.getTeamForces(history.roundSequence, history.leftTeamIsTerroristsInFirstHalf);
//                int wow = i;
                players.forEach(player -> {
//                    index.getAndIncrement();
                    float force = calculator.calculatePlayerForce(player, forces, Config.adrMultiplier,
                            Config.killsMultiplier, Config.headshotsMultiplier, Config.ratingMultiplier, Config.historyMultiplier, Config.forceTeamMultiplier,
                            false, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap, finalCurrectId, availableStatsIdsTrain.size());
                    //if (!player.team.equals(player.teamWinner)) force -= 3f;
                    PlayerForce playerForceForCalculate = playerForcesMap.get(player.playerId).stream()
                            .filter(e -> e.map.equals(player.playedMapString)).toList().get(0);
                    playerForceForCalculate.playerForce += force;
                    winStrikeCalculation(player, playerForceForCalculate);
                    loseStrikeCalculation(player, playerForceForCalculate);
                    //Задаю лимиты для силы
                    playerForceForCalculate.playerForce = calculator.correctLowLimit(playerForceForCalculate.playerForce);
//                    playerForceForCalculate.playerForce = calculator.correctLowAndHighLimit(playerForceForCalculate.playerForce);


//                    if (player.playerId == 29) {
//
//                        System.out.println("Эпоха: " + wow + "| Id игры: " + id + "| Сила игрока: " +  playerForcesMap.get(player.playerId).stream()
//                                .filter(e -> e.map.equals("MIRAGE")).toList().get(0).playerForce);
//                    }
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
                if (Config.isConsiderDifferenceCorrection) {
                    differenceCalculator.calculateTeamsDifference(leftTeamForce, rightTeamForce, players.get(0).teamWinner);
                }
            }
//            float max2 = 0;
//            for (Float f : newList.stream().map(e -> e.playerForce).toList()) {
//                max2 = max2 < f? f: max2;
//            }
//            if (max2 > Config.highLimit) {
//                for (PlayerForce player : newList) {
//                    player.playerForce = (player.playerForce / max2) * Config.highLimit;
//                }
//            }
        }
        float max2 = 0;
        for (Float f : newList.stream().map(e -> e.playerForce).toList()) {
            max2 = max2 < f? f: max2;
        }
        if (max2 > Config.highLimit) {
            for (PlayerForce player : newList) {
                player.playerForce = (player.playerForce / max2) * Config.highLimit;
            }
        }
        List<PlayerForce> changed = newList.stream().filter(e -> e.playerForce != Config.playerForceDefault || e.playerStability != Config.playerStability).toList();
//        Map<Integer, Integer> resultMap = calculateImprovementResult(availableStatsIdsTest,
//                allPlayersAnywhere, playerForcesMap, mapForThisImprovement, epochs);
        ResData resData = calculateImprovementResultObject(availableStatsIdsTest,
                allPlayersAnywhere, playerForcesMap);
//        Map<Map<Integer, List<PlayerOnMapResults>>, Map<Integer, List<PlayerForce>>> resultMap = new HashMap<>();
//        resultMap.put(allPlayersAnywhere, playerForcesMap);
        return resData;
    }

    public Map<Integer, Integer> calculateImprovementResult(List<Integer> availableStatsIdsTest,
                                           Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere,
                                           Map<Integer, List<PlayerForce>> playerForcesMap,
                                           Map<String, Object> mapForThisImprovement, int currectEpoch) {
        Map<Integer, Integer> resultMap = new HashMap<>();
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
            if ((leftForce > rightForce * Config.compareMultiplier) || (rightForce > leftForce * Config.compareMultiplier)) percentAllAnswers++;
            if ((leftForce > rightForce * Config.compareMultiplier && winner.equals("left")) || (rightForce > leftForce * Config.compareMultiplier && winner.equals("right"))) {
                percentRightAnswers++;
            }
            if ((leftForce > rightForce + 100) || (rightForce > leftForce + 100)) constAllAnswers++;
            if ((leftForce > rightForce + 100 && winner.equals("left")) || (rightForce > leftForce + 100 && winner.equals("right"))) {
                constRightAnswers++;
            }
        }
        saveImprovementResult(rightAnswers, availableStatsIdsTest.size(), mapForThisImprovement, currectEpoch);
        //System.out.println("Эпоха номер: " + (currectEpoch) + ". На " + availableStatsIdsTest.size() +
        //        " матчей приходится " + rightAnswers +
        //        " правильных ответов! Процент точности равен " +
        //        (float) rightAnswers / availableStatsIdsTest.size());
        System.out.println("(Процент) Эпоха номер: " + (currectEpoch) + ". На " + percentAllAnswers +
                " матчей приходится " + percentRightAnswers +
                " правильных ответов! Процент точности равен " +
                (float) percentRightAnswers / percentAllAnswers +
                " Общая доля равна: " + (float) percentAllAnswers/availableStatsIdsTest.size() +
                " (количество игр тестовой базы: " + availableStatsIdsTest.size() + ")");
        System.out.println("(Константа) Эпоха номер: " + (currectEpoch) + ". На " + constAllAnswers +
                " матчей приходится " + constRightAnswers +
                " правильных ответов! Процент точности равен " +
                (float) constRightAnswers / constAllAnswers +
                " Общая доля равна: " + (float) constAllAnswers/availableStatsIdsTest.size() +
                " (количество игр тестовой базы: " + availableStatsIdsTest.size() + ")");
        resultMap.put(percentRightAnswers, percentAllAnswers);
        return resultMap;
    }

    public ResData calculateImprovementResultObject(List<Integer> availableStatsIdsTest,
                                                            Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere,
                                                            Map<Integer, List<PlayerForce>> playerForcesMap) {
        List<String> forcesLimits = new ArrayList<>();
        List<Float> forcesLow = new ArrayList<>();
        List<Float> forcesHigh = new ArrayList<>();
        List<Integer> wins = new ArrayList<>();
        List<Integer> all = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            forcesLow.add(i * 1f);
            forcesHigh.add(i * 1f + 1f);
            forcesLimits.add(forcesLow.get(i).toString() + " - " + forcesHigh.get(i).toString());
            wins.add(0);
            all.add(0);
        }
        forcesLow.add(20 * 1f);
        forcesHigh.add(100000f);
        forcesLimits.add(forcesLow.get(20).toString() + " - " + forcesHigh.get(20).toString());
        wins.add(0);
        all.add(0);
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
            for (int i = 0; i < 21; i++) {
                if(leftForce > rightForce && winner.equals("left")) {
                    Float forceDiff = leftForce/rightForce;
                    if (forcesLow.get(i) < forceDiff && forceDiff < forcesHigh.get(i)) {
                        wins.set(i, wins.get(i) + 1);
                        all.set(i, all.get(i) + 1);
                    }
                } else if (rightForce > leftForce && winner.equals("right")) {
                        Float forceDiff = rightForce/leftForce;
                        if (forcesLow.get(i) < forceDiff && forceDiff < forcesHigh.get(i)) {
                            wins.set(i, wins.get(i) + 1);
                            all.set(i, all.get(i) + 1);
                        }
                } else {
                    if(leftForce > rightForce) {
                        Float forceDiff = leftForce/rightForce;
                        if (forcesLow.get(i) < forceDiff && forceDiff < forcesHigh.get(i)) {
                            all.set(i, all.get(i) + 1);
                        }
                    } else {
                        Float forceDiff = rightForce/leftForce;
                        if (forcesLow.get(i) < forceDiff && forceDiff < forcesHigh.get(i)) {
                            all.set(i, all.get(i) + 1);
                        }
                    }
                }
            }
//            if ((leftForce > rightForce && winner.equals("left")) || (rightForce > leftForce && winner.equals("right"))) {
//                rightAnswers++;
//            }
//            if ((leftForce > rightForce * Config.compareMultiplier) || (rightForce > leftForce * Config.compareMultiplier)) percentAllAnswers++;
//            if ((leftForce > rightForce * Config.compareMultiplier && winner.equals("left")) || (rightForce > leftForce * Config.compareMultiplier && winner.equals("right"))) {
//                percentRightAnswers++;
//            }
//            if ((leftForce > rightForce + 50) || (rightForce > leftForce + 50)) constAllAnswers++;
//            if ((leftForce > rightForce + 50 && winner.equals("left")) || (rightForce > leftForce + 50 && winner.equals("right"))) {
//                constRightAnswers++;
//            }
        }
        //System.out.println("Эпоха номер: " + (currectEpoch) + ". На " + availableStatsIdsTest.size() +
        //        " матчей приходится " + rightAnswers +
        //        " правильных ответов! Процент точности равен " +
        //        (float) rightAnswers / availableStatsIdsTest.size());
        ResData resData = new ResData(forcesLimits, forcesLow, forcesHigh, wins, all);
        return resData;
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
            if ((leftForce > rightForce * Config.compareMultiplier) || (rightForce > leftForce * Config.compareMultiplier)) {
                if ((leftForce > rightForce * Config.compareMultiplier && winner.equals("left")) || (rightForce > leftForce * Config.compareMultiplier && winner.equals("right"))) {
                    resultMap.put(id, true);
                } else {
                    resultMap.put(id, false);
                }
            }
        }
        return resultMap;
    }

    private void winStrikeCalculation(PlayerOnMapResults player, PlayerForce playerForceForCalculate) {
        if (player.teamWinner.equals(player.team)) {
            playerForceForCalculate.winStrike++;
        } else {
            playerForceForCalculate.winStrike = 0;
        }
        playerForceForCalculate.playerForce += playerForceForCalculate.winStrike * 0.1f;
    }

    private void loseStrikeCalculation(PlayerOnMapResults player, PlayerForce playerForceForCalculate) {
        if (player.teamWinner.equals(player.team)) {
            playerForceForCalculate.loseStrike = 0;
        } else {
            playerForceForCalculate.loseStrike++;
        }
        playerForceForCalculate.playerForce -= playerForceForCalculate.loseStrike * 0.1f;
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
                queryFactory.from(improvementResults).orderBy(improvementResults.id.desc()).fetch();
        return results;
    }

    private List<Integer> getAvailableStatsIdsTrain(ImprovementRequestDto requestDto) {
        return calculatingReader.getAvailableStatsIdsOrderedDatasetAndInactive(
                requestDto.getTestDatasetPercent(),
                false,
                requestDto.getInactiveDatasetPercent());
    }

    private List<Integer> getAvailableStatsIdsTest(ImprovementRequestDto requestDto) {
        return calculatingReader.getAvailableStatsIdsOrderedDatasetAndInactive(
                requestDto.getTestDatasetPercent(),
                true,
                requestDto.getInactiveDatasetPercent());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class ResData {
        public List<String> forcesLimits;
        public List<Float> forcesLow;
        public List<Float> forcesHigh;
        public List<Integer> wins;
        public List<Integer> all;
        public List<Float> percent;

        ResData (List<String> forcesLimits, List<Float> forcesLow, List<Float> forcesHigh, List<Integer> wins, List<Integer> all) {
            this.forcesLimits = forcesLimits;
            this.forcesLow = forcesLow;
            this.forcesHigh = forcesHigh;
            this.wins = wins;
            this.all = all;
        }

        public void calculatePercent() {
            this.percent = new ArrayList<>();
            for (int i = 0; i < wins.size(); i ++) {
                percent.add(Float.parseFloat(wins.get(i).toString()) / Float.parseFloat(all.get(i).toString()));
            }
        }

        public void merge(ResData resDataToMerge) {
            for (int i = 0; i < wins.size(); i ++) {
                resDataToMerge.wins.set(i, (resDataToMerge.wins.get(i) + this.wins.get(i)));
                resDataToMerge.all.set(i, (resDataToMerge.all.get(i) + this.all.get(i)));
            }
        }
    }

}
