package me.hcfalerts.practice.event.game.command;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.event.Event;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.event.game.map.vote.EventGameMapVoteData;
import me.hcfalerts.practice.event.game.menu.EventHostMenu;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.DiscordWebhook;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventHostCommand extends BaseCommand {

	@Command(name = "host", permission = "practice.event.host")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			new EventHostMenu().openMenu(player);
		}
		else if (args.length == 1) {
			Event event = Event.getByName(args[0]);
			if (event == null) {
				player.sendMessage(CC.RED + "This event doesn't exist.");
				return;
			}

			if (EventGame.getActiveGame() != null) {
				player.sendMessage(CC.RED + "There is already an active event.");
				return;
			}

			if (!EventGame.getCooldown().hasExpired()) {
				player.sendMessage(CC.RED + "The event cooldown is active.");
				return;
			}

			if (EventGameMap.getMaps().isEmpty()) {
				player.sendMessage(CC.RED + "There are no available event maps.");
				return;
			}

			List<EventGameMap> validMaps = new ArrayList<>();

			for (EventGameMap gameMap : EventGameMap.getMaps()) {
				if (event.getAllowedMaps().contains(gameMap.getMapName())) {
					validMaps.add(gameMap);
				}
			}

			if (validMaps.isEmpty()) {
				player.sendMessage(CC.RED + "There are no available event maps.");
				return;
			}

			if (HCFPractice.get().isWebHook()) {
				BasicConfigurationFile webhookConfig = HCFPractice.get().getDiscordWebhookConfig();
				try {
					DiscordWebhook webhook = HCFPractice.get().getDiscordWebhook();
					webhook.setUsername(webhookConfig.getString("EVENT.USERNAME"));
					webhook.setContent(webhookConfig.getString("EVENT.CONTENT")
							.replace("{type}", event.getName())
							.replace("{slots}", String.valueOf(Profile.getHostSlots(player.getUniqueId())))
							.replace("{hoster}", player.getName()));
					webhook.setTts(webhookConfig.getBoolean("EVENT.TTS"));
					webhook.setAvatarUrl(webhookConfig.getString("EVENT.AVATAR_URL"));

					DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
					embed.setTitle(webhookConfig.getString("EVENT.EMBED.TITLE"));
					try {
						embed.setColor(Color.getColor(webhookConfig.getString("EVENT.EMBED.COLOR")));
					} catch (Exception e) {
						throw new IOException("Invalid color on DiscordWebhook.");
					}
					embed.setDescription(webhookConfig.getString("EVENT.EMBED.DESCRIPTION")
							.replace("{type}", event.getName())
							.replace("{slots}", String.valueOf(Profile.getHostSlots(player.getUniqueId())))
							.replace("{hoster}", player.getName()));
					embed.setUrl(webhookConfig.getString("EVENT.EMBED.URL"));
					embed.setImage(webhookConfig.getString("EVENT.EMBED.IMAGE"));
					embed.setFooter(webhookConfig.getString("EVENT.EMBED.FOOTER.TEXT"),
							webhookConfig.getString("EVENT.EMBED.FOOTER.ICON_URL"));
					embed.setAuthor(webhookConfig.getString("EVENT.EMBED.AUTHOR.NAME"),
							webhookConfig.getString("EVENT.EMBED.AUTHOR.URL"),
							webhookConfig.getString("EVENT.EMBED.AUTHOR.ICON_URL"));
					embed.setThumbnail(webhookConfig.getString("EVENT.EMBED.THUMBNAIL"));
					for (String s : webhookConfig.getStringList("EVENT.EMBED.FIELDS")) {
						embed.addField(s
								.replace("{type}", event.getName())
								.replace("{slots}", String.valueOf(Profile.getHostSlots(player.getUniqueId())))
								.replace("{hoster}", player.getName()));
					}
					webhook.addEmbed(embed);

					webhook.execute();
				} catch (IOException e) {
					throw new IllegalArgumentException("Please check if your webhook url is valid or send your discord-webhook.yml in support discord");
				}
			}

			try {
				EventGame game = new EventGame(event, player, Profile.getHostSlots(player.getUniqueId()));

				for (EventGameMap gameMap : validMaps) {
					game.getVotesData().put(gameMap, new EventGameMapVoteData());
				}

				game.broadcastJoinMessage();
				game.start();
				game.getGameLogic().onJoin(player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			Event event = Event.getByName(args[0]);
			if (event == null) {
				player.sendMessage(CC.RED + "This event doesn't exist.");
				return;
			}

			int slots;
			if (!StringUtils.isNumeric(args[1])) {
				player.sendMessage(CC.RED + "Please insert a valid Integer.");
				return;
			}
			slots = Integer.getInteger(args[1]);

			if (!player.hasPermission("practice.event.admin")) {
				player.sendMessage(CC.RED + "You don't have permissions to host with Slots.");
				return;
			}

			if (EventGame.getActiveGame() != null) {
				player.sendMessage(CC.RED + "There is already an active event.");
				return;
			}

			if (!EventGame.getCooldown().hasExpired()) {
				player.sendMessage(CC.RED + "The event cooldown is active.");
				return;
			}

			if (EventGameMap.getMaps().isEmpty()) {
				player.sendMessage(CC.RED + "There are no available event maps.");
				return;
			}

			List<EventGameMap> validMaps = new ArrayList<>();

			for (EventGameMap gameMap : EventGameMap.getMaps()) {
				if (event.getAllowedMaps().contains(gameMap.getMapName())) {
					validMaps.add(gameMap);
				}
			}

			if (validMaps.isEmpty()) {
				player.sendMessage(CC.RED + "There are no available event maps.");
				return;
			}

			if (HCFPractice.get().isWebHook()) {
				BasicConfigurationFile webhookConfig = HCFPractice.get().getDiscordWebhookConfig();
				try {
					DiscordWebhook webhook = HCFPractice.get().getDiscordWebhook();
					webhook.setUsername(webhookConfig.getString("EVENT.USERNAME"));
					webhook.setContent(webhookConfig.getString("EVENT.CONTENT")
							.replace("{type}", event.getName())
							.replace("{slots}", String.valueOf(slots))
							.replace("{hoster}", player.getName()));
					webhook.setTts(webhookConfig.getBoolean("EVENT.TTS"));
					webhook.setAvatarUrl(webhookConfig.getString("EVENT.AVATAR_URL"));

					DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
					embed.setTitle(webhookConfig.getString("EVENT.EMBED.TITLE"));
					try {
						embed.setColor(Color.getColor(webhookConfig.getString("EVENT.EMBED.COLOR")));
					} catch (Exception e) {
						throw new IOException("Invalid color on DiscordWebhook.");
					}
					embed.setDescription(webhookConfig.getString("EVENT.EMBED.DESCRIPTION")
							.replace("{type}", event.getName())
							.replace("{slots}", String.valueOf(slots))
							.replace("{hoster}", player.getName()));
					embed.setUrl(webhookConfig.getString("EVENT.EMBED.URL"));
					embed.setImage(webhookConfig.getString("EVENT.EMBED.IMAGE"));
					embed.setFooter(webhookConfig.getString("EVENT.EMBED.FOOTER.TEXT"),
							webhookConfig.getString("EVENT.EMBED.FOOTER.ICON_URL"));
					embed.setAuthor(webhookConfig.getString("EVENT.EMBED.AUTHOR.NAME"),
							webhookConfig.getString("EVENT.EMBED.AUTHOR.URL"),
							webhookConfig.getString("EVENT.EMBED.AUTHOR.ICON_URL"));
					embed.setThumbnail(webhookConfig.getString("EVENT.EMBED.THUMBNAIL"));
					for (String s : webhookConfig.getStringList("EVENT.EMBED.FIELDS")) {
						embed.addField(s
								.replace("{type}", event.getName())
								.replace("{slots}", String.valueOf(slots))
								.replace("{hoster}", player.getName()));
					}
					webhook.addEmbed(embed);

					webhook.execute();
				} catch (IOException e) {
					throw new IllegalArgumentException("Please check if your webhook url is valid or send your discord-webhook.yml in support discord");
				}
			}

			try {
				EventGame game = new EventGame(event, player, slots);

				for (EventGameMap gameMap : validMaps) {
					game.getVotesData().put(gameMap, new EventGameMapVoteData());
				}

				game.broadcastJoinMessage();
				game.start();
				game.getGameLogic().onJoin(player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
