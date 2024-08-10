package com.pedrovh.tortuga.discord.music.service;

import com.pedrovh.tortuga.discord.music.persistence.DAO;
import com.pedrovh.tortuga.discord.music.persistence.model.GuildPreferences;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@SuppressWarnings("unused")
public class GuildPreferencesService {

    private static final DAO<GuildPreferences, Long> dao = new DAO<>(GuildPreferences.class);

    private GuildPreferencesService() {}

    public static boolean exists(Long id) {
        return dao.exists(id);
    }

    public static Optional<GuildPreferences> findById(Long id) {
        return dao.findById(id);
    }

    public static void save(GuildPreferences guildPreferences) {
        if(guildPreferences.getGuildId() == null)
            throw new IllegalArgumentException("guildId is not set!");

        if(exists(guildPreferences.getGuildId()))
            dao.save(guildPreferences);
        else
            dao.insert(guildPreferences);

        log.info("Saved guild preferences {}", guildPreferences);
    }

    public static void remove(GuildPreferences guildPreferences) {
        dao.remove(guildPreferences);
        log.info("Removed guild preferences {}", guildPreferences);
    }

}
