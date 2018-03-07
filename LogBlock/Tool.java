package LogBlock;

import SpoutSDK.*;

import java.util.LinkedList;

public class Tool {
    public static LinkedList<String> TooledPlayers = new LinkedList<String>();
    public static final CraftItemStack bedrock = SpoutHelper.getServer().createItemStack(7, 1, 0);
    public static final CraftItemStack wooden_pickaxe = SpoutHelper.getServer().createItemStack(270, 1, 0);

	public static void getCoordEdits(CraftPlayer Player, CraftLocation coords, CraftDirectionNESWUD dir, boolean placed) {
        int World = coords.dimension;
		int X = coords.getBlockX();
		int Y = coords.getBlockY();
		int Z = coords.getBlockZ();

        if (placed) {
            switch (dir) {
                case DOWN:
                    Y = Y - 1;
                    break;
                case UP:
                    Y = Y + 1;
                    break;
                case NORTH:
                    Z = Z - 1;
                    break;
                case SOUTH:
                    Z = Z + 1;
                    break;
                case EAST:
                    X = X + 1;
                    break;
                case WEST:
                    X = X - 1;
                    break;
            }
        }

		SQL.getBlockRecord(Player, World, X, Y, Z);
	}

	public static boolean isPlayerTooled(String Player) {
		if (!(TooledPlayers.isEmpty() || TooledPlayers.size() == 0)) {
			if (TooledPlayers.contains(Player)) {
				return true;
			}
		}
		return false;
	}

}
