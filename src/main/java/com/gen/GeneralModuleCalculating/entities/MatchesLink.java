package com.gen.GeneralModuleCalculating.entities;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class MatchesLink {
    @Id
    public int matchId;
    public String matchUrl;
    public String leftTeam;
    public String rightTeam;
    public String matchFormat;
    public String matchMapsNames;
    public String leftTeamOdds;
    public String rightTeamOdds;
}
