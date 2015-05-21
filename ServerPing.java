import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.security.*;
import java.math.*;


class ServerPing extends Thread{
	DatagramSocket server;

	public ServerPing() {
		//server = new DatagramSocket(s+1);  
	}


	public void run() {
		try {
			while(true) {
				Thread.sleep(5000);
				for(int i = 0; i < 50; i++) {
					Server.pingSent = 0;
					if(Server.workers[i][0] != null) {
						if(Server.workers[i][5].equals("1")) {
							int port = Integer.parseInt(Server.workers[i][0]);
							byte [] buf = new byte[256];
							String message ="PING";
							//System.out.println("ll");
							//server.setSoTimeout(2000);
							buf = message.getBytes();
							InetAddress address = InetAddress.getByName("localhost");
							DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
							Ping.socket.send(packet);
							Server.pingSent = port;
							Thread.sleep(1500);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}