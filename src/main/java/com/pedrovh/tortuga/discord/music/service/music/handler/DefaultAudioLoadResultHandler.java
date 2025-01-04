package com.pedrovh.tortuga.discord.music.service.music.handler;

import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import com.pedrovh.tortuga.discord.music.service.statistic.GuildStatisticsService;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.music.util.TortugaProperties.DESC_LIMIT;

@Slf4j
public class DefaultAudioLoadResultHandler extends AbstractAudioLoadResultHandler {

    private final Message message;

    public DefaultAudioLoadResultHandler(GuildAudioManager manager,
                                         ServerVoiceChannel voiceChannel,
                                         String identifier,
                                         Message message) {
        super(manager, voiceChannel, identifier);
        this.message = message;
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        manager.getScheduler().queue(track);
        GuildStatisticsService.addTrackInfo(server.getId(), message.getAuthor().getId(), track.getInfo(), identifier);
    }

    @Override
    protected void handlePlaylistLoaded(AudioPlaylist playlist) {
        List<AudioTrack> tracks = manager.getScheduler().queuePlaylist(playlist);
        StringBuilder sb = new StringBuilder();
        tracks.forEach(track -> sb.append(track.getInfo().title).append("\n"));

        log.info("[{}] Loading playlist '{}' with '{}' tracks", server.getName(), playlist.getName(), playlist.getTracks().size());
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(MessageResource.getMessage(server.getPreferredLocale(), "command.music.loadingPlaylist", tracks.size()))
                .setDescription(sb.substring(0, DESC_LIMIT))
                .setColor(getColor(COLOR_SUCCESS));

        new MessageBuilder()
                .addEmbed(embed)
                .send(message.getChannel());

        GuildStatisticsService.addTrackInfoInBulk(message.getAuthor().getId(), server.getId(), tracks, identifier);
    }

    @Override
    protected void respondNoMatches(EmbedBuilder embed) {
        new MessageBuilder().addEmbed(embed).send(message.getChannel());
    }

    @Override
    protected void respondLoadFailed(EmbedBuilder embed) {
        new MessageBuilder().addEmbed(embed).send(message.getChannel());
    }
}