package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.entities.PlayerForce;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DifferenceCalculator {
    public void calculateTeamsDifference(List<PlayerForce> leftTeam,
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
        float difference = Math.abs(leftTeamForce - rightTeamForce);
        float differenceToAdding = difference * Config.differencePercent;
        if (leftTeamForce > rightTeamForce && teamWinner.equals("right")) { //левых надо наказать, правых - поощрить
            leftTeam.forEach(p -> p.playerForce = p.playerForce - differenceToAdding/5);
            rightTeam.forEach(p -> p.playerForce = p.playerForce + differenceToAdding/5);
        }
        if (rightTeamForce > leftTeamForce && teamWinner.equals("left")) { //левых поощряем, а правых наказываем
            leftTeam.forEach(p -> p.playerForce = p.playerForce + differenceToAdding/5);
            rightTeam.forEach(p -> p.playerForce = p.playerForce - differenceToAdding/5);
        }
    }

}
