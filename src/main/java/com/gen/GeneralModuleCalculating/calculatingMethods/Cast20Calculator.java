package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import org.springframework.stereotype.Component;

@Component
public class Cast20Calculator {

    public float getForceByCast20(PlayerOnMapResults player) {
        // средний каст = 69.23
        float normalizedCast20 = (player.cast20 - 69) * Config.normalizingCoeffCast20;
        if (normalizedCast20 < 0) {
            return (float) (Config.fourCoeffFuncCast20 * Math.pow(normalizedCast20, 4) +
                    Config.threeCoeffFuncCast20 * Math.pow(normalizedCast20, 3) +
                    Config.twoCoeffFuncCast20 * Math.pow(normalizedCast20, 2) +
                    Config.oneCoeffFuncCast20 * normalizedCast20 +
                    Config.zeroCoeffFuncCast20);
        } else {
            return (float) (-Config.fourCoeffFuncCast20 * Math.pow(normalizedCast20, 4) +
                    Config.threeCoeffFuncCast20 * Math.pow(normalizedCast20, 3) +
                    -Config.twoCoeffFuncCast20 * Math.pow(normalizedCast20, 2) +
                    Config.oneCoeffFuncCast20 * normalizedCast20 +
                    Config.zeroCoeffFuncCast20);
        }
    }
}
