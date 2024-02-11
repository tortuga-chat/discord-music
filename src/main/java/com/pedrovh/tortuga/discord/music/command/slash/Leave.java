package com.pedrovh.tortuga.discord.music.command.slash;

import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.music.service.music.VoiceConnectionService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Command(name = "leave", description = "Leaves the voice channel")
public class Leave extends BaseSlashVoiceChannelCommandHandler {

    @Override
    protected void handle() throws BotException {
        VoiceConnectionService.leaveVoiceChannel(voiceChannel);
        interaction.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle(getMessage(server.getPreferredLocale(), "command.leave.title"))
                        .setColor(getColor(COLOR_SUCCESS)))
                .respond();
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return null;
    }
}
