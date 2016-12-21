package com.tjstudios.mynesweeper.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tjstudios.mynesweeper.Mynesweeper;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Mynesweeper";
		config.width = 1920;
		config.height = 1080;
		config.vSyncEnabled = false; // Setting to false disables vertical sync
		config.foregroundFPS = 120; // Setting to 0 disables foreground fps throttling
		config.backgroundFPS = 120; // Setting to 0 disables background fps throttling
		new LwjglApplication(new Mynesweeper(), config);
	}
}
