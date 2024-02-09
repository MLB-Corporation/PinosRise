package com.mbl.pinoscastle;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mbl.pinoscastle.GameClass;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setIdleFPS(600);
		config.useVsync(true);
		config.setWindowedMode(900, 600);
		config.setTitle("GiocoGiava");
		new Lwjgl3Application(new GameClass(900, 600), config);
	}
}
