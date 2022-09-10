package com.gen.GeneralModuleCalculating.controllers;

import com.gen.GeneralModuleCalculating.calculatingMethods.Config;
import com.gen.GeneralModuleCalculating.common.CommonUtils;
import com.gen.GeneralModuleCalculating.dtos.ImprovementRequestDto;
import com.gen.GeneralModuleCalculating.dtos.MapsCalculatingQueueResponseDto;
import com.gen.GeneralModuleCalculating.services.CalculatingService;
import com.gen.GeneralModuleCalculating.services.DebugService;
import com.gen.GeneralModuleCalculating.services.ImprovementService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/improvement")
@Log4j2
public class ImprovementController {
    @Autowired
    ImprovementService improvementService;

    @GetMapping("/get-config")
    public Map<String, Object> improvementGetConfig() {
        return CommonUtils.invokeConfig();
    }

    @PostMapping("/no-config")
    public void improvementNoConfig(@RequestBody ImprovementRequestDto request) {
        improvementService.improvementTest(request);
    }

    @PostMapping("/with-config")
    public void improvementWithConfig(@RequestBody ImprovementRequestDto request) {
        CommonUtils.resetConfig(request.getConfig());
        improvementService.improvementTest(request);
    }

}
