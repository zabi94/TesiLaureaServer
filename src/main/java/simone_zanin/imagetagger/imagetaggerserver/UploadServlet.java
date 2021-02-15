package simone_zanin.imagetagger.imagetaggerserver;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Upload", urlPatterns = {"/upload"})
public class UploadServlet extends HttpServlet {
    
    private static final Gson gson = new Gson();
    private static final File imageStoragePath = new File("/opt/payara/pictures");
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        PictureData pic = gson.fromJson(request.getReader(), PictureData.class);
        String address = Utils.getRealRemoteAddress(request);
        String username = request.getHeader("ImageTaggerUser");
        Utils.log(this, address, "Upload", pic.toString());
        
        try (Connection conn = DatabaseInterface.connect()) {
            int userId = DatabaseInterface.getUID(conn, username, request.getHeader("ImageTaggerAuthentication"));
            if (userId >= 0) {
                File file = createImageFile(pic, username);
                DatabaseInterface.createDatabaseEntry(conn, pic, userId, address, file);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                Utils.log(this, address, "Uploaded correctly for user "+username);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Utils.log(this, address, "Upload rejected: failed authentication for "+username);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
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
    
}
