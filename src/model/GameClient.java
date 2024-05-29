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
    private int playerNum;

    private byte[] buf = new byte[8192];

    public boolean ready = false;

    public GameClient(String ip, int port, Consumer<GameData> onMessageReceived, String name) {
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

        this.sendChat("connect");
        this.receivePlayerNum();
        // wait for server to have 4 players
        this.waitServer();

        new Thread(() -> {
        	while (true) {
        		onMessageReceived.accept(receiveData());
        	}
        }).start();
    }

    //CLIENT SENDING CHAT MESSAGE
    public void sendChat(String msg) {
    	String newMsg = "chat|" + name + ": " + msg;
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
    public void sendPlayerData(CHARACTER character, String name) {
    	String msg = "player|" + playerNum + " " + name + " " + character.getColor();
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
    public void sendGameData(int lives, int xpos, int ypos) {
    	String msg = "game|" + playerNum + " " + lives + " " + xpos + " " + ypos;
        buf = msg.getBytes();
        DatagramPacket packet
          = new DatagramPacket(buf, buf.length, serverAddress, serverPort); //PORT

        try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public GameData receiveData() {
    	buf = new byte[8192];
    	DatagramPacket packet = new DatagramPacket(buf, buf.length);

    	try {
    		socket.receive(packet);
		} catch (IOException e) {
    		e.printStackTrace();
		}

    	String received = new String(
    			packet.getData(), 0, packet.getLength());
    	// temp[0] = type; temp[1] is the info.
    	// depending on the type, the info carries differing values.
    	// chat: [0] is msg (appended with nickname of sender)
    	// player: [0] is playerNum, [1] is nickname, [2] is character chosen
    	// game: [0] is playerNum, [1] is lives, [2] is xpos, [3] is ypos
    	String[] temp = received.split("[|]");
    	String[] info = temp[1].split(" ");
    	GameData data = new GameData();
    	data.type = temp[0];
    	if (temp[0].equals("chat")) {
    		data.msg = temp[1]; // no splitting!
    	} else if (temp[0].equals("player")) {
    		data.playerNum = Integer.parseInt(info[0]);
    		data.name = info[1];
    		if (info[2].equals("blue")) data.character = CHARACTER.BLUE;
    		if (info[2].equals("red")) data.character = CHARACTER.RED;
    		if (info[2].equals("yellow")) data.character = CHARACTER.YELLOW;
    		if (info[2].equals("green")) data.character = CHARACTER.GREEN;
    	} else if (temp[0].equals("game")) {
    		data.playerNum = Integer.parseInt(info[0]);
    		data.lives = Integer.parseInt(info[1]);
    		data.xpos = Integer.parseInt(info[2]);
    		data.ypos = Integer.parseInt(info[3]);
    	}

    	return data;
    }

    public void receivePlayerNum() {
    	buf = new byte[8192];
    	DatagramPacket packet = new DatagramPacket(buf, buf.length);

    	try {
    		socket.receive(packet);
		} catch (IOException e) {
    		e.printStackTrace();
		}

    	String received = new String(
			packet.getData(), 0, packet.getLength());

    	this.playerNum = Integer.parseInt(received);
    }

    //literally does nothing until server has 4 clients
    public void waitServer() {
    	buf = new byte[8192];
    	DatagramPacket packet = new DatagramPacket(buf, buf.length);

    	try {
        	socket.receive(packet);
        } catch (IOException e) {
        	e.printStackTrace();
        }

    	ready = true;
    }

    public void close() {
        socket.close();
    }
}