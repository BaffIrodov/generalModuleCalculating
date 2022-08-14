package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import org.springframework.stereotype.Component;

@Component
public class KillsCalculator {
    public float oldGetForceByKills(PlayerOnMapResults player) {
        //среднее кд = 17,74
        float normalizedKills = ((float) player.kills - 17) * Config.normalizingCoeffKills;
        float result = (float)
                (Config.fourCoeffFuncKills * Math.pow(normalizedKills, 4) +
                        Config.threeCoeffFuncKills * Math.pow(normalizedKills, 3) +
                        Config.twoCoeffFuncKills * Math.pow(normalizedKills, 2) +
                        Config.oneCoeffFuncKills * normalizedKills +
                        Config.zeroCoeffFuncKills);
        return result;
    }

    public float getForceByKills(PlayerOnMapResults player) {
        //среднее кд = 17,74
        float normalizedKills = (float) ((float) player.kills - 17.74) * Config.normalizingCoeffKills;
        if (normalizedKills > 0) {
            return (float) (Config.sixthCoeffFuncKills * Math.pow(normalizedKills, 6) +
                    Config.fourCoeffFuncKills * Math.pow(normalizedKills, 4) +
                    Config.threeCoeffFuncKills * Math.pow(normalizedKills, 3) +
                    Config.twoCoeffFuncKills * Math.pow(normalizedKills, 2) +
                    Config.oneCoeffFuncKills * normalizedKills +
                    Config.zeroCoeffFuncKills);
        } else {
            //шестая степень не нужна
            return (float) (-Config.fourCoeffFuncKills * Math.pow(normalizedKills, 4) +
                    Config.threeCoeffFuncKills * Math.pow(normalizedKills, 3) +
                    -Config.twoCoeffFuncKills * Math.pow(normalizedKills, 2) +
                    Config.oneCoeffFuncKills * normalizedKills +
                    Config.zeroCoeffFuncKills);
        }
    }
}
