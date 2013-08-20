package me.sirantony.minezchests;

import java.util.List;

import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class getItems{
	int ChosenItems = 0;
	private main pl;

	public getItems(main instance){
		this.pl = instance;
	}
	public void GiveItems(Chest chest, Boolean respawn) {
		chest.getInventory().clear();
		this.pl.itemchests.put(chest.getLocation(), chest.getInventory());
		getRandomList();
		this.pl.chest.put(chest.getLocation(), chest);
		this.pl.trapped(chest.getLocation());
		itemGiver(chest);
		int drop = this.pl.getConfig().getInt("drop");
		if (respawn){
	    
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
		}else{
			switch (drop) { 
				case 0:
					this.pl.cb.chestBreakFast(chest, chest.getInventory(), false);
					break;
			    case 1:
			    	this.pl.cb.chestBreakMinez(chest, chest.getInventory(), false);
			    	break;
			    case 2:
			    	this.pl.cb.chestBreakSlow(chest, chest.getInventory(), false);
			    	break;
			    default:
			    	this.pl.getLogger().severe("wrong configuration!"); 
		    }
		}
	}

	public void getRandomList(){
		this.pl.ChosenConfiguration = null;
		int rnd = 0 + (int)(Math.random() * 101.0D);
		List<String> lists = this.pl.getConfig().getStringList("activated-configurations");
		boolean chosen = false;
		for (String name : lists) {
			String chance = this.pl.getConfig().getString("configurations." + name + ".chancetopick");
			String[] split = chance.split("->");
			int chance1 = 0;
			int chance2 = 0;
			chance1 = Integer.parseInt(split[0]);
			chance2 = Integer.parseInt(split[1]);
			if ((chance1 <= rnd) && (chance2 >= rnd) && (!chosen)) {
				chosen = true;
				this.pl.ChosenConfiguration = name;
			}
		}
	}
	public void returnItems(Chest chest){
		itemGiver(chest);  
	}
	public void itemGiver(Chest chest) {
		chest.getInventory().clear();
		Inventory inv = chest.getInventory();
		List<String> items = this.pl.getConfig().getStringList("configurations." + this.pl.ChosenConfiguration + ".items");
		for (String item : items){
			ItemStack newItem = parseItem(item);

			if (newItem != null) {
				inv.addItem(new ItemStack[] { newItem });
			}

			if (this.ChosenItems == this.pl.getConfig().getInt("configurations." + this.pl.ChosenConfiguration + ".maximum-items")){
				break;
			}
		}
		if (this.ChosenItems <= this.pl.getConfig().getInt("configurations." + this.pl.ChosenConfiguration + ".minimum-items") - 1) {
			this.ChosenItems = 0;
			returnItems(chest);
			return;
		}
		this.ChosenItems = 0;
		chest.getInventory().setContents(inv.getContents());
		this.pl.itemchests.put(chest.getLocation(), inv);
	}

	private ItemStack parseItem(String item){
	    String[] splits = item.split(" ");
	    String items = splits[0];
	    String[] spliti = items.split("\\|");
	    int itemid = 0;
	    int amount = 1;
	    short damage = 1;
	    int percentage = 0;
	
	    int enchantmentlevel = 1;
	
	    ItemStack itemstack = null;
	    itemid = Integer.parseInt(spliti[0]);
	    amount = Integer.parseInt(spliti[1]);
	    damage = Short.parseShort(spliti[2]);
	    percentage = Integer.parseInt(spliti[3]);
	
	    Integer rnd = Integer.valueOf(0 + (int)(Math.random() * 101.0D));
	    if (percentage > rnd.intValue()) {
	    	itemstack = new ItemStack(itemid, amount);
	    	itemstack.setDurability(damage);
	    	try{
	    		String[] alle = splits;
	    		boolean first = true;
	    		for (String s : alle){
	    			if (!first) {        	  
	    				try{
	    					String[] splite = s.split("~");
	    					int enchantmentid = Integer.parseInt(splite[0]);
	    					enchantmentlevel = Integer.parseInt(splite[1]);
	    					itemstack.addEnchantment(Enchantment.getById(enchantmentid), enchantmentlevel);
	    				}catch(Exception e){
	    					ItemMeta im = itemstack.getItemMeta();	
	    					String n = s.trim().replaceAll("&@", " ");
	    					im.setDisplayName(n);
	    					itemstack.setItemMeta(im);
	    				}
	        	  
	    			} else {
	    				first = false;
	    			}
	    		} 
	    		this.ChosenItems += 1;
	    	}catch (Exception localException) {	    	  
    		}
	    	
	    }
	    return itemstack;
	}
}
