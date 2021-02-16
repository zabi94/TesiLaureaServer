package simone_zanin.imagetagger.imagetaggerserver;

import com.google.gson.Gson;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseInterface {
    private static final Gson gson = new Gson();
    
    private static final String DATABASE_ADDRESS = "mariadb:3306";
    private static final String DATABASE_NAME = "ImageTagger";
    private static final String DATABASE_USER = "imagetagger";
    private static final String DATABASE_PASS = "imagetagger";
    
    private static final String CREATION_QUERY =
            "CREATE TABLE IF NOT EXIST `"+DATABASE_NAME+"`.`users` ( "
            + "`uid` INT NOT NULL AUTO_INCREMENT , "
            + "`username` VARCHAR(255) NOT NULL , "
            + "`password` VARCHAR(255) NOT NULL , "
            + "PRIMARY KEY (`uid`)"
            + ");"
            + "CREATE TABLE IF NOT EXIST `"+DATABASE_NAME+"`.`pictures` ( "
            + "`file` VARCHAR(255) NOT NULL , "
            + "`user` INT NOT NULL REFERENCES users(uid), "
            + "`ip` VARCHAR(15) NOT NULL , "
            + "`description` TEXT NOT NULL , "
            + "`tags` TEXT NOT NULL , "
            + "`latitude` DOUBLE NOT NULL , "
            + "`longitude` DOUBLE NOT NULL , "
            + "`uploadtime` DATETIME NOT NULL , "
            + "PRIMARY KEY (`file`)"
            + ");";
    
    public static Connection connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://"+DATABASE_ADDRESS+"/"+DATABASE_NAME + "?user="+DATABASE_USER+"&password="+DATABASE_PASS);
        conn.prepareStatement(CREATION_QUERY).execute();
        return conn;
    }
    
    public static void createDatabaseEntry(Connection conn, PictureData pic, int user_id, String address, File file) throws Exception {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO pictures VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, file.getAbsolutePath());
        statement.setInt(2, user_id);
        statement.setString(3, address);
        statement.setString(4, pic.description);
        statement.setString(5, serializeTags(pic.tags));
        statement.setDouble(6, pic.latitude);
        statement.setDouble(7, pic.longitude);
        statement.setDate(8, new Date(System.currentTimeMillis()));
        
        statement.execute();
    }
    
    private static String serializeTags(String[] tags) {
        return gson.toJson(tags);
    }
    
    public static int getUID(Connection conn, String un, String pw) throws Exception {
        PreparedStatement statement = conn.prepareStatement("SELECT uid FROM users WHERE username = ? AND password = ?;", ResultSet.TYPE_SCROLL_SENSITIVE);
        statement.setString(1, un);
        statement.setString(2, pw);
        
        ResultSet results = statement.executeQuery();
        int uid = -1;
        while (results.next()) {
            if (uid >= 0) throw new Exception("Multiple users for same login data: "+un+", "+pw);
            uid = results.getInt("uid");
        }
        return uid;
    }
}
