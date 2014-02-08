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
import easyrpc.test.IFace;

/**
 * Created by mmacias on 08/02/14.
 */
public class Test {
    public static final void main(String[] args) throws Exception {

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

        IFace obj = (IFace) new Instantiator().instantiate(IFace.class);

        obj.method1(1);
        obj.method2(1,"33");
        obj.method3(3L);
        obj.elMetodazoZasca();

    }
}
