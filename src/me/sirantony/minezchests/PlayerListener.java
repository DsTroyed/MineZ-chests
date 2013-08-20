package me.sirantony.minezchests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class PlayerListener	implements Listener{
	private main pl;

	public PlayerListener(main instance){
		this.pl = instance;
	}

	@EventHandler
	public void OpenChest(PlayerInteractEvent e) {
		if (e.getPlayer().hasPermission("minezchests.user"))
			try {
				if (!pl.destroy.containsKey(e.getPlayer())){
					if (e.getClickedBlock().getType() == Material.CHEST) {
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK){		        
							if ((this.pl.selection.containsKey(e.getPlayer())) && (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
								Location loc = e.getClickedBlock().getLocation();
								String confname = (String)this.pl.selection.get(e.getPlayer());
								List<String> chests = new ArrayList<String>();
								try{
									chests = this.pl.getConfig().getStringList("chests");
								}catch(Exception E){            	
								}		           
			            
								String cl;		            	
			       
					            if ((confname == null) || (confname == "")){
					              cl = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
					            } else {
					              cl = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName() + "," + confname;
					            }
					            if (chests.contains(cl)){
				            		e.getPlayer().sendMessage(ChatColor.RED + "That chest already exists!");
				            		e.setCancelled(true);
				            		return;
				            	}
						        chests.add(cl);
					            this.pl.getConfig().set("chests", chests);
					            this.pl.saveConfig();
					            this.pl.reloadConfig();
					            e.getPlayer().sendMessage(ChatColor.GREEN + "chest chosen!");
					            e.setCancelled(true);
					            return;
							}
						}
	          
						if (pl.randomspawn.containsKey(e.getClickedBlock().getLocation())){
							Action a = e.getAction();
							int drop = this.pl.getConfig().getInt("drop");
							switch (drop) {
								case 0:
									if (a == Action.RIGHT_CLICK_BLOCK) {
										this.pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer(), false);
									}
									break;
								case 1:
									if (a == Action.RIGHT_CLICK_BLOCK) {
										this.pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer(), false);
									}				
									if (a == Action.LEFT_CLICK_BLOCK) {
										this.pl.minez = true;
										this.pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer(), false);
									}
									break;
								case 2:
									if (a == Action.RIGHT_CLICK_BLOCK)
										this.pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer(), false);
									break;
								}
						}else{
							Action a = e.getAction();
							int drop = this.pl.getConfig().getInt("drop");
							switch (drop) {
								case 0:
									if (a == Action.RIGHT_CLICK_BLOCK) {
										this.pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer(), true);
									}
									break;
								case 1:
									if (a == Action.RIGHT_CLICK_BLOCK) {
										this.pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer(), true);
									}				
									if (a == Action.LEFT_CLICK_BLOCK) {
										this.pl.minez = true;
										this.pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer(), true);
									}
									break;
								case 2:
									if (a == Action.RIGHT_CLICK_BLOCK)
										this.pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer(), true);
									break;
							}
						}
	          
					}
				}
			}catch (Exception localException){
		}
	}

	@EventHandler
	public void safeSave(PluginDisableEvent event) {
	    Set<Location> keys = this.pl.chest.keySet();
	    for (Location loc : keys) {
	    	loc.getBlock().setType(Material.CHEST);
	    	this.pl.chest.remove(this.pl.chest.get(loc));
	    }
	    this.pl.chest.clear();
	}
	@EventHandler
	public void onChestBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.CHEST) {
			List<String> enabled = this.pl.getConfig().getStringList("enabled-worlds");
			Chest destroyed;
			for (String s : enabled) {
				if (event.getBlock().getWorld().getName().equalsIgnoreCase(s)) {
					if (this.pl.destroy.containsKey(event.getPlayer())) {
						destroyed = (Chest)event.getBlock().getState();
						if (this.pl.chest.containsKey(destroyed)) {
							this.pl.chest.remove(destroyed);
							this.pl.itemchests.remove(destroyed);
						}
					} else {
						event.setCancelled(true);
					}
				}
			}

			List<String> lists = this.pl.getConfig().getStringList("chests");
			for (String name : lists) {
				Location loc = event.getBlock().getLocation();
				String[] split = name.split(",");
				Integer x1 = Integer.valueOf(loc.getBlockX());
				Integer y1 = Integer.valueOf(loc.getBlockY());
				Integer z1 = Integer.valueOf(loc.getBlockZ());
				Integer x2 = Integer.valueOf(Integer.parseInt(split[0]));
				Integer y2 = Integer.valueOf(Integer.parseInt(split[1]));
				Integer z2 = Integer.valueOf(Integer.parseInt(split[2]));
				String listworld = split[3];
				if ((listworld.equalsIgnoreCase(event.getBlock().getWorld().getName())) && (x1.equals(x2)) && (y1.equals(y2)) && (z1.equals(z2))) {
					if (this.pl.destroy.containsKey(event.getPlayer())) {
						lists.remove(name);
						this.pl.getConfig().set("chests", null);
						this.pl.getConfig().set("chests", lists);
						this.pl.saveConfig();
						this.pl.reloadConfig();
						this.pl.chest.remove(loc.getBlock().getState());
						break;
					}
					event.setCancelled(true);
				}

			}

			Location sign = event.getBlock().getLocation();
			sign.setY(sign.getBlockY() - 2);
			if ((sign.getBlock().getType() == Material.SIGN) || (sign.getBlock().getType() == Material.SIGN_POST) || (sign.getBlock().getType() == Material.WALL_SIGN)) {
				Sign signs = (Sign)sign.getBlock().getState();
				if (signs.getLine(0).equalsIgnoreCase("[MC]")){
					if (this.pl.destroy.containsKey(event.getPlayer())) {
						Chest destroy = (Chest)event.getBlock().getState();
						if (this.pl.chest.containsKey(destroy)) {
							this.pl.chest.remove(destroy);
							this.pl.itemchests.remove(destroy);
							signs.getLocation().getBlock().setType(Material.AIR);
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
