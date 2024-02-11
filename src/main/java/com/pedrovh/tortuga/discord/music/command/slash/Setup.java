package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashServerCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import com.pedrovh.tortuga.discord.music.persistence.model.GuildPreferences;
import com.pedrovh.tortuga.discord.music.service.GuildPreferencesService;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Command(name = "setup", description = "Sets everything up!", permissions = PermissionType.ADMINISTRATOR)
public class Setup extends BaseSlashServerCommandHandler {

    private static final String OPTION_CHANNEL = "channel";
    private static final String OPTION_LANGUAGE = "language";

    @Override
    protected void handle() throws BotException {
        CompletableFuture<InteractionOriginalResponseUpdater> updater = interaction.respondLater();

        Optional<SlashCommandInteractionOption> channelOption = interaction.getOptionByName(OPTION_CHANNEL);
        Optional<SlashCommandInteractionOption> languageOption = interaction.getOptionByName(OPTION_LANGUAGE);

        ServerTextChannel serverTextChannel = channelOption.isPresent() ?
            channelOption.get()
                    .getChannelValue().orElseThrow(BotException::new)
                    .asServerTextChannel().orElseThrow(BotException::new)
                :
            server.createTextChannelBuilder()
                    .setName(getMessage(server.getPreferredLocale(), "channel.music.name"))
                    .create()
                    .join();

        GuildPreferences preferences = GuildPreferencesService.findById(server.getId())
                .orElse(new GuildPreferences(server.getId()));

        preferences.setMusicChannelId(serverTextChannel.getId());

        if(languageOption.isPresent()) {
            preferences.setLocale(languageOption.get().getStringValue().orElseThrow(BotException::new));
        }

        GuildPreferencesService.save(preferences);

        updater.whenComplete((u, t) -> u.addEmbed(
                new EmbedBuilder()
                        .setTitle(getMessage(server.getPreferredLocale(), "command.setup.title"))
                        .setDescription(getMessage(server.getPreferredLocale(), "command.setup.description", serverTextChannel.getMentionTag()))
                        .setColor(getColor(COLOR_SUCCESS)))
                .update());
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createChannelOption(
                        OPTION_CHANNEL,
                        "Select an exclusive text channel to request songs",
                        false,
                        List.of(ChannelType.SERVER_TEXT_CHANNEL)),
                SlashCommandOption.createWithChoices(
                        SlashCommandOptionType.STRING,
                        OPTION_LANGUAGE,
                        "Select a language",
                        false,
                        MessageResource.getSupportedLocalesAsChoices())
                );
    }

}
