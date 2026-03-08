package Tenzinn.Commands.DEBUG;

import Tenzinn.ThePrice;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ActiveEventsCommand extends CommandBase {

    private ThePrice main;

    public ActiveEventsCommand(@NonNullDecl String name, @NonNullDecl String description, ThePrice main) { super(name, description); this.main = main; }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        main.activateEvents.set(!main.activateEvents.get());

        commandContext.sendMessage(Message.raw("Event: " + (main.activateEvents.get() ? "ACTIVE" : "INACTIVE")));
    }
}
