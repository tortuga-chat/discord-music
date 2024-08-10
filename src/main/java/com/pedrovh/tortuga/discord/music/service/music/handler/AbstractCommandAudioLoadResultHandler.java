package com.pedrovh.tortuga.discord.music.service.music.handler;

import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

public abstract class AbstractCommandAudioLoadResultHandler extends AbstractAudioLoadResultHandler {

    protected final InteractionOriginalResponseUpdater updater;

    protected AbstractCommandAudioLoadResultHandler(GuildAudioManager manager,
                                                 ServerVoiceChannel voiceChannel,
                                                 String identifier,
                                                 InteractionOriginalResponseUpdater updater) {
        super(manager, voiceChannel, identifier);
        this.updater = updater;
    }

    @Override
    protected void respondNoMatches(EmbedBuilder embed) {
        updater.addEmbed(embed).update();
    }

    @Override
    protected void respondLoadFailed(EmbedBuilder embed) {
        updater.addEmbed(embed).update();
    }

}
