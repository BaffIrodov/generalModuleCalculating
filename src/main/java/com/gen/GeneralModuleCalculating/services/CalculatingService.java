package com.gen.GeneralModuleCalculating.services;

import com.gen.GeneralModuleCalculating.calculatingMethods.*;
import com.gen.GeneralModuleCalculating.common.MapsEnum;
import com.gen.GeneralModuleCalculating.dtos.MapsCalculatingQueueResponseDto;
import com.gen.GeneralModuleCalculating.entities.*;
import com.gen.GeneralModuleCalculating.repositories.MapsCalculatingQueueRepository;
import com.gen.GeneralModuleCalculating.repositories.PlayerForceRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CalculatingService {

    @Autowired
    JPAQueryFactory queryFactory;

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
            for (int i = 0; i < playerForceTableSize; i++) {
                for (int map = 0; map < MapsEnum.values().length; map++) {
                    //новые игроки не должны иметь нулевую силу - приложение для тир10 команд будет считать легкую победу, что не так
                    PlayerForce playerForce = new PlayerForce();
                    playerForce.playerId = i;
                    playerForce.playerForce = playerForceDefault;
                    playerForce.playerStability = playerStability;
                    playerForce.map = Arrays.stream(MapsEnum.values()).toList().get(map).toString();
                    playerForces.add(playerForce);
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
        List<Integer> availableStatsIds = queryFactory.from(mapsCalculatingQueue)
                .leftJoin(roundHistory).on(mapsCalculatingQueue.idStatsMap
                        .eq(roundHistory.idStatsMap))
                .select(mapsCalculatingQueue.idStatsMap)
                .where(mapsCalculatingQueue.processed.eq(false))
                .orderBy(roundHistory.dateOfMatch.desc())
                .fetch();

        List<Integer> existingPlayerIds = queryFactory.from(playerOnMapResults)
                .select(playerOnMapResults.playerId)
                .where(playerOnMapResults.idStatsMap.in(availableStatsIds))
                .fetch().stream().toList();

        List<Integer> playerIdsFromForceTable = queryFactory.from(playerForce)
                .select(playerForce.id).where(playerForce.playerId.in(existingPlayerIds)).fetch();

        List<PlayerForce> allPlayerForces = playerForceRepository.findAllById(playerIdsFromForceTable).stream().toList();
        Map<Integer, List<PlayerForce>> playerForcesMap = allPlayerForces.stream().collect(Collectors.groupingBy(e -> e.playerId));
        long now = System.currentTimeMillis();
        for (Integer id : availableStatsIds) {
            List<PlayerOnMapResults> players = (List<PlayerOnMapResults>)
                    queryFactory.from(playerOnMapResults)
                            .where(playerOnMapResults.idStatsMap.eq(id)).fetch();
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
        int epochs = 4;
        for (int i = 0; i < epochs; i++) {
            for (Integer id : availableStatsIds) {
                List<PlayerOnMapResults> players = (List<PlayerOnMapResults>)
                        queryFactory.from(playerOnMapResults)
                                .where(playerOnMapResults.idStatsMap.eq(id)).fetch();
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
                        leftTeamForce.add(playerForcesMap.get(p.playerId).get(0));
                    } else {
                        rightTeamForce.add(playerForcesMap.get(p.playerId).get(0));
                    }
                });

                stabilityCalculator.calculateCorrectedStability(leftTeamForce, rightTeamForce, players.get(0).teamWinner);
            }
        }
        System.out.println("Расчет занял: " + (System.currentTimeMillis() - now) + " мс");
        playerForceRepository.saveAll(allPlayerForces);
        System.out.println("Запись расчета в базу (вместе с расчетом) заняла: " + (System.currentTimeMillis() - now) + " мс");
    }
}
