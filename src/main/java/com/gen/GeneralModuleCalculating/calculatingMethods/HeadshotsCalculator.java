package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import org.springframework.stereotype.Component;

@Component
public class HeadshotsCalculator {

    public float getForceByHeadshots(PlayerOnMapResults player) {
        // среднее количество хэдшотов = 8.1
        float normalizedHeadshots = (player.headshots - 8) * Config.normalizingCoeffHeadshots;
        if (normalizedHeadshots < 0) {
            return (float) (Config.fourCoeffFuncHeadshots * Math.pow(normalizedHeadshots, 4) +
                    Config.threeCoeffFuncHeadshots * Math.pow(normalizedHeadshots, 3) +
                    Config.twoCoeffFuncHeadshots * Math.pow(normalizedHeadshots, 2) +
                    Config.oneCoeffFuncHeadshots * normalizedHeadshots +
                    Config.zeroCoeffFuncHeadshots);
        } else {
            return (float) (-Config.fourCoeffFuncHeadshots * Math.pow(normalizedHeadshots, 4) +
                    Config.threeCoeffFuncHeadshots * Math.pow(normalizedHeadshots, 3) +
                    -Config.twoCoeffFuncHeadshots * Math.pow(normalizedHeadshots, 2) +
                    Config.oneCoeffFuncHeadshots * normalizedHeadshots +
                    Config.zeroCoeffFuncHeadshots);
        }
    }
}
