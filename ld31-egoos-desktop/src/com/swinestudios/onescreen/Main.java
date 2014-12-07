package com.swinestudios.onescreen;

import org.mini2Dx.core.game.Mini2DxGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main{
	public static void main(String[] args){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "TO BE NAMED LATER";
		cfg.useGL20 = true;
		cfg.width = 640;
		cfg.height = 480;
		cfg.vSyncEnabled = true;
		new LwjglApplication(new Mini2DxGame(new OneScreen()), cfg);
	}
}
