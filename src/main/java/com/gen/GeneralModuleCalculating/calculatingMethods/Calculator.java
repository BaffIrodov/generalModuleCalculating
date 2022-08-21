package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.dtos.PlayerWithForce;
import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    //history добавляю сюда, чтобы записывать в базу результаты игроков и автоматом учитывать результативность по раундам
    public float calculatePlayerForce(PlayerOnMapResults player, float adrMultiplier,
                                      float killsMultiplier, float headshotsMultiplier,
                                      float ratingMultiplier, List<Float> forcesHistory,
                                      float historyMultiplier) {
        float forceFromHistory;
        if(player.team.equals("Left")) {
            forceFromHistory = forcesHistory.get(0);
        } else {
            forceFromHistory = forcesHistory.get(1);
        }
        float adr = adrCalculator.getForceByAdr(player);
        float kills = killsCalculator.getForceByKills(player);
        float headshots = headshotsCalculator.getForceByHeadshots(player);
        float rating = rating20Calculator.getForceByRating20(player);
        return adr * adrMultiplier +
                kills * killsMultiplier +
                headshots * headshotsMultiplier +
                rating * ratingMultiplier +
                forceFromHistory * historyMultiplier;
    }

    public float calculateTeamForce(List<PlayerWithForce> players, List<Float> forces, Boolean teamIsLeft) {
        float result = 0f;
        for (PlayerWithForce player : players) {
            result += player.playerForce.playerForce * (float) (player.playerForce.playerStability / 100);
        }
        return result;
    }

}
