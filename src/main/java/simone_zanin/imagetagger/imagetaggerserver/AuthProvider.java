package simone_zanin.imagetagger.imagetaggerserver;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AuthProvider", urlPatterns = {"/auth"})
public class AuthProvider extends HttpServlet {

    private static final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuthParams ap = gson.fromJson(request.getReader(), AuthParams.class);
        Utils.log(this, ap.username+"\t"+ap.password);
        try (Connection conn = DatabaseInterface.connect()) {
            if (DatabaseInterface.getUID(conn, ap.username, ap.password) >= 0) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception sqle) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Utils.log(this, "Auth exception", sqle.toString());
            sqle.printStackTrace();
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Login management";
    }

}
