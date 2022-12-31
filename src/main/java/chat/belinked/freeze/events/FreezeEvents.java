package chat.belinked.freeze.events;

import chat.belinked.freeze.Freeze;
import chat.belinked.freeze.utils.FreezeAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FreezeEvents implements Listener {

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
            FreezeAPI.frozenPlayers.remove(e.getPlayer().getUniqueId());
            FreezeAPI.offlineFrozenPlayers.add(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        if(FreezeAPI.offlineFrozenPlayers.contains(e.getPlayer().getUniqueId())) {
            new BukkitRunnable() {
                public void run() {
                    FreezeAPI.freezePlayer(e.getPlayer());
                    FreezeAPI.frozenPlayers.add(e.getPlayer().getUniqueId());
                    FreezeAPI.offlineFrozenPlayers.remove(e.getPlayer().getUniqueId());
                }
            }.runTaskLater(Freeze.instance, 10L);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(FreezeAPI.frozenPlayers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
