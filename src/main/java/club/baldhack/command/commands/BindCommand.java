package club.baldhack.command.commands;

import club.baldhack.command.syntax.parsers.ModuleParser;
import club.baldhack.command.Command;
import club.baldhack.command.syntax.ChunkBuilder;
import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import club.baldhack.util.Wrapper;

/**
 * Created by 086 on 12/11/2017.
 */
public class BindCommand extends Command {

    public BindCommand() {
        super("bind", new ChunkBuilder()
                .append("module", true, new ModuleParser())
                .append("key", true)
                .build()
        );
    }

    @Override
    public void call(String[] args) {
        if (args.length == 1) {
            Command.sendChatMessage("Please specify a module.");
            return;
        }

        String rkey = args[1];
        String module = args[0];

        Module m = ModuleManager.getModuleByName(module);

        if (m == null){
            sendChatMessage("Unknown module '" + module + "'!");
            return;
        }

        if (rkey == null){
            sendChatMessage(m.getName() + " is bound to &b" + m.getBindName());
            return;
        }

        int key = Wrapper.getKey(rkey);

        if (rkey.equalsIgnoreCase("none")){
            key = -1;
        }

        if (key == 0){
            sendChatMessage("Unknown key '" + rkey + "'!");
            return;
        }

        m.getBind().setKey(key);
        sendChatMessage("Bind for &b" + m.getName() + "&r set to &b" + rkey.toUpperCase());
    }
}
