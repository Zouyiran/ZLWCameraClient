package com.ctcc.zlwcamera.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zouyiran on 2014/11/23.
 *
 */
public class HttpRequest {
    public static String sendPost(String url, String param) {

        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        try{
            URL realUrl= new URL(url);//MalformedURLException
            connection  = (HttpURLConnection) realUrl.openConnection();//IOException
            connection.setRequestMethod("POST");
            setProperty(connection);

            OutputStream out = connection.getOutputStream();
            //DataOutputStream
            DataOutputStream postForm = new DataOutputStream(out);
            postForm.writeBytes(param);

            InputStream in = connection.getInputStream();
            //BufferReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null){
                response.append(line);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        return response.toString();
    }

    private static void setProperty(HttpURLConnection connection){
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
    }
}
