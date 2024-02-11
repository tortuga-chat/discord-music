package com.pedrovh.tortuga.discord.music.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Playlist implements Serializable {

    private String name;
    private List<Track> tracks;

}
