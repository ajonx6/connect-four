package com.curaxu.game.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.curaxu.game.Game;

public class MouseInput extends MouseAdapter {
	public static final int NUM_BUTTONS = 10;
	public static final boolean[] BUTTONS = new boolean[NUM_BUTTONS];
	public static final boolean[] LAST_BUTTONS = new boolean[NUM_BUTTONS];

	public static int x = -1, y = -1;
	public static int lastX = x, lastY = y;
	public static boolean moving = false;
	public static int ticksNotMoving = 0;

	public void mousePressed(MouseEvent e) {
		BUTTONS[e.getButton()] = true;
	}

	public void mouseReleased(MouseEvent e) {
		BUTTONS[e.getButton()] = false;
	}

	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		moving = true;
	}

	public static void tick() {
		for (int i = 0; i < NUM_BUTTONS; i++) {
			LAST_BUTTONS[i] = BUTTONS[i];
		}
		if (x == lastX && y == lastY) moving = false;
		lastX = x;
		lastY = y;
		if (moving) ticksNotMoving = 0;
		else ticksNotMoving++;
	}

	public static boolean isPressed(int button) {
		return BUTTONS[button];
	}

	public static boolean wasPressed(int button) {
		return isPressed(button) && !LAST_BUTTONS[button];
	}

	public static boolean wasReleased(int button) {
		return !isPressed(button) && LAST_BUTTONS[button];
	}

	public static boolean notMovedFor(int ticks) {
		return ticksNotMoving >= ticks;
	}

	public static boolean notMovedFor(double seconds) {
		return ticksNotMoving >= (seconds * Game.FPS);
	}

	public static boolean isMoving() {
		return moving;
	}
}