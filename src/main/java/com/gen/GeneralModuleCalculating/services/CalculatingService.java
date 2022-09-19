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
                    for (int i = 0; i < Config.playerForceTableSize; i++) {
                        //новые игроки не должны иметь нулевую силу - приложение для тир10 команд будет считать легкую победу, что не так
                        PlayerForce playerForce = new PlayerForce();
                        playerForce.playerId = i;
                        playerForce.playerForce = Config.playerForceDefault;
                        playerForce.playerStability = Config.playerStability;
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
        Integer currectId = 0;
        Map<Integer, List<PlayerOnMapResults>> allPlayersAnywhere =
                queryFactory.from(playerOnMapResults).transform(GroupBy.groupBy(playerOnMapResults.idStatsMap).as(GroupBy.list(playerOnMapResults)));
        for (Integer id : availableStatsIds) {
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
                        true, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap, finalCurrectId, availableStatsIds.size());
                playerForcesMap.get(player.playerId).stream()
                        .filter(e -> e.map.equals(player.playedMapString)).toList().get(0).playerForce += force;
            });
        }
        System.out.println("Первичный расчет занял: " + (System.currentTimeMillis() - now) + " мс");
        int epochs = Config.epochsNumber;
        currectId = 0;
        for (int i = 0; i < epochs; i++) {
            for (Integer id : availableStatsIds) {
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
                            false, player.team.equals("left") ? rightTeam : leftTeam, playerForcesMap, finalCurrectId, availableStatsIds.size());
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
                if (Config.isConsiderStabilityCorrection) {
                    stabilityCalculator.calculateCorrectedStability(leftTeamForce, rightTeamForce, players.get(0).teamWinner);
                }
            }
        }
        System.out.println("Расчет занял: " + (System.currentTimeMillis() - now) + " мс");
        List<PlayerForce> changed = newList.stream().filter(e -> e.playerForce != 5 || e.playerStability != 100).toList();
        playerForceRepository.saveAll(changed);
        System.out.println("Запись расчета в базу (вместе с расчетом) заняла: " + (System.currentTimeMillis() - now) + " мс");
    }
}
