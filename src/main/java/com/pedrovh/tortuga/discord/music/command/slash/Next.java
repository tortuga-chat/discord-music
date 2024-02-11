package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;
import java.util.Optional;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.MESSAGE_CHARACTER_LIMIT;

@Command(name = "next", description = "Enter track as next in the queue")
public class Next extends BaseSlashVoiceChannelCommandHandler {

    private static final String OPTION_QUERY = "query";

    @Override
    protected void handle() throws BotException {
        Optional<SlashCommandInteractionOption> query = interaction.getOptionByName(OPTION_QUERY);
        if (query.isPresent()) {
            String value = query.get().getStringValue().orElseThrow(BotException::new);
            MusicService.next(voiceChannel, value, interaction.respondLater().join());
        }
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createStringOption(
                        OPTION_QUERY,
                        "Link to a track, playlist or a search query",
                        true,
                        0,
                        DiscordResource.getInt(MESSAGE_CHARACTER_LIMIT, 4_000)));
    }
}
