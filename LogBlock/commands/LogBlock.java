package LogBlock.commands;

import SpoutSDK.*;

import java.util.Arrays;
import java.util.List;

import LogBlock.SQL;
import LogBlock.Tool;

public class LogBlock implements CraftCommand {
    @Override
    public String getCommandName() {
        return "lb";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("logblock");
    }

    @Override
    public String getHelpLine(CraftPlayer mc_player) {
        return ChatColor.GOLD + "/lb" + ChatColor.WHITE + " --- LogBlock commands";
    }

    @Override
    public void handleCommand(CraftPlayer plr, String[] args) {
    	if(!(this.hasPermissionToUse(plr))) {
    		plr.sendMessage(ChatColor.RED + "You do not have permission for that command.");
    		return;
    	}
        String Player = plr.getName();

        if (args.length == 0) {
            if (Tool.isPlayerTooled(Player)) {
                Tool.TooledPlayers.remove(Player);
                plr.sendMessage(" ");
                plr.sendMessage(ChatColor.GOLD + "Tool Disabled.");
                plr.sendMessage(" ");
                return;
            } else {
                Tool.TooledPlayers.add(Player);
                plr.sendMessage(" ");
                plr.sendMessage(ChatColor.GOLD + "Tool Enabled.");
                plr.sendMessage(" ");
                return;
            }
        }
        if (args[0].equalsIgnoreCase("help")) {
        	this.sendHelpMessage(plr);
        	return;
        }
        if (args[0].equalsIgnoreCase("tool")) {
            String oldItem = plr.getItemInHand().getFriendlyName();
            plr.setItemInHand(Tool.wooden_pickaxe);
            plr.sendMessage(" ");
            plr.sendMessage(ChatColor.GOLD + "Replaced " + oldItem + " with the tool.");
            plr.sendMessage(" ");
            return;
        }
        if (args[0].equalsIgnoreCase("toolblock")) {
            String oldItem = plr.getItemInHand().getFriendlyName();
            plr.setItemInHand(Tool.bedrock);
            plr.sendMessage(" ");
            plr.sendMessage(ChatColor.GOLD + "Replaced " + oldItem + " with the toolblock.");
            plr.sendMessage(" ");
            return;
        }
        if (args[0].equalsIgnoreCase("player")) {
            if (args.length == 2) {
                SQL.lookupPlayer(plr, args[1], 0);
                return;
            }
            if (args.length == 3) {
                SQL.lookupPlayer(plr, args[1], Integer.parseInt(args[2]));
                return;
            }
            plr.sendMessage(ChatColor.RED + "Usage: /lb player (username) [page]");
            return;
        }
        if (args[0].equalsIgnoreCase("coord")) {
            if (args.length == 4) {
                SQL.lookupCoord(plr, plr.getLocation().dimension, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), 0, 0);
                return;
            }
            if (args.length == 5) {
                SQL.lookupCoord(plr, plr.getLocation().dimension, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), 0,Integer.parseInt(args[4]));
                return;
            }
            if (args.length == 6) {
                SQL.lookupCoord(plr, plr.getLocation().dimension, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[5]), Integer.parseInt(args[4]));
                return;
            }
            if (args.length == 7) {
                SQL.lookupCoord(plr, Integer.parseInt(args[6]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[5]), Integer.parseInt(args[4]));
                return;
            }
            plr.sendMessage(ChatColor.RED + "Usage: /lb coord (x) (y) (z) [page] [radius] [world]");
            return;
        }
        if (args[0].equalsIgnoreCase("recent")) {
            if (args.length == 1) {
                SQL.lookupRecent(plr, 0);
                return;
            }
            if (args.length == 2) {
                SQL.lookupRecent(plr, Integer.parseInt(args[1]));
                return;
            }
            plr.sendMessage(ChatColor.RED + "Usage: /lb recent [page]");
            return;
        }
        this.sendHelpMessage(plr);
        if (args.length > 0) {
            getHelpLine(plr);
        }
    }

    @Override
    public boolean hasPermissionToUse(CraftPlayer plr) {
        return plr.hasPermission("logblock.use");
    }

    @Override
    public List<String> getTabCompletionList(CraftPlayer plr, String[] strings) {
        return null;
    }
    
    public void sendHelpMessage(CraftPlayer plr) {
    	plr.sendMessage(" ");
        plr.sendMessage(ChatColor.DARK_AQUA + "LogBlock commands");
        plr.sendMessage(ChatColor.GOLD + "/lb --- Toggle tools");
        plr.sendMessage(ChatColor.GOLD + "/lb help --- View help page");
        plr.sendMessage(ChatColor.GOLD + "/lb tool --- Get the inspect tool");
        plr.sendMessage(ChatColor.GOLD + "/lb toolblock --- Get the inspect toolblock");
        plr.sendMessage(ChatColor.GOLD + "/lb player --- Get information about a player");
        plr.sendMessage(ChatColor.GOLD + "/lb coord --- Get information about a location");
        plr.sendMessage(ChatColor.GOLD + "/lb recent --- View recent block edits");
        plr.sendMessage(" ");
    }
}
