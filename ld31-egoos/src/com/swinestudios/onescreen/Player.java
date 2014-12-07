package com.swinestudios.onescreen;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Player{

	//Note: The half-pixel-offset problem has not been fixed yet.
	public float x, y;
	public float velX, velY;
	public float accelX, accelY;

	//Constants which may be adjusted later
	public final float frictionX = 0.4f;
	public final float frictionY = 0.4f;
	public final float gravity = 0.2f;
	public final float moveSpeed = 1.0f;
	public final float jumpSpeed = 8.0f;
	public final float maxSpeedX = 3.0f;
	public final float maxSpeedY = 6.0f;

	public boolean onGround;

	public Rectangle hitbox;

	public MainMenu level;

	public String type;

	public Player(float x, float y, float width, float height, MainMenu level){
		this.x = x;
		this.y = y;
		hitbox = new Rectangle(x, y, width, height);
		velX = 0;
		velY = 0;
		accelX = 0;
		accelY = 0;
		onGround = false;
		this.level = level;
		type = "Player";
	}

	public void render(GameContainer gc, Graphics g){
		g.drawRect(x, y, hitbox.width, hitbox.height);
	}

	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		if(collisionExistsAt(x, y + 1)){
			onGround = true;
		}
		else{
			onGround = false;
		}

		accelX = 0; //keep resetting the x acceleration

		//Move Left
		if(Gdx.input.isKeyPressed(Keys.A) && velX > -maxSpeedX){
			accelX = -moveSpeed;
		}
		
		//Move Right
		if(Gdx.input.isKeyPressed(Keys.D) && velX < maxSpeedX){
			accelX = moveSpeed;
		}

		//Apply friction when not moving or when exceeding the max horizontal speed
		if(Math.abs(velX) > maxSpeedX || !Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)){
			friction(true, false);
		}

		//Jump
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			if(onGround){
				jump();
			}
		}

		limitSpeed(false, true);
		move();
		gravity();
	}

	/*
	 * Checks if there is a collision if the player was at the given position.
	 */
	public boolean isColliding(Rectangle other, float x, float y){
		Rectangle temp = new Rectangle(x, y, this.hitbox.width, this.hitbox.height);
		return temp.overlaps(other);
	}

	/*
	 * Helper method for checking whether there is a Rectangle if the player moves at the given position
	 */
	public boolean collisionExistsAt(float x, float y){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x, y)){
				return true;
			}
		}
		return false;
	}

	public void move(){
		moveX();
		moveY();
	}

	/*
	 * Applies a friction force in the given axes by subtracting the respective velocity components
	 * with the given friction components.
	 */
	public void friction(boolean horizontal, boolean vertical){
		//if there is horizontal friction
		if(horizontal){
			if(velX > 0){
				velX -= frictionX; //slow down
				if(velX < 0){
					velX = 0;
				}
			}
			if(velX < 0){
				velX += frictionX; //slow down
				if(velX > 0){
					velX = 0;
				}
			}
		}
		//if there is vertical friction
		if(vertical){
			if(velY > 0){
				velY -= frictionY; //slow down
				if(velY < 0){
					velY = 0;
				}
			}
			if(velY < 0){
				velY += frictionY; //slow down
				if(velY > 0){
					velY = 0;
				}
			}
		}
	}

	/*
	 * Limits the speed of the player to a set maximum
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

	public void jump(){
		velY = -jumpSpeed;
	}

	/*
	 * Returns the current tile position of the player, given the specific tile dimensions
	 */
	public float getTileX(int tileSize){
		return (int)(x / tileSize) * tileSize;
	}

	/*
	 * Returns the current tile position of the player, given the specific tile dimensions
	 */
	public float getTileY(int tileSize){
		return (int)(y / tileSize) * tileSize;
	}

	/*
	 * Returns the distance between the player and the given target
	 */
	public float distanceTo(Rectangle target){
		return((float)Math.pow(Math.pow((target.y - this.y), 2.0) + Math.pow((target.x - this.x), 2.0), 0.5));
	}
	
	public void gravity(){
		velY += gravity;
	}
	
	/*
	 * Move horizontally in the direction of the x-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveX(){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x + velX, y)){
				while(!isColliding(solid, x + Math.signum(velX), y)){
					x += Math.signum(velX);
				}
				velX = 0;
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
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x, y + velY)){
				while(!isColliding(solid, x, y + Math.signum(velY))){
					y += Math.signum(velY);
				}
				velY = 0;
			}
		}
		y += velY;
		velY += accelY;
	}

}

