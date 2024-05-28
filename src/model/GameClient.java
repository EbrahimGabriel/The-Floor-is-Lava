package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.function.Consumer;

public class GameClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private String name;

    private byte[] buf = new byte[8192];

    public GameClient(String ip, int port, Consumer<String> onChatReceived, String name) {
    	this.name = name;
        try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

        try {
			this.serverAddress = InetAddress.getByName(ip);
			this.serverPort = port;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

        new Thread(() -> {
        	while (true) {
        		onChatReceived.accept(receiveData());
        	}
        }).start();

        this.sendChat("connect");
    }



    //CLIENT SENDING CHAT MESSAGE
    public void sendChat(String msg) {
    	String newMsg = name + ": " + msg;
        buf = newMsg.getBytes();
        DatagramPacket packet
          = new DatagramPacket(buf, buf.length, serverAddress, serverPort);
        try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    //CLIENT SENDING PLAYER DATA (character, nickname)
    public void sendPlayerData(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet
          = new DatagramPacket(buf, buf.length, serverAddress, serverPort); //PORT

        try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    //CLIENT SENDING GAME DATA (lives, position)
    public void sendGameData(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet
          = new DatagramPacket(buf, buf.length, serverAddress, serverPort); //PORT

        try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public String receiveData() {
    	buf = new byte[8192];
    	DatagramPacket packet = new DatagramPacket(buf, buf.length);

    	try {
    		socket.receive(packet);
		} catch (IOException e) {
    		e.printStackTrace();
		}

    	String received = new String(
    			packet.getData(), 0, packet.getLength());
    	return received;
    }

    public void close() {
        socket.close();
    }
}