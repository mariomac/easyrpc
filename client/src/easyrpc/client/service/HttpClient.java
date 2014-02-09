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

package easyrpc.client.service;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mmacias on 09/02/14.
 */
public class HttpClient {
    String host, path;
    int port;

    public HttpClient(String host, int port, String path) throws MalformedURLException {
        this.host = host;
        this.port = port;

        if(path != null) {
            if(!path.startsWith("/")) {
                path = "/" + path;
            }
            while(path.endsWith("/")) {
                path = path.substring(0, path.length() - 1 );
            }
        }
        this.path = path;
    }

    public void sendMessage(String endpoint, byte[] info) {
        try {
            URL endpointUrl = new URL("http",host,port, path + "/" + endpoint);
            HttpURLConnection conn = (HttpURLConnection) endpointUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.write(info);
            dos.flush();
            dos.close();
            System.out.println("Response code: " + conn.getResponseCode());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
