package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.command.BotCommandLoader;
import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import com.pedrovh.tortuga.discord.music.service.GuildPreferencesService;
import java.util.Collections;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;

@Command(name = "help", description = "About me")
public class Help extends BaseSlashCommandHandler {

    @Override
    protected void handle() throws BotException {
        boolean setupNeeded = interaction.getServer().isEmpty() || ! GuildPreferencesService.exists(interaction.getServer().orElseThrow().getId());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(MessageResource.getMessage("command.help.title"))
                .setDescription(MessageResource.getMessage("command.help.%b.description", setupNeeded))
                .setColor(getColor(COLOR_SUCCESS))
                .setAuthor(DiscordResource.get("help.author", "pedrones"));

        for (Command command : BotCommandLoader.getCommands()) {
            embed.addInlineField(command.name(), command.description());
        }
        interaction.createImmediateResponder()
                .addEmbed(embed)
                .respond();
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return Collections.emptyList();
    }

}
