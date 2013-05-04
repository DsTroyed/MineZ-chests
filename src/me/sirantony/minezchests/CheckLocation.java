package me.sirantony.minezchests;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

public class CheckLocation {
  public main pl;
	public CheckLocation(main instance) {
		pl = instance;
	}
	
	public void WorldChecker(Block chest, Player player){			
		Chest chestc = (Chest) chest.getState();
		if (!pl.chest.containsKey(chest.getLocation())){						
			if (BlockChecker(player, chest.getLocation().getBlockX(), chest.getLocation().getBlockY(),chest.getLocation().getBlockZ(),chestc) == false){				
				List<String> enabled = pl.getConfig().getStringList("enabled-worlds");	
				for (String s : enabled){					
					if (chest.getLocation().getWorld().getName().equalsIgnoreCase(s)){							
						if (!pl.chest.containsKey(chest.getLocation())){				
							pl.gi.GiveItems(chestc);      			
							         			
						}			
					} 	
				}	
			}
		}
	}
	public boolean BlockChecker(Player player, Integer x1, Integer y1, Integer z1, Chest chest){	
		if (pl.si.signChecker(chest.getLocation()) == false){
			List<String> lists = pl.getConfig().getStringList("chests");		
			for (String name : lists) {
				
	    		String[] split = name.split(",");  	    
	    	    Integer x2 = Integer.parseInt(split[0]);
	    	    Integer y2 = Integer.parseInt(split[1]);
	    	    Integer z2 = Integer.parseInt(split[2]); 
	    	    String listworld = split[3];
	    	    try{
	    	    	pl.ChosenConfiguration = split[4];
	    	    }catch (Exception e){
	    	    	pl.gi.getRandomList();
	    	    }    	      	     	    
	    	    if (listworld.equalsIgnoreCase(player.getWorld().getName())){    	    
	    	    	if (x1.equals(x2) && y1.equals(y2) && z1.equals(z2)){	    	    			    		
	    	    			int y3 = y2+1;
	        				Location loc = new Location(player.getWorld(),x2,y3,z2);
	        				pl.trapped(loc);    	    		
	        				chest.getInventory().clear();
	    					pl.itemchests.put(chest.getLocation(), chest.getInventory());											
	    					pl.gi.itemGiver(chest);	            			
	                		pl.chest.put(chest.getLocation(), chest);                			                			
	                		pl.trapped(chest.getLocation());                		
	                		int drop = pl.getConfig().getInt("drop");
	                		switch (drop){
	                			case 0: pl.cb.chestBreakFast(chest);
	                			case 1: pl.cb.chestBreakMinez(chest);
	                			case 2: pl.cb.chestBreakSlow(chest);                			
	                		}
	                		return true;
	    	    		}       			
	    	    		    	    		
	    	    	}
	    	    }
		}       
		return false;
	}
}
