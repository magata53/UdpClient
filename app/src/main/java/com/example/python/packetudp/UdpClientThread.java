package com.example.python.packetudp;

import android.os.Message;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;


public class UdpClientThread extends Thread {

    String dstAddress;
    String msgUdp;
    int dstPort;
    private boolean running;
    final long timeInterval = 5000;
    MainActivity.UdpClientHandler handler;

    DatagramSocket socket;
    InetAddress address;
    Calendar date;
    Date dates;


    public UdpClientThread(String addr, int port, MainActivity.UdpClientHandler handler, String msg) {
        super();
        dstAddress = addr;
        dstPort = port;
        msgUdp = msg;
        this.handler = handler;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void sendState(String state) {
        handler.sendMessage(
                Message.obtain(handler,
                        MainActivity.UdpClientHandler.UPDATE_STATE, state));
    }


    @Override
    public void run() {

        while (!UdpClientThread.currentThread().isInterrupted()) {
            sendState("connecting...");

            running = true;

            try {
                dates = new Date();
                date = Calendar.getInstance();
                socket = new DatagramSocket();
                address = InetAddress.getByName(dstAddress);


                String msg = "Message : " + msgUdp + "==> Date : " + date.get(Calendar.YEAR) + " - " + date.get(Calendar.MONTH) + " - " + date.get(Calendar.DATE) + " - " + date.get(Calendar.HOUR_OF_DAY) + " - " + date.get(Calendar.MINUTE) + " - " + date.get(Calendar.SECOND) + " - " + date.get(Calendar.MILLISECOND);

                byte[] buf, buff = new byte[1024];

                buf = msg.getBytes();

                DatagramPacket packet =
                        new DatagramPacket(buf, buf.length, address, dstPort);
                socket.send(packet);

                sendState("Connected");


                // get response
                packet = new DatagramPacket(buff, buff.length);


                socket.receive(packet);
                String line = new String(packet.getData(), 0, packet.getLength());

                handler.sendMessage(
                        Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_MSG, line));

                UdpClientThread.sleep(timeInterval);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            catch (Exception e) {
//                UdpClientThread.currentThread().interrupt();
//            }
// finally {
//             if (socket != null) {
//                   socket.close();
//                   handler.sendEmptyMessage(MainActivity.UdpClientHandler.UPDATE_END);
//               }
        }

    }

}


