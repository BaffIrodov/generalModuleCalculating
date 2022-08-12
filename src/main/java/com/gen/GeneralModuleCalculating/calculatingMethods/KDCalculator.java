package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import org.springframework.stereotype.Component;

@Component
public class KDCalculator {
    public float oldGetForceByKD(PlayerOnMapResults player) {
        //среднее кд = 17,74
        float normalizedKD = (player.kills - 17) * Config.normalizingCoeffKD;
        float result = (float)
                (Config.fourCoeffFuncKD * Math.pow(normalizedKD, 4) +
                        Config.threeCoeffFuncKD * Math.pow(normalizedKD, 3) +
                        Config.twoCoeffFuncKD * Math.pow(normalizedKD, 2) +
                        Config.oneCoeffFuncKD * normalizedKD +
                        Config.zeroCoeffFuncKD);
        return result;
    }
}
