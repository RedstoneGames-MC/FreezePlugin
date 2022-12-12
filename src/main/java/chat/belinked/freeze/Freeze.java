package chat.belinked.freeze;

import chat.belinked.freeze.commands.FreezeCommand;
import chat.belinked.freeze.events.FreezeEvents;
import dev.sergiferry.playernpc.api.NPCLib;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Freeze extends JavaPlugin implements TabExecutor {

    public final static String PREFIX_MAIN = "§6§lRedstoneGames §r§0» §r";
    public final static String PREFIX_ERROR = "§6§lRedstoneGames §r§4ERROR §0» §r";
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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
