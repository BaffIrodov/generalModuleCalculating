package com.gen.GeneralModuleCalculating.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoundHistory is a Querydsl query type for RoundHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoundHistory extends EntityPathBase<RoundHistory> {

    private static final long serialVersionUID = -775444417L;

    public static final QRoundHistory roundHistory = new QRoundHistory("roundHistory");

    public final DateTimePath<java.util.Date> dateOfMatch = createDateTime("dateOfMatch", java.util.Date.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> idStatsMap = createNumber("idStatsMap", Integer.class);

    public final StringPath roundSequence = createString("roundSequence");

    public QRoundHistory(String variable) {
        super(RoundHistory.class, forVariable(variable));
    }

    public QRoundHistory(Path<? extends RoundHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoundHistory(PathMetadata metadata) {
        super(RoundHistory.class, metadata);
    }

}

