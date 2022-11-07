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

    public void improvementByInactivePercent(ImprovementRequestDto requestDto) {
        Integer inactivePercent = requestDto.getTestDatasetPercent() * 5;
        Map<Integer, Integer> resultMap = new HashMap<>();
        for (int i = 0; i <= inactivePercent; i++) {
            requestDto.setInactiveDatasetPercent(inactivePercent - i);
            resultMap.putAll(improvementTest(requestDto));
        }
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
                    PlayerForce playerForceForCalculate = playerForcesMap.get(player.playerId).stream()
                            .filter(e -> e.map.equals(player.playedMapString)).toList().get(0);
                    playerForceForCalculate.playerForce += force;
                    winStrikeCalculation(player, playerForceForCalculate);
                    loseStrikeCalculation(player, playerForceForCalculate);
                    //Задаю лимиты для силы
                    playerForceForCalculate.playerForce = calculator.correctLowLimit(playerForceForCalculate.playerForce);
//                    playerForceForCalculate.playerForce = calculator.correctLowAndHighLimit(playerForceForCalculate.playerForce);
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
            if (Config.isPlayerForceCompressingInsideEpoch) playerForceCompressing(newList);
        }
        if (Config.isPlayerForceCompressingOutsideEpoch) playerForceCompressing(newList);
        List<PlayerForce> changed = newList.stream().filter(e -> e.playerForce != Config.playerForceDefault || e.playerStability != Config.playerStability).toList();
        Map<Integer, Integer> resultMap = calculateImprovementResult(availableStatsIdsTest,
                allPlayersAnywhere, playerForcesMap, mapForThisImprovement, epochs);
//        Map<Map<Integer, List<PlayerOnMapResults>>, Map<Integer, List<PlayerForce>>> resultMap = new HashMap<>();
//        resultMap.put(allPlayersAnywhere, playerForcesMap);
        return resultMap;
    }

    public void playerForceCompressing(List<PlayerForce> newList) {
        float max2 = 0;
        for (Float f : newList.stream().map(e -> e.playerForce).toList()) {
            max2 = max2 < f? f: max2;
        }
        if (max2 > Config.highLimit) {
            for (PlayerForce player : newList) {
                player.playerForce = (player.playerForce / max2) * Config.highLimit;
            }
        }
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
            if ((leftForce > rightForce + Config.compareSummand) || (rightForce > leftForce + Config.compareSummand)) constAllAnswers++;
            if ((leftForce > rightForce + Config.compareSummand && winner.equals("left")) || (rightForce > leftForce + Config.compareSummand && winner.equals("right"))) {
                constRightAnswers++;
            }
        }
        System.out.println("(Процент) Эпоха номер: " + (currectEpoch) + ". На " + percentAllAnswers +
                " матчей приходится " + percentRightAnswers +
                " правильных ответов! Процент точности равен " +
                (float) percentRightAnswers / percentAllAnswers +
                " Общая доля равна: " + (float) percentAllAnswers/availableStatsIdsTest.size() +
                " (количество игр тестовой базы: " + availableStatsIdsTest.size() + ")");
        saveImprovementResult(percentRightAnswers, percentAllAnswers, mapForThisImprovement, currectEpoch);
        System.out.println("(Константа) Эпоха номер: " + (currectEpoch) + ". На " + constAllAnswers +
                " матчей приходится " + constRightAnswers +
                " правильных ответов! Процент точности равен " +
                (float) constRightAnswers / constAllAnswers +
                " Общая доля равна: " + (float) constAllAnswers/availableStatsIdsTest.size() +
                " (количество игр тестовой базы: " + availableStatsIdsTest.size() + ")");
        resultMap.put(percentRightAnswers, percentAllAnswers);
        return resultMap;
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
}
