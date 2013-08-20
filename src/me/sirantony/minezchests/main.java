package me.sirantony.minezchests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin{
	public final ChestBreak cb = new ChestBreak(this);
	public final getItems gi = new getItems(this);
	public final signs si = new signs(this);
	public final PlayerListener pl = new PlayerListener(this);
	public final CheckLocation cl = new CheckLocation(this);
	public final RandomSpawn rs = new RandomSpawn(this);

	public boolean minez = false;
	public boolean signtime = false;

	private FileConfiguration config = null;
	private File configFile = null;

	public final HashMap<Location, String> randomspawn = new HashMap<Location, String>();
	public final HashMap<Player, String> destroy = new HashMap<Player, String>();
	public HashMap<Location, Chest> chest = new HashMap<Location, Chest>();
	public HashMap<Player, String> selection = new HashMap<Player, String>();
	public final HashMap<Location, Inventory> itemchests = new HashMap<Location, Inventory>();
	public Integer timer;
	public String ChosenConfiguration;

	public void onEnable(){
		getLogger().info("Looking for config file...");
		File file = new File(getDataFolder(), "config.yml");

		if (!file.exists()) {
			getLogger().info("generating config.yml...");
			saveResource("config.yml", false);
		} else {		    
			if (this.getConfig().getBoolean("check_for_updates")){
	    	   try {
					 URL url = new URL("https://dl.dropboxusercontent.com/u/64215178/versioncheck.txt");
				     BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				     String str;
				     str = in.readLine();
				     if (!str.equalsIgnoreCase(this.getDescription().getVersion().toString())){
				    	 this.getLogger().info(ChatColor.RED + "A new version is avaible for download");
				    	 this.getLogger().info(ChatColor.RED + "your version: " + this.getDescription().getVersion().toString());
				    	 this.getLogger().info(ChatColor.RED + "new version: " + str);
				     }
				     in.close();
				} catch (IOException e) {
				}
			}	        
	        
	        
			if (!this.getDescription().getVersion().toString().equals(this.getConfig().getString("version"))){
				getLogger().info("The config file is outdated! Replacing the old one with the new one!");
				getLogger().info("your version: v" + this.getConfig().getString("version"));
	    	    getLogger().info("new version: v" + this.getDescription().getVersion());
	    	    this.configFile.renameTo(new File(getDataFolder(), "config-old.yml"));
	    	    this.configFile.delete();
	    	    saveResource("config.yml", false);
			}    
		} 	  
		if (this.getConfig().getBoolean("random_spawning.enabled") == true){
			rs.SpawnChests();
		}
		getLogger().info("Config loaded!");
		getCommand("minezchests").setExecutor(new commands(this));
		getCommand("mc").setExecutor(new commands(this));
		getServer().getPluginManager().registerEvents(this.pl, this);
	}

 	public void onDisable(){
	 	this.getConfig().set("chunks", null);
	 	this.saveConfig();
	 	this.reloadConfig();
	  
	 	for (Location loc : this.randomspawn.keySet()){
	 		if (loc.getBlock().getType() == Material.CHEST){
	 			loc.getBlock().setType(Material.AIR);
	 		}		  
	 	}
 	}

 	public void reloadConfig(){
 		if (this.configFile == null) {
 			this.configFile = new File(getDataFolder(), "config.yml");
 		}
 		this.config = YamlConfiguration.loadConfiguration(this.configFile);
 		InputStream defConfigStream = getResource("config.yml");
 		if (defConfigStream != null) {
 			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
 			this.config.setDefaults(defConfig);
 		}
 	}

 	public FileConfiguration getConfig() {
 		if (this.config == null) {
 			reloadConfig();
 		}
 		return this.config;
 	}	

 	public void saveConfig() {
 		if ((this.config == null) || (this.configFile == null))
 			return;
 		try{
 			getConfig().save(this.configFile);
 		} catch (IOException ex) {
 			getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
 		}
 	}

 	public void trapped(Location loc) { 
 		int rnd = 0 + (int)(Math.random() * 101.0D);
 		if (getConfig().getInt("trapped-chests") > rnd) {
 			int x = loc.getBlockX();
 			int y = loc.getBlockY() + 1;
 			int z = loc.getBlockZ();
 			Location spawn = new Location(loc.getWorld(), x, y, z);
 			loc.getWorld().spawnEntity(spawn, EntityType.ZOMBIE);
 		} 
 	}

 	public void chestRespawn(final Location loc) {
 		loc.getBlock().setType(Material.AIR);
 		if (!this.signtime) {
 			this.timer = Integer.valueOf(getConfig().getInt("timing.time-until-respawn") * 20);
 		}
 		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
 			public void run() {
 				if (main.this.chest.containsKey(loc)) {
 					Block block = loc.getWorld().getBlockAt(loc);
 					block.setType(Material.CHEST);
 					main.this.chest.remove(loc);
 				}
 			}
 		}, this.timer.intValue());
 	}
}
