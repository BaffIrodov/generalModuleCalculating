package com.gen.GeneralModuleCalculating.readers;

import com.gen.GeneralModuleCalculating.entities.*;
import com.gen.GeneralModuleCalculating.repositories.PlayerForceRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalculatingReader {

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

    @Autowired
    PlayerForceRepository playerForceRepository;

    public List<Integer> getAvailableStatsIdsOrdered() {
        return queryFactory.from(mapsCalculatingQueue)
                .leftJoin(roundHistory).on(mapsCalculatingQueue.idStatsMap
                        .eq(roundHistory.idStatsMap))
                .select(mapsCalculatingQueue.idStatsMap)
                .where(mapsCalculatingQueue.processed.eq(false))
                .orderBy(roundHistory.dateOfMatch.desc())
                .fetch();
    }

    public List<Integer> getAvailableStatsIdsOrderedDataset(Integer testPercent, Boolean isTestDataset) {
        List<Integer> allIds = queryFactory.from(mapsCalculatingQueue)
                .leftJoin(roundHistory).on(mapsCalculatingQueue.idStatsMap
                        .eq(roundHistory.idStatsMap))
                .select(mapsCalculatingQueue.idStatsMap)
                .where(mapsCalculatingQueue.processed.eq(false))
                .orderBy(roundHistory.dateOfMatch.desc())
                .fetch();
        Integer waterLineIndex = allIds.size() * testPercent / 100;
        List<Integer> testDatasetIds = allIds.subList(0, waterLineIndex);
        List<Integer> trainDatasetIds = allIds.subList(waterLineIndex+1, allIds.size());
        return isTestDataset? testDatasetIds: trainDatasetIds;
    }

    public List<Integer> getPlayerIdsWhoExistsInCalculatingMatches(List<Integer> availableStatsIds) {
        return queryFactory.from(playerOnMapResults)
                .select(playerOnMapResults.playerId)
                .where(playerOnMapResults.idStatsMap.in(availableStatsIds))
                .fetch();
    }

    public List<PlayerForce> getPlayerForceListByPlayerIds(List<Integer> existingPlayerIds) {
        List<Integer> playerIdsFromForceTable = queryFactory.from(playerForce)
                .select(playerForce.id).where(playerForce.playerId.in(existingPlayerIds)).fetch();
        return playerForceRepository.findAllById(playerIdsFromForceTable).stream().toList();
    }



}
