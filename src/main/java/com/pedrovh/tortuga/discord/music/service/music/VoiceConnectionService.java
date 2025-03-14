package com.pedrovh.tortuga.discord.music.service.music;

import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class VoiceConnectionService {

    @Getter
    private static final AudioPlayerManager playerManager;
    @Getter
    private static final ConcurrentHashMap<Long, GuildAudioManager> audioManagers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, AudioConnection> connections = new ConcurrentHashMap<>();

    static {
        playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        // TODO add spotify
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager();

        playerManager.registerSourceManager(youtube);
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
    }

    private VoiceConnectionService(){}

    public static void leaveVoiceChannel(ServerVoiceChannel channel) {
        Long id = channel.getServer().getId();

        GuildAudioManager manager = audioManagers.remove(id);
        if(manager != null)
            manager.getPlayer().destroy();
        connections.remove(id);

        if(channel.isConnected(channel.getApi().getYourself()))
            channel.disconnect()
                    .whenComplete((c,p) -> log.info("[{}] Disconnected from voice channel {}", channel.getServer().getName(), channel))
                    .join();
    }

    public static void createAudioConnection(ServerVoiceChannel channel, AudioSource source) {
        connections.computeIfAbsent(channel.getServer().getId(), f -> {
            log.info("[{}] connecting to voice channel {}", channel.getServer().getName(), channel.getName());
            try {
                return channel.connect().get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("[{}] Failed to connect to voice channel {}", channel.getServer().getName(), channel.getName(), e);
                throw new RuntimeException(e);
            }
        });
        AudioConnection connection = connections.get(channel.getServer().getId());

        log.debug("[{}] setting audio source...", channel.getServer().getName());
        if(connection.getAudioSource().isEmpty())
            connection.setAudioSource(source);
    }

    public static GuildAudioManager getGuildAudioManager(ServerVoiceChannel channel) {
        Long guildId = channel.getServer().getId();
        if(audioManagers.containsKey(guildId))
            return audioManagers.get(guildId);

        GuildAudioManager audioManager = new GuildAudioManager(channel.getApi(), playerManager, channel.getServer());
        audioManagers.put(guildId, audioManager);
        log.debug("[{}] Created GuildAudioManager", channel.getServer().getName());
        return audioManager;
    }

    public static Optional<GuildAudioManager> getGuildAudioManager(Long guildId) {
        return Optional.ofNullable(audioManagers.get(guildId));
    }

}
