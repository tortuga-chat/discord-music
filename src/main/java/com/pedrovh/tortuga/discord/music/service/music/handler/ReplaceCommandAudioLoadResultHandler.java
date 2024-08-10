package com.pedrovh.tortuga.discord.music.service.music.handler;

import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import com.pedrovh.tortuga.discord.music.util.ResponseUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;

@Slf4j
@SuppressWarnings("LoggingSimilarMessage")
public class ReplaceCommandAudioLoadResultHandler extends NextCommandAudioLoadResultHandler {

    private final Long position;

    public ReplaceCommandAudioLoadResultHandler(GuildAudioManager manager,
                                                ServerVoiceChannel voiceChannel,
                                                String identifier,
                                                InteractionOriginalResponseUpdater updater,
                                                Long position) {
        super(manager, voiceChannel, identifier, updater);
        this.position = position;
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        if(position == -1) {
            manager.getScheduler().nextTrack(track, false);
            updater.addEmbed(ResponseUtils.getPLayingEmbed(track))
                    .update();
            return;
        }
        manager.getScheduler().replaceTrack(position.intValue(), track);

        log.info("[{}] Replacing track {} with playlist '{}'", position.intValue(), server.getName(), track.getInfo().title);
        updater.addEmbed(new EmbedBuilder()
                        .setTitle(MessageResource.getMessage(server.getPreferredLocale(), "command.replace.track.title", position+1, track.getInfo().title))
                        .setFooter(track.getInfo().author)
                        .setColor(getColor(COLOR_SUCCESS)))
                .update();
    }

    @Override
    protected void handlePlaylistLoaded(AudioPlaylist playlist) {
        if(position == 0) {
            super.handlePlaylistLoaded(playlist);
            manager.getScheduler().nextTrack();
            return;
        }
        manager.getScheduler().replaceTrack(position.intValue(), playlist);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playlist.getTracks().size(); i++)
            sb.append(i+1).append(". ").append(playlist.getTracks().get(i)).append("\n");

        log.info("[{}] Replacing track {} with playlist '{}'", position.intValue(), server.getName(), playlist.getName());
        updater.addEmbed(new EmbedBuilder()
                        .setTitle(MessageResource.getMessage(server.getPreferredLocale(), "command.replace.playlist.title", position+1, playlist.getName()))
                        .setDescription(sb.toString())
                        .setColor(getColor(COLOR_SUCCESS)))
                .update();
    }

}