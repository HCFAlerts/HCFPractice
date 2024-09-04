package me.hcfalerts.practice.essentials;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.arena.command.ArenaCommand;
import me.hcfalerts.practice.arena.command.ArenasCommand;
import me.hcfalerts.practice.chat.impl.command.ClearChatCommand;
import me.hcfalerts.practice.chat.impl.command.MuteChatCommand;
import me.hcfalerts.practice.chat.impl.command.SlowChatCommand;
import me.hcfalerts.practice.clan.commands.ClanCommand;
import me.hcfalerts.practice.duel.command.*;
import me.hcfalerts.practice.essentials.command.*;
import me.hcfalerts.practice.event.command.EventCommand;
import me.hcfalerts.practice.event.command.EventsCommand;
import me.hcfalerts.practice.event.game.command.EventHostCommand;
import me.hcfalerts.practice.event.game.map.vote.command.EventMapVoteCommand;
import me.hcfalerts.practice.kit.command.EditorKitCommand;
import me.hcfalerts.practice.kit.command.KitCommand;
import me.hcfalerts.practice.kit.command.KitsCommand;
import me.hcfalerts.practice.leaderboard.commands.*;
import me.hcfalerts.practice.match.command.*;
import me.hcfalerts.practice.party.command.PartyCommand;
import me.hcfalerts.practice.profile.category.commands.CategoryCommand;
import me.hcfalerts.practice.profile.command.FlyCommand;
import me.hcfalerts.practice.profile.command.FollowCommand;
import me.hcfalerts.practice.profile.command.ViewMatchCommand;
import me.hcfalerts.practice.profile.conversation.command.MessageCommand;
import me.hcfalerts.practice.profile.conversation.command.ReplyCommand;
import me.hcfalerts.practice.profile.deatheffects.commands.DeathEffectCommand;
import me.hcfalerts.practice.profile.meta.option.command.*;
import me.hcfalerts.practice.profile.modmode.commands.StaffModeCommand;
import me.hcfalerts.practice.tournament.commands.TournamentCommand;

public class MainCommand {

    public static void init() {
        if (HCFPractice.get().getMainConfig().getBoolean("MESSAGE-REPLY-BOOLEAN")) {
            new MessageCommand();
            new ReplyCommand();
        }
        new ArenaCommand();
        new ArenasCommand();
        new DuelCommand();
        new DuelAcceptCommand();
        new EventCommand();
        new EventHostCommand();
        new EventsCommand();
        new EventMapVoteCommand();
        new RematchCommand();
        new SpectateCommand();
        new StopSpectatingCommand();
        new FlyCommand();
        new ViewMatchCommand();
        new PartyCommand();
        new KitCommand();
        new KitsCommand();
        new ViewInventoryCommand();
        new ToggleScoreboardCommand();
        new ToggleSpectatorsCommand();
        new ToggleDuelRequestsCommand();
        new ClanCommand();
        new CategoryCommand();
        new TournamentCommand();
        new ClearCommand();
        new DayCommand();
        new GameModeCommand();
        new PracticeCommand();
        new HealCommand();
        new LangCommand();
        new LocationCommand();
        new MoreCommand();
        new NightCommand();
        new PingCommand();
        new RenameCommand();
        new SetSlotsCommand();
        new SetSpawnCommand();
        new ShowAllPlayersCommand();
        new ShowPlayerCommand();
        new SpawnCommand();
        new SudoAllCommand();
        new SudoCommand();
        new SunsetCommand();
        new TeleportWorldCommand();
        new OptionsCommand();
        new ClearChatCommand();
        new SlowChatCommand();
        new MuteChatCommand();
        new EloCommand();
        new SetEloCommand();
        new ResetEloCommand();
        new CreateWorldCommand();
        new StatsCommand();
        new LeaderboardCommand();
        new RankedCommand();
        new UnRankedCommand();
        new ResetCommand();
        new ToggleGlobalChatCommand();
        new TogglePrivateMessagesCommand();
        new ToggleScoreboardCommand();
        new ToggleSoundsCommand();
        new ToggleSpectatorsCommand();
        new FollowCommand();
        new ForceQueueCommand();
        new RemoveWorldCommand();
        new ListWorldsCommand();
        new EditorKitCommand();
        new TimerCommand();
        new ServerInfoCommand();
        new CategoryCommand();
        new DeathEffectCommand();
        if (HCFPractice.get().getMainConfig().getBoolean("MOD_MODE")) new StaffModeCommand();
        if (HCFPractice.get().isLunarClient()) {
            new RallyCommand();
            new FocusCommand();
        }
    }
}
