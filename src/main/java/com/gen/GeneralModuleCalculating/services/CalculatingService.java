package com.gen.GeneralModuleCalculating.services;

import com.gen.GeneralModuleCalculating.calculatingMethods.*;
import com.gen.GeneralModuleCalculating.common.MapsEnum;
import com.gen.GeneralModuleCalculating.dtos.ImprovementRequestDto;
import com.gen.GeneralModuleCalculating.dtos.MapsCalculatingQueueResponseDto;
import com.gen.GeneralModuleCalculating.entities.*;
import com.gen.GeneralModuleCalculating.readers.CalculatingReader;
import com.gen.GeneralModuleCalculating.repositories.MapsCalculatingQueueRepository;
import com.gen.GeneralModuleCalculating.repositories.PlayerForceRepository;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CalculatingService {

    @Autowired
    MapsCalculatingQueueRepository mapsCalculatingQueueRepository;

    @Autowired
    PlayerForceRepository playerForceRepository;

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

    private static int playerForceTableSize = 30000; //В какой-то момент этого станет недостаточно. На 210822 примерное кол-во --- 23к
    private static float playerForceDefault = 5; //Дефолтная сила игроков
    private static int playerStability = 100; //Дефолтная стабильность игроков

    public MapsCalculatingQueueResponseDto createQueue() {
        long now = System.currentTimeMillis();
        MapsCalculatingQueueResponseDto result = new MapsCalculatingQueueResponseDto();
        List<Integer> statsIds = queryFactory.from(roundHistory).select(roundHistory.idStatsMap)
                .distinct().fetch();
        List<MapsCalculatingQueue> resultList = new ArrayList<>();
        statsIds.forEach(id -> {
            if (mapsCalculatingQueueRepository.findById(id).isEmpty()) { //пишем в базу только то, что ранее записано не было
                MapsCalculatingQueue queue = new MapsCalculatingQueue();
                queue.idStatsMap = id;
                queue.calculationTime = 0;
                queue.processed = false;
                resultList.add(queue);
            }
        });
        mapsCalculatingQueueRepository.saveAll(resultList);
        result.mapsAddingTime = (int) (System.currentTimeMillis() - now);
        result.mapsAddingCount = statsIds.size();
        result.currentNotProcessedMaps = -1;
        return result;
    }

    //для расчета нужно знать силу противника, однако при первом проходе её не будет, потому надо инициировать
    public void createPlayerForceTable() {
        long now = System.currentTimeMillis();
        //если ранее таблица не была инициирована
        if (queryFactory.from(playerForce).select(playerForce.playerId).fetch().isEmpty()) {
            List<PlayerForce> playerForces = new ArrayList<>();
            for (int map = 0; map < MapsEnum.values().length; map++) {
                if(!Objects.equals(Arrays.stream(MapsEnum.values()).toList().get(map).toString(), "ALL")) {
                    for (int i = 0; i < playerForceTableSize; i++) {
                        //новые игроки не должны иметь нулевую силу - приложение для тир10 команд будет считать легкую победу, что не так
                        PlayerForce playerForce = new PlayerForce();
                        playerForce.playerId = i;
                        playerForce.playerForce = playerForceDefault;
                        playerForce.playerStability = playerStability;
                        playerForce.map = Arrays.stream(MapsEnum.values()).toList().get(map).toString();
                        playerForces.add(playerForce);
                    }
                }
            }
            playerForceRepository.saveAll(playerForces);
        }
        System.out.println("Создание таблицы заняло: " + (System.currentTimeMillis() - now) + " мс");
    }

    public MapsCalculatingQueueResponseDto getCurrentQueueSize() {
        MapsCalculatingQueueResponseDto result = new MapsCalculatingQueueResponseDto();
        result.currentNotProcessedMaps = (int) queryFactory.from(mapsCalculatingQueue)
                .where(mapsCalculatingQueue.processed.eq(false)).stream().count();
        result.mapsAddingCount = -1;
        result.mapsAddingTime = -1;
        return result;
    }

    public void calculateForces() {
        System.out.println("Расчет начался");
        List<Integer> availableStatsIds = calculatingReader.getAvailableStatsIdsOrdered();
        List<Integer> existingPlayerIds = calculatingReader
                .getPlayerIdsWhoExistsInCalculatingMatches(availableStatsIds);
        List<PlayerForce> allPlayerForces = calculatingReader.getPlayerForceListByPlayerIds(existingPlayerIds, false);
        List<PlayerForce> newList = new ArrayList<>();
        allPlayerForces.forEach(e -> {
            newList.add(new PlayerForce(e.id, e.playerId, e.playerForce, e.playerStability, e.map));
        });

        Map<Integer, List<PlayerForce>> playerForcesMap = newList.stream().collect(Collectors.groupingBy(e -> e.playerId));
        long now = System.currentTimeMillis();
        Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere =
                queryFactory.from(playerOnMapResults).transform(GroupBy.groupBy(playerOnMapResults.idStatsMap).as(GroupBy.list(playerOnMapResults)));
        for (Integer id : availableStatsIds) {
            List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
            List<PlayerOnMapResults> leftTeam = players.stream().filter(e -> e.team.equals("left")).toList();
            List<PlayerOnMapResults> rightTeam = players.stream().filter(e -> e.team.equals("right")).toList();
            RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                    .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
            List<Float> forces = roundHistoryCalculator.getTeamForces(history.roundSequence, history.leftTeamIsTerroristsInFirstHalf);
            players.forEach(player -> {
                float force = calculator.calculatePlayerForce(player, forces, 1,
                        1, 0.1f, 1, 1, 0.05f,
                        true, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap);
                playerForcesMap.get(player.playerId).stream()
                        .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce += force;
            });
        }
        System.out.println("Первичный расчет занял: " + (System.currentTimeMillis() - now) + " мс");
        int epochs = 4;
        for (int i = 0; i < epochs; i++) {
            for (Integer id : availableStatsIds) {
                List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
                List<PlayerOnMapResults> leftTeam = players.stream().filter(e -> e.team.equals("left")).toList();
                List<PlayerOnMapResults> rightTeam = players.stream().filter(e -> e.team.equals("right")).toList();
                RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                        .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
                List<Float> forces = roundHistoryCalculator.getTeamForces(history.roundSequence, history.leftTeamIsTerroristsInFirstHalf);
                players.forEach(player -> {
                    float force = calculator.calculatePlayerForce(player, forces, 1,
                            1, 0.1f, 1, 1, 0.05f,
                            false, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap);
                    playerForcesMap.get(player.playerId).stream()
                            .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce += force;
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
        }
        System.out.println("Расчет занял: " + (System.currentTimeMillis() - now) + " мс");
        List<PlayerForce> changed = newList.stream().filter(e -> e.playerForce != 5 || e.playerStability != 100).toList();
        playerForceRepository.saveAll(changed);
        System.out.println("Запись расчета в базу (вместе с расчетом) заняла: " + (System.currentTimeMillis() - now) + " мс");
    }

    public void improvementTest(ImprovementRequestDto requestDto) {
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
        Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere =
                queryFactory.from(playerOnMapResults).transform(GroupBy.groupBy(playerOnMapResults.idStatsMap).as(GroupBy.list(playerOnMapResults)));
        for (Integer id : availableStatsIdsTrain) {
            List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
            List<PlayerOnMapResults> leftTeam = players.stream().filter(e -> e.team.equals("left")).toList();
            List<PlayerOnMapResults> rightTeam = players.stream().filter(e -> e.team.equals("right")).toList();
            RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                    .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
            List<Float> forces = roundHistoryCalculator.getTeamForces(history.roundSequence, history.leftTeamIsTerroristsInFirstHalf);
            players.forEach(player -> {
                float force = calculator.calculatePlayerForce(player, forces, 1,
                        1, 0.1f, 1, 1, 0.05f,
                        true, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap);
                playerForcesMap.get(player.playerId).stream()
                        .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce += force;
            });
        }
        System.out.println("Первичный расчет занял: " + (System.currentTimeMillis() - now) + " мс");
        int epochs = 9;
        for (int i = 0; i < epochs; i++) {
            for (Integer id : availableStatsIdsTrain) {
                List<PlayerOnMapResults> players = allPlayersAnywhere.get(id);
                List<PlayerOnMapResults> leftTeam = players.stream().filter(e -> e.team.equals("left")).toList();
                List<PlayerOnMapResults> rightTeam = players.stream().filter(e -> e.team.equals("right")).toList();
                RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                        .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
                List<Float> forces = roundHistoryCalculator.getTeamForces(history.roundSequence, history.leftTeamIsTerroristsInFirstHalf);
                players.forEach(player -> {
                    float force = calculator.calculatePlayerForce(player, forces, 1,
                            1, 0.1f, 1, 1, 0.05f,
                            false, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap);
                    playerForcesMap.get(player.playerId).stream()
                            .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce += force;
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
                for (PlayerOnMapResults p: players) {
                    for(int j = 0; j < 7; j++) {
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
                String winner = players.get(0).teamWinner;
                if((leftForce > rightForce && winner.equals("left")) || (rightForce > leftForce && winner.equals("right"))) {
                    rightAnswers++;
                }
            }
            System.out.println("Эпоха номер: " + (i+1) + ". На " + availableStatsIdsTest.size() +
                    " матчей приходится " + rightAnswers +
                    " правильных ответов! Процент точности равен " +
                    (float) rightAnswers/availableStatsIdsTest.size());
        }
        //TODO надо увеличивать влияние последних матчей, и уменьшать влияние первых
        //TODO надо сделать ограничение сил - снизу 0
        //TODO именно в процессе расчета предикта меняются кожффициенты для достижения консенсуса! Консенсус выкидывает ненадежные матчи!
        System.out.println("Вторичный расчет занял: " + (System.currentTimeMillis() - now) + " мс");
    }
}
