package me.hcfalerts.practice.chat.impl;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.chat.PracticeChatFormat;
import me.hcfalerts.practice.chat.impl.filter.ChatFilter;
import me.hcfalerts.practice.chat.impl.format.DefaultChatFormat;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.Cooldown;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Chat {

	private final HCFPractice plugin = HCFPractice.get();

	@Getter @Setter private static int delayTime = 3;
	@Getter private static boolean publicChatMuted = false;
	@Getter private static boolean publicChatDelayed = false;
	@Getter private static final List<ChatFilter> filters = new ArrayList<>();
	@Getter private static final List<String> filteredPhrases = new ArrayList<>();
	@Getter private static final List<String> linkWhitelist = new ArrayList<>();
	@Getter @Setter public static ChatFormat chatFormat = new DefaultChatFormat();

	public static void togglePublicChatMute() {
		publicChatMuted = !publicChatMuted;
	}

	public static void togglePublicChatDelay() {
		publicChatDelayed = !publicChatDelayed;
	}

	public static void init() {
		if (HCFPractice.get().getMainConfig().getBoolean("CHAT.ACTIVE")) {
			HCFPractice.get().getServer().getPluginManager().registerEvents(new ChatListener(), HCFPractice.get());
			setChatFormat(new PracticeChatFormat());
			System.out.println("[HCFPractice] Chat Format successfully enabled");
		}
	}

	public static ChatAttempt attemptChatMessage(Player player, String message) {
		Profile profile = Profile.get(player.getUniqueId());

		if (publicChatMuted && !player.hasPermission("practice.staff")) {
			return new ChatAttempt(ChatAttempt.Response.CHAT_MUTED);
		}

		if (publicChatDelayed && !profile.getChatCooldown().hasExpired() && !player.hasPermission("practice.staff")) {
			ChatAttempt attempt = new ChatAttempt(ChatAttempt.Response.CHAT_DELAYED);
			attempt.setValue(profile.getChatCooldown().getRemaining());
			return attempt;
		}

		String msg = message.toLowerCase()
		                    .replace("3", "e")
		                    .replace("1", "i")
		                    .replace("!", "i")
		                    .replace("@", "a")
		                    .replace("7", "t")
		                    .replace("0", "o")
		                    .replace("5", "s")
		                    .replace("8", "b")
		                    .replaceAll("\\p{Punct}|\\d", "").trim();

		String[] words = msg.trim().split(" ");

		for (ChatFilter chatFilter : filters) {
			if (chatFilter.isFiltered(msg, words)) {
				return new ChatAttempt(ChatAttempt.Response.MESSAGE_FILTERED);
			}
		}

		if (publicChatDelayed) {
			profile.setChatCooldown(new Cooldown(delayTime * 1000L));
		}

		return new ChatAttempt(ChatAttempt.Response.ALLOWED);
	}

}
