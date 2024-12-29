package com.pedrovh.tortuga.discord.music.service.statistic;

import com.pedrovh.tortuga.discord.music.persistence.model.GuildStatistics;
import com.pedrovh.tortuga.discord.music.persistence.model.TrackInfo;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;

@Getter
public class GuildStatisticsReport {

    // guild
    private final Map<String, Integer> trackPlayed = new HashMap<>();
    private final Map<String, Integer> artistPlayed = new HashMap<>();
    private final Map<Long, Integer> userPlayed = new HashMap<>();

    private long totalPlaytime = 0;
    // guild - user
    private final Map<Long, List<TrackInfo>> trackByUser = new HashMap<>();

    public GuildStatisticsReport(GuildStatistics statistics) {
        for (TrackInfo track : statistics.getTracks()) {
            this.trackPlayed.compute(track.getName(), (k, v) -> v == null ? 1 : v + 1);
            this.artistPlayed.compute(track.getArtist(), (k, v) -> v == null ? 1 : v + 1);
            this.userPlayed.compute(track.getUserId(), (k, v) -> v == null ? 1 : v + 1);

            this.totalPlaytime += track.getDuration();

            this.trackByUser.computeIfAbsent(track.getUserId(), k -> new ArrayList<>()).add(track);
        }
    }

    public Map<String, Integer> getFavoriteTracksByGuild() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        trackPlayed.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }

    public LinkedHashMap<String, Integer> getFavoriteArtistsByGuild() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        artistPlayed.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }

    public LinkedHashMap<Long, Integer> getFavoriteUsersByGuild() {
        LinkedHashMap<Long, Integer> map = new LinkedHashMap<>();
        userPlayed.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }

    public int getTotalTracksPlayed() {
        return getTrackPlayed().values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<Long, List<String>> getArtistsByUser() {
        return getXByUser(TrackInfo::getArtist);
    }

    public Map<Long, List<String>> getTracksByUser() {
        return getXByUser(TrackInfo::getName);
    }

    public Map<Long, Long> getDurationByUser() {
        Map<Long, Long> durationByUser = new HashMap<>();
        for (Map.Entry<Long, List<TrackInfo>> entry : trackByUser.entrySet()) {
            durationByUser.put(entry.getKey(), entry.getValue().stream().mapToLong(TrackInfo::getDuration).sum());
        }
        return durationByUser;
    }

    public Map<Long, List<String>> getXByUser(Function<TrackInfo, String> getter) {
        Map<Long, List<String>> favoriteXByUser = new HashMap<>();
        for (Map.Entry<Long, List<TrackInfo>> entry : trackByUser.entrySet()) {
            favoriteXByUser.put(entry.getKey(), entry.getValue().stream().map(getter).toList());
        }
        return favoriteXByUser;
    }

}
