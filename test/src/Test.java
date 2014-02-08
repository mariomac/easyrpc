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

import easyrpc.client.Instantiator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mmacias on 08/02/14.
 */
public class Test {
    public static final void main(String[] args) throws Exception {

        Server server = new Server(8080);
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
        server.start();

        IFace obj = (IFace) new Instantiator().instantiate(IFace.class);

        obj.method1(1);
        obj.method2(1,"33");
        obj.method3(1234L);
        obj.elMetodazoZasca();

    }
}
