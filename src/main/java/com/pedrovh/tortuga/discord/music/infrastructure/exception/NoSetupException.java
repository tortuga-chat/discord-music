package com.pedrovh.tortuga.discord.music.infrastructure.exception;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_ERROR;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;

public class NoSetupException extends BotException {

    @Override
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle("You need to setup first!")
                .setDescription("Enter /setup to configure tortuga in your server")
                .setColor(getColor(COLOR_ERROR));
    }

}
