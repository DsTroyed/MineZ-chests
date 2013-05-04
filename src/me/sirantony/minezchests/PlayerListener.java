package me.sirantony.minezchests;

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

public class PlayerListener implements Listener{
  
	private main pl;
	public PlayerListener(main instance) {
		pl = instance;
	}
	
	@EventHandler
	public void OpenChest(PlayerInteractEvent e){
		if (e.getMaterial() == Material.CHEST){				
			boolean stop = false;
			if (pl.selection.containsKey(e.getPlayer())){
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
					Location loc = e.getClickedBlock().getLocation();						
					String confname = pl.selection.get(e.getPlayer());
					List<String> chests = pl.getConfig().getStringList("chests");
					if (confname == null || confname == ""){
						chests.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName());
					}else{
						chests.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName() + "," + confname);
					}					
					pl.getConfig().set("chests", chests);
					pl.saveConfig();
					pl.reloadConfig();
					e.getPlayer().sendMessage(ChatColor.GREEN + "chest chosen!");
					e.setCancelled(true);
					stop = true;
				}
												
			}	
			if (stop == false){
				Action a = e.getAction();
				int drop = pl.getConfig().getInt("drop");
				switch (drop){			
					case 0: 
						if (a == Action.RIGHT_CLICK_BLOCK){
							pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer());
						}
						break;
					case 1:
						if (a == Action.RIGHT_CLICK_BLOCK){
							pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer());
						}
					
						if (a == Action.LEFT_CLICK_BLOCK){
							pl.minez = true;
							pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer());					
						}
						break;
					case 2: 
						if (a == Action.RIGHT_CLICK_BLOCK){
							pl.cl.WorldChecker(e.getClickedBlock(), e.getPlayer());
						}
						break;
				}
			}			
		}		
	}
	
	@EventHandler
	public void safeSave(PluginDisableEvent event){
		Set<Location> keys = pl.chest.keySet();						
		for (Location loc : keys){							
			loc.getBlock().setType(Material.CHEST);
			pl.chest.remove(pl.chest.get(loc));
			
		}		
		pl.chest.clear();
	}
	@EventHandler
	public void onChestBreak(BlockBreakEvent event){		
		if (event.getBlock().getType() == Material.CHEST){
			List<String> enabled = pl.getConfig().getStringList("enabled-worlds");
			for (String s : enabled){
				if (event.getBlock().getWorld().getName().equalsIgnoreCase(s)){
					if (pl.destroy.containsKey(event.getPlayer())){
						Chest destroyed = (Chest) event.getBlock().getState();
						if (pl.chest.containsKey(destroyed)){
							pl.chest.remove(destroyed);
						}								
					} else {
						event.setCancelled(true);
					}
				} 
			}
			
			List<String> lists = pl.getConfig().getStringList("chests");			
			for (String name : lists) {
				Location loc = event.getBlock().getLocation();
	    		String[] split = name.split(",");  
	    		Integer x1 = loc.getBlockX();
	    		Integer y1 = loc.getBlockY();
	    		Integer z1 = loc.getBlockZ();
	    	    Integer x2 = Integer.parseInt(split[0]);
	    	    Integer y2 = Integer.parseInt(split[1]);
	    	    Integer z2 = Integer.parseInt(split[2]); 
	    	    String listworld = split[3];  	    	      	    
	    	    if (listworld.equalsIgnoreCase(event.getBlock().getWorld().getName())){	    	    	
	    	    	if (x1.equals(x2) && y1.equals(y2) && z1.equals(z2)){
	    	    		if (pl.destroy.containsKey(event.getPlayer())){    	    			
	    	    			lists.remove(name);
	    	    			pl.getConfig().set("chests", null);   	    			
	    	    			pl.getConfig().set("chests", lists);
	    	    			pl.saveConfig();
	    	    			pl.reloadConfig();	    	    				    	    				    	    			
		    	    		pl.chest.remove(loc.getBlock().getState());		
		    	    		break;
	    	    		} else {
							event.setCancelled(true);
						}	    	    		
	    	    	}
	    	    }  	    
	    		
	        }
			Location sign = event.getBlock().getLocation();
			sign.setY(sign.getBlockY() - 2);
			if (sign.getBlock().getType() == Material.SIGN || sign.getBlock().getType() == Material.SIGN_POST){				
				Sign signs = (Sign) sign.getBlock().getState();
				if (signs.getLine(0).equalsIgnoreCase("[MC]")){				
					if (pl.destroy.containsKey(event.getPlayer())){					
						Chest destroyed = (Chest) event.getBlock().getState();
						if (pl.chest.containsKey(destroyed)){
							pl.chest.remove(destroyed);
						}								
					} else {					
						event.setCancelled(true);
					}
				}
			}
			
		}		
	}
}
