package com.gen.GeneralModuleCalculating.calculatingMethods;

import org.springframework.stereotype.Component;
import com.gen.GeneralModuleCalculating.entities.*;

@Component
public class AdrCalculator {
    public float oldGetForceByAdr(PlayerOnMapResults player) {
        //средний адр - 74 единицы. Берем результат игрока на карте, вычисляем - плохо ли отыграна карта
        //делим на 80 - легаси. Нормализация стремится закинуть любой реальный адр в пределы от 0 до 2
        float normalizedAdr = (player.adr - 74) / 80;
        //5x^4 - x^3 - 6x^2 + 5x    ----> потом делится на такой же x
        float result = (float) (5 * Math.pow(normalizedAdr, 4) - 2 * Math.pow(normalizedAdr, 3)
                - 6 * Math.pow(normalizedAdr, 3) + 5 * normalizedAdr);
        result = result / normalizedAdr; //странное условие, которое режет смысл функции
        return result;
    }

    public float getForceByAdr(PlayerOnMapResults player) {
        //средний адр - 74 единицы. Берем результат игрока на карте, вычисляем - плохо ли отыграна карта
        /**
         * делим на 85 - иногда происходит выброс статистики, и игроки набивают 160+ адр. Для них чуть сдвинули,
         * однако они всё равно выбиваются из статистики - забиваем на это.
         * Случается достаточно редко, вряд ли вносит большую ошибку.
         *
         * Формула стала 4x^4 - x^3 - 6x^2 + 5x - лучше выглядит плато хороших игроков - более протяженное
         *
         * Для отрицательных результатов делаем инверсию относительно оси x (y = -f(x))
         * И относительно оси y (y = f(-x)). То есть меняем знаки у четных степеней
         * Получается формула -4x^4 - x^3 + 6x^2 + 5x
         */
        float normalizedAdr = (player.adr - 74) * Config.normalizingCoeffAdr;
        if(normalizedAdr < 0) {
            return  (float) (Config.fourCoeffFuncAdr * Math.pow(normalizedAdr, 4) +
                    Config.threeCoeffFuncAdr * Math.pow(normalizedAdr, 3) +
                    Config.twoCoeffFuncAdr * Math.pow(normalizedAdr, 2) +
                    Config.oneCoeffFuncAdr * normalizedAdr +
                    Config.zeroCoeffFuncAdr);
        } else {
            return  (float) (-Config.fourCoeffFuncAdr * Math.pow(normalizedAdr, 4) +
                            Config.threeCoeffFuncAdr * Math.pow(normalizedAdr, 3) +
                            -Config.twoCoeffFuncAdr * Math.pow(normalizedAdr, 2) +
                            Config.oneCoeffFuncAdr * normalizedAdr +
                            Config.zeroCoeffFuncAdr);
        }
    }
}
