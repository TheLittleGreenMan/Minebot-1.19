package net.darkabyss.minebot;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.JDABuilder;
//import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Minebot implements ModInitializer {
	public static final String MOD_ID = "minebot";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	final String token = "Njc0MDExOTc3Mjc3ODk4NzY0.GzX4K5.m3lQdX-P6zX2VkNSf1CRQithdNXUUEvKdZlRAI";
	public static final String chatid = "";
	public static final String commandchatid = "";
	public Gson gson = new GsonBuilder().setPrettyPrinting().create();
	//public static JDA jda = null;
	@Override
	public void onInitialize() {
	LOGGER.info("Hello Fabric World!");
	/*		try {
			 jda = JDABuilder.createDefault(token)
								.setAutoReconnect(true)
								.setActivity(Activity.playing("Minecraft"))
								.build().awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (LoginException e) {
			e.printStackTrace();
		}
*/
	}

}
