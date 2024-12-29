package com.pedrovh.tortuga.discord.music.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackInfo {

    private long userId;
    private String name;
    private String artist;
    private long duration;
    private String identifier;

}
