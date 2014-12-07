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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class MainMenu implements GameScreen, InputProcessor{

	public static int ID = 1;
	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

	public ArrayList<Block> solids;

	public Player player;

	public Rectangle mouse;
	public Block currentSelection;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){

	}

	@Override
	public void postTransitionIn(Transition t){
		solids = new ArrayList<Block>();
		currentSelection = null;
		//Test code
		solids.add(new Block(0, 400, 520, 48, this, false));
		solids.add(new Block(0, 240, 32, 240, this, false));
		solids.add(new Block(32, 480 - 100, 63, 63, this, false));
		Block block = new Block(220, 270, 32, 32, this, true);
		solids.add(block);
		solids.add(new Block(170, 270, 32, 32, this, true));
		player = new Player(320, 240, 16, 16, this);
		mouse = new Rectangle(Gdx.input.getX(), Gdx.input.getY(), 1, 1);
		//solids.add(mouse);
		
		//Input handling
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(this);
		multiplexer.addProcessor(player);
		Gdx.input.setInputProcessor(multiplexer);
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
		player.render(g);
		g.drawRect(mouse.x, mouse.y, 4, 4); //Test code
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){	
		mouse.setX(Gdx.input.getX());
		mouse.setY(Gdx.input.getY());

		if(Gdx.input.isKeyPressed(Keys.ENTER)){
			player.isActive = true;
		}
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		updateSolids(delta);
		if(player.isActive){
			player.update(delta);
		}
	}

	public void renderSolids(Graphics g){
		for(int i = 0; i < solids.size(); i++){
			solids.get(i).render(g);
		}
	}
	
	public void updateSolids(float delta){
		for(int i = 0; i < solids.size(); i++){
			solids.get(i).update(delta);
		}
	}

	@Override
	public void interpolate(GameContainer gc, float delta){
	}
	
	/*
	 * ----------------------Input functions-----------------------
	 */
	
	@Override
	public boolean keyDown(int keycode){
		return false;
	}

	@Override
	public boolean keyUp(int keycode){
		return false;
	}

	@Override
	public boolean keyTyped(char character){
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		//Select block
		if(currentSelection == null){ //if there is no block selected currently
			for(int i = 0; i < solids.size(); i++){ //find the block that was clicked on
				Block temp = solids.get(i);
				if(!temp.isSelectionBlock){ //if it's not a selection block, skip it
					continue;
				}
				if(mouse.overlaps(temp)){
					if(!temp.isActive){ //only runs once for each inactive block
						temp.isActive = true;
					}
					temp.isSelected = true;
					currentSelection = temp;
					break;
				}
			}
		}
		else{ //there is a block currently selected
			currentSelection.isSelected = false;
			currentSelection = null;
		}
		return true; //touchDown() has been handled by this class
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
