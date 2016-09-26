package me.monnierant.obole;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import me.monnierant.obole.DonateEvent;

public class Donation extends JavaPlugin
{
	Connection connection;
	Boolean connectionFailed = true;
	Boolean debuging = false;
	String password;
	Donation plugin;
	String url;
	String user;

	public void connectMysql()
	{
		user = getConfig().getString("database.username");
		password = getConfig().getString("database.password");
		url = ("jdbc:mysql://" + getConfig().getString("database.hostname") + ":" + getConfig().getInt("database.port") + "/" + getConfig().getString("database.database_name"));
		try
		{
			connection = DriverManager.getConnection(url, user, password);
			connectionFailed = Boolean.valueOf(false);
		}
		catch (Exception exception)
		{
			getLogger().log(Level.SEVERE, "Could not connect to database! Verify your database details in the configuration are correct.");
			if (debuging) exception.printStackTrace();
			getServer().getPluginManager().disablePlugin(plugin);
		}	
	}
	
	public void checkForDonations()
	{
		if (!(connectionFailed))
		{
			ResultSet result = getNewDonors();
			try
			{
				if (result != null)
				{
					while (result.next())
					{
						String user = result.getString("username");
						Double amount = Double.valueOf(result.getDouble("amount"));
						DonateEvent event = new DonateEvent(result.getString("item_name"),result.getString("username"), Double.valueOf(result.getDouble("amount")), result.getString("date"), result.getString("first_name"), result.getString("last_name"), result.getString("payer_email"), result.getString("expires"));
						getServer().getPluginManager().callEvent(event);
						result.updateString("expires", plugin.getExpiresDate(result.getString("item_name").replace(" ", "_").toLowerCase()));
						result.updateString("processed", "true");
						result.updateRow();
						updateDonorPlayers(user,Double.valueOf(getTotalDonated(user).doubleValue() + amount.doubleValue()));
					}
				}
			}
			catch (Exception exception)
			{
				if (debuging.booleanValue())
				{
					exception.printStackTrace();
				}
			}
		}
	}
	
	public void checkForExpireDonations()
	{
		if (!(connectionFailed.booleanValue()))
		{
			ResultSet rx = getResultSet("SELECT * FROM donations WHERE expired='false'");
			try
			{
				while (rx.next())
				{
					if ((rx.getString("expires") != null) && (rx.getString("expires") != "null")
							&& (rx.getString("expires").equalsIgnoreCase(getCurrentDate())))
					{
						String user = rx.getString("username");
						String item = rx.getString("item_name");
						Double amount = rx.getDouble("amount");
						String pack = item.replace(" ", "_").toLowerCase();
						List<String> commands = getConfig().getStringList("packages." + pack + ".expires-commands");
						for (String cmd : commands)
						{
							getServer().dispatchCommand(getServer().getConsoleSender(), cmd.replace("%player", user).replace("%amount", Double.toString(amount)));
						}
						rx.updateString("expired", "true");
						rx.updateRow();
					}
				}
			}
			catch (Exception exception)
			{
				if (this.debuging.booleanValue())
				{
					exception.printStackTrace();
				}
			}
		}
	}
	
	public void updateDonorPlayers(String user, Double amount)
	{
		try
		{
			Statement statement = connection.createStatement();
			String newStatement = "REPLACE INTO players SET username = '" + user + "', amount = '" + amount + "'";
			statement.executeUpdate(newStatement);
		}
		catch (SQLException exception)
		{
			if (this.debuging.booleanValue())
			{
				exception.printStackTrace();
			}
		}
	}

