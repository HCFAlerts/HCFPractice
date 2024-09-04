package me.hcfalerts.practice.profile.conversation;

import lombok.Getter;
import me.hcfalerts.practice.profile.Profile;
import org.bukkit.entity.Player;

import java.util.*;

public class ProfileConversations {

	@Getter private final Profile profile;
	@Getter private final Map<UUID, Conversation> conversations;

	public ProfileConversations(Profile profile) {
		this.profile = profile;
		this.conversations = new HashMap<>();
	}

	/*public boolean canBeMessagedBy(Player player) {

		//if(player.hasPermission("gxcore.staff.bypass")) return true;

		if (!profile.getOptions().receivingNewConversations()) {
			return conversations.containsKey(player.getUniqueId());
		}

		return !profile.getIgnoreList().contains(player.getUniqueId());
	}*/

	public Conversation getOrCreateConversation(Player target) {
		Player sender = profile.getPlayer();

		if (sender != null) {
			Conversation conversation = conversations.get(target.getUniqueId());

			if (conversation == null) {
				conversation = new Conversation(profile.getUuid(), target.getUniqueId());
			}

			return conversation;
		}

		return null;
	}

	public Conversation getLastRepliedConversation() {
		List<Conversation> list = new ArrayList<>(conversations.values());
		list.sort(Comparator.comparingLong(Conversation::getLastMessageSentAt));

		Collections.reverse(list);

		return list.isEmpty() ? null : list.get(0);
	}

	public void expireAllConversations() {
		this.conversations.clear();
	}

}
