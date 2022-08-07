package com.gen.GeneralModuleCalculating.dtos;

import com.gen.GeneralModuleCalculating.common.MapsEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class PlayerOnMapResultsDto {
    public int id; //id записи

    public int playerId; //id игрока
    public int idStatsMap; //id stats-страницы
    public String url; //url игрока, вероятно, может быть удалено
    public String playerName; //ник игрока
    public Date dateOfMatch; //дата матча
    public MapsEnum playedMap; //карта, на которой был сыгран матч
    public String playedMapString; //карта в удобном виде
    public String team; //команда, в которой играет человек - left, right
    public int kills; //убийства (парсинг: целое число)
    public int assists; //помощь в убийстве (парсинг: строка вида " (8)")
    public int deaths; //смерти (парсинг: целое число)
    public float kd; //отношение киллов к смертям, (парсинга нет, считается)
    public int headshots; //количество хедшотов за карту, (парсин: целое число)
    public float adr; //АДР - средний урон, нанесенный за раунд, (парсинг: число в формате 75.1)
    public float rating20; //рейтинг 2.0, (парсинг: число в формате 1.23)
    public float cast20; //каст - количество раундов, когда игрок сделал хоть что-то для победы, (парсинг: число в формате 72.3%)
}
