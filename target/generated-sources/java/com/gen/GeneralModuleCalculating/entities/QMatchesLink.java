package com.gen.GeneralModuleCalculating.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMatchesLink is a Querydsl query type for MatchesLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchesLink extends EntityPathBase<MatchesLink> {

    private static final long serialVersionUID = 814005140L;

    public static final QMatchesLink matchesLink = new QMatchesLink("matchesLink");

    public final StringPath leftTeam = createString("leftTeam");

    public final StringPath leftTeamOdds = createString("leftTeamOdds");

    public final StringPath matchFormat = createString("matchFormat");

    public final NumberPath<Integer> matchId = createNumber("matchId", Integer.class);

    public final StringPath matchMapsNames = createString("matchMapsNames");

    public final StringPath matchUrl = createString("matchUrl");

    public final StringPath rightTeam = createString("rightTeam");

    public final StringPath rightTeamOdds = createString("rightTeamOdds");

    public QMatchesLink(String variable) {
        super(MatchesLink.class, forVariable(variable));
    }

    public QMatchesLink(Path<? extends MatchesLink> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMatchesLink(PathMetadata metadata) {
        super(MatchesLink.class, metadata);
    }

}

