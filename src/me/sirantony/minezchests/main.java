package me.sirantony.minezchests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener{
	public final ChestBreak cb = new ChestBreak(this);
	public final getItems gi = new getItems(this);
	public final signs si = new signs(this);
	public final PlayerListener pl = new PlayerListener(this);
	public final CheckLocation cl = new CheckLocation(this);	
	
	public boolean minez = false;
	public boolean signtime = false;	
	
	private FileConfiguration config = null;
	private File configFile = null;
	
	public final HashMap<Player, String> destroy = new HashMap<Player,String>();
	public HashMap<Location,Chest>chest = new HashMap<Location,Chest>(); 
	public HashMap<Player, String>selection = new HashMap<Player, String>(); 
	public final HashMap<Location,Inventory> itemchests = new HashMap<Location,Inventory>();
	
	public Integer timer;	
	public String ChosenConfiguration;
	
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
		this.getCommand("minezchests").setExecutor(new commands(this));
		this.getCommand("mc").setExecutor(new commands(this));		
		this.getServer().getPluginManager().registerEvents(pl, this);
		
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
	public void chestRespawn(final Chest chestc){
		if (signtime == false){
			timer = this.getConfig().getInt("timing.time-until-respawn") * 20;			
		}		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()	    {
		      public void run()
		      {
		    	  chestc.getLocation().getBlock().setType(Material.CHEST);
		    	
		    	  chest.remove(chestc.getLocation());
		      }
		    }
		    , timer);
	}	
}
