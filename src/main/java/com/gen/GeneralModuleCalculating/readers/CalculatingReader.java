package com.gen.GeneralModuleCalculating.readers;

import com.gen.GeneralModuleCalculating.calculatingMethods.Config;
import com.gen.GeneralModuleCalculating.entities.*;
import com.gen.GeneralModuleCalculating.repositories.PlayerForceRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        List<Integer> availableStats = queryFactory.from(mapsCalculatingQueue)
                .leftJoin(roundHistory).on(mapsCalculatingQueue.idStatsMap
                        .eq(roundHistory.idStatsMap))
                .select(mapsCalculatingQueue.idStatsMap)
                .where(mapsCalculatingQueue.processed.eq(false))
                .orderBy(roundHistory.dateOfMatch.asc())
                .fetch();
        if(Config.calculatingStatsIdNumber != 0) {
            availableStats = availableStats
                    .subList(availableStats.size() - Config.calculatingStatsIdNumber, availableStats.size());
        }
        return availableStats;
    }

    public List<Integer> getAvailableStatsIdsOrderedDataset(Integer testPercent, Boolean isTestDataset) {
        List<Integer> allIds = queryFactory.from(mapsCalculatingQueue)
                .leftJoin(roundHistory).on(mapsCalculatingQueue.idStatsMap
                        .eq(roundHistory.idStatsMap))
                .select(mapsCalculatingQueue.idStatsMap)
                .where(mapsCalculatingQueue.processed.eq(false))
                .orderBy(roundHistory.dateOfMatch.asc())
                .fetch();
        if(Config.calculatingStatsIdNumber != 0) {
            allIds = allIds
                    .subList(allIds.size() - Config.calculatingStatsIdNumber, allIds.size());
        }
        Integer waterLineIndex = allIds.size() * testPercent / 100;

        //todo неправильно считается - выкидывается не то, что нужно - надо с другой стороны
        List<Integer> testDatasetIds = allIds.subList(0, waterLineIndex);
        List<Integer> trainDatasetIds = allIds.subList(waterLineIndex, allIds.size());
        return isTestDataset? testDatasetIds: trainDatasetIds;
    }

    public List<Integer> getAvailableStatsIdsOrderedDatasetAndInactive(Integer testPercent, Boolean isTestDataset,
                                                                       Integer inactivePercent) {
        List<Integer> allIds = queryFactory.from(mapsCalculatingQueue)
                .leftJoin(roundHistory).on(mapsCalculatingQueue.idStatsMap
                        .eq(roundHistory.idStatsMap))
                .select(mapsCalculatingQueue.idStatsMap)
                .where(mapsCalculatingQueue.processed.eq(false))
                .orderBy(roundHistory.dateOfMatch.asc())
                .fetch();
        if(Config.calculatingStatsIdNumber != 0) {
            allIds = allIds
                    .subList(allIds.size() - Config.calculatingStatsIdNumber, allIds.size());
        }
        Integer waterLineIndex = allIds.size() * (100-testPercent-inactivePercent) / 100;
        Integer inactiveIndex = allIds.size() * (100-inactivePercent) / 100;
        List<Integer> trainDatasetIds = allIds.subList(0, waterLineIndex);
        List<Integer> testDatasetIds = allIds.subList(waterLineIndex, inactiveIndex);
        List<Integer> inactiveIds = allIds.subList(inactiveIndex, allIds.size());
        return isTestDataset? testDatasetIds: trainDatasetIds;
    }

    public List<Integer> getPlayerIdsWhoExistsInCalculatingMatches(List<Integer> availableStatsIds) {
        List<Integer> result = new ArrayList<>();
        if(availableStatsIds.size() > 30000) {
            int mpl = availableStatsIds.size() / 30000;
            int size = availableStatsIds.size() / (mpl+1);
            for(int i = 0; i < mpl+1; i++) {
                List<Integer> subList = availableStatsIds.subList(i*(size), (i+1)*(size));
                result.addAll(existingPlayersQuery(subList));
            }
            return result;
        } else {
            return existingPlayersQuery(availableStatsIds);
        }
    }

    private List<Integer> existingPlayersQuery(List<Integer> availableStatsIds) {
        return queryFactory.from(playerOnMapResults)
                .select(playerOnMapResults.playerId)
                .where(playerOnMapResults.idStatsMap.in(availableStatsIds))
                .distinct()
                .fetch();
    }

    public List<PlayerForce> getPlayerForceListByPlayerIds(List<Integer> existingPlayerIds, Boolean isGettingAll) {
        JPAQuery playerIdsFromForceTableQuery = queryFactory.from(playerForce)
                .select(playerForce.id);
        if(!isGettingAll) {
            playerIdsFromForceTableQuery.where(playerForce.playerId.in(existingPlayerIds));
        }
        List<Integer> playerIdsFromForceTable = playerIdsFromForceTableQuery.fetch();
        // может быть большим числом >32k, надо нарезать
        List<PlayerForce> result = new ArrayList<>();
        if(playerIdsFromForceTable.size() > 30000) {
            int mpl = playerIdsFromForceTable.size() / 30000;
            int size = playerIdsFromForceTable.size() / (mpl+1);
            for(int i = 0; i < mpl+1; i++) {
                List<Integer> subList = playerIdsFromForceTable.subList(i*(size), (i+1)*(size));
                result.addAll(playerForceRepository.findAllById(subList).stream().toList());
            }
            return result;
        } else {
            return playerForceRepository.findAllById(playerIdsFromForceTable).stream().toList();
        }
    }



}
