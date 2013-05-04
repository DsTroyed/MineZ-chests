package me.sirantony.minezchests;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

public class signs {
  public main pl;
	public signs(main instance) {
		pl = instance;
	}
	public boolean signChecker(Location loc){
		int X = loc.getBlockX();
		int Y = loc.getBlockY() - 2;
		int Z = loc.getBlockZ();
		Location newLoc = new Location(loc.getWorld(),X,Y,Z);	
		if (newLoc.getBlock().getType() == Material.SIGN_POST || newLoc.getBlock().getType() == Material.SIGN){
			Sign sign = (Sign) newLoc.getBlock().getState();					
			if (sign.getLine(0).equalsIgnoreCase("[MC]")){
				try{			
					Chest chest = (Chest) loc.getBlock().getState();					
					if (sign.getLine(1).isEmpty() == true){
						pl.gi.getRandomList();
					}else{
						pl.ChosenConfiguration = sign.getLine(1);
					}									
					chest.getInventory().clear();					
					pl.itemchests.put(chest.getLocation(), chest.getInventory());				            			
            		pl.chest.put(chest.getLocation(), chest);
            		pl.gi.itemGiver(chest);	
            		pl.trapped(chest.getLocation());             		           			
            		
            		if (sign.getLine(2).isEmpty() == false){            			
            			pl.timer = Integer.parseInt(sign.getLine(2)) * 20;
            			pl.signtime = true;           			 
            		}        		
            		
					int drop = pl.getConfig().getInt("drop");
					switch (drop){
					case 0: pl.cb.chestBreakFast(chest);
    					break;
					case 1: pl.cb.chestBreakMinez(chest);
    					break;
					case 2: pl.cb.chestBreakSlow(chest);      					
    					break;
					default: pl.getLogger().severe("wrong configuration!"); 
            		return true;
					}
				}catch (Exception e){		
					sign.setLine(0, ChatColor.RED + "WRONG");
					sign.setLine(1, ChatColor.RED + "CONFIGURATION");
					sign.setLine(2, null);
					sign.setLine(3, null);
				}
				
				
				
			}
		}
		return false;
	}
}
