package com.pedrovh.tortuga.discord.music.persistence.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Document(collection = GuildPreferences.COLLECTION, schemaVersion = "2.0")
public class GuildPreferences implements Serializable {

    public static final String COLLECTION = "GUILD_PREFERENCES";
    @Id
    private Long guildId;
    private Long musicChannelId;
    private String locale;

    public GuildPreferences(Long guildId) {
        this.guildId = guildId;
    }

}
