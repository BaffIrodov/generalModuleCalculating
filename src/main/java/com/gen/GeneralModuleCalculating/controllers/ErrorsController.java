package com.gen.GeneralModuleCalculating.controllers;

import com.gen.GeneralModuleCalculating.entities.Errors;
import com.gen.GeneralModuleCalculating.services.ErrorsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/errors")
@Log4j2
public class ErrorsController {
    @Autowired
    ErrorsService errorsService;

    @GetMapping("/search-all")
    public List<Errors> getAllErrors() {
        return errorsService.getAllErrors();
    }

    @GetMapping("/search-not-verified")
    public List<Errors> getNotVerifiedErrors() {
        return errorsService.getNotVerifiedErrors();
    }
}
