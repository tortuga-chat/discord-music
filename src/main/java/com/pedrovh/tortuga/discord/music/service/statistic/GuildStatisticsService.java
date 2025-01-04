package com.pedrovh.tortuga.discord.music.service.statistic;

import com.pedrovh.tortuga.discord.music.persistence.DAO;
import com.pedrovh.tortuga.discord.music.persistence.model.GuildStatistics;
import com.pedrovh.tortuga.discord.music.persistence.model.TrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GuildStatisticsService {

    private static final DAO<GuildStatistics, Long> DAO = new DAO<>(GuildStatistics.class);

    private GuildStatisticsService() {}

    public static List<GuildStatistics> getAll() {
        return DAO.findAll();
    }

    public static void addTrackInfo(long guildId, long userId, AudioTrackInfo info, String identifier) {
        addTrackInfo(guildId, userId, info.title, info.author, info.length, identifier);
    }

    public static void addTrackInfo(long guildId, long userId, String name, String artist, long duration, String identifier) {
        addTrackInfo(guildId, new TrackInfo(userId, name, artist, duration, identifier));
    }

    public static void addTrackInfo(long guildId, TrackInfo trackInfo) {
        log.debug("Adding TrackInfo {} to Guild statistics: {}", trackInfo.getIdentifier(), guildId);
        GuildStatistics statistics = get(guildId);
        statistics.addTrackInfo(trackInfo);
        DAO.save(statistics);
    }

    public static void addTrackInfoInBulk(long userId, long guildId, List<AudioTrack> tracks, String identifier) {
        log.debug("Adding TrackInfo for playlist {} to Guild statistics: {}", identifier, guildId);
        GuildStatistics statistics = get(guildId);
        tracks.stream()
                .map(AudioTrack::getInfo)
                .forEachOrdered(i ->
                        statistics.addTrackInfo(new TrackInfo(userId, i.title, i.author, i.length, identifier)));
        DAO.save(statistics);
    }

    public static void save(GuildStatistics statistics) {
        DAO.save(statistics);
    }

    public static GuildStatistics get(long guildId) {
        return DAO.findById(guildId).orElseGet(() -> {
            GuildStatistics stat = new GuildStatistics(guildId);
            DAO.insert(stat);
            return stat;
        });
    }

}