	public void onEnable()
	{
		plugin = this;
		checkConfigFile();
		debuging = getConfig().getBoolean("settings.debug");
		connectMysql();
		setupMysql();
		if (!(connectionFailed.booleanValue()))
		{
			getServer().getPluginManager().registerEvents(new DonationListeners(this), this);
			getServer().getPluginManager().registerEvents(new DonateEventListener(this), this);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
			{
				public void run()
				{
					plugin.checkForDonations();
				}
			}, 200L, getConfig().getInt("settings.checkdelay") * 20);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
			{
				public void run()
				{
					plugin.checkForExpireDonations();
				}
			}, 720000L, getConfig().getInt("settings.checkExpiredDelay") * 20);
		}
		getLogger().info("Donation system enabled");
	}

	public void setupMysql()
	{
		if (!(connectionFailed.booleanValue()))
		{
			try
			{
				Statement statement = connection.createStatement();
				String mainTable = "CREATE TABLE IF NOT EXISTS donations(id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), item_name TEXT, username TEXT(20), amount TEXT(5), date TEXT, processed TEXT, sandbox TEXT, first_name TEXT, last_name TEXT, payer_email TEXT, expires TEXT, expired TEXT, canGet INT)";
				String playerTable = "CREATE TABLE IF NOT EXISTS players(username TEXT(20), amount TEXT(5))";
				statement.executeUpdate(mainTable);
				statement.executeUpdate(playerTable);
			}
			catch (Exception exception)
			{
				if (debuging)
				{
					exception.printStackTrace();
				}
			}
		}
	}

	public void onDisable()
	{
		HandlerList.unregisterAll(plugin);
		Bukkit.getScheduler().cancelTasks(plugin);
		plugin = null;
		getLogger().info("Donation system disabled");
	}
	
	private boolean checkConfigFile()
	{
		boolean result = false;
		if (!(getDataFolder().exists()))
		{
			getDataFolder().mkdirs();
		}
		File file = new File(getDataFolder(), "config.yml");
		if (!(file.exists()))
		{
			ArrayList<String> paths = new ArrayList<String>();
			ArrayList<Object> values = new ArrayList<Object>();
			paths.add("settings.debug");
			values.add(Boolean.valueOf(false));
			paths.add("settings.checkdelay");
			values.add(Integer.valueOf(30));
			paths.add("settings.checkExpiredDelay");
			values.add(Integer.valueOf(2));
			paths.add("settings.sandbox");
			values.add(Boolean.valueOf(false));
			paths.add("settings.enablesignwall");
			values.add(Boolean.valueOf(true));
			paths.add("settings.broadcast-message");
			values.add("&aPlease thank %player for donating %amount!");
			paths.add("database.hostname");
			values.add("hostname");
			paths.add("database.database_name");
			values.add("database_name");
			paths.add("database.username");
			values.add("username");
			paths.add("database.password");
			values.add("pwd");
			paths.add("database.port");
			values.add(Integer.valueOf(1234));
			List<String> defaultcommands = new ArrayList<String>();
			List<String> defaultexpirescommands = new ArrayList<String>();
			List<String> onlinecommands = new ArrayList<String>();
			List<String> signwall = new ArrayList<String>();
			defaultcommands.add("promote %player");
			defaultcommands.add("msg %player Thanks for donating %amount!");
			defaultexpirescommands.add("demote %player");
			onlinecommands.add("msg %player Thanks for donating %amount!");
			signwall.add("This is line 1!");
			signwall.add("This is line 2!");
			signwall.add("%player");
			signwall.add("%amount");
			paths.add("signwall-format");
			values.add(signwall);
			paths.add("packages.member.price");
			values.add("2.50");
			paths.add("packages.member.item");
			values.add("Member Rank");
			paths.add("packages.member.expires");
			values.add(0);
			paths.add("packages.member.commands");
			values.add(defaultcommands);
			paths.add("packages.member.expirescommands");
			values.add(defaultexpirescommands);
			paths.add("packages.example.onlineCommands");
			values.add(defaultexpirescommands);
			File configFile = new File(getDataFolder(), "config.yml");
			if (!(configFile.exists()))
			{
				for (int t = 0; t < paths.size(); t++)
				{
					getConfig().addDefault((String) paths.get(t), values.get(t));
				}
				result = true;
			}
			else
			{
				for (int t = 0; t < paths.size(); t++)
				{
					if (!getConfig().contains((String) paths.get(t)))
					{
						getConfig().addDefault((String) paths.get(t), values.get(t));
						result = true;
					}
				}
			}
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		return result;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = (Player) sender;
		if ((cmd.getName().equalsIgnoreCase("donationreload")) && (player.hasPermission("Donation.Admin")))
		{
			reloadConfig();
			sender.sendMessage(ChatColor.GRAY + "configuration file reloaded...");
			return true;
		}
		return false;
	}
	
	public ResultSet getNewDonors()
	{
		return getResultSet("SELECT * FROM donations WHERE processed='false'");
	}

	public ResultSet getResultSet(String SQLstatement)
	{
		ResultSet result = null;
		try
		{
			Statement statement = connection.createStatement(1005, 1008);
			if (!getConfig().getBoolean("settings.sandbox"))
			{
				return statement.executeQuery(SQLstatement);
			}
			return statement.executeQuery(SQLstatement);
		}
		catch (SQLException exception)
		{
			if (debuging)
			{
				exception.printStackTrace();
			}
		}
		return result;
	}
	
	public String parseAmount(Double amount)
	{
		if (amount.toString().endsWith(".0"))
		{
			return "$" + amount + "0";
		}
		return "$" + amount;
	}

	public String parseAmount(String amount)
	{
		if (amount.toString().endsWith(".0"))
		{
			return "$" + amount + "0";
		}
		return "$" + amount;
	}
	
	public Integer getDupes(String username, String amount)
	{
		ResultSet result = getResultSet("SELECT * FROM donations WHERE username = '" + username + "' AND amount = '" + amount + "'");
		Integer donationcount = 0;
		try
		{
			if (result != null)
			{
				while (result.next())
				{
					donationcount = (donationcount.intValue() + 1);
				}
			}
		}
		catch (SQLException exception)
		{
			if (debuging)
			{
				exception.printStackTrace();
			}
		}
		return donationcount;
	}

	public String colorize(String string)
	{
		if (string == null)
		{
			return null;
		}
		return string.replaceAll("&([l-o0-9a-f])", "�$1");
	}
	
	public void updateSignWall(String use, Double amount)
	{
		Integer x1 = getConfig().getInt("signwall.1.x");
		Integer y1 = getConfig().getInt("signwall.1.y");
		Integer z1 = getConfig().getInt("signwall.1.z");
		Integer x2 = getConfig().getInt("signwall.2.x");
		Integer y2 = getConfig().getInt("signwall.2.y");
		Integer z2 = getConfig().getInt("signwall.2.z");
		Integer minx = Math.min(x1.intValue(), x2.intValue());
		Integer minz = Math.min(z1.intValue(), z2.intValue());
		Integer miny = Math.min(y1.intValue(), y2.intValue());
		Integer maxx = Math.max(x1.intValue(), x2.intValue());
		Integer maxy = Math.max(y1.intValue(), y2.intValue());
		Integer maxz = Math.max(z1.intValue(), z2.intValue());
		List<String> li = getConfig().getStringList("signwall-format");
		for (Integer x = minx; x.intValue() <= maxx.intValue(); x = x.intValue() + 1)
		{
			for (Integer y = miny; y.intValue() <= maxy.intValue(); y = y.intValue() + 1)
			{
				for (Integer z = minz; z.intValue() <= maxz.intValue(); z = z.intValue() + 1)
				{
					Block block = getServer().getWorld(getConfig().getString("signwall.2.w")).getBlockAt(x.intValue(), y.intValue(), z.intValue());
					Sign sign = (Sign) block.getState();
					if ((sign.getLine(0).isEmpty()) || (sign.getLine(0) == null))
					{
						Integer currentline = Integer.valueOf(-1);
						for (String line : li)
						{
							if (currentline.intValue() < 5)
							{
								currentline = Integer.valueOf(currentline.intValue() + 1);
								sign.setLine(currentline.intValue(), line.replace("%player", use).replace("%amount", parseAmount(amount)));
							}
						}
						sign.update();
						currentline = Integer.valueOf(-1);
						return;
					}
				}
			}
		}
	}

	public String getExpiresDate(String packagename)
	{
		Integer days = getConfig().getInt("packages." + packagename + ".expires");
		if (days.intValue() != 0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			try
			{
				calendar.setTime(sdf.parse(getCurrentDate()));
			}
			catch (ParseException exception)
			{
				if (debuging)
				{
					exception.printStackTrace();
				}
			}
			calendar.add(5, days.intValue());
			String exp = sdf.format(calendar.getTime());
			return exp;
		}
		return null;
	}

	public String getCurrentDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public Double getTotalDonated(String player)
	{
		String statement = "SELECT * FROM donations WHERE username='" + player + "'";
		ResultSet result = getResultSet(statement);
		Double amount = null;
		if (result == null)
		{
			return new Double(0.0D);
		}
		try
		{
			do
			{
				amount = result.getDouble("amount");
			} while (result.next());
		}
		catch (SQLException exception)
		{
			if (this.debuging.booleanValue())
			{
				exception.printStackTrace();
			}
		}
		if (amount != null)
		{
			return amount;
		}
		return new Double(0.0D);
	}
}