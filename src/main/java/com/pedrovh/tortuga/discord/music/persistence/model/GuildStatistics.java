package com.pedrovh.tortuga.discord.music.persistence.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Data
@NoArgsConstructor
@Document(collection = GuildStatistics.COLLECTION, schemaVersion = "1.0")
public class GuildStatistics implements Serializable {

    public static final String COLLECTION = "GUILD_STATISTICS";

    @Id
    private Long guildId;
    private List<TrackInfo> tracks = new ArrayList<>();
    private int year = Calendar.getInstance().get(Calendar.YEAR);

    public GuildStatistics(long guildId) {
        this.guildId = guildId;
    }

    public void addTrackInfo(TrackInfo trackInfo) {
        tracks.add(trackInfo);
    }

    public boolean canReport() {
        return Calendar.getInstance().get(Calendar.YEAR) > year && !tracks.isEmpty();
    }

    public void resetYear() {
        year = Calendar.getInstance().get(Calendar.YEAR);
        tracks.clear();
    }

}
