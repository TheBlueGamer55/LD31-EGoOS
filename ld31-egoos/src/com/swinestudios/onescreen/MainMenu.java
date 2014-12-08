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
import com.badlogic.gdx.graphics.Color;

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
		//TODO Test code
		solids.add(new Block(0, 400, 520, 48, this, false));
		solids.add(new Block(0, 240, 32, 240, this, false));
		solids.add(new Block(32, 480 - 100, 63, 63, this, false));
		
		//TODO Test for recursive floodFill
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < i; j++){
				solids.add(new Block(i * 32, j * 32, 32, 32, this, true, Color.GREEN));
			}
		}
		
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

	/*
	 * A modified flood fill algorithm where instead of coloring the blocks, they become active.
	 * This should occur whenever the player removes a wrong block, therefore "breaking" the circuit.
	 */
	public void floodFill(Block start, Color target){
		if(start == null){
			return;
		}
		if(start.isActive == true){
			return;
		}
		else{
			float x = start.getX() + 1;
			float y = start.getY() + 1;
			float offset = start.width;
			if(start.color == target){
				start.isActive = true;
			}
			floodFill(blockExistsAt(x, y + offset), start.color); //south
			floodFill(blockExistsAt(x, y - offset), start.color); //north
			floodFill(blockExistsAt(x + offset, y), start.color); //east
			floodFill(blockExistsAt(x - offset, y), start.color); //west
		}
	}

	/*
	 * Helper method for checking whether there is a Block at a given position
	 */
	public Block blockExistsAt(float x, float y){
		Rectangle scanner = new Rectangle(x, y, 1, 1); //the rectangle used to check for collision
		for(int i = 0; i < solids.size(); i++){
			Block solid = solids.get(i);
			if(scanner.overlaps(solid) && solid.isSelectionBlock){
				return solid;
			}
		}
		return null;
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
				if(mouse.overlaps(temp) && temp.isSelectionBlock){
					floodFill(temp, temp.color); //TODO test line of code
					if(!temp.isActive){ //only runs once for each inactive block
						temp.isActive = true;
					}
					currentSelection = temp;
					currentSelection.isSelected = true;
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
