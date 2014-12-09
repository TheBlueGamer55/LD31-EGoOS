package com.swinestudios.onescreen;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class Block extends Rectangle{
	
	//debug variables
	public static int id = 0;
	public int thisID = id;
	
	public float velX, velY;
	public float accelX, accelY;
	
	//Constants which may be adjusted later
	public final float frictionX = 0.4f;
	public final float frictionY = 0.4f;
	public final float gravity = 0.2f;
	public final float maxSpeedX = 3.0f;
	public final float maxSpeedY = 6.0f;
	
	public boolean isSelected;
	public boolean isActive;
	public boolean onGround;
	public boolean isSelectionBlock; //hacky, used to determine what kind of block this is
	
	public Color color;
	
	private MainMenu level;
	
	public Block(float x, float y, float width, float height, MainMenu level, boolean selectionType){
		super(x, y, width, height);
		id++;
		velX = 0;
		velY = 0;
		accelX = 0;
		accelY = 0;
		isSelected = false;
		isActive = false;
		onGround = false;
		isSelectionBlock = selectionType;
		this.level = level;
		this.color = Color.WHITE;
	}
	
	public Block(float x, float y, float width, float height, MainMenu level, boolean selectionType, Color color){
		super(x, y, width, height);
		id++;
		velX = 0;
		velY = 0;
		accelX = 0;
		accelY = 0;
		isSelected = false;
		isActive = false;
		onGround = false;
		isSelectionBlock = selectionType;
		this.level = level;
		this.color = color;
	}
	
	public void render(Graphics g){
		if(isSelectionBlock){
			g.setColor(color);
			g.fillRect(x, y, width, height);
		}
		//g.setColor(Color.BLACK);
		//g.drawString("" + thisID, x, y);
	}
	
	public void update(float delta){
		if(isActive && isSelectionBlock){
			//Fall and undergo gravity physics if not selected
			if(!isSelected){
				if(collisionExistsAt(x, y + 1)){
					onGround = true;
				}
				else{
					onGround = false;
				}
				limitSpeed(false, true);
				move();
				gravity();
				//setX() and setY() need to be called to actually update this block's position
				this.setX(x);
				this.setY(y);
			}
			else{
				this.setX(level.camX + Gdx.input.getX() - this.width / 2);
				this.setY(level.camY + Gdx.input.getY() - this.height / 2);
			}
		}
	}
	
	public void move(){
		moveX();
		moveY();
	}
	
	/*
	 * Move horizontally in the direction of the x-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveX(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(solid.isSelectionBlock && solid.isSelected && solid != this){
				continue; //ignore collision checks with selected blocks
			}
			if(isColliding(solid, x + velX, y) && solid != this){
				while(!isColliding(solid, x + Math.signum(velX), y)){
					x += Math.signum(velX);
				}
				velX = 0;
				//"corrupt" any other blocks touched by setting them active
				if(solid.isSelectionBlock && !solid.isActive){ 
					solid.isActive = true;
					MainMenu.blocksCorrupted++;
				}
			}
		}
		x += velX;
		velX += accelX;
	}
	
	/*
	 * Move vertically in the direction of the y-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveY(){
		for(int i = 0; i < level.solids.size(); i++){
			Block solid = level.solids.get(i);
			if(solid.isSelectionBlock && solid.isSelected && solid != this){
				continue; //ignore collision checks with selected blocks
			}
			if(isColliding(solid, x, y + velY) && solid != this){
				while(!isColliding(solid, x, y + Math.signum(velY))){
					y += Math.signum(velY);
				}
				velY = 0;
				//"corrupt" any other blocks touched by setting them active
				if(solid.isSelectionBlock && !solid.isActive){ 
					solid.isActive = true;
					MainMenu.blocksCorrupted++;
				}
			}
		}
		y += velY;
		velY += accelY;
	}
	
	public void gravity(){
		velY += gravity;
	}
	
	/*
	 * Limits the speed of the block to a set maximum
	 */
	private void limitSpeed(boolean horizontal, boolean vertical){
		//If horizontal speed should be limited
		if(horizontal){
			if(Math.abs(velX) > maxSpeedX){
				velX = maxSpeedX * Math.signum(velX);
			}
		}
		//If vertical speed should be limited
		if(vertical){
			if(Math.abs(velY) > maxSpeedY){
				velY = maxSpeedY * Math.signum(velY);
			}
		}
	}
	
	/*
	 * Checks if there is a collision if the player was at the given position.
	 */
	public boolean isColliding(Rectangle other, float x, float y){
		if(x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y){
			return true;
		}
		return false;
	}
	
	/*
	 * Helper method for checking whether there is a Rectangle if the block moves at the given position
	 */
	public boolean collisionExistsAt(float x, float y){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(solid != this && isColliding(solid, x, y)){
				return true;
			}
		}
		return false;
	}

}
