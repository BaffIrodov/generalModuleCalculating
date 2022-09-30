package com.gen.GeneralModuleCalculating.controllers;

import com.gen.GeneralModuleCalculating.entities.QImprovementResults;
import com.gen.GeneralModuleCalculating.entities.QMapsCalculatingQueue;
import com.gen.GeneralModuleCalculating.entities.QPlayerForce;
import com.gen.GeneralModuleCalculating.repositories.ImprovementResultsRepository;
import com.gen.GeneralModuleCalculating.repositories.MapsCalculatingQueueRepository;
import com.gen.GeneralModuleCalculating.repositories.PlayerForceRepository;
import com.gen.GeneralModuleCalculating.services.DebugService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/debug")
@Log4j2
public class DebugController {
    @Autowired
    DebugService debugService;

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    ImprovementResultsRepository improvementResultsRepository;

    @Autowired
    MapsCalculatingQueueRepository mapsCalculatingQueueRepository;

    @Autowired
    PlayerForceRepository playerForceRepository;

    private static final QImprovementResults improvementResults = new QImprovementResults("improvementResults");
    private static final QMapsCalculatingQueue mapsCalculatingQueue = new QMapsCalculatingQueue("mapsCalculatingQueue");
    private static final QPlayerForce playerForce = new QPlayerForce("playerForce");


    @GetMapping("/files-distribution")
    public void filesWithDistribution() {
        debugService.getFilesWithDistribution();
    }

    @GetMapping("/debug")
    public void debug() {
//        calculatingService.debug();
    }

    @GetMapping("/reset-players-forces")
    public void resetPlayersForces() {
        debugService.resetAllPlayerForcesToDefault();
    }

    @GetMapping("/clear-players-forces")
    public void clearPlayersForces() {
        debugService.clearTablePlayersForces();
    }

    @GetMapping("/enabled")
    public Boolean thisServiceEnabled() {
        return true;
    }

    @GetMapping("/improvement-results-exist")
    public Boolean improvementResultsExist() {
        try {
            queryFactory.from(improvementResults).select(improvementResults.id).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @Transactional
    @GetMapping("/create-improvement-results")
    public void createPlayerOnMapResultsTable() {
        improvementResultsRepository.createImprovementResultsTable();
    }

    @GetMapping("/maps-calculating-queue-exist")
    public Boolean mapsCalculatingQueueExist() {
        try {
            queryFactory.from(mapsCalculatingQueue).select(mapsCalculatingQueue.idStatsMap).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @GetMapping("/maps-calculating-queue-filled")
    public Boolean mapsCalculatingQueueFilled() {
        Integer tableSize = 0;
        try {
            tableSize = queryFactory.from(mapsCalculatingQueue).select(mapsCalculatingQueue.idStatsMap).fetch().size();
        } catch (Exception exception) {
            return false;
        }
        return tableSize != 0;
    }

    @Transactional
    @GetMapping("/create-maps-calculating-queue")
    public void createMapsCalculatingQueueTable() {
        mapsCalculatingQueueRepository.createMapsCalculatingQueueTable();
    }

    @GetMapping("/player-force-exist")
    public Boolean playerForceExist() {
        try {
            queryFactory.from(playerForce).select(playerForce.id).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @GetMapping("/player-force-filled")
    public Boolean playerForceFilled() {
        Integer tableSize = 0;
        try {
            tableSize = queryFactory.from(playerForce).select(playerForce.id).fetch().size();
        } catch (Exception exception) {
            return false;
        }
        return tableSize != 0;
    }

    @Transactional
    @GetMapping("/create-player-force")
    public void createPlayerForceTable() {
        playerForceRepository.createPlayerForceTable();;
    }
}
