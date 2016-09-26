package me.monnierant.obole;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class DonationListeners implements Listener
{
	public Donation plugin;

	public DonationListeners(Donation plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if ((player.hasPermission("Donation.Admin")) && (player.getInventory().getItemInMainHand().getType() == Material.GOLD_AXE))
		{
			if (event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				plugin.getConfig().set("signwall.1.x",(event.getClickedBlock().getLocation().getX()));
				plugin.getConfig().set("signwall.1.y",(event.getClickedBlock().getLocation().getY()));
				plugin.getConfig().set("signwall.1.z",(event.getClickedBlock().getLocation().getZ()));
				plugin.getConfig().set("signwall.1.w", event.getClickedBlock().getLocation().getWorld().getName());
				plugin.saveConfig();
				event.setCancelled(true);
				player.sendMessage("Point one successfully set.");
			}
			else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				plugin.getConfig().set("signwall.2.x",(event.getClickedBlock().getLocation().getX()));
				plugin.getConfig().set("signwall.2.y",(event.getClickedBlock().getLocation().getY()));
				plugin.getConfig().set("signwall.2.z",(event.getClickedBlock().getLocation().getZ()));
				plugin.getConfig().set("signwall.2.w",(event.getClickedBlock().getLocation().getWorld().getName()));
				plugin.saveConfig();
				player.sendMessage("Point two successfully set.");
			}
		}
	}
}
