package me.sirantony.minezchests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;



public class main extends JavaPlugin implements Listener{
	public final storeItems si = new storeItems(this);
	
	public final HashMap<Location, Chest> chests = new HashMap<Location,Chest>();
	public final HashMap<String, String> select = new HashMap<String,String>();	
	public final HashMap<Player, String> destroy = new HashMap<Player,String>();
	public final HashMap<Location,Inventory> itemchests = new HashMap<Location,Inventory>();
	private FileConfiguration config = null;
	private File configFile = null;
	public String chosenname;
	public boolean minez = false;
	public boolean ready = false;
	public boolean broken = false;
	public Chest chest;
	public Integer tid;
	public void onEnable(){
		this.getLogger().info("Looking for config file...");
		File file = new File(this.getDataFolder(), "config.yml");
		if (!file.exists()){						
			this.getLogger().info("generating config.yml...");
			this.saveResource("config.yml", false);
		}	
		else if (!this.getConfig().get("version").toString().equalsIgnoreCase(getDescription().getVersion().toString())){
			this.getLogger().info("The config file is outdated! Replacing the old one with the new one!!");
			this.getLogger().info("old version: v" + this.getConfig().get("version"));
			this.getLogger().info("new version: v" + getDescription().getVersion());
			configFile.renameTo(new File(this.getDataFolder(), "config-old.yml"));
			configFile.delete();
			this.saveResource("config.yml", false);
		}
		
		this.getLogger().info("Config loaded!");
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	public void onDisable(){
		
	}
	public void reloadConfig() {
	    if (configFile == null) {
	    	configFile = new File(this.getDataFolder(), "config.yml");
	    }
	    config = YamlConfiguration.loadConfiguration(configFile);	 
	    InputStream defConfigStream = this.getResource("config.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
	}
	public FileConfiguration getConfig() {
	    if (config == null) {
	        this.reloadConfig();
	    }
	    return config;
	}
	public void saveConfig() {
	    if (config == null || configFile == null) {
	    return;
	    }
	    try {
	        getConfig().save(configFile);
	    } catch (IOException ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
	    }
	}
	public void dropItems(ItemStack i, Location loc){
		loc.getWorld().dropItem(loc, i); 			 
	}
	public void chestBreakMinez(final Chest chest){
		int time = this.getConfig().getInt("timing.time-until-break-MineZ") * 20;		
		if (minez == true){			
			chest.getBlock().setType(Material.AIR);		    	  
			for (ItemStack i : itemchests.get(chest.getLocation())){
				if (i == null){
					break;
				}else{
					Location loc = chest.getLocation();
					dropItems(i,loc);
				}	
				
			}
			minez = false;
			chestRespawn(chest);
		}else{
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()	    {
				public void run(){	    
					ready = true;
				}
		    }
			, time);
			tid = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				@Override
				public void run() {						
					Boolean empty = false;
			        for (ItemStack stack : chest.getInventory().getContents()) {
			             if (stack!=null&&stack.getType()!=null&&!stack.getType().equals(Material.AIR)) {			                 
			                 break;   
			             }else{			            	 
			            	 empty = true;
			            	 
			             }
			        }	
			        if (empty == true){
			        	 chest.getBlock().setType(Material.AIR);		            	 		            	 
		            	 getServer().getScheduler().cancelTask(tid);		            	 
			        }   
			        
					else if(ready == true){
						chestBreakFast(chest);						
						
					}
				}
			    
			}, 20L, 20L);
		}
		
	}
	public void chestBreakFast(Chest chest){		
		chest.getLocation().getBlock().setType(Material.AIR);		    	  
		for (ItemStack i : itemchests.get(chest.getLocation())){
			if (i == null){
				break;
			}else{
				Location loc = chest.getLocation();
				dropItems(i,loc);
			}	
			
		}
		chestRespawn(chest);
	}
	public void chestBreak(final Chest chest){		
		int time = this.getConfig().getInt("timing.time-until-break") * 20;
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()	    {
			public void run(){	    
				chest.getLocation().getBlock().setType(Material.AIR);
				for (ItemStack i : itemchests.get(chest.getLocation())){
					if (i == null){
						break;
					}else{
						Location loc = chest.getLocation();
						dropItems(i,loc);
					}	
					
				}    	
				chestRespawn(chest);	        
			}
	    }
		, time);
	}	
	public boolean onCommand(CommandSender sender, Command cmd,String commandLabel, String[]args){
		if (commandLabel.equalsIgnoreCase("mc") || commandLabel.equalsIgnoreCase("minezchests")){			
			if (sender instanceof Player){	
				Player player = (Player) sender;
				if (args.length == 1){
					if (args[0].equalsIgnoreCase("reload")){
						this.reloadConfig();
						this.getLogger().info("config reloaded!");
						sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
					}
					if (args[0].equalsIgnoreCase("stop")){
						if (select.containsKey(player.getName())){
							player.sendMessage(ChatColor.GREEN + "Transforming mode off!");
							select.remove(player.getName());
						}						
					}					
					if(args[0].equalsIgnoreCase("destroy")){
						if(destroy.containsKey(player)){
							destroy.remove(player);
							player.sendMessage(ChatColor.GREEN + "Destroy mode off!");
						}else{
							destroy.put(player, null);
							player.sendMessage(ChatColor.GREEN + "Destroy mode on!");
						}					
					}
					if(args[0].equalsIgnoreCase("restore")){
						Set<Location> keys = chests.keySet();						
						for (Location loc : keys){							
							loc.getBlock().setType(Material.CHEST);
							chests.remove(chests.get(loc));
							
						}
						chests.clear();
					}
				}
				if (args.length == 2){
					if (args[0].equalsIgnoreCase("select")){
						boolean chosen = false;
						List<String> lists = this.getConfig().getStringList("activated-configurations");
						for (String s : lists){
							if (args[1].equalsIgnoreCase(s)){
								select.put(player.getName(), args[1].toString());
								player.sendMessage(ChatColor.GREEN + "Transforming mode on!");
								player.sendMessage(ChatColor.GREEN + "Select a chest!");
								chosen = true;								
							}
						}
						if (chosen == false){
							player.sendMessage(ChatColor.RED + "Wrong configuration-name!");
						}
						
					}
				}
			}				
		}
		return false;		
	}
	public void chestRespawn(final Chest chest){
		int time = this.getConfig().getInt("timing.time-until-respawn") * 20;
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()	    {
		      public void run()
		      {
		    	  chest.getLocation().getBlock().setType(Material.CHEST);
		    	
		    	  chests.remove(chest.getLocation());
		      }
		    }
		    , time);
	}	
	public void trapped(Location loc){
		int rnd = 0 + (int)(Math.random()*101);
		if (this.getConfig().getInt("trapped-chests") > rnd){
			int x = loc.getBlockX();
			int y = loc.getBlockY() + 1;
			int z = loc.getBlockZ();
			Location spawn = new Location(loc.getWorld(),x,y,z);
			@SuppressWarnings("unused")
			Zombie zombie = (Zombie)loc.getWorld().spawnEntity(spawn, EntityType.ZOMBIE);
		}
	}
	@EventHandler
	public void safeSave(PluginDisableEvent event){
		Set<Location> keys = chests.keySet();						
		for (Location loc : keys){							
			loc.getBlock().setType(Material.CHEST);
			chests.remove(chests.get(loc));
			
		}
		chests.clear();
	}
	@EventHandler
	public void onChestBreak(BlockBreakEvent event){		
		if (event.getBlock().getType() == Material.CHEST){
			List<String> enabled = this.getConfig().getStringList("enabled-worlds");
			for (String s : enabled){
				if (event.getBlock().getWorld().getName().equalsIgnoreCase(s)){
					if (destroy.containsKey(event.getPlayer())){
						Chest destroyed = (Chest) event.getBlock().getState();
						if (chests.containsKey(destroyed)){
							chests.remove(destroyed);
						}								
					} else {
						event.setCancelled(true);
					}
				} 
			}
			
			List<String> lists = this.getConfig().getStringList("chests");
			Integer removec = 1;
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
	    	    chosenname = split[4];    	    
	    	    if (listworld.equalsIgnoreCase(event.getBlock().getWorld().getName())){	    	    	
	    	    	if (x1.equals(x2) && y1.equals(y2) && z1.equals(z2)){
	    	    		if (destroy.containsKey(event.getPlayer())){	    	    			
	    	    			this.getConfig().set("chests", null);  
	    	    			lists.remove(removec.intValue());
	    	    			this.getConfig().set("chests", lists);
	    	    			this.saveConfig();
	    	    			this.reloadConfig();	    	    				    	    				    	    			
		    	    		chests.remove(loc.getBlock().getState());		
		    	    		removec = removec + 1;
	    	    		} else {
							event.setCancelled(true);
						}	    	    		
	    	    	}
	    	    }
	    	    
	    		
	        }
			broken = true;
		}		
	}
	@EventHandler
	public void onChestInteract(PlayerInteractEvent event){	
		if (event.getClickedBlock().getType() == Material.CHEST){
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
				si.checker(event.getClickedBlock(), event.getPlayer());
			}else if (event.getAction() == Action.LEFT_CLICK_BLOCK){
				if (event.getPlayer().getGameMode() == GameMode.SURVIVAL && this.getConfig().getBoolean("drop.minez")==true){					
						minez = true;
						si.checker(event.getClickedBlock(), event.getPlayer());				
				}
				
			}else if (select.containsKey(event.getPlayer().getName())){
				Location loc = event.getClickedBlock().getLocation();				
					
				String test = select.get(event.getPlayer().getName());
				List<String> chests = this.getConfig().getStringList("chests");
				chests.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName() + "," + test);
					
				this.getConfig().set("chests", chests);
				this.saveConfig();
				this.reloadConfig();
				event.getPlayer().sendMessage(ChatColor.GREEN + "chest chosen!");
				event.setCancelled(true);								
			}				
		}
	}	
}
