package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[8192];
    private static List<Client> clients = new ArrayList<>();

    public GameServer(int port, String ip) {
        try {
        	InetAddress address = InetAddress.getByName(ip);
			socket = new DatagramSocket(port, address);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

    }

    public void run() {
    	DatagramPacket packet = new DatagramPacket(buf, buf.length);

        // Wait for 4 clients to connect (this includes the server!)
    	int count = 0;
        while (clients.size() < 2) {
            try {
				socket.receive(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

            boolean exists = false;
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            for (Client client : clients) {
            	if (client.getClientAddress() == address && client.getClientPort() == port) exists = true;
            }

            if (!exists) {
            	Client newClient = new Client(address, port);
            	clients.add(newClient);
            	// send the client their player number
            	buf = String.valueOf(count).getBytes();
            	packet = new DatagramPacket(buf, buf.length, address, port);
            	try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
            	count++;
            }
        }
        // make all clients stop waiting
        broadcastData("ready!");

    	running = true;
        while (running) {
        	receiveData();
        }
        socket.close();
    }

    public void broadcastData(String msg) {
    	buf = msg.getBytes();

    	for (Client client : clients) {
        	DatagramPacket packet = new DatagramPacket(buf, buf.length, client.getClientAddress(), client.getClientPort());
        	try {
        		socket.send(packet);
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
    	}
    }

    public void receiveData() {
    	buf = new byte[8192];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

      String received = new String(
        packet.getData(), 0, packet.getLength());

      broadcastData(received);
    }

    public void close() {
    	running = false;
    	socket.close();
    }
}

class Client {
	private InetAddress clientAddress;
	private int clientPort;

	public Client (InetAddress clientAddress, int clientPort) {
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
	}

	public InetAddress getClientAddress() {
		return this.clientAddress;
	}

	public int getClientPort() {
		return this.clientPort;
	}
}
