package com.pedrovh.tortuga.discord.music.service.task;

import com.pedrovh.tortuga.discord.core.scheduler.Task;
import com.pedrovh.tortuga.discord.music.TortugaDiscordMusicBot;
import com.pedrovh.tortuga.discord.music.service.music.VoiceConnectionService;
import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Task(initialDelay = "8", period = "8", unit = "HOURS")
public class RestartConnectionTask implements Runnable {

    @Override
    public void run() {
        boolean inactive = true;
        if (!VoiceConnectionService.getAudioManagers().isEmpty()) {
            for (GuildAudioManager manager : VoiceConnectionService.getAudioManagers().values()) {
                if (!manager.getScheduler().getQueue().isEmpty() || manager.getScheduler().getPlayer().getPlayingTrack() != null) {
                    log.debug("Aborting connection restart - bot is in use...");
                    inactive = false;
                    break;
                }
            }
        }
        if(inactive)
            TortugaDiscordMusicBot.getBot().restart();
    }

}
