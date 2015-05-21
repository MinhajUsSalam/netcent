import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.security.*;
import java.math.*;



public class Ping extends Thread{
	DatagramSocket server;
	public static DatagramSocket socket;


	public Ping(int s,DatagramSocket srvr) {
		try {
			s++;
			socket = new DatagramSocket(s);
			server = srvr;  
			ServerPing sp = new ServerPing();
			sp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
		byte[] buf = new byte[256];
			while(true) {
				try {	
				socket.setSoTimeout(2000);
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String input = new String(packet.getData(), 0, packet.getLength());
				//System.out.println("i am Ping");
				ServerThread sT = new ServerThread(server, packet);
				System.out.println("port");
				sT.start();
				} catch (Exception e1) {
					if(Server.pingSent > 0) {
						String failedPort = Integer.toString(Server.pingSent);
						//System.out.println("lolz timedout");
						Server.pingSent = 0;
						Server.failed = 1;
						Server.noOfWorkers--;
						String hash = "";
						String last = "";
						String first = "";
						for(int j = 0; j < 50; j++) {
							if(Server.workers[j][0] != null) {
								if(Server.workers[j][0].equals(failedPort)) {
									hash = Server.workers[j][3];
									first = Server.workers[j][1];
									if(Server.workers[j][4] != null) {
										last = Server.workers[j][4];
									} else {
										last = Server.workers[j][2];
									}
									Server.workers[j][5] = "0";
									break;
								}
							}
						}
						//System.out.println(failedPort);
						//System.out.println(first);
						//System.out.println(last);
						for(int i = 0; i < 1000; i++) {
							if(Server.incompleteJobs[i][0] == null) {
								Server.incompleteJobs[i][0] = Integer.toString(Server.pingSent);
								Server.incompleteJobs[i][1] = first;
								Server.incompleteJobs[i][2] = last;
								Server.incompleteJobs[i][3] = hash;
								break;
							}
						}
						for(int i = 0; i < 100; i++) {
							if(Server.jobs[i][0] != null) {
								if(Server.jobs[i][0].equals(hash)) {
									Server.jobs[i][3] =  Integer.toString(Integer.parseInt(Server.jobs[i][3]) - 1); 
								}
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}