package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.EmptyQueueException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Command(name = "skip", description = "Skips to the next track in queue")
public class Skip extends BaseSlashVoiceChannelCommandHandler {

    @Override
    protected void handle() throws BotException {
        if(MusicService.isQueueEmpty(server)) throw new EmptyQueueException(server.getPreferredLocale());

        MusicService.skip(voiceChannel);
        interaction.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle(getMessage(server.getPreferredLocale(), "command.skip.title"))
                        .setColor(getColor(COLOR_SUCCESS)))
                .respond();
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return null;
    }

}
