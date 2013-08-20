package me.sirantony.minezchests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class RandomSpawn{
	public main pl;

	public RandomSpawn(main instance){
		this.pl = instance;
	}
	public void SpawnChests(){
		this.pl.getServer().getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable(){
			public void run() {
				
				
				
				Player[] players = RandomSpawn.this.pl.getServer().getOnlinePlayers();
				for (Player p : players) {
					if (pl.getConfig().getStringList("enabled-worlds").contains(p.getLocation().getWorld().getName())){
						int x1 = p.getLocation().getBlockX() + 20;
						int y1 = p.getLocation().getBlockY() + 10;
						int z1 = p.getLocation().getBlockZ() + 20;
						int x2 = p.getLocation().getBlockX() - 20;
						int y2 = p.getLocation().getBlockY() - 10;
						int z2 = p.getLocation().getBlockZ() - 20;
						Boolean chest = false;
						for (int X = x2; X < x1; X++){
							for (int Y = y2; Y < y1; Y++){
								for (int Z = z2; Z < z1; Z++) {
	            	  
									
	            	  				Location loc = new Location(p.getWorld(), X, Y, Z);
	            	  				Block b = loc.getBlock();
						            if (b.getType() == Material.AIR){
						            	if (b.getRelative(BlockFace.DOWN).getType() != Material.AIR && b.getRelative(BlockFace.DOWN).getType() != Material.CHEST) {		
						            		if (b.getRelative(BlockFace.UP).getType() == Material.AIR){
								            	int rnd = 0 + (int)(Math.random() * 1001.0D);
							            		if (RandomSpawn.this.pl.getConfig().getInt("random_spawning.chance") >= rnd) {
							            			List<String> list = RandomSpawn.this.pl.getConfig().getStringList("chunks");
							            			List<String> lists = list;
							            			if (list.isEmpty()){
							            				chest = true;
						            					loc.getBlock().setType(Material.CHEST);
						            					List<String> L = new ArrayList<String>();			            					
						            					L.add(loc.getChunk().toString());
						            					RandomSpawn.this.pl.getConfig().set("chunks", L);
						            					RandomSpawn.this.pl.saveConfig();
						            					RandomSpawn.this.pl.reloadConfig();
						            					pl.randomspawn.put(loc, null);
						            					UnloadChest(loc.getBlock());
							            			}else{
							            				if (!lists.contains(loc.getChunk().toString())){
							            					chest = true;
							            					loc.getBlock().setType(Material.CHEST);						            								            					
							            					list.add(loc.getChunk().toString());
							            					RandomSpawn.this.pl.getConfig().set("chunks", list);
							            					RandomSpawn.this.pl.saveConfig();
							            					RandomSpawn.this.pl.reloadConfig();
							            					pl.randomspawn.put(loc, null);
							            					UnloadChest(loc.getBlock());
							            				}						            				
							            			}
							            		}
						            		} 
						            	}
						            		
						            }
						            if (chest == true){
										break;
									}
								}
								if (chest == true){
									break;
								}
								
							}
							if (chest == true){
								break;
							}					
						}
					}
				}
			}
		}, 0L, 1200L);
	}
	public void UnloadChest(final Block b){
		this.pl.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, new Runnable(){
			public void run(){
				b.setType(Material.AIR);
			}
		},6000L);
	}
}
