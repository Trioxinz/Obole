package me.monnierant.obole;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DonateEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	private String i;
	private String u;
	private Double a;
	private String d;
	private String f;
	private String l;
	private String e;
	private String ex;

	public DonateEvent(String itemName, String username, Double amount, String date, String first, String last, String email,
			String expiresdate)
	{
		this.i = itemName;
		this.u = username;
		this.a = amount;
		this.d = date;
		this.f = first;
		this.l = last;
		this.e = email;
		this.ex = expiresdate;
	}
	
	public String getItemName()
	{
		return i;
	}

	public String getUsername()
	{
		return u;
	}

	public Double getAmount()
	{
		return a;
	}

	public String getDate()
	{
		return d;
	}

	public String getFirstName()
	{
		return f;
	}

	public String getLastName()
	{
		return l;
	}

	public String getEmail()
	{
		return e;
	}

	public String getExpiresDate()
	{
		return ex;
	}

	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}