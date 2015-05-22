import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;

public class requestPing extends Thread{
	DatagramSocket socket;
	int server;

	public requestPing(DatagramSocket s, int p) {
		try {
			socket = s;
			server = p;
		} catch (Exception e) {

		}
	}

	public void run() {
		try {
			while(true) {
				Thread.sleep(5000);
				byte [] buf = new byte[256];
				String message ="PING";
				buf = message.getBytes();
				InetAddress address = InetAddress.getByName("localhost");
				DatagramPacket packet = new DatagramPacket(buf, buf.length, address, server);
				socket.send(packet);
			}
		} catch(Exception e) {

		}
	}
}