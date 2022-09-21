package com.gen.GeneralModuleCalculating.dtos;

import com.gen.GeneralModuleCalculating.calculatingMethods.Config;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ImprovementRequestDto {
    public Integer testDatasetPercent;
    public Integer inactiveDatasetPercent = 0;
    public Map<String, Object> config;
    public List<ConfigAsList> configList;
}
