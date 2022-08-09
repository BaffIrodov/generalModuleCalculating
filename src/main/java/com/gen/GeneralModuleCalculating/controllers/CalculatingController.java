package com.gen.GeneralModuleCalculating.controllers;

import com.gen.GeneralModuleCalculating.dtos.MapsCalculatingQueueResponseDto;
import com.gen.GeneralModuleCalculating.entities.Errors;
import com.gen.GeneralModuleCalculating.services.CalculatingService;
import com.gen.GeneralModuleCalculating.services.DebugService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/calculating")
@Log4j2
public class CalculatingController {

    @Autowired
    CalculatingService calculatingService;

    @Autowired
    DebugService debugService;

    @GetMapping("/create-queue")
    public MapsCalculatingQueueResponseDto createQueue() {
        return calculatingService.createQueue();
    }

    @GetMapping("/current-queue-size")
    public MapsCalculatingQueueResponseDto getCurrentQueueSize(){
        return calculatingService.getCurrentQueueSize();
    }

    @GetMapping("/debug")
    public void debug(){
        debugService.init();
//        calculatingService.debug();
    }

}
