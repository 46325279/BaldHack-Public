package club.baldhack.command.commands;

import club.baldhack.BaldHack;
import club.baldhack.command.Command;
import club.baldhack.command.syntax.SyntaxChunk;

import java.util.Comparator;

/**
 * Created by 086 on 12/11/2017.
 */
public class CommandsCommand extends Command {

    public CommandsCommand() {
        super("commands", SyntaxChunk.EMPTY);
    }

    @Override
    public void call(String[] args) {
        BaldHack.getInstance().getCommandManager().getCommands().stream().sorted(Comparator.comparing(command -> command.getLabel())).forEach(command ->
            Command.sendChatMessage("&7" + Command.getCommandPrefix() + command.getLabel() + "&r ~ &8" + command.getDescription())
        );
    }
}
