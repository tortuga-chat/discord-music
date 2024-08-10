package com.pedrovh.tortuga.discord.music.infrastructure.exception;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.Locale;

@SuppressWarnings("unused")
public class PlaylistNeverSavedException extends BotException {

    private final Locale locale;

    public PlaylistNeverSavedException() {
        this(Locale.getDefault());
    }

    public PlaylistNeverSavedException(Locale locale) {
        super(MessageResource.getMessage(locale, "command.playlist.neverSaved.title"));
        this.locale = locale;
    }

    @Override
    public EmbedBuilder getEmbed() {
        return super.getEmbed()
                .setDescription(MessageResource.getMessage(locale, "command.playlist.neverSaved.description"));
    }
}
