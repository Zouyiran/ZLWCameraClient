package com.ctcc.zlwcamera.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Zouyiran on 2014/11/27.
 *
 */
public class SendImageThread extends Thread {
    private byte byteBuffer[] = new byte[1024];
    private OutputStream outsocket;
    private ByteArrayOutputStream outputStream;
    private String server;
    private int port;

    public SendImageThread(ByteArrayOutputStream myoutputstreame, String server, int port){
        this.outputStream = myoutputstreame;
        this.server = server;
        this.port = port;
    }

    public void run() {
        try{
            Socket tempSocket = new Socket(server, port);
            outsocket = tempSocket.getOutputStream();
            ByteArrayInputStream inputstream = new ByteArrayInputStream(outputStream.toByteArray());
            int amount;
            while ((amount = inputstream.read(byteBuffer)) != -1) {
                outsocket.write(byteBuffer, 0, amount);
            }
            outputStream.flush();
            outputStream.close();
            tempSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}