package chat.belinked.freeze;

import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static chat.belinked.freeze.FreezeAPI.freezePlayer;
import static chat.belinked.freeze.FreezeAPI.unfreezePlayer;

public final class Freeze extends JavaPlugin implements TabExecutor, Listener {

    public final static String PREFIX_MAIN = "§6§lRedstoneGames §r§0» §r";
    public final static String PREFIX_ERROR = "§6§lRedstoneGames §r§4ERROR §0» §r";
    public static Freeze instance;

    @Override
    public void onEnable() {
        instance = this;
        NPCLib.getInstance().registerPlugin(this);

        // Plugin startup logic
        this.getCommand("freeze").setExecutor(this);
        this.getCommand("freeze").setTabCompleter(this);

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        if(!(sender instanceof Player)) return true;
        if(!sender.isOp()) {
            sender.sendMessage(PREFIX_ERROR + "Insufficient permissions!");
            return true;
        }

        Player p = (Player) sender;

        if(cmd.getName().equals("freeze")) {
            if(args.length == 0) {
                sender.sendMessage("Freeze Command Help");
                sender.sendMessage("-------------------");
                sender.sendMessage("/freeze enable <player>");
                sender.sendMessage("/freeze disable <player>");
                sender.sendMessage("/freeze toggle <player>");
                sender.sendMessage("/freeze clear");
                return true;
            }

            if(args[0].equals("enable") || args[0].equals("disable") || args[0].equals("toggle")) {
                if(args.length == 1) {
                    sender.sendMessage(PREFIX_ERROR + "Please enter a player name!");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    sender.sendMessage(PREFIX_ERROR + "This player is not online!");
                    return true;
                }

                switch(args[0]) {
                    case "enable":
                        if(!FreezeAPI.frozenPlayers.contains(target.getUniqueId())) {
                            FreezeAPI.freezePlayer(target);
                            sender.sendMessage(PREFIX_MAIN + "Successfully freezed player " + target.getName() + "!");
                            FreezeAPI.frozenPlayers.add(p.getUniqueId());

                        } else {
                            sender.sendMessage(PREFIX_MAIN + "This player is already frozen!");
                        }
                        break;
                    case "disable":
                        if(FreezeAPI.frozenPlayers.contains(target.getUniqueId())) {
                            FreezeAPI.unfreezePlayer(target);
                            sender.sendMessage(PREFIX_MAIN + "Successfully unfreezed player " + target.getName() + "!");
                            FreezeAPI.frozenPlayers.remove(p.getUniqueId());

                        } else {
                            sender.sendMessage(PREFIX_MAIN + "This player is not frozen!");
                        }
                        break;
                    case "toggle":
                        FreezeAPI.toggleFreeze(p);
                        if(FreezeAPI.frozenPlayers.contains(p.getUniqueId())) {
                            FreezeAPI.frozenPlayers.remove(p.getUniqueId());
                        } else {
                            FreezeAPI.frozenPlayers.add(p.getUniqueId());
                        }
                        sender.sendMessage(PREFIX_MAIN + "Successfully toggled player " + target.getName() + "!");
                        break;
                }
            } else if(args[0].equals("clear")) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(FreezeAPI.frozenPlayers.contains(player.getUniqueId())) {
                        unfreezePlayer(player);
                        FreezeAPI.frozenPlayers.remove(p.getUniqueId());
                    }
                }
                for(Entity en : p.getWorld().getEntities()) {
                    if(en.getScoreboardTags().contains(FreezeAPI.FREEZE_STAND_TAG)) {
                        en.remove();
                    }
                }
                for(NPC.Global g : NPCLib.getInstance().getAllGlobalNPCs()) {
                    NPCLib.getInstance().removeGlobalNPC(g);
                }
            } else {
                sender.sendMessage(PREFIX_MAIN + "Unknown argument '" + args[0] + "'!");
            }

        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String args[]) {
        List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            if("enable".startsWith(args[0])) {
                completions.add("enable");
            }
            if("disable".startsWith(args[0])) {
                completions.add("disable");
            }
            if("toggle".startsWith(args[0])) {
                completions.add("toggle");
            }
            if("clear".startsWith(args[0])) {
                completions.add("clear");
            }
        }
        if(args.length == 2) {
            if(args[0].equals("enable") || args[0].equals("toggle")) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(p.getName().startsWith(args[1])) {
                        completions.add(p.getName());
                    }
                }
            }
            if(args[0].equals("disable")) {
                for(UUID uid : FreezeAPI.frozenPlayers) {
                    Player p = Bukkit.getPlayer(uid);
                    if(p.getName().startsWith(args[1])) {
                        completions.add(p.getName());
                    }
                }
            }
        }
        return completions;
    }

    @EventHandler
    public void onDismount(PlayerToggleSneakEvent e) {
        if(FreezeAPI.frozenPlayers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        if(FreezeAPI.frozenPlayers.contains(e.getPlayer().getUniqueId())) {
            FreezeAPI.unfreezePlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        if(FreezeAPI.frozenPlayers.contains(e.getPlayer().getUniqueId())) {
            new BukkitRunnable() {
                public void run() {
                    FreezeAPI.freezePlayer(e.getPlayer());
                }
            }.runTaskLater(this, 10L);
        }
    }
}
