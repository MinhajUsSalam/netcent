import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;



class ServerPing extends Thread{
	DatagramSocket server;

	public ServerPing(DatagramSocket s) {
		server = s;
	}


	public void run() {
		try {
			while(true) {
				Thread.sleep(5000);
				for(int i = 0; i < 50; i++) {
					if(Server.workers[i][0] != null) {
						int port = Integer.parseInt(Server.workers[i][0]);
						byte [] buf = new byte[256];
						String message ="PING";
						buf = message.getBytes();
						InetAddress address = InetAddress.getByName("localhost");
						DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
						server.send(packet);
						Thread.sleep(1500);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}