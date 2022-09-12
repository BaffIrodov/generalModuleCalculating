package com.gen.GeneralModuleCalculating.entities;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class ImprovementResults {
    @Id
    @SequenceGenerator(name = "sq_improvement_results", sequenceName = "sq_improvement_results_id", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_improvement_results")
    public int id;
    public float accuracy;
    public int currentEpoch;
    public int rightCount;
    public int allCount;
    public String fullConfig;
}
