package com.gen.GeneralModuleCalculating.services;

import com.gen.GeneralModuleCalculating.entities.QPlayerOnMapResults;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
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
            getAverageOfList(result);
            getSliceByPercent(95, 100, result);
            getGroupForGauss(10, result);
            out.print(result.get(0));
        } catch (Exception e) {
            //nothing
        }
        return new ArrayList<>();
    }

    private List<Float> getSliceByPercent(int minPercent, int maxPercent, List<Float> source) {
        Collections.sort(source);
        List<Float> result = new ArrayList<>();
        int minIndex = (source.size()/100) * minPercent;
        int maxIndex = (source.size()/100) * maxPercent;
        int index = 0;
        for(Float item : source) {
            if(minIndex <= index && index <= maxIndex) {
                result.add(item);
            }
            index++;
        }
        return result;
    }

    //просто для красивого графика делаем подлог в данных
    private List<Float> getGroupForGauss(int parts, List<Float> source) {
        Collections.sort(source);
        int indexOfPart = source.size() / parts;
        for(int i = 0; i < parts; i++) {
            int minIndex = indexOfPart * i;
            int maxIndex = indexOfPart * (i+1);
            float average = (source.get(minIndex) + source.get(maxIndex)) / 2;
            for(int index = minIndex; index < maxIndex; index++) {
                source.set(index, average);
            }
        }
        return source;
    }

    private float getAverageOfList(List<Float> source) {
        return (float) source.stream().mapToDouble(e -> e).average().orElse(0);
    }
}
