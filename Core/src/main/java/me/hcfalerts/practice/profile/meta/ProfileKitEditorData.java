package me.hcfalerts.practice.profile.meta;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.kit.KitLoadout;

public class ProfileKitEditorData {

	@Getter @Setter private boolean active;
	@Setter private boolean rename;
	@Getter @Setter private Kit selectedKit;
	@Getter @Setter private KitLoadout selectedKitLoadout;

	public boolean isRenaming() {
		return this.active && this.rename && this.selectedKit != null;
	}

}
