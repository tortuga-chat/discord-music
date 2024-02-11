package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.service.UserPlaylistService;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.List;
import java.util.Optional;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_WARNING;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Command(name = "playlist", description = "Saves the current queue for later")
public class Playlist extends BaseSlashVoiceChannelCommandHandler {

    private static final String OPTION_SAVE = "save";
    private static final String OPTION_UPDATE = "update";
    private static final String OPTION_LOAD = "load";
    private static final String OPTION_DELETE = "delete";
    private static final String OPTION_LIST = "list";
    private static final String OPTION_NAME = "name";
    private InteractionOriginalResponseUpdater updater;

    @Override
    protected void load(SlashCommandCreateEvent event) throws BotException {
        super.load(event);
        this.updater = interaction.respondLater().join();
    }

    @Override
    protected void handle() throws BotException {
        var save = interaction.getOptionByName(OPTION_SAVE);
        if (save.isPresent()) optionSave(save.get());

        var update = interaction.getOptionByName(OPTION_UPDATE);
        if (update.isPresent()) optionUpdate(update.get());

        var load = interaction.getOptionByName(OPTION_LOAD);
        if (load.isPresent()) optionLoad(load.get());

        var delete = interaction.getOptionByName(OPTION_DELETE);
        if (delete.isPresent()) optionDelete(delete.get());

        var list = interaction.getOptionByName(OPTION_LIST);
        if (list.isPresent()) optionList(list.get());
    }

    public void optionSave(SlashCommandInteractionOption option) throws BotException {
        var value = option.getOptions().getFirst().getStringValue().orElseThrow(BotException::new);
        if(UserPlaylistService.save(user.getIdAsString(), server, value))
            updater.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(getMessage(server.getPreferredLocale(), "command.playlist.save.title", value))
                                    .setColor(getColor(COLOR_SUCCESS)))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .update();
        else
            updater.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(getMessage(server.getPreferredLocale(), "command.playlist.shouldReplace.title", value))
                                    .setDescription(getMessage(server.getPreferredLocale(), "command.playlist.shouldReplace.description"))
                                    .setColor(getColor(COLOR_WARNING)))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .update();
    }

    public void optionUpdate(SlashCommandInteractionOption option) throws BotException {
        var value = option.getOptions().getFirst().getStringValue().orElseThrow(BotException::new);
        UserPlaylistService.update(user.getIdAsString(), server, value);

        updater.addEmbed(new EmbedBuilder()
                    .setTitle(getMessage(server.getPreferredLocale(), "command.playlist.update.title", value))
                    .setColor(getColor(COLOR_SUCCESS)))
            .update();
    }

    public void optionLoad(SlashCommandInteractionOption option) throws BotException {
        var value = option.getOptions().getFirst().getStringValue().orElseThrow(BotException::new);
        updater.addEmbed(new EmbedBuilder()
                        .setTitle(getMessage(server.getPreferredLocale(), "command.playlist.load.title", value))
                        .setDescription(UserPlaylistService.load(user.getIdAsString(), voiceChannel, value, updater))
                        .setColor(getColor(COLOR_SUCCESS)))
                .update();
    }

    private void optionDelete(SlashCommandInteractionOption option) throws BotException {
        var value = option.getOptions().getFirst().getStringValue().orElseThrow();
        UserPlaylistService.delete(user.getIdAsString(), server, value);

        updater.addEmbed(new EmbedBuilder()
                        .setTitle(getMessage(server.getPreferredLocale(), "command.playlist.delete.title", value))
                        .setColor(getColor(COLOR_SUCCESS)))
                .setFlags(MessageFlag.EPHEMERAL)
                .update();
    }

    public void optionList(SlashCommandInteractionOption option) throws BotException {
        Optional<SlashCommandInteractionOption> name = option.getOptionByName(OPTION_NAME);
        if(name.isPresent() && name.get().getStringValue().isPresent()) {
            updater.addEmbed(new EmbedBuilder()
                            .setTitle(getMessage(server.getPreferredLocale(), "command.playlist.list.title", name))
                            .setDescription(UserPlaylistService.list(user.getIdAsString(), server, name.get().getStringValue().get()))
                            .setColor(getColor(COLOR_SUCCESS)))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .update();
        } else {
            updater.addEmbed(new EmbedBuilder()
                            .setTitle(getMessage(server.getPreferredLocale(), "command.playlist.listAll.title"))
                            .setDescription(UserPlaylistService.list(user.getIdAsString(), server))
                            .setColor(getColor(COLOR_SUCCESS)))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .update();
        }
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        SlashCommandOption name = SlashCommandOption.createStringOption(OPTION_NAME, "The name of the playlist", true);

        return List.of(
                SlashCommandOption.createSubcommand(
                        OPTION_SAVE,
                        "Saves the current playlist",
                        List.of(name)
                ),
                SlashCommandOption.createSubcommand(
                        OPTION_UPDATE,
                        "Updates saved playlist with the current queue",
                        List.of(name)
                ),
                SlashCommandOption.createSubcommand(
                        OPTION_LOAD,
                        "Loads the saved playlist",
                        List.of(name)
                ),
                SlashCommandOption.createSubcommand(
                        OPTION_DELETE,
                        "Deletes a playlist",
                        List.of(name)
                ),
                SlashCommandOption.createSubcommand(
                        OPTION_LIST,
                        "Lists saved playlists",
                        List.of(SlashCommandOption.createStringOption(OPTION_NAME, "The name of the playlist", false))
                )
        );
    }
}
