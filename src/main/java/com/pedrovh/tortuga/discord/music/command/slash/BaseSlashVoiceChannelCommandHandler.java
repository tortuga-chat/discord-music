package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashServerCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.VoiceChannelRequiredException;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

public abstract class BaseSlashVoiceChannelCommandHandler extends BaseSlashServerCommandHandler {

    protected ServerVoiceChannel voiceChannel;

    @Override
    protected void load(SlashCommandCreateEvent event) throws BotException {
        super.load(event);
        this.voiceChannel = user.getConnectedVoiceChannel(server).orElseThrow(() -> new VoiceChannelRequiredException(server));
    }
}
