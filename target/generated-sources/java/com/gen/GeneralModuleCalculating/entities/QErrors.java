package com.gen.GeneralModuleCalculating.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QErrors is a Querydsl query type for Errors
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QErrors extends EntityPathBase<Errors> {

    private static final long serialVersionUID = 1742507844L;

    public static final QErrors errors = new QErrors("errors");

    public final StringPath classAndLine = createString("classAndLine");

    public final DateTimePath<java.util.Date> dateTime = createDateTime("dateTime", java.util.Date.class);

    public final StringPath descriptionError = createString("descriptionError");

    public final NumberPath<Integer> Id = createNumber("Id", Integer.class);

    public final StringPath payload = createString("payload");

    public final BooleanPath verificationError = createBoolean("verificationError");

    public QErrors(String variable) {
        super(Errors.class, forVariable(variable));
    }

    public QErrors(Path<? extends Errors> path) {
        super(path.getType(), path.getMetadata());
    }

    public QErrors(PathMetadata metadata) {
        super(Errors.class, metadata);
    }

}

