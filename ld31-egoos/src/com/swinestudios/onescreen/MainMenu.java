package com.swinestudios.onescreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.NullTransition;
import org.mini2Dx.tiled.TiledMap;
import org.mini2Dx.tiled.TiledObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MainMenu implements GameScreen, InputProcessor{

	public static int ID = 1;
	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

	public ArrayList<Block> solids;

	public Player player;

	public static int blocksCorrupted = 0;
	public static int maxBlocksCorrupted = 100;

	public float camX, camY;

	public Rectangle mouse;
	public Block currentSelection;

	public boolean isGameOverBad;
	public boolean isGameOverGood;

	private TiledMap map;
	private Sprite mainMenuBackground;
	private Sprite gameOverBackground;
	private Sprite gameOverBackgroundBad;

	private Sound gameplayTheme;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		try{
			map = new TiledMap(Gdx.files.internal("monitorCircuitMap2.tmx"));
		}catch (IOException e){
			e.printStackTrace();
		}

		gameplayTheme = Gdx.audio.newSound(Gdx.files.internal("bient.ogg"));

		mainMenuBackground = new Sprite(new Texture(Gdx.files.internal("mainMenuScreen.png")));
		gameOverBackground = new Sprite(new Texture(Gdx.files.internal("blueScreenOfDeath.png")));
		gameOverBackgroundBad = new Sprite(new Texture(Gdx.files.internal("blueScreenOfDeathBad.png")));
		adjustSprite(mainMenuBackground, gameOverBackground, gameOverBackgroundBad);
		mainMenuBackground.scale(3);
		gameOverBackground.scale(1);
		gameOverBackgroundBad.scale(1);
	}

	@Override
	public void postTransitionIn(Transition t){
		isGameOverGood = false;
		isGameOverBad = false;
		blocksCorrupted = 0; 
		solids = new ArrayList<Block>();
		currentSelection = null;

		player = new Player(5 * 16, 55 * 16, this);
		mouse = new Rectangle(Gdx.input.getX(), Gdx.input.getY(), 1, 1);

		camX = player.x - Gdx.graphics.getWidth() / 2;
		camY = player.y - Gdx.graphics.getHeight() / 2;
		if(map != null){
			generateSolids(map);
			generateBlueBlocks(map);
			generateRedBlocks(map);
			generateGreenBlocks(map);
			generateWhiteBlocks(map);
			generateYellowBlocks(map);
		}

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
		if(!player.isActive && !isGameOverGood && !isGameOverBad){
			g.drawSprite(mainMenuBackground);
			System.out.println("drawing main menu");
		}
		else{
			if(isGameOverGood){
				g.drawSprite(gameOverBackground);
			}
			else if(isGameOverBad){
				g.drawSprite(gameOverBackgroundBad);
			}
			else{
				g.translate((float) Math.round(camX), (float) Math.round(camY)); //camera movement
				map.draw(g, 0, 0);
				renderSolids(g);
				player.render(g);
				g.drawString("" + blocksCorrupted, camX, camY);
			}
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){	
		if(blocksCorrupted >= maxBlocksCorrupted & !isGameOverBad){
			System.out.println("Game over");
			isGameOverBad = true;
			player.isActive = false;
		}
		if(player.x >= 76 * 16 && player.y <= 30 && !isGameOverGood){ //extremely hacky code
			System.out.println("Win");
			isGameOverGood = true;
			player.isActive = false;
		}
		if(!isGameOverGood && !isGameOverBad){ //if gameplay has started
			camX = player.x - Gdx.graphics.getWidth() / 2;
			camY = player.y - Gdx.graphics.getHeight() / 2;
			mouse.setX(camX + Gdx.input.getX());
			mouse.setY(camY + Gdx.input.getY());
			updateSolids(delta);
			if(player.isActive){
				player.update(delta);
			}
		}
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
			Gdx.app.exit();
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
		if(start.color != target){
			return;
		}
		else{
			float x = start.getX() + 1;
			float y = start.getY() + 1;
			float offset = start.width;
			if(start.color == target){
				start.isActive = true;
				blocksCorrupted++;
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

	/* 
	 * Generates all solids based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateSolids(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("Solids").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Block block = new Block(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this, false);
				if(solids != null){
					solids.add(block);
				}
				else{
					System.out.println("ArrayList solids does not exist."); //error message
				}
			}
		}
	}

	/* 
	 * Generates all blocks based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateBlueBlocks(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("BlocksBlue").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Block block = new Block(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this, true, Color.BLUE);
				if(solids != null){
					solids.add(block);
				}
				else{
					System.out.println("ArrayList solids does not exist."); //error message
				}
			}
		}
	}

	/* 
	 * Generates all blocks based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateRedBlocks(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("BlocksRed").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Block block = new Block(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this, true, Color.RED);
				if(solids != null){
					solids.add(block);
				}
				else{
					System.out.println("ArrayList solids does not exist."); //error message
				}
			}
		}
	}

	/* 
	 * Generates all blocks based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateGreenBlocks(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("BlocksGreen").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Block block = new Block(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this, true, Color.GREEN);
				if(solids != null){
					solids.add(block);
				}
				else{
					System.out.println("ArrayList solids does not exist."); //error message
				}
			}
		}
	}

	/* 
	 * Generates all blocks based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateWhiteBlocks(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("BlocksWhite").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Block block = new Block(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this, true, Color.WHITE);
				if(solids != null){
					solids.add(block);
				}
				else{
					System.out.println("ArrayList solids does not exist."); //error message
				}
			}
		}
	}

	/* 
	 * Generates all blocks based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateYellowBlocks(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("BlocksYellow").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Block block = new Block(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this, true, Color.YELLOW);
				if(solids != null){
					solids.add(block);
				}
				else{
					System.out.println("ArrayList solids does not exist."); //error message
				}
			}
		}
	}

	/*
	 * Sets up any images that the player may have. Necessary because images are flipped and have the origin
	 * on the bottom-left by default.
	 */
	public void adjustSprite(Sprite... s){
		for(int i = 0; i < s.length; i++){
			s[i].setOrigin(0, 0);
			s[i].flip(false, true);
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
		if(keycode == Keys.ENTER){
			if(!player.isActive && !isGameOverGood && !isGameOverBad){ //if in main menu
				player.isActive = true;
				gameplayTheme.loop();
			}
			else if((isGameOverGood || isGameOverBad)){ //if in any game over screen, go back to main menu
				player.isActive = false;
				isGameOverGood = false;
				isGameOverBad = false;
				gameplayTheme.stop();
				postTransitionIn(new NullTransition());
			}
		}
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
					floodFill(temp, temp.color); 
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
			//remove the block if it's placed inside of another
			if(currentSelection.collisionExistsAt(currentSelection.x, currentSelection.y)){
				solids.remove(currentSelection);
			}
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
