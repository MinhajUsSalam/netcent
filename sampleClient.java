import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;


class sampleClient {
	public static void main(String [] args) {
		try {
			int port = Integer.parseInt(args[0]);
			int myport = Integer.parseInt(args[1]);
			DatagramSocket sock = new DatagramSocket(myport);
			byte [] buf = new byte[512];
			String msg = args[2];
			String message ="HASH " + Server.calcHash(msg) + " " + myport;
			buf = message.getBytes();
			InetAddress address = InetAddress.getByName("localhost");
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			sock.send(packet);
			requestPing rp = new requestPing(sock,port);
			rp.start();
			buf = new byte[256];
			packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
			sock.receive(packet);
			String input = new String(packet.getData(), 0, packet.getLength());
			rp.interrupt();
			System.out.println(input);
		} catch (Exception e) {

		} 
	}
}