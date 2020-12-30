package simone_zanin.imagetagger.imagetaggerserver;

import javax.servlet.GenericServlet;
import javax.servlet.http.HttpServletRequest;

public class Utils {
    
    public static String getRealRemoteAddress(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        final String ipForward = request.getHeader( "X-Forwarded-For");
        if (ipForward != null && ipForward.length() > 0) {
            ip = ipForward;
        }
        return ip;
    }
    
    public static void log(GenericServlet servlet, String... strings) {
        StringBuilder sb = new StringBuilder('\n');
        sb.append('<');
        sb.append(servlet.getServletName());
        sb.append('>');
        for (int i = 0; i < strings.length - 1; i++) {
            sb.append('[');
            sb.append(strings[i]);
            sb.append(']');
        }
        sb.append(' ');
        sb.append(strings[strings.length - 1]);
        sb.append('\n');
        servlet.getServletContext().log(sb.toString());
    }
    
}
