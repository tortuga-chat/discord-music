package com.pedrovh.tortuga.discord.music.infrastructure.exception;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.Locale;

public class PlaylistNotFoundException extends BotException {

    private final Locale locale;

    public PlaylistNotFoundException() {
        this(Locale.getDefault());
    }

    public PlaylistNotFoundException(Locale locale) {
        super(MessageResource.getMessage(locale, "command.playlist.notFound.title"));
        this.locale = locale;
    }

    @Override
    public EmbedBuilder getEmbed() {
        return super.getEmbed()
                .setDescription(MessageResource.getMessage(locale, "command.playlist.notFound.description"));
    }

}
