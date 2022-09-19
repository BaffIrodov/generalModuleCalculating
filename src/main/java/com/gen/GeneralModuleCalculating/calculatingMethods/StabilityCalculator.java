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
        float leftTeamStability = (float) leftTeam.stream().mapToDouble(e -> e.playerStability).average().getAsDouble();
        float rightTeamStability = (float) rightTeam.stream().mapToDouble(e -> e.playerStability).average().getAsDouble();
        float leftTeamForce = (float) leftTeam.stream().mapToDouble(e -> e.playerForce).sum()
                * (float) leftTeamStability/100;
        float rightTeamForce = (float) rightTeam.stream().mapToDouble(e -> e.playerForce).sum()
                * (float) rightTeamStability/100;
        // левые существенно сильнее правых по расчетам, но проиграли на практике
        if ((leftTeamForce / rightTeamForce) > Config.stabilityCompareCoeff
                && teamWinner.equals("right")) {
            //вся команда левых снижает свою стабильность
            leftTeam.forEach(l -> l.playerStability -= 1f);
        }
        // правые существенно сильнее левых по расчетам, но проиграли на практике
        if ((rightTeamForce / leftTeamForce) > Config.stabilityCompareCoeff
            && teamWinner.equals("left")) {
            //вся команда правых снижает свою стабильность
            rightTeam.forEach(r -> r.playerStability -= 1f);
        }
    }

}
