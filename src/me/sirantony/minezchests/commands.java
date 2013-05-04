package me.sirantony.minezchests;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class commands implements CommandExecutor{
  private main pl;
	public commands(main instance) {
		pl = instance;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (sender instanceof Player){
			Player p = (Player) sender;
			if (label.equalsIgnoreCase("minezchests") || label.equalsIgnoreCase("mc")){
				if (args.length == 1){
					if (args[0].equalsIgnoreCase("reload")){
						pl.reloadConfig();
						pl.getLogger().info("config reloaded!");
						sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
					}
					if (args[0].equalsIgnoreCase("stop")){
						if (pl.selection.containsKey(p)){
							p.sendMessage(ChatColor.GREEN + "Transforming mode off!");
							pl.selection.remove(p);
						}						
					}					
					if(args[0].equalsIgnoreCase("destroy")){
						if(pl.destroy.containsKey(p)){
							pl.destroy.remove(p);
							p.sendMessage(ChatColor.GREEN + "Destroy mode off!");
						}else{
							pl.destroy.put(p, null);
							p.sendMessage(ChatColor.GREEN + "Destroy mode on!");
						}					
					}
					if(args[0].equalsIgnoreCase("restore")){
						Set<Location> keys = pl.chest.keySet();						
						for (Location loc : keys){							
							loc.getBlock().setType(Material.CHEST);
							pl.chest.remove(pl.chest.get(loc));
							
						}
						pl.chest.clear();
					}
				}
				if (args[0].equalsIgnoreCase("select")){
					if (args.length == 1){
						pl.selection.put(p ,"");
						p.sendMessage(ChatColor.GREEN + "Select chests!");
					}
					else if (args.length == 2){
						List<String> lists = pl.getConfig().getStringList("activated-configurations");
						for (String s : lists){
							if (args[1].equalsIgnoreCase(s)){
								pl.selection.put(p, args[1]);
								p.sendMessage(ChatColor.GREEN + "Select chests!");
								break;
							}
							
						}
						if (!pl.selection.containsKey(p)){
							p.sendMessage(ChatColor.RED + "Wrong configuration name!");
						}
						
					}
				}
			}
			p.sendMessage(ChatColor.YELLOW + "-=help=-");
			p.sendMessage(ChatColor.BLUE + "/mc select (configuration-name)");
			p.sendMessage(ChatColor.BLUE + "/mc destroy");
			p.sendMessage(ChatColor.BLUE + "/mc stop");
			p.sendMessage(ChatColor.BLUE + "/mc reload");
			p.sendMessage(ChatColor.BLUE + "/mc restore");
		}		
		return false;
	}
}
