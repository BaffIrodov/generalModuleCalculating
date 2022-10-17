package com.gen.GeneralModuleCalculating.calculatingMethods;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Config {

    /**
     * Возможные кандидаты на роль используемых функций: lg(x+0.1) + 1 - для положительной оси
     * -lg(-x+0.1) - 1 - для отрицательной оси
     *
     * x^0.6 - для обеих осей
     */

    // global calculating settings
    public static int calculatingStatsIdNumber = 0; //Сколько забираем из базы последних игр. 0 - забираем всё
    public static int epochsNumber = 2; //количество эпох при расчете
    public static float adrMultiplier = 0.6f;
    public static float killsMultiplier = 0.8f;
    public static float headshotsMultiplier = 0.02f;
    public static float ratingMultiplier = 0.8f;
    public static float historyMultiplier = 3;
    public static float forceTeamMultiplier = 0.02f;
    public static boolean isConsiderStabilityCorrection = true;
    public static boolean isConsiderDifferenceCorrection = false;
    public static int highLimit = 300;
    public static int lowLimit = 5;
    public static float actualityMultiplier = 0.2f;
    public static float actualityConst = 0.9f;
    public static float compareMultiplier = 3f;
    public static float differencePercent = 0.1f;

    //адр
    public static float normalizingCoeffAdr = (float) (1.0/85); //нормировочный коэффициент
    public static float zeroCoeffFuncAdr = (float) (0); //коэффициент возле члена с нулевой степенью в полиноме
    public static float oneCoeffFuncAdr = (float) (5); //коэффициент возле члена с нулевой степенью в полиноме
    public static float twoCoeffFuncAdr = (float) (-6); //коэффициент возле члена с нулевой степенью в полиноме
    public static float threeCoeffFuncAdr = (float) (-1); //коэффициент возле члена с нулевой степенью в полиноме
    public static float fourCoeffFuncAdr = (float) (4); //коэффициент возле члена с нулевой степенью в полиноме

    //киллы
    public static float normalizingCoeffKills = (float) (1.0/33); //нормировочный коэффициент
    public static float zeroCoeffFuncKills = (float) (0); //коэффициент возле члена с нулевой степенью в полиноме
    public static float oneCoeffFuncKills = (float) (5); //коэффициент возле члена с нулевой степенью в полиноме
    public static float twoCoeffFuncKills = (float) (-6); //коэффициент возле члена с нулевой степенью в полиноме
    public static float threeCoeffFuncKills = (float) (-1); //коэффициент возле члена с нулевой степенью в полиноме
    public static float fourCoeffFuncKills = (float) (4); //коэффициент возле члена с нулевой степенью в полиноме
    public static float sixthCoeffFuncKills = (float) (-0.8); //шестая степень


    // хэдшоты
    public static float normalizingCoeffHeadshots = (float) (1.0/30); //нормировочный коэффициент
    public static float zeroCoeffFuncHeadshots = (float) (0); //коэффициент возле члена с нулевой степенью в полиноме
    public static float oneCoeffFuncHeadshots = (float) (5); //коэффициент возле члена с нулевой степенью в полиноме
    public static float twoCoeffFuncHeadshots = (float) (-6); //коэффициент возле члена с нулевой степенью в полиноме
    public static float threeCoeffFuncHeadshots = (float) (-1); //коэффициент возле члена с нулевой степенью в полиноме
    public static float fourCoeffFuncHeadshots = (float) (4); //коэффициент возле члена с нулевой степенью в полиноме

    // рейтинг20
    public static float normalizingCoeffRating20 = (float) (1.0/1.8); //нормировочный коэффициент
    public static float zeroCoeffFuncRating20 = (float) (0); //коэффициент возле члена с нулевой степенью в полиноме
    public static float oneCoeffFuncRating20 = (float) (5); //коэффициент возле члена с нулевой степенью в полиноме
    public static float twoCoeffFuncRating20 = (float) (-6); //коэффициент возле члена с нулевой степенью в полиноме
    public static float threeCoeffFuncRating20 = (float) (-1); //коэффициент возле члена с нулевой степенью в полиноме
    public static float fourCoeffFuncRating20 = (float) (4); //коэффициент возле члена с нулевой степенью в полиноме

    // history
    public static float normalizingCoeffHistory = (float) (1.0/20);

    // stability calculator
    public static float stabilityCompareCoeff = 1.1f;

    // active maps
    public static List<Integer> activeMaps = Arrays.asList(1,2,3,4,5,6,7);
    public static boolean isConsiderActiveMaps = true; // Если true - считаем силу игрока не только по этой карте, но и по всем, которые есть в ротации


    // player force table init
    public static int playerForceTableSize = 30000; //В какой-то момент этого станет недостаточно. На 210822 примерное кол-во --- 23к
    public static float playerForceDefault = 20; //Дефолтная сила игроков
    public static int playerStability = 100; //Дефолтная стабильность игроков
}
