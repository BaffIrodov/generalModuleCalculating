package com.gen.GeneralModuleCalculating.calculatingMethods;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PressureCalculator {
    public List<Float> getPressure(String roundSequence) { //определяем моральное напряжение. Отстающие, но выигравшие - более сильные игроки
        char[] chars = roundSequence.toCharArray();
        Float leftScore = 0f;
        Float leftPressure = 0f;
        Float rightScore = 0f;
        Float rightPressure = 0f;
        for (int i = 0; i < chars.length; i++) {
            char round = chars[i];
            if (round == 'L') {
                rightScore += 1f;
            } else {
                leftScore += 1f;
            }
            leftPressure += rightScore - leftScore;
            rightPressure += leftScore - rightScore;
        }
        List<Float> result = new ArrayList<>();
        result.add(leftPressure);
        result.add(rightPressure);
        return result;
    }
}
