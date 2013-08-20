package me.sirantony.minezchests;


import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class ChestBreak{
	public main pl;
	public int tid;
	public boolean ready = false;

	public ChestBreak(main instance){
		this.pl = instance;
	}

	public void dropItems(ItemStack i, Location loc){
		loc.getWorld().dropItem(loc, i);
	}

  	public void chestBreakMinez(final Chest chest, final Inventory inv, final Boolean respawn) {
  		Integer timed = Integer.valueOf(this.pl.getConfig().getInt("timing.time-until-break-MineZ") * 20);
  		if (this.pl.minez) {
  			for (ItemStack i : inv) {
  				if (i == null) {
  					break;
  				}
  				Location loc = chest.getLocation();
  				dropItems(i, loc);
  				inv.remove(i);
  			}

  			this.pl.minez = false;
  			if (respawn){
  				ChestBreak.this.pl.chestRespawn(chest.getLocation());
  			}else{
  				List<String> L = pl.getConfig().getStringList("chunks");
  				L.remove(chest.getLocation().getChunk().toString());
  				pl.getConfig().set("chunks", L);
  			}
  		} else {
  			this.pl.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, new Runnable() {
  				public void run() {
  					ChestBreak.this.ready = true;
  				}
  			} , timed.intValue());
	      	this.tid = this.pl.getServer().getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable(){
	      		public void run() {
	      			Boolean empty = Boolean.valueOf(false);
	      			for (ItemStack stack : inv) {
	      				if ((stack != null) && (stack.getType() != null) && (!stack.getType().equals(Material.AIR))) {
	      					empty = false;
	      					break;
	      				}else{
	      					empty = true;
	      				}
	            
	      			}
	      			
		      		if (empty) {
		      			pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
		        	        public void run() {
		        	        	chest.getBlock().setType(Material.AIR);
		        	            if (respawn){
		        	            	ChestBreak.this.pl.chestRespawn(chest.getLocation());
		        	            }else{
		        	            	List<String> L = pl.getConfig().getStringList("chunks");
		        	            	L.remove(chest.getLocation().getChunk().toString());
		        	            	pl.getConfig().set("chunks", L);
		        	            }
		        	            ChestBreak.this.pl.getServer().getScheduler().cancelTask(ChestBreak.this.tid);
		        	        }
		      			}, 70L);
		            
		      		} else if (ChestBreak.this.ready) {
		      			ChestBreak.this.chestBreakFast(chest, inv, true);
		      			ChestBreak.this.pl.getServer().getScheduler().cancelTask(ChestBreak.this.tid);
		      		}
		      	}
		    }, 20L, 20L);
  		}
  	}

	public void chestBreakFast(Chest chest, Inventory inv, Boolean respawn) {
		for (ItemStack i : (Inventory)this.pl.itemchests.get(chest.getLocation())) {
			if (i == null) {
				break;
			}
			Location loc = chest.getLocation();
			dropItems(i, loc);
			inv.remove(i);
	    }
		
	    if (respawn){
	    	ChestBreak.this.pl.chestRespawn(chest.getLocation());
	    }else{
	    	List<String> L = pl.getConfig().getStringList("chunks");
	    	L.remove(chest.getLocation().getChunk().toString());
	    	pl.getConfig().set("chunks", L);
	    }
	}
	public void chestBreakSlow(final Chest chest, final Inventory inv, final Boolean respawn) {
	    Integer time = Integer.valueOf(this.pl.getConfig().getInt("timing.time-until-break") * 20);
	    this.pl.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, new Runnable(){
	    	public void run() {
	    		for (ItemStack i : inv) {
	    			if (i == null) {
	    				break;
	    			}
	    			Location loc = chest.getLocation();
	    			ChestBreak.this.dropItems(i, loc);
	    			inv.remove(i);
	    		}
	    		if (respawn){
	    			ChestBreak.this.pl.chestRespawn(chest.getLocation());
	    		}else{
	    			List<String> L = pl.getConfig().getStringList("chunks");
	    			L.remove(chest.getLocation().getChunk().toString());
	    			pl.getConfig().set("chunks", L);
	    		}
	    	}
	    }, time.intValue());
	}
}
