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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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

    public HttpService(int port, String path) {
        this.port = port;
        if(path == null || path.trim().equals("")) {
            this.path = "/";
        }
    }

    public void start() {
       Server server = new Server(port);
        ServletContextHandler webApp = new ServletContextHandler(ServletContextHandler.SESSIONS);
        webApp.setContextPath(path);
        webApp.addServlet(new ServletHolder(new TheServlet()), "/*");
        server.setHandler(webApp);
        try {
            server.start();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
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