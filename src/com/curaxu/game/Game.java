package com.curaxu.game;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.curaxu.game.board.Grid;
import com.curaxu.game.input.KeyInput;
import com.curaxu.game.input.MouseInput;
import com.curaxu.game.server.Client;
import com.curaxu.game.server.Server;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 25 + Grid.CELL_SIZE * Grid.COLS;
	public static final int HEIGHT = 495;
	public static final String TITLE = "Connect 4";
	public static final double FPS = 300.0;

	public JFrame frame;
	public boolean running;

	public Grid grid;
	public boolean playerRed = false;
	public String username;
	public Client client;
	public Server server;
	public boolean host = false;

	public Game(String name, String ip, int port) {
		System.out.println("Playing as " + name);

		grid = new Grid(this);

		frame = new JFrame(Game.TITLE);
		frame.add(this);
		frame.pack();
		frame.setSize(Game.WIDTH, Game.HEIGHT);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		username = name;
		client = new Client(this, ip.equals("") ? "localhost" : ip, port);
		client.start();

		if (ip.equals("")) {
			server = new Server(port);
			server.start();
			playerRed = true;
			client.sendData(("\\c\\" + username).getBytes());
			host = true;
			frame.setTitle(name + " - Awaiting a player...");
		} else {
			client.sendData(("\\c\\" + username).getBytes());
			grid.isYourTurn = false;
			grid.hasSecondPlayer = true;
		}

		KeyInput ki = new KeyInput();
		MouseInput mi = new MouseInput();
		addKeyListener(ki);
		addMouseListener(mi);
		addMouseMotionListener(mi);

		start();
	}

	public void start() {
		if (running) return;
		new Thread(this, "Game").start();
	}

	public void stop() {
		if (!running) return;
		running = false;
	}

	public void run() {
		running = true;
		requestFocus();

		long frameCounter = 0;
		double frameTime = 1.0 / FPS;
		long lastTime = Time.getTime();
		double unprocessedTime = 0;

		while (running) {
			boolean render = false;

			long startTime = Time.getTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime / (double) Time.SECOND;
			frameCounter += passedTime;

			while (unprocessedTime > frameTime) {
				render = true;
				unprocessedTime -= frameTime;
				Time.delta = frameTime;
				tick();
				if (frameCounter >= Time.SECOND) {
					frameCounter = 0;
				}
			}
			if (render) {
				render();
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}

	public void tick() {
		KeyInput.tick();
		MouseInput.tick();
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);

		if (!grid.dropping && !grid.won && !grid.lost) grid.renderNewCounter(g);
		else if (!grid.won && !grid.lost) grid.renderDroppedCounter(g);
		grid.renderGrid(g);

		g.dispose();
		bs.show();
	}
}