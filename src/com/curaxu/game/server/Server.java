package com.curaxu.game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javafx.util.Pair;

public class Server extends Thread {
	public Pair<InetAddress, Integer> hostPlayer;
	public Pair<InetAddress, Integer> otherPlayer;
	public String hostName, otherPlayerName;

	public DatagramSocket socket;
	public InetAddress hostClientAddress;
	public int hostClientPort;

	public Server(int port) {
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String msg = new String(data).trim();
		if (msg.startsWith("\\c\\")) {
			if (hostPlayer == null) {
				hostPlayer = new Pair<>(address, port);
				hostName = msg.substring(3);
			} else {
				otherPlayer = new Pair<>(address, port);
				otherPlayerName = msg.substring(3);

				sendData(("\\c\\" + otherPlayerName).getBytes(), hostPlayer.getKey(), hostPlayer.getValue());
				sendData(("\\c\\" + hostName).getBytes(), otherPlayer.getKey(), otherPlayer.getValue());
			}
		} else if (msg.startsWith("\\t\\")) {
			if (port == hostPlayer.getValue()) {
				sendData(data, otherPlayer.getKey(), otherPlayer.getValue());
			} else {
				sendData(data, hostPlayer.getKey(), hostPlayer.getValue());
			}
		} else if (msg.startsWith("\\w\\")) {
			if (port == hostPlayer.getValue()) {
				sendData(data, otherPlayer.getKey(), otherPlayer.getValue());
			} else {
				sendData(data, hostPlayer.getKey(), hostPlayer.getValue());
			}
		}
	}

	private void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}