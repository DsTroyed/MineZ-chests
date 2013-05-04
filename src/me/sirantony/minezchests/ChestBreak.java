package me.sirantony.minezchests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class ChestBreak {
  public main pl;
	public ChestBreak(main instance) {
		pl = instance;
	}
	
	public int tid;
	public boolean ready = false;
	
	public void dropItems(ItemStack i, Location loc){
		loc.getWorld().dropItem(loc, i); 		
	}
	public void chestBreakMinez(final Chest chest){
		
		Integer timed = pl.getConfig().getInt("timing.time-until-break-MineZ") * 20;			
		if (pl.minez == true){				    	  
			for (ItemStack i : chest.getInventory()){
				if (i == null){
					break;
				}else{
					Location loc = chest.getLocation();
					dropItems(i,loc);
					chest.getInventory().remove(i);
				}	
				
			}
			chest.getBlock().setType(Material.AIR);	
			pl.minez = false;
			pl.chestRespawn(chest);			
		}else{			
			pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable()	    {
				public void run(){	    
					ready = true;
				}
		    }
			, timed);
			tid = pl.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable(){
				@Override
				public void run() {						
					Boolean empty = false;
			        for (ItemStack stack : chest.getInventory()) {
			             if (stack!=null&&stack.getType()!=null&&!stack.getType().equals(Material.AIR)) {			                 
			                 break;   
			             }else{			            	 
			            	 empty = true;
			            	 
			             }
			        }	
			        if (empty == true){
			        	 chest.getBlock().setType(Material.AIR);		            	 		            	 
		            	 pl.getServer().getScheduler().cancelTask(tid);		            	 
			        }   
			        
					else if(ready == true){
						chestBreakFast(chest);						
						
					}
				}
			    
			}, 20L, 20L);
		}
		
	}
	public void chestBreakFast(Chest chest){		    	  
		for (ItemStack i : pl.itemchests.get(chest.getLocation())){
			if (i == null){
				break;
			}else{
				Location loc = chest.getLocation();
				dropItems(i,loc);
				chest.getInventory().remove(i);
			}	
			
		}
		chest.getLocation().getBlock().setType(Material.AIR);		
		pl.chestRespawn(chest);
	}
	public void chestBreakSlow(final Chest chest){
		Integer time = pl.getConfig().getInt("timing.time-until-break") * 20;		
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable(){
			public void run(){	 
				
				for (ItemStack i : chest.getInventory()){					
					if (i == null){
						break;
					}else{						
						Location loc = chest.getLocation();
						dropItems(i,loc);						
				        chest.getInventory().remove(i);

					}	
					
				} 
				chest.getLocation().getBlock().setType(Material.AIR);		
				pl.chestRespawn(chest);	        
			}
	    }
		, time);
	}	

}
