package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashCommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;

@Slf4j
@Command(name = "ping", description = "Check if the bot is alive!")
public class Ping extends BaseSlashCommandHandler {

    @Override
    protected void handle() {
        interaction.createImmediateResponder()
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle("Pong!")
                                .setColor(getColor(COLOR_SUCCESS))
                                .setDescription(user.getMentionTag()))
                .respond()
                .whenComplete((r, e) -> log.info("{} Pong!", user.getName()));
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return null;
    }

    @Override
    public boolean enabledInDMs() {
        return true;
    }
}
