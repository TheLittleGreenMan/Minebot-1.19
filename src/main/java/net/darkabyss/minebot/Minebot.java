package net.darkabyss.minebot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.darkabyss.minebot.accounts.AccountLinker;
import net.darkabyss.minebot.accounts.LinkedAccounts;

import net.darkabyss.minebot.listener.DiscordListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Minebot implements ModInitializer {

	public static final String MOD_ID = "Minebot";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	final static String token = "";
	public static final String chatid = "995002061089427577";
	public static final String commandchatid = "";
	public static AccountLinker linker;
	public static LinkedAccounts accountinfo;
	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static File linkedAccountsFile = new File(new File("").getAbsolutePath() , "Minebot/linkedaccounts.json");
	public static File linkedAccountsInfo = new File(new File("").getAbsolutePath() , "Minebot/linkedaccountsinfo.json");
	public static JDA jda = null;
	public static MinecraftDedicatedServer mcserver;

	@Override
	public void onInitialize()
	{
		startMinebot();

		//creates files if it doesn't already exist
		new File("Minebot").getAbsoluteFile().mkdirs();
		if(!linkedAccountsFile.exists())
			LOGGER.info("The file linkedaccounts.json was created in: "+linkedAccountsFile.getPath());
		try {
			linkedAccountsFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!linkedAccountsInfo.exists())
			LOGGER.info("The file linkedaccountsinfo.json was created in: "+linkedAccountsInfo.getPath());
		try {
			linkedAccountsInfo.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//loads linked accounts
		linker = new AccountLinker();
		accountinfo = new LinkedAccounts();

		//Server Start Event Registration and Discord Notificaton
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			jda.getTextChannelById(chatid).sendMessage(":white_check_mark: **Server has started**").queue();
			//Minebot.getLogger().info("Server Started!");
		});

		//link Command Registration and Execution
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, registrationEnvironment) -> {
			dispatcher.register(CommandManager.literal("link").executes(context -> {
				UUID playerUUID = context.getSource().getPlayer().getUuid();
				ServerPlayerEntity player = context.getSource().getPlayer();
				if(getAccountLinker().getLinkedAccounts().containsValue(playerUUID))
				{
					player.sendMessage(Text.of("Your account has already been linked"));
					return 1;
				}
				else if(getAccountLinker().getLinkingCodes().containsValue(playerUUID))
				{
					player.sendMessage(Text.of("You have already recieved a link code"));
					return 1;
				}
				else
				{
					String code = getAccountLinker().generateLinkCode(playerUUID);
					player.sendMessage(Text.of("Please DM code: "+code+" to MineBot to link your accounts"));
					return 1;
				}
			}));

		});

		ServerMessageEvents.GAME_MESSAGE.register((message, typeKey) -> {
			String msg = message.getString();
			if(!typeKey.equals(MessageType.TELLRAW_COMMAND))
				jda.getTextChannelById(chatid).sendMessage(msg).queue();
		});

		ServerMessageEvents.CHAT_MESSAGE.register(((message, sender, typeKey) -> {
			if(!typeKey.equals(MessageType.TELLRAW_COMMAND))
				jda.getTextChannelById(chatid).sendMessage(sender.getName().getString()+": "+ message.raw().getContent().getString()).queue();
		}));

		//Server Stop Event Registration, Discord Notification, Linked Account Files Saved, and Minebot Shutdown
		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
			jda.getTextChannelById(chatid).sendMessage(":octagonal_sign: **Server has stopped**").queue();
			accountinfo.save();
			linker.save();
			stopMinebot();
		});
	}

	public static void startMinebot()
	{
		LOGGER.info("Minebot - Initialization");
		try {
			jda = JDABuilder.createDefault(token)
					.setAutoReconnect(true)
					.setActivity(Activity.playing("Minecraft"))
					.addEventListeners(new DiscordListener())
					.build().awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public static void stopMinebot()
	{
		if(jda != null) {
			jda.shutdown();
			LOGGER.info("Minebot has been shutdown");
		}

	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static Gson getGson() {
		return gson;
	}

	public static File getLinkedAccountsFile()
	{
		return linkedAccountsFile;
	}

	public static File getLinkedAccountsInfo()
	{
		return linkedAccountsInfo;
	}

	public static AccountLinker getAccountLinker()
	{
		return linker;
	}

	public static LinkedAccounts getLinkedAccounts()
	{
		return accountinfo;
	}

	public static MinecraftDedicatedServer getServerVariable()
	{
		return mcserver;
	}

	public static void setServerVariable(MinecraftDedicatedServer server)
	{
		mcserver = server;
	}
}
