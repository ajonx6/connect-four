package com.curaxu.game.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {
	public static int colourLerp(int c1, int c2, float value) {
		return (0xff << 24) | ((int) ((float) ((c1 >> 16) & 0xff) * value) + (int) ((float) ((c2 >> 16) & 0xff) * (1f - value))) << 16 | ((int) ((float) ((c1 >> 8) & 0xff) * value) + (int) ((float) ((c2 >> 8) & 0xff) * (1f - value))) << 8 | ((int) ((float) (c1 & 0xff) * value) + (int) ((float) (c2 & 0xff) * (1f - value)));
	}

	public static List<String> load(String name) {
		List<String> data = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("res/" + name)))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				data.add(line);
			}
			return data;
		} catch (IOException e) {
			System.err.println("Could not load: res/" + name);
			System.exit(1);
		}
		return null;
	}
}