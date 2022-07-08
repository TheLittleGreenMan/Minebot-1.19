package net.darkabyss.minebot.accounts;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.darkabyss.minebot.Minebot;
@SuppressWarnings("unchecked")
public class LinkedAccounts {
    public JSONArray linkedplayers = new JSONArray();
    public LinkedAccounts()
    {
        JSONParser jsonParser = new JSONParser();
        if(!Minebot.getLinkedAccountsInfo().exists() || Minebot.getLinkedAccountsInfo().length() == 0)
            return;
        try {
            FileReader reader = new FileReader(Minebot.getLinkedAccountsInfo().getPath());
            Object obj = jsonParser.parse(reader);
            linkedplayers = (JSONArray) obj;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void newlinked(String discordID, UUID playerUUID)
    {
        JSONObject playerdetails = new JSONObject();
        playerdetails.put("discordID", discordID);
        //playerdetails.put("silentJoin", false); //silence join messages to discord on or off
        //playerdetails.put("silentQuit", false); //silence quit messages to discord on or off
        //playerdetails.put("silentDeath", false); //silence death messages to discord on or off
        //playerdetails.put("silentAdvance", false); //silence advancements messages to discord on or off
        //playerdetails.put("discordChat", false); //send messages to discord on or off

        JSONObject player = new JSONObject();
        player.put("player", playerdetails);
        linkedplayers.add(player);
    }

    public void save()
    {
        try(FileWriter file = new FileWriter(Minebot.getLinkedAccountsInfo().getPath())) {
            file.write(linkedplayers.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
