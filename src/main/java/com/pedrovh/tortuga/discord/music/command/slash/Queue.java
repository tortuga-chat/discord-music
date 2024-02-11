package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashServerCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.EmptyQueueException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import com.pedrovh.tortuga.discord.music.util.TrackUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Command(name = "queue", description = "Lists the tracks currently on queue")
public class Queue extends BaseSlashServerCommandHandler {

    @Override
    protected void handle() throws BotException {
        if(MusicService.isQueueEmpty(server)) {
            throw new EmptyQueueException(server.getPreferredLocale());
        }
        StringBuilder sb = new StringBuilder();
        long totalTimeMs = MusicService.queue(server, sb);
        String totalTime = TrackUtils.formatTrackDuration(totalTimeMs);

        interaction.createImmediateResponder()
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle(getMessage(server.getPreferredLocale(), "command.queue.title"))
                                .setDescription(sb.toString())
                                .setColor(getColor(COLOR_SUCCESS))
                                .setFooter(getMessage(server.getPreferredLocale(), "command.queue.footer", totalTime))
                                .setTimestamp(Instant.now().plus(totalTimeMs, ChronoUnit.MILLIS)))
                .respond();
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return null;
    }

}
