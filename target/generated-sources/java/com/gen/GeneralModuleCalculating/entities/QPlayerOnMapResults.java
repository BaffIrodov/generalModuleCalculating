package com.gen.GeneralModuleCalculating.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlayerOnMapResults is a Querydsl query type for PlayerOnMapResults
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlayerOnMapResults extends EntityPathBase<PlayerOnMapResults> {

    private static final long serialVersionUID = -772483565L;

    public static final QPlayerOnMapResults playerOnMapResults = new QPlayerOnMapResults("playerOnMapResults");

    public final NumberPath<Float> adr = createNumber("adr", Float.class);

    public final NumberPath<Integer> assists = createNumber("assists", Integer.class);

    public final NumberPath<Float> cast20 = createNumber("cast20", Float.class);

    public final DateTimePath<java.util.Date> dateOfMatch = createDateTime("dateOfMatch", java.util.Date.class);

    public final NumberPath<Integer> deaths = createNumber("deaths", Integer.class);

    public final NumberPath<Integer> headshots = createNumber("headshots", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> idStatsMap = createNumber("idStatsMap", Integer.class);

    public final NumberPath<Float> kd = createNumber("kd", Float.class);

    public final NumberPath<Integer> kills = createNumber("kills", Integer.class);

    public final StringPath playedMap = createString("playedMap");

    public final StringPath playedMapString = createString("playedMapString");

    public final NumberPath<Integer> playerId = createNumber("playerId", Integer.class);

    public final StringPath playerName = createString("playerName");

    public final NumberPath<Float> rating20 = createNumber("rating20", Float.class);

    public final StringPath team = createString("team");

    public final StringPath url = createString("url");

    public QPlayerOnMapResults(String variable) {
        super(PlayerOnMapResults.class, forVariable(variable));
    }

    public QPlayerOnMapResults(Path<? extends PlayerOnMapResults> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlayerOnMapResults(PathMetadata metadata) {
        super(PlayerOnMapResults.class, metadata);
    }

}

