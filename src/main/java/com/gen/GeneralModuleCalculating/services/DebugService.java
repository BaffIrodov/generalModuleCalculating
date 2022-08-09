package com.gen.GeneralModuleCalculating.services;

import com.gen.GeneralModuleCalculating.entities.QPlayerOnMapResults;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DebugService {

    @Autowired
    JPAQueryFactory queryFactory;


    private static final QPlayerOnMapResults playerOnMapResults =
            new QPlayerOnMapResults("playerOnMapResults");

    public List<Float> init() {
        List<Float> result = getFullList(playerOnMapResults.adr);
        return result;
    }

    public List<Float> getFullList(NumberPath<Float> path) {
        try {
            PrintWriter out = new PrintWriter("D:/test/filename.txt");
            List<Float> result = queryFactory.from(playerOnMapResults).select(path).fetch();
            out.print(result.get(0));
        } catch (Exception e) {
            //nothing
        }
        return new ArrayList<>();
    }
}
