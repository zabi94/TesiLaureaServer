/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simone_zanin.imagetagger.imagetaggerserver;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author simone
 */
@WebServlet(name = "Heartbeat", urlPatterns = {"/heartbeat"})
public class Heartbeat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        Utils.log(this, Utils.getRealRemoteAddress(request), "heartbeat");
    }

    @Override
    public String getServletInfo() {
        return "Heartbeat and probe connection service";
    }

}
