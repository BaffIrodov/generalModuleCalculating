package com.gen.GeneralModuleCalculating.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MapsCalculatingQueueResponseDto {
    public int currentNotProcessedMaps;
    public int mapsAddingCount;
    public int mapsAddingTime;
}
