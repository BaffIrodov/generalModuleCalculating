package com.gen.GeneralModuleCalculating.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QResultsLink is a Querydsl query type for ResultsLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResultsLink extends EntityPathBase<ResultsLink> {

    private static final long serialVersionUID = -1864084265L;

    public static final QResultsLink resultsLink = new QResultsLink("resultsLink");

    public final BooleanPath archive = createBoolean("archive");

    public final BooleanPath processed = createBoolean("processed");

    public final NumberPath<Integer> resultId = createNumber("resultId", Integer.class);

    public final StringPath resultUrl = createString("resultUrl");

    public QResultsLink(String variable) {
        super(ResultsLink.class, forVariable(variable));
    }

    public QResultsLink(Path<? extends ResultsLink> path) {
        super(path.getType(), path.getMetadata());
    }

    public QResultsLink(PathMetadata metadata) {
        super(ResultsLink.class, metadata);
    }

}

