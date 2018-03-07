package LogBlock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import LogBlock.commands.LogBlock;
import SpoutSDK.*;

public class Main extends ModBase {
    public static String version = "1.0.0";
    public static ExecutorService queue;
	public static String host;
	public static String database;
	public static String dbuser;
	public static String dbpass;
    public static String dbtable;
    public static int queueThreads = 2;
    public static long queueShutdownTimeout = 60;
	public static boolean Track;

    public void onStartup(CraftServer argServer)
    {
        System.out.println("[LogBlock/INFO]: Logblock version " + version + " starting up...");

        System.out.println("[LogBlock/INFO]: Checking Config...");
        if (Config.readConfig()) {
            System.out.println("[LogBlock/INFO]: Checking Database...");
            if (SQL.checkDB()) {
                System.out.println("[LogBlock/INFO]: Checking Table...");
                if (SQL.checkTable()) {
                    queue = Executors.newFixedThreadPool(queueThreads);
                    Track = true;
                    System.out.println("[LogBlock/INFO]: Enabled!");
                } else {
                    Track = false;
                }
            } else {
                Track = false;
            }
        } else {
            Track = false;
        }

        SpoutHelper.getServer().registerCommand(new LogBlock());
    }

    public ModInfo getModInfo()
    {
        ModInfo info = new ModInfo();
        info.description = "A block logging implementation (" + version + ")";
        info.name = "LogBlock";
        info.version = version;
        return info;
    }

    public void onBlockBroke(final CraftPlayer plr, final CraftLocation loc, final CraftBlock blk) {
		if (Track) {
            queue.execute(new Runnable() {
                public void run() {
                    String Time = getTime();
                    String Player = plr.getName();
                    int World = loc.dimension;
                    int X = loc.getBlockX();
                    int Y = loc.getBlockY();
                    int Z = loc.getBlockZ();

                    String Block = BlockHelper.getBlockFriendlyName(blk.getId(), blk.getSubtype());
                    String Event = "destroyed";

                    SQL.insertBlockEvent(Player, World, X, Y, Z, Time, Block, Event);
                }
            });
		}
	}

	public void onItemPlaced(final CraftPlayer plr, final CraftLocation loc, final CraftItemStack isHandItem, CraftLocation locPlacedAgainst, CraftDirectionNESWUD dir) {
		if (Track) {
            queue.execute(new Runnable() {
                public void run() {
                    String Time = getTime();
                    String Player = plr.getName();

                    int World = loc.dimension;
                    int X = loc.getBlockX();
                    int Y = loc.getBlockY();
                    int Z = loc.getBlockZ();

                    String Block = isHandItem.getFriendlyName();
                    String Event = "created";

                    SQL.insertBlockEvent(Player, World, X, Y, Z, Time, Block, Event);
                }
            });
		}
	}

    public void onAttemptBlockBreak(final CraftPlayer plr, final CraftLocation loc, CraftEventInfo ei) {
        if (Tool.TooledPlayers.contains(plr.getName()) && (plr.getItemInHand().getId() == 7 || plr.getItemInHand().getId() == 270)) {
            ei.isCancelled = true;
            queue.execute(new Runnable() {
                public void run() {
                    Tool.getCoordEdits(plr, loc, CraftDirectionNESWUD.UNSPECIFIED, false);
                }
            });
        }
    }

    public void onAttemptPlaceOrInteract(final CraftPlayer plr, final CraftLocation loc, CraftEventInfo ei, final CraftDirectionNESWUD dir) {
        if (Tool.TooledPlayers.contains(plr.getName()) && (plr.getItemInHand().getId() == 7 || plr.getItemInHand().getId() == 270)) {
            ei.isCancelled = true;
            queue.execute(new Runnable() {
                public void run() {
                    Tool.getCoordEdits(plr, loc, dir, true);
                }
            });
        }
    }
	
	public String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

    public void onShutdown() {
        System.out.println("[LogBlock/INFO]: Shutting down LogBlock...");
        queue.shutdown();
        try {
            queue.awaitTermination(queueShutdownTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("[LogBlock/WARN]: Error waiting for unsent block logs.");
        }
        System.out.println("[LogBlock/INFO]: Successfully shutdown LogBlock.");
    }
}

