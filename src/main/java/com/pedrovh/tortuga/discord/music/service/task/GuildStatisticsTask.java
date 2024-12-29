package com.pedrovh.tortuga.discord.music.service.task;

import com.pedrovh.tortuga.discord.core.DiscordProperties;
import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.scheduler.Task;
import com.pedrovh.tortuga.discord.music.TortugaDiscordMusicBot;
import com.pedrovh.tortuga.discord.music.persistence.model.GuildStatistics;
import com.pedrovh.tortuga.discord.music.service.GuildPreferencesService;
import com.pedrovh.tortuga.discord.music.service.statistic.GuildStatisticsReport;
import com.pedrovh.tortuga.discord.music.service.statistic.GuildStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;
import static com.pedrovh.tortuga.discord.core.i18n.MessageResource.getMessage;

@Slf4j
@Task(initialDelay = "0", period = "1", unit = "DAYS")
public class GuildStatisticsTask implements Runnable {

    @Override
    public void run() {
        for (GuildStatistics statistics : GuildStatisticsService.getAll()) {
            try {
            if (statistics.canReport()) {
                doReport(statistics);
            }
            } catch (Exception e) {
                log.error("Error during report", e);
            }
        }
    }

    private void doReport(GuildStatistics statistics) {
        log.info("Creating report for guild {}", statistics.getGuildId());
        GuildStatisticsReport report = createReport(statistics);

        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(getMessage("report.yearly.title", statistics.getYear()));

        Duration d = Duration.ofMillis(report.getTotalPlaytime());

        eb.setDescription(getMessage("report.yearly.description",
                report.getTotalTracksPlayed(),
                d.toDaysPart(), d.toHoursPart(), d.toMinutesPart(), d.toSecondsPart()));

        createTop5(report.getFavoriteTracksByGuild())
                .ifPresent(tracks -> eb.addField(getMessage("report.yearly.favorite.tracks"), tracks));

        createTop5(report.getFavoriteArtistsByGuild())
                .ifPresent(artists -> eb.addField(getMessage("report.yearly.favorite.artists"), artists));

        eb.setFooter(getMessage("report.yearly.footer"));
        eb.setThumbnail(TortugaDiscordMusicBot.getBot().getApi().getYourself().getAvatar());
        eb.setColor(DiscordResource.getColor(DiscordProperties.COLOR_SUCCESS));

        Optional<EmbedBuilder> ur = createUserEmbed(report, statistics.getGuildId());
        if (ur.isPresent())
            sendMessage(statistics.getGuildId(), eb, ur.get());
        else
            sendMessage(statistics.getGuildId(), eb);

        afterReport(statistics);
    }

    private GuildStatisticsReport createReport(GuildStatistics statistics) {
        return new GuildStatisticsReport(statistics);
    }

    private void afterReport(GuildStatistics statistics) {
        log.info("Resetting report for guild {}", statistics.getGuildId());
        statistics.resetYear();
        GuildStatisticsService.save(statistics);
    }

    private Optional<EmbedBuilder> createUserEmbed(GuildStatisticsReport report, Long guildId) {
        LinkedHashMap<Long, Integer> users = report.getFavoriteUsersByGuild();
        if (users.isEmpty()) return Optional.empty();

        final Map.Entry<Long, Integer> entry = users.pollLastEntry();
        final Long userId = entry.getKey();
        final Integer count = entry.getValue();

        Server server = TortugaDiscordMusicBot.getBot().getApi().getServerById(guildId).orElseThrow();
        User user = TortugaDiscordMusicBot.getBot().getApi().getUserById(userId).join();

        List<String> tracks = report.getTracksByUser().get(userId);

        return Optional.of(new EmbedBuilder()
                .setTitle(getMessage("report.yearly.user.title"))
                .setDescription(getMessage("report.yearly.user.count", user.getDisplayName(server), count))
                .addField(getMessage("report.yearly.user.favorite.tracks"), createTop5(tracks).orElse("..."))
                .setThumbnail(user.getAvatar())
                .setColor(DiscordResource.getColor(COLOR_SUCCESS)));
    }

    private Optional<String> createTop5(Map<String, Integer> ranking) {
        LinkedHashMap<String, Integer> map = (LinkedHashMap<String, Integer>) ranking;
        if (map.isEmpty()) return Optional.empty();

        final StringBuilder builder = new StringBuilder("1. ")
                .append(map.pollLastEntry().getKey())
                .append(getMessage("emoji.crown"));

        for (int i = 1; i < 5; i++) {
            final int index = i;
            Optional.ofNullable(map.pollLastEntry())
                    .ifPresent(e -> builder.append(index + 1).append(". ").append(e.getKey()).append("\n"));
        }
        return Optional.of(builder.toString());
    }

    private Optional<String> createTop5(final List<String> info) {
        List<String> list = info.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        if (list.isEmpty()) return Optional.empty();

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(i + 1).append(". ").append(list.get(i)).append("\n");
        }
        return Optional.of(builder.toString());
    }

    private void sendMessage(long guildId, EmbedBuilder... embeds) {
        log.info("sending report embeds...");
        getChannel(guildId).orElseThrow().sendMessage(embeds);
    }

    private Optional<TextChannel> getChannel(long guildId) {
        return GuildPreferencesService.findById(guildId)
                .map(preference ->
                        TortugaDiscordMusicBot.getBot().getApi().getTextChannelById(preference.getMusicChannelId()).orElseThrow());
    }

}
