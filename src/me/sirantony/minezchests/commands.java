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

	public commands(main instance){
		this.pl = instance;
	}	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (((sender instanceof Player)) && (sender.hasPermission("minezchests.admin"))) {
			Player p = (Player)sender;
			if ((label.equalsIgnoreCase("minezchests")) || (label.equalsIgnoreCase("mc"))) {
				if (args.length == 0)
					p.sendMessage(ChatColor.AQUA + "No arguments. Typ /mc help if you don't know what to do.");
				else if (args[0].equalsIgnoreCase("select")) {
					if (args.length == 1) {
						if (this.pl.selection.containsKey(p)){
							p.sendMessage(ChatColor.RED + "You are already selecting chests!");
							p.sendMessage(ChatColor.RED + "Type \"/mc\" stop to stop selecting and save!");
						}else{
							this.pl.selection.put(p, "");
							p.sendMessage(ChatColor.GREEN + "Select chests!");
						}           
					}
					else if (args.length == 2) {
						List<String> lists = this.pl.getConfig().getStringList("activated-configurations");
						for (String s : lists) {
							if (args[1].equalsIgnoreCase(s)) {
								this.pl.selection.put(p, args[1]);
								p.sendMessage(ChatColor.GREEN + "Select chests!");
								break;
							}
						}

						if (!this.pl.selection.containsKey(p)) {
							p.sendMessage(ChatColor.RED + "Wrong configuration name!");
						}
					}
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("reload")) {
						this.pl.reloadConfig();
						this.pl.getLogger().info("config reloaded!");
						sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
					} else if (args[0].equalsIgnoreCase("stop")) {
						if (this.pl.selection.containsKey(p)) {
							p.sendMessage(ChatColor.GREEN + "Transforming mode off!");
							this.pl.selection.remove(p);
						}
					} else if (args[0].equalsIgnoreCase("destroy")) {
						if (this.pl.destroy.containsKey(p)) {
							this.pl.destroy.remove(p);
							p.sendMessage(ChatColor.GREEN + "Destroy mode off!");
						} else {
							this.pl.destroy.put(p, null);
							p.sendMessage(ChatColor.GREEN + "Destroy mode on!");
						}
					} else if (args[0].equalsIgnoreCase("restore")) {
						Set<Location> keys = this.pl.chest.keySet();
						for (Location loc : keys) {
							loc.getBlock().setType(Material.CHEST);
							this.pl.chest.remove(this.pl.chest.get(loc));
						}

						this.pl.chest.clear();
					} else if (args[0].equalsIgnoreCase("help")) {
						helpMessage(p);
					}
				}
			}
		}
		return false;
 	}
	public void helpMessage(Player p) {
	    p.sendMessage(ChatColor.YELLOW + "--==help==--");
	    p.sendMessage(ChatColor.BLUE + "/mc select (configuration-name)");
	    p.sendMessage(ChatColor.BLUE + "/mc destroy");
	    p.sendMessage(ChatColor.BLUE + "/mc stop");
	    p.sendMessage(ChatColor.BLUE + "/mc reload");
	    p.sendMessage(ChatColor.BLUE + "/mc restore");
	    p.sendMessage(ChatColor.YELLOW + "--==help==--");
	}
}
