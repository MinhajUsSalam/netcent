import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;


public class ServerThread extends Thread{
	
		DatagramPacket packet;
		DatagramSocket server;
		InetAddress ip; 

	
		public ServerThread(DatagramSocket s, DatagramPacket p){
			try{
				server = s;
				packet = p;
				ip = InetAddress.getByName("localhost");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		public int isFree(String workerPort) {
			try {
				int recPort = Integer.parseInt(workerPort);
				byte [] buf = new byte[256];
				String message ="PING";
				buf = message.getBytes();
				InetAddress address = InetAddress.getByName("localhost");
				DatagramPacket packet = new DatagramPacket(buf, buf.length, address, recPort);
				server.send(packet);
				buf = new byte[256];
				packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
				server.receive(packet);
				String input = new String(packet.getData(), 0, packet.getLength());
				if (input.equals("DONE_FOUND") || input.equals("DONE_NOT_FOUND")) {
					for(int i = 0; i < 50; i++) {
						if(Server.freeWorkers[i] == null) {
							Server.freeWorkers[i] = workerPort;
							break;
						} else {
							if(Server.freeWorkers[i].equals(workerPort)){
								break;
							}
						}
					}
					return 1;
				} else {
					return 0;
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		public void sendJob(String workerPort, int id, String hash) {
			try {	
					System.out.println("here i am O youmighty one");
					byte [] buf = new byte[256];
					String next = "";
					String begin = "";
					if(Server.failed == 0) {
						next = getNext(id);
						begin = getBegin(id);
						for(int a = 0; a < 100; a ++)  {
							if(Server.jobs[a][0] != null) {
								if(Server.jobs[a][0].equals(hash)) {
									Server.jobs[a][2] = Integer.toString(Integer.parseInt(Server.jobs[a][2]) + 1);	
									Server.jobs[a][3] = Integer.toString(Integer.parseInt(Server.jobs[a][3]) + 1);		
									break;
								}
							}
						}
					} else {
						for(int i = 0; i < 50; i++) {
							if(Server.workers[i][0] != null) {
								if(Server.workers[i][0].equals(Integer.toString(Server.failed))) {
									begin = Server.workers[i][4];
									next = Server.workers[i][4].charAt(0) + Server.workers[i][4].charAt(1) + "999"; 
									hash = Server.workers[i][3];
									break;
								}
							}
						}
					}
					String message ="JOB" + " " + begin + " " + next + "999" + " " + hash;
					buf = message.getBytes();
					InetAddress address = InetAddress.getByName("localhost");
					DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(workerPort));
					server.send(packet);
					for(int i = 0; i < 50; i ++)  {
						if(Server.workers[i][0] != null) {
							if(Server.workers[i][0].equals(workerPort)) {
								Server.workers[i][1] = begin;
								Server.workers[i][2] = (next+"999");
								Server.workers[i][3] = hash;
								Server.workers[i][4] = null;
							}
						}
					}
					
			} catch (Exception e) {

			}
		}


		public void run() {
			try {
				System.out.println("workerPort");
				String input = new String(packet.getData(), 0, packet.getLength());
				System.out.println(input);
				if(input.substring(0,4).equals("HASH")) {
					System.out.println("hashing");
					String hash = input.substring(5,37);
					String clientId = input.substring(38,42);
					int count = 0;
					Server.noOfJobs++;

					for(int k = 0; k < 100; k++) {
						if(Server.jobs[k][0] == null) {
							Server.jobs[k][0] = hash;
							Server.jobs[k][1] = clientId;
							Server.jobs[k][2] = "0";
							Server.jobs[k][3] = "0"; 
							break;
						}
					}

					for(int i = 0; i < 100; i ++) {
						if(Server.clients[i] == 0) {
							Server.clients[i] = Integer.parseInt(clientId);
							break;
						}
					}

					for(int i = 0; i < 50; i ++)  {
						if(Server.freeWorkers[i] != null) {
							if((i+1) <= (Server.noOfWorkers/Server.noOfJobs))
								sendJob(Server.freeWorkers[i],i,hash);
							else {
								break;
							}
						}
					}


				} else if (input.substring(0,7).equals("ACK_JOB")) {
					System.out.println("acked");
					String rangeBegin = input.substring(8,13);
					String rangeEnd = input.substring(14,19);
					String target = input.substring(20,52);
					String workerPort = input.substring(53,57);
					for(int i = 0; i < 50; i ++)  {
						if(Server.workers[i][0] != null) {
							if(Server.workers[i][0].equals(workerPort)) {
								if(Server.workers[i][1].equals(rangeBegin) && Server.workers[i][2].equals(rangeEnd) && Server.workers[i][3].equals(target)) {
									for(int a = 0; a < 50; a ++)  {
										if(Server.freeWorkers[a] != null) {
											if(Server.freeWorkers[a].equals(workerPort)) {
												Server.freeWorkers[a] = null;
												break;
											}
										}
									}
									for(int a = 0; a < 100; a ++)  {
										if(Server.jobs[a][0] != null) {
											if(Server.jobs[a][0].equals(target)) {
												
												System.out.println(Server.jobs[a][2]);
												break;
											}
										}
									}
									break;									
								}
							}
						}
					}	

				} else if (input.substring(0,8).equals("NOT_DONE")) {
					String last = input.substring(15,19);
					String hash = input.substring(16,48);
					String workerPort = input.substring(49,53);
					for(int i = 0; i < 50; i++) {
						if(Server.workers[i][0] != null) {
							if(Server.workers[i][0].equals(workerPort)) {
								Server.workers[i][4] = last;
							}
						}
					}

				} else if (input.substring(0,10).equals("DONE_FOUND")) {
					String password = input.substring(11,16);
					String hash = input.substring(17,49);
					String workerPort = input.substring(50,54);
					Server.noOfJobs--;
					String client = "";
					for(int i = 0; i < 100; i++) {
						if(Server.jobs[i][0] != null) {
							if(Server.jobs[i][0].equals(hash)) {
								client = Server.jobs[i][1];
								Server.jobs[i][0] = null;
								Server.jobs[i][1] = null;
								Server.jobs[i][2] = null;
								Server.jobs[i][3] = null;
							}
						}
					}
					byte [] buf1 = new byte[256];
					String mge ="Found " + password;
					buf1 = mge.getBytes();
					InetAddress address1 = InetAddress.getByName("localhost");
					DatagramPacket packet1 = new DatagramPacket(buf1, buf1.length, address1, Integer.parseInt(client));
					server.send(packet1);
					for(int i = 0; i < 50; i++) {
						if(Server.workers[i][0] != null) {
							if(Server.workers[i][3].equals(hash)) {
								byte [] buf = new byte[256];
								String message ="CANCEL_JOB";
								buf = message.getBytes();
								InetAddress address = InetAddress.getByName("localhost");
								DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(Server.workers[i][0]));
								server.send(packet);
								Server.workers[i][1] = null;
								Server.workers[i][2] = null;
								Server.workers[i][3] = null;
								Server.workers[i][4] = null;
								findJob(Server.workers[i][0]);
							}
						}
					}
					for(int i = 0; i < 50; i++) {
						if(Server.freeWorkers[i] == null) {
							Server.freeWorkers[i] = workerPort;
							//findJob(workerPort);
							break;
						}
					}

					findJob(workerPort);

						
				} else if (input.substring(0,14).equals("DONE_NOT_FOUND")) {
					System.out.println("Nf");
					String workerPort = input.substring(15,19);
					int found = 0;
					String hash = "";
					for(int i = 0; i < 50; i++) {
						if(Server.freeWorkers[i] != null) {
							if(Server.freeWorkers[i].equals(workerPort)) {
								found = 1;
								break;
							}
						}
					}

					for(int i = 0; i < 50; i++) {
						if(Server.workers[i][0] != null) {
							if(Server.workers[i][0].equals(workerPort)) {
								hash = Server.workers[i][3]; 
							}
						}
					}

					for(int i = 0; i < 100; i++) {
						if(Server.jobs[i][0] != null) {
							if(Server.jobs[i][0].equals(hash)) {
								Server.jobs[i][3] = Server.jobs[i][3] = Integer.toString(Integer.parseInt(Server.jobs[i][3]) - 1); 
							}
						}
					}	

					if(found == 0) {
						for(int i = 0; i < 50; i++) {
							if(Server.freeWorkers[i] == null) {
								Server.freeWorkers[i] = workerPort;
								break;
							}
						}
						
					}
					findJob(workerPort);

				} else if (input.substring(0,15).equals("REQUEST_TO_JOIN")) {
					int workerPort = Integer.parseInt(input.substring(16,20));
					for(int i = 0; i < 50; i ++) {
						System.out.println(i);
						if(Server.workers[i][0] == null) {
							System.out.println(workerPort);
							Server.workers[i][0] = Integer.toString(workerPort);
							break;
						}
					}
					for(int i = 0; i < 50; i++) {
						if(Server.freeWorkers[i] == null) {
							Server.freeWorkers[i] = Integer.toString(workerPort);
							break;
						}
					}
					Server.noOfWorkers++;
					System.out.println("joined");
					findJob(Integer.toString(workerPort));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}

		public String getNext(int curr){
			String sample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
			String rS = ""; 
			rS = rS + sample.charAt(curr/62)+sample.charAt(curr%62);
			return rS;
		}

		public String getBegin(int curr) {
			if(curr == 0) {
				return "aaaaa";
			} else {
				String sample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
				String rS = ""; 
				rS = rS + sample.charAt(curr/62)+sample.charAt(curr%62) + "aaa";
				return rS;
			}
		}

		public void findJob(String workerPort) {
			//for(int j = 0; j < 50 ;j++) {
			//	if(Server.freeWorkers[j] != null) {
			//		String workerPort = Server.freeWorkers[j];
					for(int i = 0; i < 100; i++) {
						if(Server.jobs[i][0] != null) {
							if(Integer.parseInt(Server.jobs[i][3]) < Server.noOfWorkers/Server.noOfJobs) {
							//	System.out.println("parsing");
							//	System.out.println(Integer.parseInt(Server.jobs[i][3]));
							//	System.out.println(Server.noOfWorkers/Server.noOfJobs);
								sendJob(workerPort, Integer.parseInt(Server.jobs[i][2]),Server.jobs[i][0]);
								break;
							}
						}
					}
			//	}
			//}
		}
		
}