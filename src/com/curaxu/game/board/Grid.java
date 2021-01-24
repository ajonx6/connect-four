package com.curaxu.game.board;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.curaxu.game.Game;
import com.curaxu.game.input.MouseInput;

public class Grid {
	public static final int ROWS = 6;
	public static final int COLS = 7;
	public static final int CELL_SIZE = 64;

	public static final int START_X = 10;
	public static final int START_Y = 70;

	public BufferedImage cellImage;
	public BufferedImage red, yellow;
	public CellType[][] cells = new CellType[ROWS][COLS];
	public boolean isYourTurn = true;
	public Game game;

	public boolean dropping = false;
	public int dropColumn = -1;
	public int dropStartY = -1, dropEndY = -1;

	public boolean hasSecondPlayer = false;
	public boolean dropYourColour = true;
	public boolean won = false, lost = false;

	public Grid(Game game) {
		this.game = game;

		try {
			cellImage = ImageIO.read(Grid.class.getResourceAsStream("/cell.png"));
			red = ImageIO.read(Grid.class.getResourceAsStream("/red_counter.png"));
			yellow = ImageIO.read(Grid.class.getResourceAsStream("/yellow_counter.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int c = 0; c < COLS; c++) {
			for (int r = 0; r < ROWS; r++) {
				cells[r][c] = CellType.EMPTY;
			}
		}
	}

	public void renderNewCounter(Graphics g) {
		if (!isYourTurn) return;

		if (dropping) renderDroppedCounter(g);
		else {
			int x = MouseInput.x, y = MouseInput.y;
			if (x < 10 || x > getRightOfGrid() || y > getBottomOfGrid() || !hasSecondPlayer) return;
			else {
				int column = (int) Math.floor((double) (x - 10.0) / (double) CELL_SIZE);
				g.drawImage(game.playerRed ? red : yellow, START_X + column * CELL_SIZE + 5, 8, null);
				if (MouseInput.isPressed(MouseEvent.BUTTON1)) {
					if (cells[0][column] != CellType.EMPTY) return;
					game.client.sendData(("\\t\\" + column).getBytes());
					drop(column, true);
				}
			}
		}
	}

	public void drop(int column, boolean yourColour) {
		dropColumn = column;
		dropping = true;
		dropStartY = 8;
		dropEndY = START_Y + getNextEmptyCell(column) * CELL_SIZE;
		dropYourColour = yourColour;
	}

	public void renderDroppedCounter(Graphics g) {
		g.drawImage((dropYourColour ^ game.playerRed) ? yellow : red, START_X + dropColumn * CELL_SIZE + 5, dropStartY, null);
		if (dropStartY == dropEndY) {
			int row = getNextEmptyCell(dropColumn);
			cells[row][dropColumn] = (dropYourColour ^ game.playerRed) ? CellType.YELLOW : CellType.RED;
			dropping = false;
			boolean won = checkWin(row, dropColumn);
			if (won) {
				this.won = true;
				game.client.sendData(("\\w\\").getBytes());
			} else {
				dropColumn = dropStartY = dropEndY = -1;
				isYourTurn = !isYourTurn;
				game.client.sendData(("\\n\\").getBytes());
			}
		}
		dropStartY++;
	}

	public boolean checkWin(int r, int c) {
		int inALine = 0;
		for (int cc = 0; cc < COLS; cc++) {
			if (cells[r][cc] == (game.playerRed ? CellType.RED : CellType.YELLOW)) inALine++;
			else inALine = 0;
		}
		if (inALine >= 4) return true;

		inALine = 0;
		for (int rr = 0; rr < ROWS; rr++) {
			if (cells[rr][c] == (game.playerRed ? CellType.RED : CellType.YELLOW)) inALine++;
			else inALine = 0;
		}
		if (inALine >= 4) return true;

		inALine = 0;
		int startRow = r;
		int startCol = c;
		while (startRow > 0 && startCol > 0) {
			startRow--;
			startCol--;
		}
		while (startRow < ROWS && startCol < COLS) {
			if (cells[startRow][startCol] == (game.playerRed ? CellType.RED : CellType.YELLOW)) inALine++;
			startRow++;
			startCol++;
		}
		if (inALine >= 4) return true;

		inALine = 0;
		startRow = r;
		startCol = c;
		while (startRow > 0 && startCol < COLS - 1) {
			startRow--;
			startCol++;
		}
		while (startRow < ROWS && startCol >= 0) {
			if (cells[startRow][startCol] == (game.playerRed ? CellType.RED : CellType.YELLOW)) inALine++;
			startRow++;
			startCol--;
		}
		if (inALine >= 4) return true;

		return false;
	}

	public int getNextEmptyCell(int column) {
		for (int r = 0; r < ROWS; r++) {
			if (cells[r][column] != CellType.EMPTY) return r - 1;
		}
		return ROWS - 1;
	}

	public void renderGrid(Graphics g) {
		for (int c = 0; c < COLS; c++) {
			for (int r = 0; r < ROWS; r++) {
				g.drawImage(cellImage, START_X + c * CELL_SIZE, START_Y + r * CELL_SIZE, null);
				if (cells[r][c] == CellType.RED) g.drawImage(red, START_X + c * CELL_SIZE + 5, START_Y + r * CELL_SIZE + 5, null);
				else if (cells[r][c] == CellType.YELLOW) g.drawImage(yellow, START_X + c * CELL_SIZE + 5, START_Y + r * CELL_SIZE + 5, null);
			}
		}

		if (!hasSecondPlayer) {
			g.setColor(new Color(0, 0, 0, 230));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 48));
			g.drawString("Waiting For Player", 20, Game.HEIGHT / 2);
		} else if (!isYourTurn) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			g.drawString("Opponents Turn", 80, 45);
		}

		if (lost) {
			g.setColor(new Color(0, 0, 0, 230));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 48));
			g.drawString("You lost...", 120, Game.HEIGHT / 2);
		} else if (won) {
			g.setColor(new Color(0, 0, 0, 230));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 48));
			g.drawString("You won!!!", 120, Game.HEIGHT / 2);
		}
	}

	public int getWidthOfGrid() {
		return COLS * CELL_SIZE;
	}

	public int getRightOfGrid() {
		return START_X + COLS * CELL_SIZE;
	}

	public int getBottomOfGrid() {
		return START_Y + ROWS * CELL_SIZE;
	}
}