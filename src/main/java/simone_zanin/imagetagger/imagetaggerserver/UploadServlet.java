package simone_zanin.imagetagger.imagetaggerserver;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Upload", urlPatterns = {"/upload"})
public class UploadServlet extends HttpServlet {
    
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        PictureData pic = gson.fromJson(request.getReader(), PictureData.class);
        String address = Utils.getRealRemoteAddress(request);
        String username = request.getHeader("ImageTaggerUser");
        Utils.log(this, address, "Upload", pic.toString());
        
        if (AuthProvider.isCorrectLogin(username, request.getHeader("ImageTaggerAuthentication"))) {
            response.setStatus(HttpServletResponse.SC_OK);
            Utils.log(this, address, "Uploaded correctly for user "+username);
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

}
