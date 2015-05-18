import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;


public class WorkerClient extends Thread {
	public static int Done = 1;
	public static int Found = 0;
	public static String start;
	public static String end; 
	public static String hash;
	public static String last;
	public static int running;
	public static void main(String [] args) {
		try {
			int port = Integer.parseInt(args[0]);
			int server = Integer.parseInt(args[1]);
			DatagramSocket sock = new DatagramSocket(port);
			byte [] buf = new byte[512];
			String message ="REQUEST_TO_JOIN " + Integer.toString(port);
			buf = message.getBytes();
			InetAddress address = InetAddress.getByName("localhost");
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, server);
			sock.send(packet);
			while(true) {
				Worker w1 = new Worker("s","e","w",sock,packet,1);
				buf = new byte[256];
				packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
				System.out.println("waiting");
				sock.receive(packet);
				String input = new String(packet.getData(), 0, packet.getLength());
				if(input.substring(0,3).equals("JOB")) {
					start = input.substring(4,9);
					end = input.substring(10,15);
					hash = input.substring(16,48);
					w1 = new Worker(start,end,hash,sock,packet,port);
					w1.start();
					running = 1;
					Done = 0;
					Found = 0;
					buf = ("ACK_JOB " + input.substring(4,48) + " " + Integer.toString(port) ).getBytes();
					packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
					sock.send(packet);
					System.out.println("acking");
				} else if (input.substring(0,4).equals("PING")) {
					System.out.println("pinged");
					if(Done == 1 && Found == 1) {
						buf = ("DONE_FOUND " + last + " " + hash + " " + Integer.toString(port)).getBytes();
						packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
						sock.send(packet);
					} else if(Done == 1 && Found == 0) {
						buf = ("DONE_NOT_FOUND " + Integer.toString(port) + " " + start + " " + end).getBytes();
						packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
						sock.send(packet);
					} else if (Done == 0) {
						buf = ("NOT_DONE " + start + " " + last + " " + hash + " " + Integer.toString(port)).getBytes();
						packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
						sock.send(packet);
					}
				} else if(input.substring(0,10).equals("CANCEL_JOB")) {
					System.out.println("canelling");
					if(running == 1) {
						Done = 1;
						Found = 0;
						w1.interrupt();
						running = 2;
					}
				}
			}
		} catch (Exception e) {

		}
	}

	
}