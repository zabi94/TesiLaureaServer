package simone_zanin.imagetagger.imagetaggerserver;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Upload", urlPatterns = {"/upload"})
public class UploadServlet extends HttpServlet {
    
    private static final Gson gson = new Gson();
    
    private static final String DATABASE_ADDRESS = "mariadb:3306";
    private static final String DATABASE_NAME = "ImageTagger";
    private static final String DATABASE_USER = "imagetagger";
    private static final String DATABASE_PASS = "imagetagger";
    
    private static final String CREATION_QUERY =
            "CREATE TABLE `"+DATABASE_NAME+"`.`pictures` ( "
            + "`file` VARCHAR(255) NOT NULL , "
            + "`user` TEXT NOT NULL , "
            + "`ip` VARCHAR(15) NOT NULL , "
            + "`description` TEXT NOT NULL , "
            + "`tags` TEXT NOT NULL , "
            + "`latitude` DOUBLE NOT NULL , "
            + "`longitude` DOUBLE NOT NULL , "
            + "`uploadtime` DATETIME NOT NULL , "
            + "PRIMARY KEY (`file`)"
            + ");";
    
    
    private static final File imageStoragePath = new File("/opt/payara/pictures");
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        PictureData pic = gson.fromJson(request.getReader(), PictureData.class);
        String address = Utils.getRealRemoteAddress(request);
        String username = request.getHeader("ImageTaggerUser");
        Utils.log(this, address, "Upload", pic.toString());
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Utils.log(this, "Upload failed", e.getMessage(), e.toString());
        }
        
        if (AuthProvider.isCorrectLogin(username, request.getHeader("ImageTaggerAuthentication"))) {
            
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://"+DATABASE_ADDRESS+"/"+DATABASE_NAME + "?user="+DATABASE_USER+"&password="+DATABASE_PASS)) {
                File file = createImageFile(pic, username);
                createDatabaseEntry(conn, pic, username, address, file);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                Utils.log(this, address, "Uploaded correctly for user "+username);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                Utils.log(this, "Upload failed", e.getMessage(), e.toString());
            }
            
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Utils.log(this, address, "Upload rejected: failed authentication for "+username);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("Error 405: GET method not supported");
        }
        Utils.log(this, Utils.getRealRemoteAddress(req), "Rejected 'GET' request");
    }
    
    @Override
    public String getServletInfo() {
        return "Handles uploads";
    }
    
    private static String serializeTags(String[] tags) {
        return gson.toJson(tags);
    }
    
    private static File createImageFile(PictureData pic, String username) throws Exception {
        
        File subfolder = new File(imageStoragePath, username);
        if (!subfolder.exists() && !subfolder.mkdirs()) {
            throw new Exception("Can't create pictures folder");
        }
        
        String filename = pic.imagePath.substring(pic.imagePath.lastIndexOf('/'));
        File file = new File(subfolder, filename);
        
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(Base64.getDecoder().decode(pic.imageBase64));
        }
        
        return file;
    }
    
    private static void createDatabaseEntry(Connection conn, PictureData pic, String username, String address, File file) throws Exception {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO pictures VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, file.getAbsolutePath());
        statement.setString(2, username);
        statement.setString(3, address);
        statement.setString(4, pic.description);
        statement.setString(5, serializeTags(pic.tags));
        statement.setDouble(6, pic.latitude);
        statement.setDouble(7, pic.longitude);
        statement.setDate(8, new Date(System.currentTimeMillis()));
        
        statement.execute();
    }
    
}
