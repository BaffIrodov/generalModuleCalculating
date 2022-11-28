package com.gen.GeneralModuleCalculating.controllers;

import com.gen.GeneralModuleCalculating.calculatingMethods.Config;
import com.gen.GeneralModuleCalculating.common.CommonUtils;
import com.gen.GeneralModuleCalculating.dtos.ImprovementRequestDto;
import com.gen.GeneralModuleCalculating.dtos.ImprovementResultsDto;
import com.gen.GeneralModuleCalculating.dtos.PatternTemplateNumber;
import com.gen.GeneralModuleCalculating.entities.ImprovementResults;
import com.gen.GeneralModuleCalculating.services.ImprovementService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/improvement")
@Log4j2
public class ImprovementController {
    @Autowired
    ImprovementService improvementService;

    CommonUtils commonUtils = new CommonUtils();

    @GetMapping("/get-config")
    public Map<String, Object> improvementGetConfig() {
        return CommonUtils.invokeConfig();
    }

    @PostMapping("/inactive-percent")
    public void improvementInactivePercent(@RequestBody ImprovementRequestDto request) {
        improvementService.improvementByInactivePercent(request);
    }

    @PostMapping("/no-config")
    public void improvementNoConfig(@RequestBody ImprovementRequestDto request) {
        improvementService.improvementTest(request);
    }

    @PostMapping("/shuffling")
    public void improvementShuffling(@RequestBody ImprovementRequestDto request) {
        while (true) {
            commonUtils.randomShuffleConfig();
            improvementService.improvementTest(request);
        }
    }

    @PostMapping("/consensus")
    public void improvementConsensus(@RequestBody ImprovementRequestDto request) {
        Integer inactiveMultiplier = 5;
        Integer inactiveCount = request.getTestDatasetCount() * inactiveMultiplier;
        for (int i = 0; i <= inactiveCount; i += request.testDatasetCount) {
            request.setInactiveDatasetCount(inactiveCount - i);
            Config.calculatingStatsIdNumber = 0;
            Config.epochsNumber = 1;
            Config.compareMultiplier = 3f;
            Map<Integer, Boolean> mapOne = improvementService.improvementConsensus(request);

//        НУЖНО СЛОЖИТЬ У ДВУХ МЕТОДОВ СИЛЫ ИГРОКОВ ВМЕСТЕ (либо просто, либо с делением на комп коэф)

            //мало, но высокий процент 80%+
            Config.calculatingStatsIdNumber = 8000;
            Config.epochsNumber = 4;
            Config.compareMultiplier = 5f;

            //мало, но высокий процент 80%+ - старый расчет, без компрессии
//        Config.isPlayerForceCompressingOutsideEpoch = false;
//        Config.compareMultiplier = 1.5f;
//        Config.isCorrectLowLimit = false;
//        Config.isCorrectLowAndHighLimit = true;

            //много, процент 74,6%, полустарый расчет
//        Config.epochsNumber = 2;
//        Config.adrMultiplier = 0.6f;
//        Config.killsMultiplier = 0.8f;
//        Config.headshotsMultiplier = 0.02f;
//        Config.ratingMultiplier = 0.8f;
//        Config.historyMultiplier = 3;
//        Config.forceTeamMultiplier = 0.02f;
//        Config.actualityMultiplier = 0.2f;
//        Config.actualityConst = 0.9f;
//        Config.compareMultiplier = 3f;
//        Config.compareSummand = 200f;
//        Config.differencePercent = 0.1f;
//        Config.highLimit = 300;
//        Config.lowLimit = 5;
//        Config.winStrikeMultiplier = 0.1f;
//        Config.loseStrikeMultiplier = 0.1f;

            Map<Integer, Boolean> mapTwo = improvementService.improvementConsensus(request);
            Map<Integer, Boolean> mapThree = new HashMap<>();
            List<Integer> one = mapOne.keySet().stream().toList();
            List<Integer> two = mapTwo.keySet().stream().toList();
            Set<Integer> three = new HashSet<>();
            three.addAll(one);
            three.addAll(two);
            AtomicInteger together = new AtomicInteger();
            AtomicInteger notTogether = new AtomicInteger();
            one.forEach(e -> {
                if (mapTwo.get(e) != null) {
                    together.getAndIncrement();
                } else {
                    notTogether.getAndIncrement();
                }
            });

            mapThree.putAll(mapOne);
            //объединяем, затирая значения; Второе должно быть заведомо более точным!!
            mapThree.putAll(mapTwo);
            System.out.println("Общее количество 1 часть: " + mapOne.size() + " из " + request.getTestDatasetCount() + "| в процентах: " +
                    (float) mapOne.size() / request.getTestDatasetCount() + " %; Точность: " +
                    (float) mapOne.values().stream().filter(e -> e.equals(true)).toList().size() / mapOne.size() + "%");
            System.out.println("Общее количество 2 часть: " + mapTwo.size() + " из " + request.getTestDatasetCount() + "| в процентах: " +
                    (float) mapTwo.size() / request.getTestDatasetCount() + " %; Точность: " +
                    (float) mapTwo.values().stream().filter(e -> e.equals(true)).toList().size() / mapTwo.size() + "%");
            System.out.println("Общее количество Консенсус: " + mapThree.size() + " из " + request.getTestDatasetCount() + "| в процентах: " +
                    (float) mapThree.size() / request.getTestDatasetCount() + " %; Точность: " +
                    (float) mapThree.values().stream().filter(e -> e.equals(true)).toList().size() / mapThree.size() + "%");
        }
    }

    @PostMapping("/with-config")
    public void improvementWithConfig(@RequestBody ImprovementRequestDto request) {
        commonUtils.resetConfig(request.getConfig());
        improvementService.improvementTest(request);
    }

    @PostMapping("/with-config-and-pattern")
    public void improvementWithConfig(@RequestBody PatternTemplateNumber patternTemplateNumber) {
        Map<String, Object> initConfig = patternTemplateNumber.improvementRequest.getConfig();
        int maxIndex = (int) ((patternTemplateNumber.highLimit - patternTemplateNumber.lowLimit) / patternTemplateNumber.incrementStep);
        for (int i = 0; i < maxIndex + 1; i++) {
            float value = patternTemplateNumber.lowLimit + (patternTemplateNumber.incrementStep * i);
            initConfig.put(patternTemplateNumber.configPropertyName, value);
            commonUtils.resetConfig(patternTemplateNumber.improvementRequest.getConfig());
            improvementService.improvementTest(patternTemplateNumber.improvementRequest);
        }
    }

    @GetMapping("/get-improvement-results")
    public List<ImprovementResultsDto> getImprovementResults() {
        List<ImprovementResultsDto> improvementResultsDtoList = new ArrayList<>();
        List<ImprovementResults> results = improvementService.getImprovementResultsFromDB();
        results.forEach(res -> {
            ImprovementResultsDto improvementResultsDto = new ImprovementResultsDto();
            improvementResultsDto.accuracy = res.accuracy;
            improvementResultsDto.current_epoch = res.currentEpoch;
            improvementResultsDto.right_count = res.rightCount;
            improvementResultsDto.all_count = res.allCount;
            improvementResultsDto.full_config = Arrays.stream(res.fullConfig.
                    substring(1, res.fullConfig.length() - 1).split(", ")).toList();
            improvementResultsDtoList.add(improvementResultsDto);
        });
        return improvementResultsDtoList;
    }
}
