package simone_zanin.imagetagger.imagetaggerserver;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
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
        if ((ap.username.equals("zabi94") && ap.password.equals("hi")) || ap.username.equals("guest")) {
            response.setStatus(204);
        } else {
            response.setStatus(400);
        }
    }

    @Override
    public String getServletInfo() {
        return "Login management";
    }

}
