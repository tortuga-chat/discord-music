package com.pedrovh.tortuga.discord.music.infrastructure.exception;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import org.javacord.api.entity.server.Server;

public class VoiceChannelRequiredException extends BotException {


    public VoiceChannelRequiredException(Server server) {
        super(MessageResource.getMessage(server.getPreferredLocale(), "command.error.vcRequired.description"));
    }

}
