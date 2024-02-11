package com.pedrovh.tortuga.discord.music.service.music.handler;

import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import com.pedrovh.tortuga.discord.music.service.music.VoiceConnectionService;
import com.pedrovh.tortuga.discord.music.service.music.manager.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_ERROR;
import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_WARNING;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;

@Slf4j
public abstract class AbstractAudioLoadResultHandler implements AudioLoadResultHandler {

    protected final GuildAudioManager manager;
    protected final ServerVoiceChannel voiceChannel;
    protected final String identifier;
    protected final Server server;

    public AbstractAudioLoadResultHandler(GuildAudioManager manager,
                                          ServerVoiceChannel voiceChannel,
                                          String identifier) {
        this.manager = manager;
        this.voiceChannel = voiceChannel;
        this.identifier = identifier;
        this.server = voiceChannel.getServer();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        VoiceConnectionService.createAudioConnection(voiceChannel, manager.getSource());
        handleTrackLoaded(track);
    }

    protected abstract void handleTrackLoaded(AudioTrack track);

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if(playlist.isSearchResult()) {
            trackLoaded(playlist.getTracks().getFirst());
            return;
        }

        VoiceConnectionService.createAudioConnection(voiceChannel, manager.getSource());

        handlePlaylistLoaded(playlist);
    }

    protected abstract void handlePlaylistLoaded(AudioPlaylist playlist);

    @Override
    public void noMatches() {
        log.warn("[{}] no matches found for {}", server.getName(), identifier);
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(MessageResource.getMessage(server.getPreferredLocale(), "command.music.noMatches"))
                .setColor(getColor(COLOR_WARNING));

        respondNoMatches(embed);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        log.warn("[{}] error loading item: {}", server.getName(), exception.getMessage());
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(MessageResource.getMessage(server.getPreferredLocale(), "command.music.loadingFailed"))
                .setDescription(exception.getMessage())
                .setColor(getColor(COLOR_ERROR));

        respondLoadFailed(embed);
    }

    protected abstract void respondNoMatches(EmbedBuilder embed);
    protected abstract void respondLoadFailed(EmbedBuilder embed);

}
