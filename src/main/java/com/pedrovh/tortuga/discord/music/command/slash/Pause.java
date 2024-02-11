package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashServerCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Command(name = "pause", description = "Pauses current track")
public class Pause extends BaseSlashServerCommandHandler {

    @Override
    protected void handle() throws BotException {
        boolean isPaused = MusicService.pause(server);
        interaction.createImmediateResponder()
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle(getMessage(server.getPreferredLocale(), "command.pause.%b.title", isPaused))
                                .setColor(getColor(COLOR_SUCCESS)))
                .respond();
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return null;
    }
}
