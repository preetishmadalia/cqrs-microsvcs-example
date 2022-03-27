package com.techbank.cqrs.core.infrastructure;

import com.techbank.cqrs.core.commands.BaseCommand;
import com.techbank.cqrs.core.commands.CommandHandlerMethod;

/**
 * This is a Mediator interface.
 * This interface handles communication between commands and events.
 */
public interface CommandDispatcher {
    <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> method);
    void send(BaseCommand command);
}
