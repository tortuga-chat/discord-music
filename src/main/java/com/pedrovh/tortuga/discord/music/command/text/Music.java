package com.pedrovh.tortuga.discord.music.command.text;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(name = Music.COMMAND, description = "Plays music in your discord voice call")
public class Music extends BaseTextVoiceChannelCommandHandler {

    public static final String COMMAND = "music";

    @Override
    protected void handle() throws BotException {
        MusicService.addToQueue(message, voiceChannel);
    }

}
