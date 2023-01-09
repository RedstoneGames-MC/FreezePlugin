package chat.belinked.freeze;

import chat.belinked.freeze.commands.FreezeCommand;
import chat.belinked.freeze.events.FreezeEvents;
import chat.belinked.freeze.utils.FreezeAPI;
import dev.sergiferry.playernpc.api.NPCLib;
import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class Freeze extends JavaPlugin implements TabExecutor {

    public final static String PREFIX_MAIN = "§6§lRedstoneGames §r§0» §r";
    public final static String PREFIX_ERROR = "§6§lRedstoneGames §r§4ERROR §0» §r";
    public static final String PLAYER_FROZEN_RELOAD_TAG = "frozenReload";
    public static Freeze instance;

    @Override
    public void onEnable() {

        instance = this;
        FreezeCommand cmd = new FreezeCommand();
        NPCLib.getInstance().registerPlugin(this);

        // Plugin startup logic
        this.getCommand("freeze").setExecutor(cmd);
        this.getCommand("freeze").setTabCompleter(cmd);

        this.getServer().getPluginManager().registerEvents(new FreezeEvents(), this);

        // recover players from last session
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getScoreboardTags().contains(PLAYER_FROZEN_RELOAD_TAG)) {
                FreezeAPI.freezePlayer(p);
                FreezeAPI.frozenPlayers.add(p.getUniqueId());
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(UUID uid : FreezeAPI.frozenPlayers) {
            Bukkit.getPlayer(uid).addScoreboardTag(PLAYER_FROZEN_RELOAD_TAG);
            FreezeAPI.unfreezePlayer(Bukkit.getPlayer(uid));
        }
    }
}
