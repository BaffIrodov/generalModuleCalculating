package com.gen.GeneralModuleCalculating.services;

import com.gen.GeneralModuleCalculating.calculatingMethods.*;
import com.gen.GeneralModuleCalculating.dtos.MapsCalculatingQueueResponseDto;
import com.gen.GeneralModuleCalculating.entities.*;
import com.gen.GeneralModuleCalculating.repositories.MapsCalculatingQueueRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class CalculatingService {

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    MapsCalculatingQueueRepository mapsCalculatingQueueRepository;

    @Autowired
    AdrCalculator adrCalculator;

    @Autowired
    KDCalculator kdCalculator;

    @Autowired
    HeadshotsCalculator headshotsCalculator;

    @Autowired
    Cast20Calculator cast20Calculator;

    @Autowired
    Rating20Calculator rating20Calculator;

    private static final QPlayerOnMapResults playerOnMapResults =
            new QPlayerOnMapResults("playerOnMapResults");
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

    public MapsCalculatingQueueResponseDto getCurrentQueueSize() {
        MapsCalculatingQueueResponseDto result = new MapsCalculatingQueueResponseDto();
        result.currentNotProcessedMaps = (int) queryFactory.from(mapsCalculatingQueue)
                .where(mapsCalculatingQueue.processed.eq(false)).stream().count();
        result.mapsAddingCount = -1;
        result.mapsAddingTime = -1;
        calculate();
        return result;
    }

    public void debug() {
        calculate();
    }

    public void calculate() {
        List<Integer> availableStatsIds = queryFactory.from(mapsCalculatingQueue)
                .select(mapsCalculatingQueue.idStatsMap)
                .where(mapsCalculatingQueue.processed.eq(false))
                .fetch();
        for(Integer id : availableStatsIds) {
            List<PlayerOnMapResults> players = (List<PlayerOnMapResults>)
                    queryFactory.from(playerOnMapResults)
                    .where(playerOnMapResults.idStatsMap.eq(id)).fetch();
            RoundHistory history = (RoundHistory) queryFactory.from(roundHistory)
                    .where(roundHistory.idStatsMap.eq(id)).fetchFirst();
            float adr1 = adrCalculator.getForceByAdr(players.get(0));
            float kd1 = kdCalculator.getForceByKD(players.get(0));
            float headshots1 = headshotsCalculator.getForceByHeadshots(players.get(0));
            float cast1 = cast20Calculator.getForceByCast20(players.get(0));
            float rating1 = rating20Calculator.getForceByRating20(players.get(0));
            float adr2 = adrCalculator.getForceByAdr(players.get(1));
            float kd2 = kdCalculator.getForceByKD(players.get(1));
            float headshots2 = headshotsCalculator.getForceByHeadshots(players.get(1));
            float cast2 = cast20Calculator.getForceByCast20(players.get(1));
            float rating2 = rating20Calculator.getForceByRating20(players.get(1));
            float adr3 = adrCalculator.getForceByAdr(players.get(2));
            float kd3 = kdCalculator.getForceByKD(players.get(2));
            float headshots3 = headshotsCalculator.getForceByHeadshots(players.get(2));
            float cast3 = cast20Calculator.getForceByCast20(players.get(2));
            float rating3 = rating20Calculator.getForceByRating20(players.get(2));
            float adr4 = adrCalculator.getForceByAdr(players.get(3));
            float kd4 = kdCalculator.getForceByKD(players.get(3));
            float headshots4 = headshotsCalculator.getForceByHeadshots(players.get(3));
            float cast4 = cast20Calculator.getForceByCast20(players.get(3));
            float rating4 = rating20Calculator.getForceByRating20(players.get(3));
            float adr5 = adrCalculator.getForceByAdr(players.get(4));
            float kd5 = kdCalculator.getForceByKD(players.get(4));
            float headshots5 = headshotsCalculator.getForceByHeadshots(players.get(4));
            float cast5 = cast20Calculator.getForceByCast20(players.get(4));
            float rating5 = rating20Calculator.getForceByRating20(players.get(4));
            int i = 0;
            break;
        }
    }
}
