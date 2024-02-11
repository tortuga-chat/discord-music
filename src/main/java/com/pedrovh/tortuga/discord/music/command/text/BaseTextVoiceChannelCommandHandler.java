package com.pedrovh.tortuga.discord.music.command.text;

import com.pedrovh.tortuga.discord.core.command.text.BaseTextServerCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.VoiceChannelRequiredException;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.event.message.MessageCreateEvent;

public abstract class BaseTextVoiceChannelCommandHandler extends BaseTextServerCommandHandler {

    protected ServerVoiceChannel voiceChannel;

    @Override
    protected void load(MessageCreateEvent event) throws BotException {
        super.load(event);
        this.voiceChannel = user.getConnectedVoiceChannel().orElseThrow(() -> new VoiceChannelRequiredException(server));
    }
}
