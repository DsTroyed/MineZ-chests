package me.sirantony.minezchests;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

public class CheckLocation{
	public main pl;

	public CheckLocation(main instance){
		this.pl = instance;
	}

	public void WorldChecker(Block chest, Player player, Boolean respawn) {
		Chest chestc = (Chest)chest.getState();
		if ((!this.pl.chest.containsKey(chest.getLocation())) && (!BlockChecker(player, Integer.valueOf(chest.getLocation().getBlockX()), Integer.valueOf(chest.getLocation().getBlockY()), Integer.valueOf(chest.getLocation().getBlockZ()), chestc))) {
			List<String> enabled = this.pl.getConfig().getStringList("enabled-worlds");
			for (String s : enabled){
				if ((chest.getLocation().getWorld().getName().equalsIgnoreCase(s)) && (!this.pl.chest.containsKey(chest.getLocation()))){
					this.pl.gi.GiveItems(chestc, respawn);
				}
			}
		}
	}

	public boolean BlockChecker(Player player, Integer x1, Integer y1, Integer z1, Chest chest){
		if (!this.pl.si.signChecker(chest.getLocation())) {
			List<String> lists = this.pl.getConfig().getStringList("chests");
			for (String name : lists) {
		        String[] split = name.split(",");
		        Integer x2 = Integer.valueOf(Integer.parseInt(split[0]));
		        Integer y2 = Integer.valueOf(Integer.parseInt(split[1]));
		        Integer z2 = Integer.valueOf(Integer.parseInt(split[2]));
		        String listworld = split[3];
		        if ((listworld.equalsIgnoreCase(player.getWorld().getName())) && (x1.equals(x2)) && (y1.equals(y2)) && (z1.equals(z2))) {
					this.pl.gi.getRandomList();
					if (split.length == 5) {
						this.pl.ChosenConfiguration = split[4].toString();
					}
				}
				int y3 = y2.intValue() + 1;
				Location loc = new Location(player.getWorld(), x2.intValue(), y3, z2.intValue());
				this.pl.trapped(loc);
				chest.getInventory().clear();
		        this.pl.itemchests.put(chest.getLocation(), chest.getInventory());
		        this.pl.gi.itemGiver(chest);
		        this.pl.chest.put(chest.getLocation(), chest);
		        int drop = this.pl.getConfig().getInt("drop");
		        switch (drop) { 
		        	case 0:	        
			            this.pl.cb.chestBreakFast(chest, chest.getInventory(), true);
			            break;
		        	case 1:
			            this.pl.cb.chestBreakMinez(chest, chest.getInventory(), true);
			            break;
		        	case 2:
		        		this.pl.cb.chestBreakSlow(chest, chest.getInventory(), true);
		        		break;
		        	default:
		        		this.pl.getLogger().severe("wrong configuration!");
		        }		
	            return true;	     
		    }

		}
		return false;
	}
}
