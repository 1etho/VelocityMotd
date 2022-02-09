package com.etho.motd.commands;

import com.etho.motd.utils.Chat;
import com.etho.motd.utils.Config;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.List;

public class MotdCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource sender = invocation.source();
        if (invocation.source().hasPermission("vmotd.command.reload")) {
            Config.init();
            sender.sendMessage(Chat.color("&cReloaded configuration"));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }
}
