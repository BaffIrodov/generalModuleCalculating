package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import org.springframework.stereotype.Component;

@Component
public class Rating20Calculator {

    public float getForceByRating20(PlayerOnMapResults player) {
        // средний каст = 1.048
        float normalizedRating20 = (player.rating20 - 1) * Config.normalizingCoeffRating20;
        if (normalizedRating20 < 0) {
            return (float) (Config.fourCoeffFuncRating20 * Math.pow(normalizedRating20, 4) +
                    Config.threeCoeffFuncRating20 * Math.pow(normalizedRating20, 3) +
                    Config.twoCoeffFuncRating20 * Math.pow(normalizedRating20, 2) +
                    Config.oneCoeffFuncRating20 * normalizedRating20 +
                    Config.zeroCoeffFuncRating20);
        } else {
            return (float) (-Config.fourCoeffFuncRating20 * Math.pow(normalizedRating20, 4) +
                    Config.threeCoeffFuncRating20 * Math.pow(normalizedRating20, 3) +
                    -Config.twoCoeffFuncRating20 * Math.pow(normalizedRating20, 2) +
                    Config.oneCoeffFuncRating20 * normalizedRating20 +
                    Config.zeroCoeffFuncRating20);
        }
    }
}
