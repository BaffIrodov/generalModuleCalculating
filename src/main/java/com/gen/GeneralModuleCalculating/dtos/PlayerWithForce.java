package com.gen.GeneralModuleCalculating.dtos;

import com.gen.GeneralModuleCalculating.entities.PlayerForce;
import com.gen.GeneralModuleCalculating.entities.PlayerOnMapResults;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerWithForce {
    public PlayerOnMapResults playerOnMapResults;
    public PlayerForce playerForce;
}
