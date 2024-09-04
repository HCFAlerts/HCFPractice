package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.regex.Pattern;

public class KitSetRuleCommand extends BaseCommand {

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    @Command(name = "kit.setrule", permission = "practice.kit.admin")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0 || args.length == 1) {
            player.sendMessage(CC.RED + "Please usage: /kit setrule <kit> <rule> <value>");
            return;
        }

        Kit kit = Kit.getByName(args[0]);
        String rule = args[1];
        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        if (args.length > 2) {
            String value = args[2];
            switch (rule.toLowerCase()) {
                case "build": {
                    kit.getGameRules().setBuild(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now build.");
                    else player.sendMessage(CC.RED + "Kit is no longer build.");
                    break;
                }
                case "spleef": {
                    kit.getGameRules().setSpleef(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now spleef.");
                    else player.sendMessage(CC.RED + "Kit is no longer spleef.");
                    break;
                }
                case "sumo": {
                    kit.getGameRules().setSumo(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now sumo.");
                    else player.sendMessage(CC.RED + "Kit is no longer sumo.");
                    break;
                }
                case "parkour": {
                    kit.getGameRules().setParkour(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now parkour.");
                    else player.sendMessage(CC.RED + "Kit is no longer parkour.");
                    break;
                }
                case "healthregeneration": {
                    kit.getGameRules().setHealthRegeneration(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now healthregeneration.");
                    else player.sendMessage(CC.RED + "Kit is no longer healthregeneration.");
                    break;
                }
                case "showhealth": {
                    kit.getGameRules().setShowHealth(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now show health.");
                    else player.sendMessage(CC.RED + "Kit is no longer show health.");
                    break;
                }
                case "hcf": {
                    kit.getGameRules().setHcf(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now HCF Mode.");
                    else player.sendMessage(CC.RED + "Kit is no longer HCF Mode.");
                    break;
                }
                case "hitdelay": {
                    if (pattern.matcher(value).matches()) {
                        kit.getGameRules().setHitDelay(Integer.parseInt(value));
                        player.sendMessage(CC.GREEN + "Kit hitdelay is " + Integer.parseInt(value) + ".");
                    } else player.sendMessage(CC.RED + "Please insert valid value.");
                    break;
                }
                case "kbprofile": {
                    kit.getGameRules().setKbProfile(value);
                    player.sendMessage(CC.GREEN + "Kit now have kbprofile: " + value);
                    break;
                }
                case "bridge": {
                    kit.getGameRules().setBridge(Boolean.parseBoolean(value));

                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now bridge.");
                    else player.sendMessage(CC.RED + "Kit is no longer bridge.");

                    break;
                }
                case "ranked": {
                    kit.getGameRules().setRanked(Boolean.parseBoolean(value));
                    for (Queue queue : Queue.getQueues()) {
                        if (queue.getKit().getName().equalsIgnoreCase(kit.getName())) {
                            queue.setRanked(kit.getGameRules().isRanked());
                            break;
                        }
                    }

                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now ranked.");
                    else player.sendMessage(CC.RED + "Kit is no longer ranked.");

                    break;
                }
                case "boxing": {
                    kit.getGameRules().setBoxing(Boolean.parseBoolean(value));

                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now boxing.");
                    else player.sendMessage(CC.RED + "Kit is no longer boxing.");

                    break;
                }
                case "allowpotionfill": {
                    kit.getEditRules().setAllowPotionFill(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now allow potion fill.");
                    else player.sendMessage(CC.RED + "Kit is no longer allow potion fill.");
                    break;
                }
                case "hcftrap": {
                    kit.getGameRules().setHcfTrap(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now allow HCF Trap Mode.");
                    else player.sendMessage(CC.RED + "Kit is no longer allow HCF Trap Mode.");
                    break;
                }
                case "skywars": {
                    kit.getGameRules().setSkywars(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now allow skywars rule.");
                    else player.sendMessage(CC.RED + "Kit is no longer allow skywars rule.");
                    break;
                }
                case "nofood": {
                    kit.getGameRules().setNoFood(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now allow nofood rule.");
                    else player.sendMessage(CC.RED + "Kit is no longer allow nofood rule.");
                    break;
                }
                case "nofall": {
                    kit.getGameRules().setNoFallDamage(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now allow nofall-damage rule.");
                    else player.sendMessage(CC.RED + "Kit is no longer allow nofall-damage rule.");
                    break;
                }
                case "soup": {
                    kit.getGameRules().setSoup(Boolean.parseBoolean(value));
                    if (Boolean.parseBoolean(value)) player.sendMessage(CC.GREEN + "Kit is now allow soup rule.");
                    else player.sendMessage(CC.RED + "Kit is no longer allow soup rule.");
                    break;
                }
                case "effect": {
                    if (args[2].equalsIgnoreCase("add")) {
                        if (args.length < 5) {
                            player.sendMessage(CC.RED + "Please usage: /kit setrule " + kit.getName() + "effect add <effect> <amplifier>");
                            return;
                        }

                        PotionEffectType effectType = PotionEffectType.getByName(args[3]);
                        if (effectType == null) {
                            player.sendMessage(CC.RED + "This effect doesn't exist.");
                            return;
                        }

                        int amplifier;
                        if (!StringUtils.isNumeric(args[4])) {
                            player.sendMessage(CC.RED + "Use a valid amplifier.");
                            return;
                        }
                        amplifier = Integer.parseInt(args[4]);

                        kit.getGameRules().getEffects().add(new PotionEffect(effectType, Integer.MAX_VALUE, amplifier - 1));
                        player.sendMessage(CC.RED + "Effect " + effectType.getName() + " added correctly.");
                    }
                    else if (args[2].equalsIgnoreCase("remove")) {
                        if (args.length < 4) {
                            player.sendMessage(CC.RED + "Please usage: /kit setrule " + kit.getName() + "effect add <effect> <amplifier>");
                            return;
                        }

                        PotionEffectType effectType = PotionEffectType.getByName(args[3]);
                        if (effectType == null) {
                            player.sendMessage(CC.RED + "This effect doesn't exist.");
                            return;
                        }

                        kit.getGameRules().getEffects().removeIf(effect -> effect.getType().getName().equalsIgnoreCase(args[3]));
                        player.sendMessage(CC.RED + "Effect " + effectType.getName() + " removed correctly.");
                    }
                    else if (args[2].equalsIgnoreCase("list")) {
                        if (kit.getGameRules().getEffects().isEmpty()) {
                            player.sendMessage(CC.RED + "This list is empty.");
                            return;
                        }

                        for (PotionEffect effect : kit.getGameRules().getEffects()) {
                            player.sendMessage(CC.translate("&7[&c-&7] &b" + effect.getType().getName() + " &7(" + (effect.getAmplifier() + 1) + ")"));
                        }
                    }
                    else if (args[2].equalsIgnoreCase("help")) {
                        player.sendMessage(CC.RED + "Please usage: ");
                        player.sendMessage(CC.RED + "/kit setrule " + kit.getName() + " effect list");
                        player.sendMessage(CC.RED + "/kit setrule " + kit.getName() + " effect add <effect> <amplifier>");
                        player.sendMessage(CC.RED + "/kit setrule " + kit.getName() + " effect remove <effect>");
                    }
                    break;
                }
                default: {
                    player.sendMessage(CC.RED + "A rule with that name does not exist.");
                    break;
                }
            }
            kit.save();
        } else {
            if (rule.equalsIgnoreCase("editoritems")) {
                kit.getEditRules().getEditorItems().clear();
                for (ItemStack content : player.getInventory().getContents()) {
                    if (content != null && content.getType() != Material.AIR)
                        kit.getEditRules().getEditorItems().add(content);
                }
                player.sendMessage(CC.GREEN + "Kit editor items update."); // TODO: Mejorar
            }
            else if (rule.equalsIgnoreCase("effect")) {
                player.sendMessage(CC.RED + "Please usage: ");
                player.sendMessage(CC.RED + "/kit setrule " + kit.getName() + " effect list");
                player.sendMessage(CC.RED + "/kit setrule " + kit.getName() + " effect add <effect> <amplifier>");
                player.sendMessage(CC.RED + "/kit setrule " + kit.getName() + " effect remove <effect>");
            }
            else {
                player.sendMessage(CC.RED + "/kit setrule <rule> <value>");
            }
        }
    }
}
