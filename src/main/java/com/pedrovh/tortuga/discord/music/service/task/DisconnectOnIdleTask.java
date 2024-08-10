package com.pedrovh.tortuga.discord.music.service.task;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.scheduler.Task;
import com.pedrovh.tortuga.discord.music.service.music.VoiceConnectionService;
import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static com.pedrovh.tortuga.discord.music.util.TortugaProperties.TASK_DISCONNECT_AFTER;

@Slf4j
@Task(initialDelay = "5", period = "5", unit = "MINUTES")
public class DisconnectOnIdleTask implements Runnable {

    @Override
    public void run() {
        log.trace("looking for inactive voice connections...");

        for (GuildAudioManager manager : VoiceConnectionService.getAudioManagers().values()) {
            Optional<Instant> endOfQueue = manager.getScheduler().getLatestEndOfQueue();
            if(endOfQueue.isEmpty())
                continue;

            Duration duration = Duration.between(endOfQueue.get(), Instant.now());
            if(duration.compareTo(Duration.ofMinutes(DiscordResource.getInt(TASK_DISCONNECT_AFTER))) < 0)
                continue;

            Server server = manager.getServer();
            Optional<ServerVoiceChannel> voiceChannel = server.getConnectedVoiceChannel(server.getApi().getYourself());
            if(voiceChannel.isEmpty())
                continue;

            VoiceConnectionService.leaveVoiceChannel(voiceChannel.get());
            log.info("[{}] left voice channel for inactivity", server.getName());
        }
    }
}
