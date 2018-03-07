package LogBlock;

import SpoutSDK.*;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {
	
	public synchronized static Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("[LogBlock/WARN]: Disabled");
			System.out.println("[LogBlock/WARN]: mySQL dependencies error " + e.getMessage());
		}
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Main.host + ":3306/",
					Main.dbuser, Main.dbpass);
		} catch (SQLException err) {
			System.out.println("[LogBlock/WARN]: Disabled");
			System.out.println("[LogBlock/WARN]: mySQL connection error " + err.getMessage());
		}
		return conn;
	}

	public static boolean checkDB() {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			String sql = "CREATE DATABASE `" + Main.database + "`;";
			statement.executeUpdate(sql);
		} catch (SQLException sqlException) {
			if (sqlException.getErrorCode() == 1007) {
				closeStatement(statement);
				closeConnection(connection);
				System.out.println("[LogBlock/INFO]: Database: OK");
				return true;
			} else {
				System.out.println("[LogBlock/WARN]: LogBlock Disabled! " + sqlException.getMessage());
				closeStatement(statement);
				closeConnection(connection);
				return false;
			}
		}
		
		System.out.println("[LogBlock/INFO]: Database: OK");
		closeStatement(statement);
		closeConnection(connection);
		return true;
	}

	public static boolean checkTable() {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			String sql = "USE `" + Main.database + "`;";
			statement.execute(sql);
			String createTable = "CREATE TABLE IF NOT EXISTS `" + Main.database + "`.`" + Main.dbtable + "` ("
					+ "`UID` INT NOT NULL AUTO_INCREMENT, "
					+ "`player` VARCHAR(45) NOT NULL, "
                    + "`world` VARCHAR(45) NOT NULL, "
					+ "`x` VARCHAR(45) NOT NULL, "
					+ "`y` VARCHAR(45) NOT NULL, "
					+ "`z` VARCHAR(45) NOT NULL, "
					+ "`time` VARCHAR(45) NOT NULL, "
					+ "`block` VARCHAR(255) NOT NULL, "
					+ "`event` VARCHAR(45) NOT NULL, "
					+ "PRIMARY KEY (`UID`));";
			statement.execute(createTable);
		} catch (SQLException e) {
			System.out.println("[LogBlock/WARN]: Disabled!");
			System.out.println("[LogBlock/WARN]: mySQL table related error" + e.getMessage());
			closeConnection(connection);
			closeStatement(statement);
			return false;
		}

		closeConnection(connection);
		closeStatement(statement);
		System.out.println("[LogBlock/INFO]: Tables OK");
		return true;
	}

	public static boolean insertBlockEvent(String player, int world, int x, int y, int z, String time, String block, String event) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			String SelectDB = "USE `" + Main.database + "`;";
			String Insert = "INSERT INTO `" + Main.dbtable + "` (`player`, `world`, `x`, `y`, `z`, `time`, `block`, `event`) VALUES ('"
					+ player
					+ "', '"
                    + world
                    + "', '"
					+ x
					+ "', '"
					+ y
					+ "', '"
					+ z
					+ "', '"
					+ time
					+ "', \""
					+ block
					+ "\", '"
					+ event
					+ "');";
			statement.execute(SelectDB);
			statement.execute(Insert);
		} catch (SQLException sqlException) {
			System.out.println("[LogBlock/WARN]: LogBlock Error!" + sqlException.getMessage());
			closeStatement(statement);
			closeConnection(connection);
			return false;
		}
		closeStatement(statement);
		closeConnection(connection);
		return true;
	}

	public static void getBlockRecord(CraftPlayer plr, int World, int X, int Y, int Z) {
		Connection connection = null;
		Statement statement = null;
		ResultSet rs;
        int start = 0;
        int end = start + 10;
        List<String> results = new ArrayList<String>();
		
		 try{
			 connection = getConnection();
				statement = connection.createStatement();
				String SelectDB = "USE `" + Main.database + "`;";
				String Fetch = "SELECT * FROM `" + Main.dbtable + "`" +
                        "WHERE `world`='" + World + "'" +
                        "AND `x`='" + X + "'" +
                        "AND `y`='" + Y + "'" +
                        "AND `z`='" + Z + "'" +
                        "ORDER BY UID DESC LIMIT " + start + ", " + end + ";";
				statement.execute(SelectDB);
				rs = statement.executeQuery(Fetch); 

		        while (rs.next()) {
                    results.add(ChatColor.GOLD + rs.getString("time") + " " + rs.getString("player") + " " + rs.getString("event") + " " + rs.getString("block"));
		        }
             plr.sendMessage(ChatColor.DARK_AQUA + "Block changes at " + X + ", " + Y + ", " + Z);
             Collections.sort(results);
             for(int i = 0; i < results.size(); i++){
                 plr.sendMessage(results.get(i));
             }

        } catch(SQLException ex){
            plr.sendMessage("Error: " + ex.getMessage());
        }
		
		closeConnection(connection);
		closeStatement(statement);
	}

	public static void lookupPlayer(CraftPlayer player, String targetPlayer, int page) {

		Connection connection = null;
		Statement statement = null;
		ResultSet rs;
		int start = page * 10;
		int end = start + 10;
		List<String> results = new ArrayList<String>();

		try{
			connection = getConnection();
			statement = connection.createStatement();
			String SelectDB = "USE `" + Main.database + "`;";
			String Fetch = "SELECT * FROM `" + Main.dbtable + "` " +
					"WHERE `player` LIKE '%" + targetPlayer + "%' " +
					"ORDER BY UID DESC LIMIT " + start + ", " + end + ";";
			statement.execute(SelectDB);
			rs = statement.executeQuery(Fetch);

			while (rs.next()) {
				results.add(ChatColor.GOLD + rs.getString("time") + " " + rs.getString("world") + " (" + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") " + rs.getString("event") + " " + rs.getString("block"));
			}
			player.sendMessage(ChatColor.DARK_AQUA + "Block changes by " + targetPlayer);
			Collections.sort(results);
			for(int i = 0; i < results.size(); i++){
				player.sendMessage(results.get(i));
			}

		} catch(SQLException ex){
			player.sendMessage("Error: " + ex.getMessage());
		}

		closeConnection(connection);
		closeStatement(statement);
	}

	public static void lookupCoord(CraftPlayer plr, int World, int X, int Y, int Z, int Radius, int page) {
		Connection connection = null;
		Statement statement = null;
		ResultSet rs;
		int start = page * 10;
		int end = start + 10;
		List<String> results = new ArrayList<String>();

		try{
			connection = getConnection();
			statement = connection.createStatement();
			String SelectDB = "USE `" + Main.database + "`;";
			String Fetch = "SELECT * FROM `" + Main.dbtable + "` " +
					"WHERE (`world`='" + World + "' " +
					"AND (`x` BETWEEN '" + (X - Radius) + "' AND '" + (X + Radius) + "') " +
					"AND (`y` BETWEEN '" + (Y - Radius) + "' AND '" + (Y + Radius) + "') " +
					"AND (`z` BETWEEN '" + (Z - Radius) + "' AND '" + (Z + Radius) + "')) " +
					"ORDER BY UID DESC LIMIT " + start + ", " + end + ";";
			statement.execute(SelectDB);
			rs = statement.executeQuery(Fetch);

			while (rs.next()) {
				results.add(ChatColor.GOLD + rs.getString("time") + " " + rs.getString("player") + " " + rs.getString("event") + " " + rs.getString("block"));
			}
			plr.sendMessage(ChatColor.DARK_AQUA + "Block changes within " + Radius + " blocks of " + World + " (" + X + ", " + Y + ", " + Z + ")");
			Collections.sort(results);
			for(int i = 0; i < results.size(); i++){
				plr.sendMessage(results.get(i));
			}

		} catch(SQLException ex){
			plr.sendMessage("Error: " + ex.getMessage());
		}

		closeConnection(connection);
		closeStatement(statement);
	}

	public static void lookupRecent(CraftPlayer player, int page) {

		Connection connection = null;
		Statement statement = null;
		ResultSet rs;
		int start = page * 10;
		int end = start + 10;
		List<String> results = new ArrayList<String>();

		try{
			connection = getConnection();
			statement = connection.createStatement();
			String SelectDB = "USE `" + Main.database + "`;";
			String Fetch = "SELECT * FROM `" + Main.dbtable + "` " +
					"ORDER BY UID DESC LIMIT " + start + ", " + end + ";";
			statement.execute(SelectDB);
			rs = statement.executeQuery(Fetch);

			while (rs.next()) {
				results.add(ChatColor.GOLD + rs.getString("time") + " " + rs.getString("world") + " (" + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") " + rs.getString("player") + " " + rs.getString("event") + " " + rs.getString("block"));
			}
			player.sendMessage(ChatColor.DARK_AQUA + "Recent block changes");
			Collections.sort(results);
			for(int i = 0; i < results.size(); i++){
				player.sendMessage(results.get(i));
			}

		} catch(SQLException ex){
			player.sendMessage("Error: " + ex.getMessage());
		}

		closeConnection(connection);
		closeStatement(statement);
	}

	public static void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("[LogBlock/WARN]: mySQL error: Could not close connection" + e.getMessage());
		}
	}

	public static void closeStatement(Statement statement) {
		try {
			statement.close();
		} catch (SQLException e) {
			System.out.println("[LogBlock/WARN]: mySQL error: Could not close statement" + e.getMessage());
		}
	}

}