package net.darkabyss.minebot.listener;

import net.darkabyss.minebot.Minebot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.List;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String author = message.getAuthor().getName();
        String content = message.getContentRaw();
        if (event.getAuthor().isBot())
            return;

        if(event.getChannel().getId().equals(Minebot.chatid)) {
            Minebot.getServerVariable().getPlayerManager().broadcast(Text.of("[Discord] " + author + ": " + content), MessageType.TELLRAW_COMMAND);

            /*List<Role> role = event.getMember().getRoles();

            if (role.isEmpty()) {
                //Bukkit.broadcastMessage(ChatColor.AQUA + "[Discord] " + ChatColor.RESET + author + ": " + content);
                return;
            }*/
        }

    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if(!event.getAuthor().isBot())
        {
            String code = event.getMessage().getContentRaw();
            String discordID = event.getAuthor().getId();
            PrivateChannel channel = event.getChannel();
            Minebot.getAccountLinker().process(discordID, code, channel);
        }
    }
}
