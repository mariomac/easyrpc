/*
 * ----------------------------------------------------------------------------
 * This code is distributed under a Beer-Ware license
 * ----------------------------------------------------------------------------
 * Mario Macias wrote this file. Considering this, you can do what the fuck you
 * want: modify it, distribute it, sell it, etc. But you MUST always credit me
 * as the original author of this code. In addition, if we met some day and you
 * think this code was useful to you, you MUST pay me a beer (a good one, if
 * possible) as reward for my contribution.
 *
 * Mario Macias Lloret, 2014
 * ----------------------------------------------------------------------------
 */

package easyrpc.server.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by mmacias on 08/02/14.
 */
public class HttpService {
    private int port;
    private String path;

    void start() {
       /*Server server = new Server(8080);
        ServletContextHandler webApp = new ServletContextHandler(ServletContextHandler.SESSIONS);
        webApp.setContextPath("/");
        webApp.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("text/html");
                resp.getWriter().println("<h1>Mariconazo</h1>");
            }
        }), "/*");
        server.setHandler(webApp);
        server.start();*/
    }

    class TheServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            StringBuilder contents = new StringBuilder();
            BufferedReader reader = req.getReader();
            char[] buffer = new char[256];
            int read = 0;
            do {
                read = reader.read(buffer);
                if(read > 0) {
                    contents.append(buffer);
                }
            } while(read != -1);
            System.out.println("Received: " + contents.toString());
        }
    }

}
