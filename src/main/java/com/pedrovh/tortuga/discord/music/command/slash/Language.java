package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashServerCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import com.pedrovh.tortuga.discord.music.persistence.model.GuildPreferences;
import com.pedrovh.tortuga.discord.music.service.GuildPreferencesService;
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

@Command(name = "language", description = "Changes the bot language", permissions = PermissionType.ADMINISTRATOR)
public class Language extends BaseSlashServerCommandHandler {

    private static final String OPTION_CHOICE = "choice";

    @Override
    protected void handle() throws BotException {
        CompletableFuture<InteractionOriginalResponseUpdater> updater = interaction.respondLater();

        Optional<SlashCommandInteractionOption> languageOption = interaction.getOptionByName(OPTION_CHOICE);

        GuildPreferences preferences = GuildPreferencesService.findById(server.getId())
                .orElse(new GuildPreferences(server.getId()));

        if(languageOption.isPresent()) {
            preferences.setLocale(languageOption.get().getStringValue().orElseThrow(BotException::new));
        }
        GuildPreferencesService.save(preferences);

        updater
                .whenComplete((u, t) -> u.addEmbed(
                        new EmbedBuilder()
                                .setTitle(getMessage(server.getPreferredLocale(), "command.language.title"))
                                .setColor(getColor(COLOR_SUCCESS)))
                .update());
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createWithChoices(
                        SlashCommandOptionType.STRING,
                        OPTION_CHOICE,
                        "Select a language",
                        false,
                        MessageResource.getSupportedLocalesAsChoices())
        );
    }

}
