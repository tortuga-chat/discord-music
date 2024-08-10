package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashServerCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import java.util.Collections;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Command(name = "stop", description = "Stops and clears the queue")
public class Stop extends BaseSlashServerCommandHandler {

    @Override
    protected void handle() throws BotException {
        MusicService.stop(server);

        interaction.createImmediateResponder()
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle(getMessage(server.getPreferredLocale(), "command.stop.title"))
                                .setDescription(getMessage(server.getPreferredLocale(), "command.stop.description"))
                                .setColor(getColor(COLOR_SUCCESS)))
                .respond();
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return Collections.emptyList();
    }
}
