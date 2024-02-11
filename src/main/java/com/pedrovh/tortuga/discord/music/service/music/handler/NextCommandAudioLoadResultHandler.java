package com.pedrovh.tortuga.discord.music.service.music.handler;

import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import com.pedrovh.tortuga.discord.music.util.TrackUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Slf4j
public class NextCommandAudioLoadResultHandler extends AbstractCommandAudioLoadResultHandler {

    public NextCommandAudioLoadResultHandler(GuildAudioManager manager,
                                             ServerVoiceChannel channel,
                                             String identifier,
                                             InteractionOriginalResponseUpdater updater) {
        super(manager, channel, identifier, updater);
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        manager.getScheduler().addAsNextInQueue(track);
        log.info("[{}] Loading track '{}' as next", manager.getServer().getName(), track.getInfo().title);
        updater.addEmbed(
                        new EmbedBuilder()
                                .setTitle(getMessage(server.getPreferredLocale(), "command.next.track.title", track.getInfo().title))
                                .setDescription(track.getInfo().author)
                                .setColor(getColor(COLOR_SUCCESS)))
                .update();
    }

    @Override
    protected void handlePlaylistLoaded(AudioPlaylist playlist) {
        List<AudioTrack> tracks = TrackUtils.getTracksAfterSelectedTrack(playlist);
        log.info("[{}] adding playlist {} as next", server.getName(), playlist.getName());

        final StringBuilder sb = new StringBuilder();
        tracks.forEach(track -> sb.append(track.getInfo().title).append("\n"));

        manager.getScheduler().addAsNextInQueue(tracks.toArray(new AudioTrack[0]));
        updater.addEmbed(
                        new EmbedBuilder()
                                .setTitle(getMessage(server.getPreferredLocale(), "command.next.playlist.title", tracks.size()))
                                .setDescription(sb.toString())
                                .setColor(getColor(COLOR_SUCCESS)))
                .update();
    }

}