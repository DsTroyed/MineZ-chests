package me.sirantony.minezchests;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

public class signs{
	public main pl;

	public signs(main instance){
		this.pl = instance;
	}
	public boolean signChecker(Location loc) {
		int X = loc.getBlockX();
		int Y = loc.getBlockY() - 2;
    	int Z = loc.getBlockZ();
    	Location newLoc = new Location(loc.getWorld(), X, Y, Z);
    	if ((newLoc.getBlock().getType() == Material.SIGN_POST) || (newLoc.getBlock().getType() == Material.SIGN) || (newLoc.getBlock().getType() == Material.WALL_SIGN)) {
    		Sign sign = (Sign)newLoc.getBlock().getState();
    		if (sign.getLine(0).equalsIgnoreCase("[MC]")) {
    			try {
    				Chest chest = (Chest)loc.getBlock().getState();
    				if (sign.getLine(1).isEmpty()){
    					this.pl.gi.getRandomList();
    				} else {
    					this.pl.ChosenConfiguration = sign.getLine(1);
    				}
    				chest.getInventory().clear();
    				this.pl.itemchests.put(chest.getLocation(), chest.getInventory());
    				this.pl.chest.put(chest.getLocation(), chest);
    				this.pl.gi.itemGiver(chest);
    				this.pl.trapped(chest.getLocation());

    				if (!sign.getLine(2).isEmpty()) {
    					this.pl.timer = Integer.valueOf(Integer.parseInt(sign.getLine(2)) * 20);
    					this.pl.signtime = true;
    				}

    				int drop = this.pl.getConfig().getInt("drop");
    				switch (drop) { 
    					case 0:
    						this.pl.cb.chestBreakFast(chest, chest.getInventory(), true);
    						this.pl.signtime = false;
    						break;
    					case 1:
    						this.pl.cb.chestBreakMinez(chest, chest.getInventory(), true);
    						this.pl.signtime = false;
    						break;
    					case 2:
    						this.pl.cb.chestBreakSlow(chest, chest.getInventory(), true);
    						this.pl.signtime = false;
    						break;
    					default:
    						this.pl.getLogger().severe("wrong configuration!");
    						return true; 
    				}
    			}
    			catch (Exception e) {
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
