package com.pigletlogic.spaceoid;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pigletlogic.util.Constants;

public abstract class AbstractGameObject
{

	public Vector2 position;

	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float rotation;
	public Vector2 velocity;
	public Vector2 terminalVelocity;
	public Vector2 friction;
	public Vector2 acceleration;
	public Rectangle bounds;
	// public Body body;
	public float stateTime;
	public Animation animation;

	public AbstractGameObject()
	{
		position = new Vector2();

		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;
		velocity = new Vector2(0, 0);
		terminalVelocity = new Vector2(100, 100);
		friction = new Vector2();
		acceleration = new Vector2();
		bounds = new Rectangle();
	}

	public void update(float deltaTime)
	{
		stateTime += deltaTime;

		position.x += velocity.x * deltaTime;
		position.y += velocity.y * deltaTime;
	}

	protected void updateMotionX(float deltaTime)
	{
		if (velocity.x != 0)
		{

			if (velocity.x > 0)
			{
				velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
			}
			else
			{
				velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
			}
		}

		velocity.x += acceleration.x * deltaTime;

		velocity.x = MathUtils.clamp(velocity.x, - terminalVelocity.x, terminalVelocity.x);
	}

	protected void updateMotionY(float deltaTime)
	{
		if (velocity.y != 0)
		{

			if (velocity.y > 0)
			{
				velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
			}
			else
			{
				velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
			}
		}

		velocity.y += acceleration.y * deltaTime;

		velocity.y = MathUtils.clamp(velocity.y, - terminalVelocity.y, terminalVelocity.y);
	}

	public void setAnimation(Animation animation)
	{
		this.animation = animation;
		stateTime = 0;
	}

	public void setPosition(float x, float y)
	{

		this.position.x = x;
		this.position.y = y;

		this.bounds.x = x;
		this.bounds.y = y;

	}

	public void setX(float x)
	{
		setPosition(x, this.getYPos());
	}

	public void setY(float y)
	{
		setPosition(this.getXPos(), y);
	}

	public Vector2 getPosition()
	{
		return this.position;
	}

	public float getXPos()
	{
		return this.position.x;
	}

	public float getYPos()
	{
		return this.position.y;
	}

	public float getRelXPos()
	{

		return this.position.x / Constants.VIEWPORT_WIDTH;
	}

	public float getRelYPos()
	{

		return this.position.y / Constants.VIEWPORT_HEIGHT;
	}

	public Vector2 getVelocity()
	{
		return velocity;
	}

	public abstract void render(SpriteBatch batch);

	public abstract void destroy();

}
