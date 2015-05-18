import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.security.*;
import java.math.*;


public class Worker extends Thread{
	String start;
	String end;
	String hash;
	DatagramSocket sock; 
	DatagramPacket packet;
	int port;
	public Worker(String s, String e, String h, DatagramSocket sc, DatagramPacket p, int po){
		start =s;
		end = e;
		hash = h;
		sock = sc;
		packet = p;
		port = po;
	}


	public void run() {
		try {
			int sent = 0;
			InetAddress address = InetAddress.getByName("localhost");
			byte [] buf = new byte[256];
			System.out.println(hash);
			System.out.println(calcHash("aaa99"));
			System.out.println(end);
			int count = 0;
			String current = start;
			for(int i = 0; i < 62; i++) {
				for(int j = 0; j < 62; j++) {
					for(int k = 0; k < 62; k++) {
						for(int l = 0; l < 62; l++) {
							for(int m = 0; m < 61; m++) {
								if(calcHash(current).equals(hash)) {
									WorkerClient.Done = 1;
									WorkerClient.Found = 1;
									System.out.println("done");
									if(sent == 0) {
										buf = ("DONE_FOUND " + current + " " + hash + " " + Integer.toString(port)).getBytes();
										packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
										sock.send(packet);
										sent++;
									}
									break;
								}
								if(current.equals(end)) {
									WorkerClient.Done = 1;
									break;
								}
								char c = getNext(current.charAt(4));
								current = current.substring(0,4) + c;
								WorkerClient.last = current; 
								count++;
								//System.out.println(current);
								//System.out.println(count); 
							}
							if(l==62) {
								break;
							}
							if(calcHash(current).equals(hash)) {
									WorkerClient.Done = 1;
									WorkerClient.Found = 1;
									System.out.println("done");
									if(sent == 0) {
										buf = ("DONE_FOUND " + current + " " + hash + " " + Integer.toString(port)).getBytes();
										packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
										sock.send(packet);
										sent++;
									}
									break;
								}
							if(current.equals(end)) {
								WorkerClient.Done = 1;
								break;
							}
							char a = current.charAt(4);
							char c = getNext(current.charAt(3));
							//System.out.println(c);
							current = current.substring(0,3) + c + 'a';
							WorkerClient.last = current;
							count++;
							//System.out.println(count);
						}
						if(k==62) {
							break;
						}
						if(calcHash(current).equals(hash)) {
							WorkerClient.Done = 1;
							WorkerClient.Found = 1;
							System.out.println("done");
							if(sent == 0) {
								buf = ("DONE_FOUND " + current + " " + hash + " " + Integer.toString(port)).getBytes();
								packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
								sock.send(packet);
								sent++;
							}
							break;
						}
						if(current.equals(end)) {
							WorkerClient.Done = 1;
							break;
						}
						char c = getNext(current.charAt(2));
						current = current.substring(0,2) + c + 'a' + 'a'; 
						WorkerClient.last = current;
						count++;
						//System.out.println(count); 
					}
					if(j==62) {
						break;
					}
					if(calcHash(current).equals(hash)) {
						WorkerClient.Done = 1;
						WorkerClient.Found = 1;
						System.out.println("done");
						if(sent == 0) {
							buf = ("DONE_FOUND " + current + " " + hash + " " + Integer.toString(port)).getBytes();
							packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
							sock.send(packet);
							sent++;
						}
						break;
					}
					if(current.equals(end)) {
						WorkerClient.Done = 1;
						break;
					}
					char c = getNext(current.charAt(1));
					current = current.substring(0,1) + c + 'a' + 'a' + 'a'; 
					WorkerClient.last = current;
					count++;
					//System.out.println(count);
				}
				if(i==62) {
					break;
				}
				if(calcHash(current).equals(hash)) {
					WorkerClient.Done = 1;
					WorkerClient.Found = 1;
					System.out.println("done");
					if(sent == 0) {
						buf = ("DONE_FOUND " + current + " " + hash + " " + Integer.toString(port)).getBytes();
						packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
						sock.send(packet);
						sent++;
					}
					break;
				}
				if(current.equals(end)) {
					WorkerClient.Done = 1;
					break;
				}
				char c = getNext(current.charAt(0));
				current = current.substring(0,0) + c + 'a' + 'a' + 'a' + 'a'; 
				WorkerClient.last = current;
				count++;
				//System.out.println(current);
			}
			System.out.println(current);
			if(sent == 0) {
				buf = ("DONE_NOT_FOUND " + port + " " + start + " " + current).getBytes();
				packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
				sock.send(packet);
				sent++;
			}
		} catch(Exception e) {

		}
		
	}

	public char getNext(char curr){
		String sample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		return sample.charAt((sample.indexOf(curr)+1)%62);
	}

	public String calcHash(String key) {
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