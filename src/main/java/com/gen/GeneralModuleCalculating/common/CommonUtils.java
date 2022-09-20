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

    public String helpInt(Map<String, Object> config, String name) {
        return (config.get(name).toString().contains(".")?
                config.get(name).toString().replaceAll("[^.]*$", "").replace(".", ""):
                config.get(name).toString());
    }

    public void resetConfig(Map<String, Object> config) {
        Config.isConsiderActiveMaps = Boolean.getBoolean(config.get("isConsiderActiveMaps").toString());
        Config.epochsNumber = Integer.parseInt(this.helpInt(config, "epochsNumber"));
        Config.calculatingStatsIdNumber = Integer.parseInt(this.helpInt(config, "calculatingStatsIdNumber"));
        Config.adrMultiplier = Float.parseFloat(config.get("adrMultiplier").toString());
        Config.killsMultiplier = Float.parseFloat(config.get("killsMultiplier").toString());
        Config.headshotsMultiplier = Float.parseFloat(config.get("headshotsMultiplier").toString());
        Config.ratingMultiplier = Float.parseFloat(config.get("ratingMultiplier").toString());
        Config.historyMultiplier = Float.parseFloat(config.get("historyMultiplier").toString());
        Config.forceTeamMultiplier = Float.parseFloat(config.get("forceTeamMultiplier").toString());
        Config.lowLimit = Integer.parseInt(config.get("lowLimit").toString());
        Config.highLimit = Integer.parseInt(config.get("highLimit").toString());
        Config.actualityMultiplier = Float.parseFloat(config.get("actualityMultiplier").toString());
        Config.actualityConst = Float.parseFloat(config.get("actualityConst").toString());
        Config.normalizingCoeffAdr = Float.parseFloat(config.get("normalizingCoeffAdr").toString());
        Config.zeroCoeffFuncAdr = Float.parseFloat(config.get("zeroCoeffFuncAdr").toString());
        Config.oneCoeffFuncAdr = Float.parseFloat(config.get("oneCoeffFuncAdr").toString());
        Config.twoCoeffFuncAdr = Float.parseFloat(config.get("twoCoeffFuncAdr").toString());
        Config.threeCoeffFuncAdr = Float.parseFloat(config.get("threeCoeffFuncAdr").toString());
        Config.fourCoeffFuncAdr = Float.parseFloat(config.get("fourCoeffFuncAdr").toString());
        Config.normalizingCoeffKills = Float.parseFloat(config.get("normalizingCoeffKills").toString());
        Config.zeroCoeffFuncKills = Float.parseFloat(config.get("zeroCoeffFuncKills").toString());
        Config.oneCoeffFuncKills = Float.parseFloat(config.get("oneCoeffFuncKills").toString());
        Config.twoCoeffFuncKills = Float.parseFloat(config.get("twoCoeffFuncKills").toString());
        Config.threeCoeffFuncKills = Float.parseFloat(config.get("threeCoeffFuncKills").toString());
        Config.fourCoeffFuncKills = Float.parseFloat(config.get("fourCoeffFuncKills").toString());
        Config.sixthCoeffFuncKills = Float.parseFloat(config.get("sixthCoeffFuncKills").toString());
        Config.normalizingCoeffHeadshots = Float.parseFloat(config.get("normalizingCoeffHeadshots").toString());
        Config.zeroCoeffFuncHeadshots = Float.parseFloat(config.get("zeroCoeffFuncHeadshots").toString());
        Config.oneCoeffFuncHeadshots =  Float.parseFloat(config.get("oneCoeffFuncHeadshots").toString());
        Config.twoCoeffFuncHeadshots =  Float.parseFloat(config.get("twoCoeffFuncHeadshots").toString());
        Config.threeCoeffFuncHeadshots =  Float.parseFloat(config.get("threeCoeffFuncHeadshots").toString());
        Config.fourCoeffFuncHeadshots =  Float.parseFloat(config.get("fourCoeffFuncHeadshots").toString());
        Config.normalizingCoeffRating20 = Float.parseFloat(config.get("normalizingCoeffRating20").toString());
        Config.zeroCoeffFuncRating20 = Float.parseFloat(config.get("zeroCoeffFuncRating20").toString());
        Config.oneCoeffFuncRating20 = Float.parseFloat(config.get("oneCoeffFuncRating20").toString());
        Config.twoCoeffFuncRating20 = Float.parseFloat(config.get("twoCoeffFuncRating20").toString());
        Config.threeCoeffFuncRating20 = Float.parseFloat(config.get("threeCoeffFuncRating20").toString());
        Config.fourCoeffFuncRating20 = Float.parseFloat(config.get("fourCoeffFuncRating20").toString());
        Config.normalizingCoeffHistory = Float.parseFloat(config.get("normalizingCoeffHistory").toString());
        Config.stabilityCompareCoeff = Float.parseFloat(config.get("stabilityCompareCoeff").toString());
        Config.isConsiderActiveMaps = Boolean.getBoolean(config.get("isConsiderActiveMaps").toString());
        Config.isConsiderStabilityCorrection = Boolean.getBoolean(config.get("isConsiderStabilityCorrection").toString());
        Config.playerForceTableSize = Integer.parseInt(this.helpInt(config, "playerForceTableSize"));
        Config.playerForceDefault = Float.parseFloat(config.get("playerForceDefault").toString());
        Config.playerStability = Integer.parseInt(this.helpInt(config, "playerStability"));
    }

    public static Map<String, Object> invokeConfig() {
        Map<String, Object> mapValueByFieldName = new LinkedHashMap<>();
        mapValueByFieldName.put("isConsiderActiveMaps", Config.isConsiderActiveMaps);
        mapValueByFieldName.put("isConsiderStabilityCorrection", Config.isConsiderStabilityCorrection);
        mapValueByFieldName.put("epochsNumber", Config.epochsNumber);
        mapValueByFieldName.put("highLimit", Config.highLimit);
        mapValueByFieldName.put("lowLimit", Config.lowLimit);
        mapValueByFieldName.put("actualityMultiplier", Config.actualityMultiplier);
        mapValueByFieldName.put("actualityConst", Config.actualityConst);
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
        mapValueByFieldName.put("oneCoeffFuncHeadshots", Config.oneCoeffFuncHeadshots);
        mapValueByFieldName.put("twoCoeffFuncHeadshots", Config.twoCoeffFuncHeadshots);
        mapValueByFieldName.put("threeCoeffFuncHeadshots", Config.threeCoeffFuncHeadshots);
        mapValueByFieldName.put("fourCoeffFuncHeadshots", Config.fourCoeffFuncHeadshots);
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
        mapValueByFieldName.put("playerStability", Config.playerStability);
        return mapValueByFieldName;
    }
}
