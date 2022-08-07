package com.gen.GeneralModuleCalculating.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStatsResponse is a Querydsl query type for StatsResponse
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStatsResponse extends EntityPathBase<StatsResponse> {

    private static final long serialVersionUID = 2130720775L;

    public static final QStatsResponse statsResponse = new QStatsResponse("statsResponse");

    public final NumberPath<Integer> batchSize = createNumber("batchSize", Integer.class);

    public final NumberPath<Integer> batchTime = createNumber("batchTime", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.util.Date> requestDate = createDateTime("requestDate", java.util.Date.class);

    public QStatsResponse(String variable) {
        super(StatsResponse.class, forVariable(variable));
    }

    public QStatsResponse(Path<? extends StatsResponse> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStatsResponse(PathMetadata metadata) {
        super(StatsResponse.class, metadata);
    }

}

