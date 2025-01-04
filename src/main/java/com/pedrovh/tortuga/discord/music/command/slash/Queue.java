package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashServerCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.EmptyQueueException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import com.pedrovh.tortuga.discord.music.util.TrackUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;
import static com.pedrovh.tortuga.discord.music.util.TortugaProperties.DESC_LIMIT;

@Command(name = "queue", description = "Lists the tracks currently on queue")
public class Queue extends BaseSlashServerCommandHandler {

    @Override
    protected void handle() throws BotException {
        if(MusicService.isQueueEmpty(server)) {
            throw new EmptyQueueException(server.getPreferredLocale());
        }
        InteractionOriginalResponseUpdater respondLater = interaction.respondLater().join();

        StringBuilder sb = new StringBuilder();
        long totalTimeMs = MusicService.queue(server, sb);
        String totalTime = TrackUtils.formatTrackDuration(totalTimeMs);
        String limitedDesc = sb.substring(0, DESC_LIMIT);

        respondLater.addEmbed(
                new EmbedBuilder()
                    .setTitle(getMessage(server.getPreferredLocale(), "command.queue.title"))
                    .setDescription(limitedDesc.substring(0, limitedDesc.lastIndexOf("\n")))
                    .setColor(getColor(COLOR_SUCCESS))
                    .setFooter(getMessage(server.getPreferredLocale(), "command.queue.footer", totalTime))
                    .setTimestamp(Instant.now().plus(totalTimeMs, ChronoUnit.MILLIS)))
                .update()
                .join();
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return Collections.emptyList();
    }

}
