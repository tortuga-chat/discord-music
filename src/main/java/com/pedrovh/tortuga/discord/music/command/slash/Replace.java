package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;
import java.util.Optional;

@Command(name = "replace", description = "Replaces a track in the queue")
public class Replace extends BaseSlashVoiceChannelCommandHandler {

    private static final String OPTION_INDEX = "index";
    private static final String OPTION_QUERY = "query";

    @Override
    protected void handle() throws BotException {
        Optional<SlashCommandInteractionOption> indexOption = interaction.getOptionByName(OPTION_INDEX);
        Optional<SlashCommandInteractionOption> queryOption = interaction.getOptionByName(OPTION_QUERY);
        if(indexOption.isPresent() && queryOption.isPresent()) {
            Long index = indexOption.get().getLongValue().orElseThrow(BotException::new);
            String str = queryOption.get().getStringValue().orElseThrow(BotException::new);
            MusicService.replace(voiceChannel, index, str, interaction.respondLater().join());
        }
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createLongOption(
                        OPTION_INDEX,
                        "The index in queue to replace",
                        true),
                SlashCommandOption.createStringOption(
                        OPTION_QUERY,
                        "Link to track, playlist or a search query",
                        true));
    }
}
