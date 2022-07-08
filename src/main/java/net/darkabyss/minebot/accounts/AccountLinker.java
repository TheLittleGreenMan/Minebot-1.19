package net.darkabyss.minebot.accounts;

import net.darkabyss.minebot.Minebot;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

import net.minecraft.sound.SoundCategory;
import org.apache.commons.io.FileUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import com.google.gson.JsonObject;

public class AccountLinker {
    final Map<String, UUID> linkingCodes = new HashMap<>();
    final Map<String, UUID> linkedAccounts = new HashMap<>();
    //DiscordRoleListener rolelistener = new DiscordRoleListener();

    public AccountLinker()
    {
        if(!Minebot.getLinkedAccountsFile().exists() || Minebot.getLinkedAccountsFile().length() == 0)
            return;
        try {
            Minebot.getGson().fromJson(FileUtils.readFileToString(Minebot.getLinkedAccountsFile(), StandardCharsets.UTF_8), JsonObject.class).entrySet().forEach(entry -> {
                try {
                    linkedAccounts.put(entry.getKey(), UUID.fromString(entry.getValue().getAsString()));
                } catch (Exception e) {
                    try {
                        linkedAccounts.put(entry.getValue().getAsString(), UUID.fromString(entry.getKey()));
                    } catch (Exception f) {
                        Minebot.getLogger().warn("It is highly recommended that you delete linkedaccounts.json in directory: MineBot");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateLinkCode(UUID playerUUID)
    {
        String codeString;
        Random rand = new Random();
        do {
            int code = rand.nextInt(10000);
            codeString = String.format("%04d", code);
        } while(linkingCodes.putIfAbsent(codeString, playerUUID) != null);
        return codeString;
    }

    public void process(String discordID, String code, PrivateChannel channel)
    {
        code = code.replaceAll("[^0-9]", "");

        if(!linkingCodes.containsKey(code))
        {
            channel.sendMessage("Link unsuccessful. Please send a code").queue();
            return;
        }
        link(discordID, linkingCodes.get(code));
        linkingCodes.remove(code);
        ServerPlayerEntity player = Minebot.mcserver.getPlayerManager().getPlayer(linkedAccounts.get(discordID));
        channel.sendMessage("Link Successful!").queue();
        if(!player.isDisconnected())
        {
            player.sendMessage(Text.of("Link Successful!"));
            player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.MASTER, 1, 1);
        }
    }

    public void link(String discordID, UUID playerUUID)
    {
        linkedAccounts.put(discordID, playerUUID);
        List<Guild> guild = Minebot.jda.getGuilds();
        Member member = guild.get(0).getMemberById(discordID);
        //rolelistener.updatePlayerRank(member, Bukkit.getPlayer(playerUUID));
        Minebot.accountinfo.newlinked(discordID, playerUUID);
    }

    public void save()
    {
        try {
            JsonObject map = new JsonObject();
            linkedAccounts.forEach((discordID, uuid) -> map.addProperty(discordID, String.valueOf(uuid)));
            FileUtils.writeStringToFile(Minebot.getLinkedAccountsFile(), map.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Minebot.getLogger().error("Linked Accounts save failed! "+e.getMessage());
            return;
        }
        Minebot.getLogger().info("Linked accounts were successfully saved!");
    }

    public Map<String, UUID> getLinkingCodes()
    {
        return linkingCodes;
    }

    public Map<String, UUID> getLinkedAccounts()
    {
        return linkedAccounts;
    }
}
