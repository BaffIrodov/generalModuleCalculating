package com.gen.GeneralModuleCalculating.controllers;

import com.gen.GeneralModuleCalculating.calculatingMethods.Config;
import com.gen.GeneralModuleCalculating.common.CommonUtils;
import com.gen.GeneralModuleCalculating.dtos.ImprovementRequestDto;
import com.gen.GeneralModuleCalculating.dtos.ImprovementResultsDto;
import com.gen.GeneralModuleCalculating.dtos.MapsCalculatingQueueResponseDto;
import com.gen.GeneralModuleCalculating.dtos.PatternTemplateNumber;
import com.gen.GeneralModuleCalculating.entities.ImprovementResults;
import com.gen.GeneralModuleCalculating.services.CalculatingService;
import com.gen.GeneralModuleCalculating.services.DebugService;
import com.gen.GeneralModuleCalculating.services.ImprovementService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public void improvementInactivePercent(@RequestBody ImprovementRequestDto request) throws IOException, InterruptedException {
        improvementService.improvementByInactivePercent(request);
    }

    @PostMapping("/no-config")
    public void improvementNoConfig(@RequestBody ImprovementRequestDto request) {
        improvementService.improvementTest(request);
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
                    substring(1, res.fullConfig.length()-1).split(", ")).toList();
            improvementResultsDtoList.add(improvementResultsDto);
        });
        return improvementResultsDtoList;
    }
}
