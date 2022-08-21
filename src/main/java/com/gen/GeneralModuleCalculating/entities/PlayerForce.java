package com.gen.GeneralModuleCalculating.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PlayerForce {
    @Id
    public int playerId;
    public float playerForce;
    public int playerStability;
}
