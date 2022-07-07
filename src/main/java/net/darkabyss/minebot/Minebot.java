package net.darkabyss.minebot;

import net.dv8tion.jda.api.AccountType;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Minebot implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("minebot");
	final String token = "Njc0MDExOTc3Mjc3ODk4NzY0.GzX4K5.m3lQdX-P6zX2VkNSf1CRQithdNXUUEvKdZlRAI";
	public static final String chatid = "";
	public static final String commandchatid = "";
	//static JDA jda = null;
	@Override
	public void onInitialize() {
		JDA jda = new JDABuilder.createDefault(token)
							.setAutoReconnect(true)
							.build().awaitReady();
	}

}
