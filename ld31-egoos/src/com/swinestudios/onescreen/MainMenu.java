package com.swinestudios.onescreen;

import java.util.ArrayList;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class MainMenu implements GameScreen{
	
	public static int ID = 1;
	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

	public ArrayList<Rectangle> solids;
	
	public Player player;
	
	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		solids = new ArrayList<Rectangle>();
	}

	@Override
	public void postTransitionIn(Transition t){
		//Test code
		solids.add(new Rectangle(0, 400, 520, 48));
		solids.add(new Rectangle(0, 240, 32, 240));
		solids.add(new Rectangle(32, 480 - 100, 63, 63));
		player = new Player(320f, 240, 16, 16, this);
	}

	@Override
	public void postTransitionOut(Transition t){
		
	}

	@Override
	public void preTransitionIn(Transition t){
		
	}

	@Override
	public void preTransitionOut(Transition t){
		
	}

	@Override
	public void render(GameContainer gc, Graphics g){
		renderSolids(g);
		player.render(gc, g);
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){	
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			//start the game
		}
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
		
		player.update(gc, sm, delta);
	}
	
	//Test method
	public void renderSolids(Graphics g){
		for(int i = 0; i < solids.size(); i++){
			g.drawShape(solids.get(i));
		}
	}
	
	@Override
	public void interpolate(GameContainer gc, float delta){
	}

}
