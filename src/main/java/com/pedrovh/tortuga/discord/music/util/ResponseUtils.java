package com.pedrovh.tortuga.discord.music.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getColor;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Slf4j
@UtilityClass
public class ResponseUtils {

    public EmbedBuilder getPLayingEmbed(AudioTrack track) {
        String duration = track.getInfo().isStream ? getMessage("emoji.live") + "Live" : TrackUtils.formatTrackDuration(track.getDuration());
        return new EmbedBuilder()
                .setTitle(String.format(
                        "%s [%s] %s",
                        getMessage("emoji.song"),
                        duration,
                        track.getInfo().title))
                .setFooter(track.getInfo().author)
                .setColor(getColor(COLOR_SUCCESS));
    }

    public EmbedBuilder getAddedToPlaylistEmbed(AudioTrack track) {
        String duration = track.getInfo().isStream ? getMessage("emoji.live") + "Live" : TrackUtils.formatTrackDuration(track.getDuration());
        return new EmbedBuilder()
                .setTitle(String.format("%s [%s] %s",
                        getMessage("emoji.list"),
                        duration,
                        track.getInfo().title))
                .setDescription(track.getInfo().author)
                .setColor(getColor(COLOR_SUCCESS));
    }

}
