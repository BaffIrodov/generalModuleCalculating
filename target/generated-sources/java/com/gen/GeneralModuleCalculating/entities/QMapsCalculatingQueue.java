package com.gen.GeneralModuleCalculating.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMapsCalculatingQueue is a Querydsl query type for MapsCalculatingQueue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMapsCalculatingQueue extends EntityPathBase<MapsCalculatingQueue> {

    private static final long serialVersionUID = 486679806L;

    public static final QMapsCalculatingQueue mapsCalculatingQueue = new QMapsCalculatingQueue("mapsCalculatingQueue");

    public final NumberPath<Integer> calculationTime = createNumber("calculationTime", Integer.class);

    public final NumberPath<Integer> idStatsMap = createNumber("idStatsMap", Integer.class);

    public final BooleanPath processed = createBoolean("processed");

    public QMapsCalculatingQueue(String variable) {
        super(MapsCalculatingQueue.class, forVariable(variable));
    }

    public QMapsCalculatingQueue(Path<? extends MapsCalculatingQueue> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMapsCalculatingQueue(PathMetadata metadata) {
        super(MapsCalculatingQueue.class, metadata);
    }

}

