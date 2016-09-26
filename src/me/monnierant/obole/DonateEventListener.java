package me.monnierant.obole;

import java.sql.ResultSet;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DonateEventListener implements Listener
{
	public Donation plugin;

	public DonateEventListener(Donation plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onDonate(DonateEvent event)
	{
		String user = event.getUsername();
		Double amount = event.getAmount();
		String item = event.getItemName();
		ResultSet SQLresults = plugin.getNewDonors();
		if (plugin.getConfig().getBoolean("settings.enablesignwall"))
		{
			plugin.updateSignWall(user, amount);
		}
		if (SQLresults != null)
		{
			String pack = event.getItemName().replace(" ", "_").toLowerCase();
			String price = plugin.getConfig().getString("packages." + pack + ".price");
			String name = plugin.getConfig().getString("packages." + pack + ".item");
			List<String> commands = plugin.getConfig().getStringList("packages." + pack + ".commands");
			if (item.equalsIgnoreCase(name))
			{
				if ((amount.equals(price)) || ((amount + "0").equals(price)))
				{
					for (Object cmnd : commands)
					{
						plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmnd.toString().replace("%player", user).replace("%amount", plugin.parseAmount(amount)));
					}
				}
			}
		}
	}
}