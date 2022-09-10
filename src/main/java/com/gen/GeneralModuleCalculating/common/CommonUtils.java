package com.gen.GeneralModuleCalculating.common;

import com.gen.GeneralModuleCalculating.calculatingMethods.Config;
import com.gen.GeneralModuleCalculating.services.ErrorsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
//This comment
public class CommonUtils {
    static ErrorsService errorsService = new ErrorsService();

    public static void waiter(int timeoutInMS){
        try {
            TimeUnit.MILLISECONDS.sleep(timeoutInMS);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

        public static String standardIdParsingBySlice(String strBeforeId, String processedString) {
        return (processedString.replaceAll(".*" + strBeforeId, "").replaceAll("/.*", ""));
    }

    public static String standardIdParsingByPlace(Integer idPosition, String processedString) {
        String[] splittedString = processedString.split("/");
        return splittedString[idPosition];
    }

    public static void resetConfig(Map<String, Object> config) {
        Config.isConsiderActiveMaps = (boolean) config.get("isConsiderActiveMaps");
        Config.epochsNumber = (int) config.get("epochsNumber");
        Config.calculatingStatsIdNumber = (int) config.get("calculatingStatsIdNumber");
        Config.adrMultiplier = (float) config.get("adrMultiplier");
        Config.killsMultiplier = (float) config.get("killsMultiplier");
        Config.headshotsMultiplier = (float) config.get("headshotsMultiplier");
        Config.ratingMultiplier = (float) config.get("ratingMultiplier");
        Config.historyMultiplier = (float) config.get("historyMultiplier") ;
        Config.forceTeamMultiplier = (float) config.get("forceTeamMultiplier");
        Config.normalizingCoeffAdr = (float) config.get("normalizingCoeffAdr");
        Config.zeroCoeffFuncAdr = (float) config.get("zeroCoeffFuncAdr");
        Config.oneCoeffFuncAdr = (float) config.get("oneCoeffFuncAdr");
        Config.twoCoeffFuncAdr = (float) config.get("twoCoeffFuncAdr");
        Config.threeCoeffFuncAdr = (float) config.get("threeCoeffFuncAdr");
        Config.fourCoeffFuncAdr = (float) config.get("fourCoeffFuncAdr");
        Config.normalizingCoeffKills = (float) config.get("normalizingCoeffKills");
        Config.zeroCoeffFuncKills = (float) config.get("zeroCoeffFuncKills");
        Config.oneCoeffFuncKills = (float) config.get("oneCoeffFuncKills");
        Config.twoCoeffFuncKills = (float) config.get("twoCoeffFuncKills");
        Config.threeCoeffFuncKills = (float) config.get("threeCoeffFuncKills");
        Config.fourCoeffFuncKills = (float) config.get("fourCoeffFuncKills");
        Config.sixthCoeffFuncKills = (float) config.get("sixthCoeffFuncKills");
        Config.normalizingCoeffHeadshots = (float) config.get("normalizingCoeffHeadshots");
        Config.zeroCoeffFuncHeadshots = (float) config.get("zeroCoeffFuncHeadshots");
        Config.oneCoeffFuncHeadshots =  (float) config.get("oneCoeffFuncHeadshots");
        Config.twoCoeffFuncHeadshots =  (float) config.get("twoCoeffFuncHeadshots");
        Config.threeCoeffFuncHeadshots =  (float) config.get("threeCoeffFuncHeadshots");
        Config.fourCoeffFuncHeadshots =  (float) config.get("fourCoeffFuncHeadshots");
        Config.normalizingCoeffRating20 = (float) config.get("normalizingCoeffRating20");
        Config.zeroCoeffFuncRating20 = (float) config.get("zeroCoeffFuncRating20");
        Config.oneCoeffFuncRating20 = (float) config.get("oneCoeffFuncRating20");
        Config.twoCoeffFuncRating20 = (float) config.get("twoCoeffFuncRating20");
        Config.threeCoeffFuncRating20 = (float) config.get("threeCoeffFuncRating20");
        Config.fourCoeffFuncRating20 = (float) config.get("fourCoeffFuncRating20");
        Config.normalizingCoeffHistory = (float) config.get("normalizingCoeffHistory");
        Config.stabilityCompareCoeff = (float) config.get("stabilityCompareCoeff");
        Config.isConsiderActiveMaps = (boolean) config.get("isConsiderActiveMaps");
        Config.playerForceTableSize = (int) config.get("playerForceTableSize");
        Config.playerForceDefault = (float) config.get("playerForceDefault");
        Config.playerStability = (int) config.get("playerStability");
    }

    public static Map<String, Object> invokeConfig() {
        Map<String, Object> mapValueByFieldName = new HashMap<>();
        mapValueByFieldName.put("isConsiderActiveMaps", Config.isConsiderActiveMaps);
        mapValueByFieldName.put("epochsNumber", Config.epochsNumber);
        mapValueByFieldName.put("calculatingStatsIdNumber", Config.calculatingStatsIdNumber);
        mapValueByFieldName.put("adrMultiplier", Config.adrMultiplier);
        mapValueByFieldName.put("killsMultiplier", Config.killsMultiplier);
        mapValueByFieldName.put("headshotsMultiplier", Config.headshotsMultiplier);
        mapValueByFieldName.put("ratingMultiplier", Config.ratingMultiplier);
        mapValueByFieldName.put("historyMultiplier", Config.historyMultiplier) ;
        mapValueByFieldName.put("forceTeamMultiplier", Config.forceTeamMultiplier);
        mapValueByFieldName.put("normalizingCoeffAdr", Config.normalizingCoeffAdr);
        mapValueByFieldName.put("zeroCoeffFuncAdr", Config.zeroCoeffFuncAdr);
        mapValueByFieldName.put("oneCoeffFuncAdr", Config.oneCoeffFuncAdr);
        mapValueByFieldName.put("twoCoeffFuncAdr", Config.twoCoeffFuncAdr);
        mapValueByFieldName.put("threeCoeffFuncAdr", Config.threeCoeffFuncAdr);
        mapValueByFieldName.put("fourCoeffFuncAdr", Config.fourCoeffFuncAdr);
        mapValueByFieldName.put("normalizingCoeffKills", Config.normalizingCoeffKills);
        mapValueByFieldName.put("zeroCoeffFuncKills", Config.zeroCoeffFuncKills);
        mapValueByFieldName.put("oneCoeffFuncKills", Config.oneCoeffFuncKills);
        mapValueByFieldName.put("twoCoeffFuncKills", Config.twoCoeffFuncKills);
        mapValueByFieldName.put("threeCoeffFuncKills", Config.threeCoeffFuncKills);
        mapValueByFieldName.put("fourCoeffFuncKills", Config.fourCoeffFuncKills);
        mapValueByFieldName.put("sixthCoeffFuncKills", Config.sixthCoeffFuncKills);
        mapValueByFieldName.put("normalizingCoeffHeadshots", Config.normalizingCoeffHeadshots);
        mapValueByFieldName.put("zeroCoeffFuncHeadshots", Config.zeroCoeffFuncHeadshots);
        mapValueByFieldName.put( "oneCoeffFuncHeadshots", Config.oneCoeffFuncHeadshots);
        mapValueByFieldName.put( "twoCoeffFuncHeadshots", Config.twoCoeffFuncHeadshots);
        mapValueByFieldName.put( "threeCoeffFuncHeadshots", Config.threeCoeffFuncHeadshots);
        mapValueByFieldName.put( "fourCoeffFuncHeadshots", Config.fourCoeffFuncHeadshots);
        mapValueByFieldName.put("normalizingCoeffRating20", Config.normalizingCoeffRating20);
        mapValueByFieldName.put("zeroCoeffFuncRating20", Config.zeroCoeffFuncRating20);
        mapValueByFieldName.put("oneCoeffFuncRating20", Config.oneCoeffFuncRating20);
        mapValueByFieldName.put("twoCoeffFuncRating20", Config.twoCoeffFuncRating20);
        mapValueByFieldName.put("threeCoeffFuncRating20", Config.threeCoeffFuncRating20);
        mapValueByFieldName.put("fourCoeffFuncRating20", Config.fourCoeffFuncRating20);
        mapValueByFieldName.put("normalizingCoeffHistory", Config.normalizingCoeffHistory);
        mapValueByFieldName.put("stabilityCompareCoeff", Config.stabilityCompareCoeff);
        mapValueByFieldName.put("playerForceTableSize", Config.playerForceTableSize);
        mapValueByFieldName.put("playerForceDefault", Config.playerForceDefault);
        mapValueByFieldName.put("playerStability", Config.playerForceDefault);
        return mapValueByFieldName;
    }
}
