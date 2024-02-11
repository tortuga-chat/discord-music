package com.pedrovh.tortuga.discord.music.service;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.EmptyQueueException;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.PlaylistNeverSavedException;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.PlaylistNotFoundException;
import com.pedrovh.tortuga.discord.music.persistence.DAO;
import com.pedrovh.tortuga.discord.music.persistence.model.Playlist;
import com.pedrovh.tortuga.discord.music.persistence.model.Track;
import com.pedrovh.tortuga.discord.music.persistence.model.UserPlaylists;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import com.pedrovh.tortuga.discord.music.service.music.VoiceConnectionService;
import com.pedrovh.tortuga.discord.music.service.music.handler.AbstractCommandAudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserPlaylistService {
    private static final DAO<UserPlaylists, String> dao = new DAO<>(UserPlaylists.class);

    public static boolean save(String userId, Server server, String name) throws BotException {
        UserPlaylists userPlaylists = get(userId).orElseGet(() -> {
            log.debug("[{}] creating new guildPlaylists", server.getName());
            var playlists = new UserPlaylists();
            playlists.setUserId(userId);
            playlists.setPlaylists(new ArrayList<>());

            dao.insert(playlists);

            return playlists;
        });
        if(get(userId, name).isPresent()) {
            return false;
        }
        userPlaylists.getPlaylists().add(new Playlist(name, buildTrackQueue(server)));
        dao.save(userPlaylists);

        log.info("[{}] saved playlist {}", server.getName(), name);
        return true;
    }

    public static void update(String userId, Server server, String name) throws BotException {
        final var userPlaylists = get(userId).orElseThrow(() -> new PlaylistNeverSavedException(server.getPreferredLocale()));
        get(userId, name).orElseThrow(() -> new PlaylistNotFoundException(server.getPreferredLocale()));

        userPlaylists.getPlaylists().removeIf(p -> p.getName().equalsIgnoreCase(name));
        userPlaylists.getPlaylists().add(new Playlist(name, buildTrackQueue(server)));
        dao.save(userPlaylists);

        log.info("[{}] updated playlist {}", server.getName(), name);
    }

    public static String load(final String userId, final ServerVoiceChannel channel, String name, final InteractionOriginalResponseUpdater updater) throws BotException {
        final var server = channel.getServer();
        final var playlist = get(userId, name).orElseThrow(() -> new PlaylistNotFoundException(server.getPreferredLocale()));

        final var manager = VoiceConnectionService.getGuildAudioManager(channel);
        for(var track : playlist.getTracks()) {
            VoiceConnectionService.getPlayerManager()
                    .loadItemOrdered(
                            manager,
                            MusicService.getIdentifier(track.getUrl()),
                            new AbstractCommandAudioLoadResultHandler(manager, channel, track.getUrl(), updater) {
                                @Override
                                protected void handleTrackLoaded(AudioTrack track) {
                                    manager.getScheduler().queue(track, false);
                                }
                                @Override
                                protected void handlePlaylistLoaded(AudioPlaylist playlist) {
                                    manager.getScheduler().queuePlaylist(playlist);
                                }
                            });
        }
        log.info("[{}] loaded playlist {}", server.getName(), name);

        var sb = new StringBuilder();
        appendTrackNames(sb, playlist);
        return sb.toString();
    }

    public static void delete(String userId, Server server, String name) throws BotException {
        final var userPlaylists = get(userId).orElseThrow(() -> new PlaylistNeverSavedException(server.getPreferredLocale()));
        if(userPlaylists.getPlaylists().removeIf(p -> p.getName().equalsIgnoreCase(name))) {
            dao.save(userPlaylists);
            log.info("[{}] deleted playlist {}", server.getName(), name);
        } else
            throw new PlaylistNotFoundException(server.getPreferredLocale());
    }

    public static String list(String userId, Server server, String name) throws BotException {
        final var playlist = get(userId, name).orElseThrow(() -> new PlaylistNotFoundException(server.getPreferredLocale()));

        final var sb = new StringBuilder();
        appendTrackNames(sb, playlist);

        log.info("[{}] Returning playlist listing for {}", server.getName(), playlist.getName());
        return sb.toString();
    }

    public static String list(String userId, Server server) throws BotException {
        final var userPlaylists = get(userId).orElseThrow(() -> new PlaylistNeverSavedException(server.getPreferredLocale()));
        final var sb = new StringBuilder();

        for(var playlist : userPlaylists.getPlaylists()) {
            sb.append("**").append(playlist.getName()).append("**\n");
            appendTrackNames(sb, playlist);
        }
        log.info("[{}] Returning playlist listing for user id {}", server.getName(), userId);
        return sb.toString();
    }

    private static Optional<UserPlaylists> get(String userId) {
        return dao.findById(userId);
    }

    private static Optional<Playlist> get(String userId, String name) {
        return get(userId)
                .map(playlist -> playlist
                        .getPlaylists()
                        .stream()
                        .filter(p -> p.getName().equalsIgnoreCase(name)).findFirst())
                .orElse(null);
    }

    private static List<Track> buildTrackQueue(Server server) throws EmptyQueueException {
        var manager = VoiceConnectionService.getGuildAudioManager(server.getId()).orElseThrow(EmptyQueueException::new);

        var queue = new ArrayList<Track>();
        queue.add(new Track(manager.getPlayer().getPlayingTrack().getInfo()));
        queue.addAll(manager.getScheduler().getQueue().stream().map(AudioTrack::getInfo).map(Track::new).toList());

        return queue;
    }

    private static void appendTrackNames(StringBuilder sb, Playlist playlist) {
        int i = 1;
        for(var track : playlist.getTracks()) {
            sb.append(i).append(". ").append(track.getName()).append("\n");
        }
    }

}
