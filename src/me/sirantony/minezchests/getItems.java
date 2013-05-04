package me.sirantony.minezchests;

import java.util.List;

import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class getItems {
  int ChosenItems = 0;
	private main pl;
	public getItems(main instance) {
		pl = instance;
	}
	public void GiveItems(Chest chest){
		chest.getInventory().clear();
		pl.itemchests.put(chest.getLocation(), chest.getInventory());		
		getRandomList();	            			
		pl.chest.put(chest.getLocation(), chest);                			                			
		pl.trapped(chest.getLocation()); 		
		this.itemGiver(chest);
		int drop = pl.getConfig().getInt("drop");		
		switch (drop){
			case 0: pl.cb.chestBreakFast(chest);
					break;
			case 1: pl.cb.chestBreakMinez(chest);
					break;
			case 2: pl.cb.chestBreakSlow(chest);
					break;
			default: pl.getLogger().severe("wrong configuration!");
					break;
		}		
	}
	public void getRandomList() {
		pl.ChosenConfiguration = null;
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
    			pl.ChosenConfiguration = name;   			
    		}
        }		
	}
	
	public void itemGiver(Chest chest){		
		chest.getInventory().clear();
		Inventory inv = chest.getInventory();		
    	List<String> items = pl.getConfig().getStringList("configurations." + pl.ChosenConfiguration + ".items");    	
		for (String item : items) {
    		
            ItemStack newItem = parseItem(item);
        
            if (newItem != null){             		
            		inv.addItem(newItem); 
            		
            }
            if (ChosenItems == pl.getConfig().getInt("configurations." + pl.ChosenConfiguration + ".maximum-items")){
            	
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

}
