import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.security.*;
import java.math.*;





class Server extends Thread{
	public static String [] [] workers = new String [50] [5];
	public static int [] clients = new int [100];
	public static String [] freeWorkers = new String [50];
	public static String [] [] jobs = new String [100][4];
	public static int noOfWorkers = 0;
	public static int noOfJobs = 0;
	public static String [] [] incompleteJobs = new String [1000][2];
	public static int failed = 0;

	public static void main(String [] args) {
		try{
			int port = Integer.parseInt(args[0]);
			System.out.println(port);
			DatagramSocket socket = new DatagramSocket(port);
			ServerPing sp = new ServerPing(socket);
			sp.start();	
			byte[] buf = new byte[256];
			while(true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				ServerThread sT = new ServerThread(socket, packet);
				System.out.println("port");
				sT.start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	public static String calcHash(String key) {
	  try {
	  	MessageDigest m=MessageDigest.getInstance("MD5");
      	m.update(key.getBytes(),0,key.length());
      	String g = new BigInteger(1,m.digest()).toString(16);
      	return g;
      } catch(Exception e) {

      }
      return "aa";
	}
}