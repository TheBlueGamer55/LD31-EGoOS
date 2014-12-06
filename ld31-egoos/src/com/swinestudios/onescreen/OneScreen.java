package com.swinestudios.onescreen;

import org.mini2Dx.core.game.ScreenBasedGame;

public class OneScreen extends ScreenBasedGame {

	@Override
	public int getInitialScreenId() {
		return MainMenu.ID;
	}

	@Override
	public void initialise() {
		addScreen(new MainMenu());
	}
	
}
