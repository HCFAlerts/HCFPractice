package me.hcfalerts.practice.kit.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.kit.KitKnockbackEditData;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import me.hcfalerts.practice.utilities.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class KitEditRulesMenu extends Menu {

    private final Kit kit;

    {
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + kit.getName() + CC.GRAY + " »" + CC.DARK_GRAY + " Editing Rules";
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(0, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Build")
                        .durability(kit.getGameRules().isBuild() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setBuild(!kit.getGameRules().isBuild());
            }
        });

        buttons.put(1, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Spleef")
                        .durability(kit.getGameRules().isSpleef() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setSpleef(!kit.getGameRules().isSpleef());
            }
        });

        buttons.put(2, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Sumo")
                        .durability(kit.getGameRules().isSumo() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setSumo(!kit.getGameRules().isSumo());
            }
        });

        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Parkour")
                        .durability(kit.getGameRules().isParkour() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setParkour(!kit.getGameRules().isParkour());
            }
        });

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Health Regeneration")
                        .durability(kit.getGameRules().isHealthRegeneration() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setHealthRegeneration(!kit.getGameRules().isHealthRegeneration());
            }
        });

        buttons.put(5, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Ranked")
                        .durability(kit.getGameRules().isRanked() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setRanked(!kit.getGameRules().isRanked());
                for (Queue queue : Queue.getQueues()) {
                    if (queue.getKit().getName().equalsIgnoreCase(kit.getName())) {
                        queue.setRanked(kit.getGameRules().isRanked());
                        break;
                    }
                }
            }
        });

        buttons.put(6, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Show Health")
                        .durability(kit.getGameRules().isShowHealth() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setShowHealth(!kit.getGameRules().isShowHealth());
            }
        });

        buttons.put(7, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "HCF")
                        .durability(kit.getGameRules().isHcf() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setHcf(!kit.getGameRules().isHcf());
            }
        });

        buttons.put(8, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Bridge")
                        .durability(kit.getGameRules().isBridge() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setBridge(!kit.getGameRules().isBridge());
            }
        });

        buttons.put(9, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Boxing")
                        .durability(kit.getGameRules().isBoxing() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setBoxing(!kit.getGameRules().isBoxing());
            }
        });

        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "HCF Trap")
                        .durability(kit.getGameRules().isHcfTrap() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setHcfTrap(!kit.getGameRules().isHcfTrap());
            }
        });

        buttons.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "SkyWars")
                        .durability(kit.getGameRules().isSkywars() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setSkywars(!kit.getGameRules().isSkywars());
            }
        });

        buttons.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "No Food")
                        .durability(kit.getGameRules().isNoFood() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setNoFood(!kit.getGameRules().isNoFood());
            }
        });

        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "No Fall Damage")
                        .durability(kit.getGameRules().isNoFallDamage() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setNoFallDamage(!kit.getGameRules().isNoFallDamage());
            }
        });

        buttons.put(14, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Soup")
                        .durability(kit.getGameRules().isSoup() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().setSoup(!kit.getGameRules().isSoup());
            }
        });

        buttons.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(CC.PINK + "Potion Fill")
                        .durability(kit.getEditRules().isAllowPotionFill() ? 5 : 14)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getEditRules().setAllowPotionFill(!kit.getEditRules().isAllowPotionFill());
            }
        });

        buttons.put(16, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .durability(8)
                        .name(CC.PINK + "Hit Delay")
                        .lore("", CC.GRAY + "Current: &f" + kit.getGameRules().getHitDelay(),
                                "", CC.GRAY + "Click to open Menu editor.")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitHitDelayMenu(kit).openMenu(player);
            }
        });

        buttons.put(17, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .durability(8)
                        .name(CC.PINK + "Knockback Profile")
                        .lore("", CC.GRAY + "Current: &f" + kit.getGameRules().getKbProfile(),
                                "", CC.GRAY + "Click to open Menu editor.")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                Profile.get(player.getUniqueId()).setKitKnockbackEditData(new KitKnockbackEditData(kit));
                player.closeInventory();
                player.sendMessage(CC.PINK + "Insert Knockback Profile name.");
                player.sendMessage(CC.PINK + "If you need to cancel this process just write \"cancel\" in the chat");
            }
        });

        buttons.put(18, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> effects = Lists.newArrayList();
                effects.add(Strings.EMPTY);
                for (PotionEffect effect : kit.getGameRules().getEffects()) {
                    effects.add(CC.GRAY + " » " +
                            CC.PINK + effect.getType().getName() + " " +
                            CC.PINK + effect.getAmplifier() + " " +
                            CC.PINK + effect.getDuration());
                }
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .durability(8)
                        .name(CC.translate("&dEffects &7(&fEffect&7:&fAmplifier&7:&fDuration&7)"))
                        .lore(effects)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitEditEffectsMenu(kit).openMenu(player);
            }
        });

        buttons.put(19, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .durability(8)
                        .name(CC.PINK + "Editor Items")
                        .lore("", CC.GRAY + "Click to open Menu editor.")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitEditorItemsOptionMenu(kit).openMenu(player);
            }
        });

        buttons.put(getSize() - 1, new BackButton(new KitEditorMenu(kit)));

        return buttons;
    }
}
