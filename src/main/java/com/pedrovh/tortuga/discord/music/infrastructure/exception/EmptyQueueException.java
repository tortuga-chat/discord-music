package com.pedrovh.tortuga.discord.music.infrastructure.exception;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;

import java.util.Locale;

public class EmptyQueueException extends BotException {

    public EmptyQueueException() {
        this(Locale.getDefault());
    }

    public EmptyQueueException(Locale locale) {
        super(MessageResource.getMessage(locale, "command.queue.empty"), true);
    }

}
