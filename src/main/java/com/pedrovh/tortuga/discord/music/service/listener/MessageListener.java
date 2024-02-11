package com.pedrovh.tortuga.discord.music.service.listener;

import com.pedrovh.tortuga.discord.core.command.BotCommandLoader;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.listener.BaseMessageListener;
import com.pedrovh.tortuga.discord.core.listener.Listener;
import com.pedrovh.tortuga.discord.music.command.text.Music;
import com.pedrovh.tortuga.discord.music.infrastructure.exception.NoSetupException;
import com.pedrovh.tortuga.discord.music.persistence.model.GuildPreferences;
import com.pedrovh.tortuga.discord.music.service.GuildPreferencesService;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Optional;

@Slf4j
@Listener(MessageCreateListener.class)
public class MessageListener extends BaseMessageListener {

    @Override
    protected void handlerNotFound(MessageCreateEvent event) throws BotException {
        Optional<Server> server = event.getServer();
        if (server.isEmpty()) {
            log.warn("Non existent text command '{}' sent by '{}' outside a server",
                    event.getMessageContent().split(" ")[0],
                    event.getMessageAuthor().getDisplayName());
            return;
        }
        Optional<GuildPreferences> preferences = GuildPreferencesService.findById(server.get().getId());
        if (preferences.isEmpty()) {
            log.info("[{}] No guild preference configured for this guild", server.get().getName());
            if (event.getMessageAuthor().isServerAdmin()) {
                throw new NoSetupException();
            }
            return;
        }
        if (event.getChannel().getId() == preferences.get().getMusicChannelId()) {
            getInstanceOf(BotCommandLoader.getHandlerForText(Music.COMMAND)).handle(event);
        }
    }

}