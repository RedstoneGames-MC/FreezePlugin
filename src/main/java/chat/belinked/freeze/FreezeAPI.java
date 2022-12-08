package chat.belinked.freeze;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class FreezeAPI {
    public static String FREEZE_STAND_TAG = "freezeStand";
    private static HashMap<UUID, ArmorStand> entities = new HashMap<>();
    private static HashMap<UUID, NPC.Global> npcs = new HashMap<>();

    /**
     * Helper function to freeze a player
     * @param p the player to be frozen
     * @return true if the player was successfully frozen
     * false if the player was already frozen
     * */
    public static void freezePlayer(Player p) {
        ArmorStand a = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);

        NPC.Global npc = NPCLib.getInstance().generateGlobalNPC(Freeze.instance, p.getName(), p.getLocation());
        npc.setSkin(NPCLib.getInstance().getDefaults().getSkin());
        npc.setText(p.getName(), "[FROZEN]");
        npc.forceUpdateText();

        EntityPlayer playerNMS = ((CraftPlayer) p).getHandle();
        GameProfile profile = playerNMS.getBukkitEntity().getProfile();

        Property property = profile.getProperties().get("textures").iterator().next();

        String texture = property.getValue();
        String signature = property.getSignature();

        npc.setHelmet(p.getEquipment().getHelmet());
        npc.setChestPlate(p.getEquipment().getChestplate());
        npc.setLeggings(p.getEquipment().getLeggings());
        npc.setBoots(p.getEquipment().getBoots());
        npc.setItemInLeftHand(p.getEquipment().getItemInMainHand());
        npc.setItemInRightHand(p.getEquipment().getItemInOffHand());

        npc.update();
        npc.forceUpdate();

        npc.setSkin(texture, signature);
        npc.update();
        npc.forceUpdate();

        a.setVisible(false);
        a.setGravity(false);
        a.setMarker(true);
        a.addScoreboardTag(FREEZE_STAND_TAG);

        FreezeAPI.entities.put(p.getUniqueId(), a);
        FreezeAPI.npcs.put(p.getUniqueId(), npc);


        p.setGameMode(GameMode.SPECTATOR);
        p.setSpectatorTarget(a);
        p.sendTitle(ChatColor.AQUA + "You were frozen", "", 10, 40, 10);
    }

    public static void unfreezePlayer(Player p) {
        FreezeAPI.entities.get(p.getUniqueId()).remove();
        FreezeAPI.entities.remove(p.getUniqueId());
        NPCLib.getInstance().removeGlobalNPC(FreezeAPI.npcs.get(p.getUniqueId()));
        FreezeAPI.npcs.get(p.getUniqueId()).destroy();
        FreezeAPI.npcs.remove(p.getUniqueId());


        p.setGameMode(GameMode.SURVIVAL);
    }

    public static void toggleFreeze(Player p) {
        if(Freeze.frozenPlayers.contains(p.getUniqueId())) {
            unfreezePlayer(p);
        } else {
            freezePlayer(p);
        }
    }
}
