package com.gen.GeneralModuleCalculating.entities;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class PlayerForce {
    @Id
    public int playerId;
    public float playerForce;
}
