package com.pedrovh.tortuga.discord.music;

import com.pedrovh.tortuga.discord.core.DiscordBot;
import com.pedrovh.tortuga.discord.core.DiscordResource;
import lombok.extern.slf4j.Slf4j;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.DISCORD_TOKEN;

@Slf4j
public class TortugaDiscordMusicBot {

    public static void main(String[] args) {
        DiscordBot bot = new DiscordBot(DiscordResource.get(DISCORD_TOKEN));
        bot.start().join();

        log.info("""
                tortuga music is online!
                  _____     ____    _/_       _/_                               o  \s
                 /      \\  |  o |   /  __ _   /  , , _,  __,    _ _ _   , , (  ,  _,
                |        |/ ___\\|  (__(_)/ (_(__(_/_(_)_(_/(_  / / / /_(_/_/_)_(_(__
                |_________/                           /|                            \s
                |_|_| |_|_|                          (/                             \s
                """);
    }

}