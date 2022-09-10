package com.gen.GeneralModuleCalculating.calculatingMethods;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class RoundHistoryCalculator {

    public List<Float> getTeamForces(String roundSequence, Boolean leftTeamIsTerroristsInFirstHalf) {
        Float leftForce = 0f;
        Float rightForce = 0f;
        char[] chars = roundSequence.toCharArray();
        if(chars.length > 16) {
            List<Character> firstHalf = getFirstHalf(chars);
            List<Character> secondHalf = getSecondHalf(chars);
            if (leftTeamIsTerroristsInFirstHalf == null) leftTeamIsTerroristsInFirstHalf = true;
            List<Float> firstHalfRes = calculateFirstHalf(firstHalf, leftTeamIsTerroristsInFirstHalf);
            List<Float> secondHalfRes = calculateSecondHalf(secondHalf, leftTeamIsTerroristsInFirstHalf);
            leftForce = firstHalfRes.get(0) + secondHalfRes.get(0);
            rightForce = firstHalfRes.get(1) + secondHalfRes.get(1);
            float average = (leftForce + rightForce) / 2;
            leftForce = (leftForce - average) * Config.normalizingCoeffHistory;
            rightForce = (rightForce - average) * Config.normalizingCoeffHistory;
            return new ArrayList<>(Arrays.asList(leftForce, rightForce));
        } else { //сломанные матчи (проводят какие-то обрубки)
            return new ArrayList<>(Arrays.asList(0f, 0f));
        }
    }

    //TODO перенесен не до конца
    private List<Float> calculateFirstHalf(List<Character> firstHalf, Boolean leftIsTer) {
        Float leftForce = 0f;
        int leftWinStreak = 0;
        Float rightForce = 0f;
        int rightWinStreak = 0;
        if(firstHalf.get(0) == 'L') {
            leftForce+=5; leftWinStreak++;
        } else {
            rightForce+=5; rightWinStreak++;
        }
        for (int index = 1; index < 15; index++) {
            if(firstHalf.get(index) == 'L') {
                leftWinStreak++;
                if(!leftIsTer) { //левые ктшники и выиграли. В зависимости от разного винстрика правых начисляем баллы
                    leftForce = ctForceCalculate(leftWinStreak, leftForce);
                } else { //левые тшники и выиграли
                    leftForce = tForceCalculate(leftWinStreak, leftForce);
                }
                rightWinStreak = 0;
            } else {
                rightWinStreak++;
                if(!leftIsTer) { //левые ктшники и проиграли. В зависимости от разного винстрика левых начисляем баллы
                    rightForce = tForceCalculate(rightWinStreak, rightForce);
                } else { //левые тшники и проиграли
                    rightForce = ctForceCalculate(rightWinStreak, rightForce);
                }
                leftWinStreak = 0;
            }
        }
        return new ArrayList<>(Arrays.asList(leftForce, rightForce));
    }

    private List<Float> calculateSecondHalf(List<Character> secondHalf, Boolean leftIsTer) {
        Float leftForce = 0f;
        int leftWinStreak = 0;
        Float rightForce = 0f;
        int rightWinStreak = 0;
        if(secondHalf.get(0) == 'L') {
            leftForce+=5; leftWinStreak ++;
        } else {
            rightForce+=5; rightWinStreak++;
        }
        for (int index = 1; index < secondHalf.size(); index++) {
            if(secondHalf.get(index) == 'L') {
                leftWinStreak++;
                if(leftIsTer) { //левые ктшники (были террами в 1ой половине) и выиграли. В зависимости от разного винстрика правых начисляем баллы
                    leftForce = ctForceCalculate(leftWinStreak, leftForce);
                } else { //левые тшники (не были террами в 1ой половине) и выиграли
                    leftForce = tForceCalculate(leftWinStreak, leftForce);
                }
                rightWinStreak = 0;
            } else {
                rightWinStreak++;
                if(leftIsTer) { //левые ктшники (были террами в 1ой половине) и проиграли. В зависимости от разного винстрика левых начисляем баллы
                    rightForce = tForceCalculate(rightWinStreak, rightForce);
                } else { //левые тшники (не были террами в 1ой половине) и проиграли
                    rightForce = ctForceCalculate(rightWinStreak, rightForce);
                }
                leftWinStreak = 0;
            }
        }
        return new ArrayList<>(Arrays.asList(leftForce, rightForce));
    }

    private Float ctForceCalculate(int ourWinStreak, Float teamForce) {
        switch (ourWinStreak) {
            case 1 -> teamForce+=3; //сразу после проигрыша, тяжело взять раунд - у кт экономика проседает, у терров - поменьше
            case 2 -> teamForce+=4; //даже если были деньги после первого поражения, после второго их сильно меньше
            case 3 -> teamForce+=2; //после двух поражений уже нормально сыпят денег - у кт появляются девайсы
            case 4 -> teamForce+=1.5f; //денег всё ещё больше. Тут может быть и форс, но не так часто
            case 5 -> teamForce+=2; //чуть добавляем за давление на мораль - они ещё не летят, но появляются звоночки, что это проигрыш
            default -> teamForce+=1.5f; //5+ раундов - деньги отследить тяжело, но если 5 подряд проиграли, то много очков не заслуживают
        }
        return teamForce;
    }

    private Float tForceCalculate(int ourWinStreak, Float teamForce) {
        switch (ourWinStreak) {
            case 1 -> teamForce+=3; //сразу после проигрыша, тяжело взять раунд - у т экономика тоже проседает, пусть и меньше
            case 2 -> teamForce+=3; //террам проще сделать форс, плюс они могли поставить бомбу или выбить много девайсов у кт (дорогих!)
            case 3 -> teamForce+=1.5f; //закуп терров дешевле
            case 4 -> teamForce+=1.5f; //закуп терров дешевле
            case 5 -> teamForce+=2; //чуть добавляем за давление на мораль - они ещё не летят, но появляются звоночки, что это проигрыш
            default -> teamForce+=1.5f; //5+ раундов - деньги отследить тяжело, но если 5 подряд проиграли, то много очков не заслуживают
        }
        return teamForce;
    }

    private List<Character> getFirstHalf(char[] chars) {
        List<Character> result = new ArrayList<>();
        for(int i = 0; i < 15; i++) {
            result.add(chars[i]);
        }
        return result;
    }

    private List<Character> getSecondHalf(char[] chars) {
        List<Character> result = new ArrayList<>();
        for(int i = 15; i < chars.length; i++) {
            result.add(chars[i]);
        }
        return result;
    }


}
