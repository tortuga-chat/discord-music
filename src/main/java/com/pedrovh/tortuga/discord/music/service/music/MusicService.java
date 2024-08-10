package com.pedrovh.tortuga.discord.music.service.music;

import com.pedrovh.tortuga.discord.music.infrastructure.exception.EmptyQueueException;
import com.pedrovh.tortuga.discord.music.service.music.handler.DefaultAudioLoadResultHandler;
import com.pedrovh.tortuga.discord.music.service.music.handler.NextCommandAudioLoadResultHandler;
import com.pedrovh.tortuga.discord.music.service.music.handler.ReplaceCommandAudioLoadResultHandler;
import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Slf4j
public class MusicService {

    private static final String YOUTUBE_QUERY = "ytsearch: ";

    private MusicService(){}

    public static void skip(final ServerVoiceChannel voiceChannel) {
        VoiceConnectionService.getGuildAudioManager(voiceChannel).getScheduler().nextTrack();
    }

    /**
     * Removes a range of songs from the queue
     * @param voiceChannel user's connected channel
     * @param start the starting index (inclusive)
     * @param end the end index (inclusive)
     * @return a list of the removed tracks
     * @throws IndexOutOfBoundsException if start and/or end indexes are invalid
     */
    public static String remove(ServerVoiceChannel voiceChannel, int start, int end) throws IndexOutOfBoundsException {
        GuildAudioManager manager = VoiceConnectionService.getGuildAudioManager(voiceChannel);
        List<AudioTrack> removed = manager.getScheduler().removeFromQueue(start, end);

        StringBuilder sb = new StringBuilder();
        int index = start + 1;
        for (AudioTrack track : removed) {
            sb.append(index++).append(". ").append(track.getInfo().title).append("\n");
        }
        log.info("[{}] Removed from {} to {}", voiceChannel.getServer().getName(), start, end);
        return sb.toString();
    }

    public static boolean pause(final Server server) throws EmptyQueueException {
        Optional<GuildAudioManager> audioManager = VoiceConnectionService.getGuildAudioManager(server.getId());
        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            boolean isPaused = !manager.getPlayer().isPaused();

            manager.getPlayer().setPaused(isPaused);

            log.info("[{}] Paused: {}", server.getName(), isPaused);
            return isPaused;
        } else {
            throw new EmptyQueueException(server.getPreferredLocale());
        }
    }

    public static void stop(Server server) throws EmptyQueueException {
        Optional<GuildAudioManager> audioManager = VoiceConnectionService.getGuildAudioManager(server.getId());

        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            manager.getPlayer().stopTrack();

            manager.getScheduler().clearQueue();
            log.info("[{}] stopped and queue cleared", server.getName());

        } else {
            throw new EmptyQueueException(server.getPreferredLocale());
        }
    }

    public static boolean loop(final ServerVoiceChannel voiceChannel) {
        GuildAudioManager manager = VoiceConnectionService.getGuildAudioManager(voiceChannel);
        boolean loop = manager.getScheduler().toggleLoop();
        log.info("[{}] looping {}", voiceChannel.getServer().getName(), loop);
        return loop;
    }

    public static Long queue(Server server, StringBuilder sb) throws EmptyQueueException {
        Optional<GuildAudioManager> audioManager = VoiceConnectionService.getGuildAudioManager(server.getId());
        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            Queue<AudioTrack> queue = manager.getScheduler().getQueue();

            AudioTrack currentTrack = manager.getPlayer().getPlayingTrack();
            sb.append(getMessage("emoji.song")).append(" ").append(currentTrack.getInfo().title).append("\n");

            long totalTimeMs = currentTrack.getDuration() - currentTrack.getPosition();

            int count = 1;
            for(AudioTrack track : queue) {
                sb.append(count++).append(". ").append(track.getInfo().title).append("\n");
                totalTimeMs += track.getDuration();
            }
            log.info("[{}] Retrieved queue", server.getName());
            return totalTimeMs;
        } else {
            throw new EmptyQueueException(server.getPreferredLocale());
        }
    }

    public static void addToQueue(final Message message, final ServerVoiceChannel channel) {
        final GuildAudioManager manager = VoiceConnectionService.getGuildAudioManager(channel);
        final String identifier = getIdentifier(message.getContent());

        VoiceConnectionService.getPlayerManager()
                .loadItemOrdered(
                        manager,
                        identifier,
                        new DefaultAudioLoadResultHandler(manager, channel, identifier, message));
    }

    public static void replace(final ServerVoiceChannel channel, final long pos, final String query, InteractionOriginalResponseUpdater updater) {
        final GuildAudioManager manager = VoiceConnectionService.getGuildAudioManager(channel);
        final String identifier = getIdentifier(query);

        VoiceConnectionService.getPlayerManager()
                .loadItemOrdered(
                        manager,
                        identifier,
                        new ReplaceCommandAudioLoadResultHandler(manager, channel, identifier, updater, pos));
    }

    public static void next(final ServerVoiceChannel channel, final String query, InteractionOriginalResponseUpdater updater) {
        final GuildAudioManager manager = VoiceConnectionService.getGuildAudioManager(channel);
        final String identifier = getIdentifier(query);

        VoiceConnectionService.getPlayerManager()
                .loadItemOrdered(
                        manager,
                        identifier,
                        new NextCommandAudioLoadResultHandler(manager, channel, identifier, updater));
    }

    public static boolean isQueueEmpty(Server server) {
        return VoiceConnectionService.getGuildAudioManager(server.getId())
                .filter(manager -> manager.getPlayer().getPlayingTrack() == null && manager.getScheduler().getQueue().isEmpty())
                .isPresent();
    }

    public static String getIdentifier(String content) {
        try {
            new URI(content);
        } catch (URISyntaxException e) {
            log.debug("youtube search: {}", content);
            return YOUTUBE_QUERY.concat(content);
        }
        return content;
    }
}
