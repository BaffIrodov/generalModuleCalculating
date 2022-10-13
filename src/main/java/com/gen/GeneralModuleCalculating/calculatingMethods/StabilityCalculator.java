package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.entities.PlayerForce;
import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import com.gen.GeneralModuleCalculating.repositories.PlayerForceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StabilityCalculator {
    public void calculateCorrectedStability(List<PlayerForce> leftTeam,
                                                         List<PlayerForce> rightTeam,
                                                         String teamWinner) {
        float leftTeamForce = 0;
        for (PlayerForce player : leftTeam) {
            leftTeamForce += player.playerForce * player.playerStability;
        }
        float rightTeamForce = 0;
        for (PlayerForce player : rightTeam) {
            rightTeamForce += player.playerForce * player.playerStability;
        }
        // левые существенно сильнее правых по расчетам, но проиграли на практике
        //todo комплексный порог стабильности - -1, -2, -3 процента
        if ((leftTeamForce / rightTeamForce) > Config.stabilityCompareCoeff
                && teamWinner.equals("right")) {
            if ((leftTeamForce / rightTeamForce) > (Config.stabilityCompareCoeff + 0.4f)) {
                leftTeam.forEach(l -> l.playerStability -= 1f);
            }
            leftTeam.forEach(l -> l.playerStability -= 1f);
        }
        // правые существенно сильнее левых по расчетам, но проиграли на практике
        if ((rightTeamForce / leftTeamForce) > Config.stabilityCompareCoeff
            && teamWinner.equals("left")) {
            if ((rightTeamForce / leftTeamForce) > (Config.stabilityCompareCoeff + 0.4f)) {
                rightTeam.forEach(r -> r.playerStability -= 1f);
            }
            //вся команда правых снижает свою стабильность
            rightTeam.forEach(r -> r.playerStability -= 1f);
        }
    }

}
