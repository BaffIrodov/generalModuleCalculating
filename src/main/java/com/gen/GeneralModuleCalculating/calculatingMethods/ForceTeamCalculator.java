package com.gen.GeneralModuleCalculating.calculatingMethods;

import com.gen.GeneralModuleCalculating.entities.PlayerForce;
import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import com.gen.GeneralModuleCalculating.repositories.PlayerForceRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ForceTeamCalculator {

    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    PlayerForceRepository playerForceRepository;

    // эту силу прибавляем к силе игрока. Если он выиграл сильных - тогда ему начисляется больший счет, иначе - меньший
    public float getForceTeam(List<PlayerOnMapResults> team) {
        List<Integer> ids = team.stream().map(e -> e.id).toList();
        List<PlayerForce> playerForces = playerForceRepository.findAllById(ids);
        return (float) playerForces.stream().map(e -> e.playerForce).mapToDouble(i -> i).sum();
    }
}
