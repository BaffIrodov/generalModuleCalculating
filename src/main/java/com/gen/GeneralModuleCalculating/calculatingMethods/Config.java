package com.gen.GeneralModuleCalculating.calculatingMethods;

public class Config {

    /**
     * Возможные кандидаты на роль используемых функций: lg(x+0.1) + 1 - для положительной оси
     * -lg(-x+0.1) - 1 - для отрицательной оси
     *
     * x^0.6 - для обеих осей
     */

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
}
