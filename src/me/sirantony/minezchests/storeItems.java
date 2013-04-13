package me.sirantony.minezchests;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;



public class storeItems {
	List<String> randomItems = null; 
	int ChosenItems = 0;
	int CurrentItemPlace;
	public main pl;
	public storeItems(main instance) {
		pl = instance;
	}
	public void checker(Block chest, Player player){
		boolean chosen = false;
		List<String> enabled = pl.getConfig().getStringList("enabled-worlds");	
		Chest chestc = (Chest) chest.getState();
		for (String s : enabled){					
			if (chest.getLocation().getWorld().getName().equalsIgnoreCase(s)){						
				if (!pl.chests.containsKey(chest.getLocation())){				
					chestc.getInventory().clear();
					pl.itemchests.put(chest.getLocation(), chestc.getInventory());		
					
						
											
					getList(chestc);	            			
            		pl.chests.put(chest.getLocation(), chestc);                			                			
            		pl.trapped(chest.getLocation());
            		chosen = true;	
            		if (pl.getConfig().getBoolean("drop.fast") == true){            			
            			pl.chestBreakFast(chestc);
            		}else{
            			if(pl.getConfig().getBoolean("drop.minez") == true){
            				pl.chestBreakMinez(chestc);            				
            			}else{
            				pl.chestBreak(chestc);            				
            			}            			
            		}   			            			
				}			
			} 	
		}
		if(chosen == false){
			if (!pl.chests.containsKey(chest.getLocation())){			    			
				check(player, chest.getLocation().getBlockX(), chest.getLocation().getBlockY(),chest.getLocation().getBlockZ(),chestc);									
			}
		}
	}
	public void check(Player player, Integer x1, Integer y1, Integer z1, Chest chest){
		List<String> lists = pl.getConfig().getStringList("chests");		
		for (String name : lists) {
			
    		String[] split = name.split(",");  	    
    	    Integer x2 = Integer.parseInt(split[0]);
    	    Integer y2 = Integer.parseInt(split[1]);
    	    Integer z2 = Integer.parseInt(split[2]); 
    	    String listworld = split[3];  
    	    pl.chosenname = split[4];    	    
    	    if (listworld.equalsIgnoreCase(player.getWorld().getName())){    	    
    	    	if (x1.equals(x2) && y1.equals(y2) && z1.equals(z2)){
    	    		int y3 = y2+1;
    				Location loc = new Location(player.getWorld(),x2,y3,z2);
    				pl.trapped(loc);    	    		
    				chest.getInventory().clear();
					pl.itemchests.put(chest.getLocation(), chest.getInventory());											
					getList(chest);	            			
            		pl.chests.put(chest.getLocation(), chest);                			                			
            		pl.trapped(chest.getLocation());            			
            		if (pl.getConfig().getBoolean("drop.fast") == true){            			
            			pl.chestBreakFast(chest);
            		}else{
            			if(pl.getConfig().getBoolean("drop.minez") == true){
            				pl.chestBreakMinez(chest);
            			}else{
            				pl.chestBreak(chest);
            			}            			
            		}   
        			
    	    		    	    		
    	    	}
    	    }
    		
        }
	}
	public String getList(Chest chest){
		pl.chosenname = null;
		int rnd = 0 + (int)(Math.random()*101);
		List<String> lists = pl.getConfig().getStringList("activated-configurations");
		boolean chosen = false;
    	for (String name : lists) {
    		String chance = pl.getConfig().getString("configurations." + name + ".chancetopick");
    		String[] split = chance.split("->");
    		int chance1 = 0;
    	    int chance2 = 0;    	    
    	    chance1 = Integer.parseInt(split[0]);
    	    chance2 = Integer.parseInt(split[1]);    	    
    		if (chance1 <= rnd && chance2 >= rnd && chosen == false){
    			chosen = true;
    			pl.chosenname = name;
    			
    			
    		}
        }
    	
    	itemGiver(chest);
    	
		return pl.chosenname;
	}
	public void itemGiver(Chest chest){
		chest.getInventory().clear();
		Inventory inv = chest.getInventory();		
    	List<String> items = pl.getConfig().getStringList("configurations." + pl.chosenname + ".items");   	
    	
		for (String item : items) {
    		
            ItemStack newItem = parseItem(item);
        
            if (newItem != null){ 
            	 
            		inv.addItem(newItem); 
            		
            }
            if (ChosenItems == pl.getConfig().getInt("configurations." + pl.chosenname + ".maximum-items")){
            	
            	break;
			}
            
        }
		
		ChosenItems = 0;
		chest.getInventory().setContents(inv.getContents());    	
    	pl.itemchests.put(chest.getLocation(), inv);    	
    	
    	
    	
    	
        
    }
	private ItemStack parseItem(String item) {		
		String[] splits = item.split(" ");
		String items = splits[0];
		String[] spliti = items.split("\\|");
	    int itemid = 0;
	    int amount = 1;
	    short damage = 1;
	    int percentage = 0;
	    int enchantmentid;
	    int enchantmentlevel = 1;
	    
	    ItemStack itemstack = null;
        itemid = Integer.parseInt(spliti[0]);
	    amount = Integer.parseInt(spliti[1]);
	    damage = Short.parseShort(spliti[2]);
	    percentage = Integer.parseInt(spliti[3]);
	    
	    Integer rnd = 0 + (int)(Math.random()*101);
	    if (percentage > rnd){
	    	itemstack = new ItemStack(itemid,amount);        
	        itemstack.setDurability(damage); 
	        try {
	        	
		    	String[] alle = splits;
		    	boolean first = true;
		    	for (String s : alle){
		    		if (first != true){
		    			String[] splite = s.split("~");
			    		enchantmentid = Integer.parseInt(splite[0]);
			    		enchantmentlevel = Integer.parseInt(splite[1]);
			    		itemstack.addEnchantment(Enchantment.getById(enchantmentid), enchantmentlevel);
		    		}else{
		    			first = false;
		    		}
		    	}
		    }catch(Exception e){		    	
		    }	        
	        ChosenItems = ChosenItems + 1;
	        
			
	    }
	    return itemstack;
	}
	public void MinMax(){
		
		
	}
}
