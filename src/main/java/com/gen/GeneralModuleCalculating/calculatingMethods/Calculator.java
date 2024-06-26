package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.dtos.PlayerWithForce;
import com.gen.GeneralModuleCalculating.entities.PlayerForce;
import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class Calculator {

    @Autowired
    AdrCalculator adrCalculator;

    @Autowired
    KillsCalculator killsCalculator;

    @Autowired
    HeadshotsCalculator headshotsCalculator;

    @Autowired
    Rating20Calculator rating20Calculator;

    @Autowired
    ForceTeamCalculator forceTeamCalculator;

    //history добавляю сюда, чтобы записывать в базу результаты игроков и автоматом учитывать результативность по раундам
    public float calculatePlayerForce(PlayerOnMapResults player, List<Float> forcesHistory,
                                      float adrMultiplier, float killsMultiplier,
                                      float headshotsMultiplier, float ratingMultiplier,
                                      float historyMultiplier, float forceTeamMultiplier,
                                      boolean isFirstCalculation, List<PlayerOnMapResults> enemyTeam,
                                      Map<Integer, List<PlayerForce>> playerForcesMap, Integer currentStatsIdNumber,
                                      Integer allStatsIdNumber) {
        float forceFromHistory;
        if(player.team.equals("left")) {
            forceFromHistory = forcesHistory.get(0);
        } else {
            forceFromHistory = forcesHistory.get(1);
        }
        float adr = adrCalculator.getForceByAdr(player);
        float kills = killsCalculator.getForceByKills(player);
        float headshots = headshotsCalculator.getForceByHeadshots(player);
        float rating = rating20Calculator.getForceByRating20(player);
        float forceEnemyTeam = !isFirstCalculation? forceTeamCalculator.getForceTeam(enemyTeam, playerForcesMap, player) : 0f;
        //для начала выборки будет 0.9, конец выборки - 1.1
        float actualityMultiplier = (float) (Config.actualityMultiplier * (currentStatsIdNumber/allStatsIdNumber)
                + Config.actualityConst);
        return actualityMultiplier *
                (adr * adrMultiplier +
                kills * killsMultiplier +
                headshots * headshotsMultiplier +
                rating * ratingMultiplier +
                forceFromHistory * historyMultiplier +
                forceEnemyTeam * forceTeamMultiplier);
    }

    public float correctLowAndHighLimit(float playerForceFloat) {
        if(playerForceFloat > Config.highLimit) {
            return Config.highLimit;
        }
        else if(playerForceFloat < Config.lowLimit) {
            return Config.lowLimit;
        } else {
            return playerForceFloat;
        }
    }

    public float correctLowLimit(float playerForceFloat) {
        if (playerForceFloat < Config.lowLimit) {
            return Config.lowLimit;
        } else {
            return playerForceFloat;
        }
    }

    public float correctHighLimit(float playerForceFloat) {
        if(playerForceFloat > Config.highLimit) {
            return Config.highLimit;
        } else {
            return playerForceFloat;
        }
    }

    public float calculateTeamForce(List<PlayerWithForce> players, List<Float> forces, Boolean teamIsLeft) {
        float result = 0f;
        for (PlayerWithForce player : players) {
            result += player.playerForce.playerForce * (float) (player.playerForce.playerStability / 100);
        }
        return result;
    }

}
