package com.curaxu.game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.curaxu.game.Game;

public class Client extends Thread {
	public DatagramSocket socket;
	public InetAddress ip;
	public int port;

	public Game game;

	public Client(Game game, String ipAddress, int port) {
		this.game = game;
		this.port = port;

		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName(ipAddress);
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public int getPort() {
		return socket.getPort();
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

	public void parsePacket(byte[] data, InetAddress address, int port) {
		String msg = new String(data).trim();
		if (msg.startsWith("\\c\\")) {
			game.grid.hasSecondPlayer = true;
			game.frame.setTitle(game.username + " vs " + msg.substring(3));
		} else if (msg.startsWith("\\t\\")) {
			game.grid.drop(Integer.parseInt(msg.substring(3)), false);
		} else if (msg.startsWith("\\w\\")) {
			game.grid.lost = true;
		}
	}

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}