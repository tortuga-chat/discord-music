package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.service.music.MusicService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;
import java.util.Optional;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Command(name = "remove", description = "Removes a range of tracks from the queue")
public class Remove extends BaseSlashVoiceChannelCommandHandler {

    private static final String OPTION_START = "start";
    private static final String OPTION_END = "end";

    @Override
    protected void handle() throws BotException {
        Optional<SlashCommandInteractionOption> startOption = interaction.getOptionByName(OPTION_START);
        Optional<SlashCommandInteractionOption> endOption = interaction.getOptionByName(OPTION_END);

        if(startOption.isPresent() && endOption.isPresent()) {
            Long start = startOption.get().getLongValue().orElseThrow(BotException::new);
            Long end = endOption.get().getLongValue().orElseThrow(BotException::new);
            try {
                interaction.createImmediateResponder()
                        .addEmbed(new EmbedBuilder()
                                .setTitle(getMessage(server.getPreferredLocale(), "command.remove.title"))
                                .setDescription(MusicService.remove(voiceChannel, start.intValue(), end.intValue()))
                                .setColor(getColor(COLOR_SUCCESS)))
                        .respond();
            } catch (IndexOutOfBoundsException e) {
                throw new BotException(getMessage(server.getPreferredLocale(), "command.remove.invalidPosition"));
            }
        }
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(SlashCommandOption.createLongOption(
                        OPTION_START,
                        "The tracks index to delete from (inclusive)",
                        true,
                        0,
                        1_000),
                SlashCommandOption.createLongOption(
                        OPTION_END,
                        "The end index of the track to delete (inclusive)",
                        true,
                        0,
                        1_000));
    }
}
