package com.pigletlogic.spaceoid;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.pigletlogic.spaceoid.types.PlanetType;
import com.pigletlogic.util.Constants;

public class Bullet extends Planet
{
	private static final String TAG = Bullet.class.getName();

	private Planet source = null;
	private Planet target = null;

	public Bullet(PlanetType p_type, Planet p_source, Planet p_target, int p_x, int p_y, float p_velocity_x, float p_velocity_y, float p_power)
	{

		super(p_type, p_x, p_y, p_power);

		source = p_source;
		target = p_target;
		velocity.x = p_velocity_x;
		velocity.y = p_velocity_y;
		visible = true;
	}

	@Override
	public void render(SpriteBatch batch)
	{

		batch.draw(getType().getSprite(), getBottomLeftX(), // x
				getBottomLeftY(), // y
				getType().getSprite().getWidth() / 2, // origin x
				getType().getSprite().getHeight() / 2, // origin // y
				getType().getSprite().getWidth(), // width
				getType().getSprite().getHeight(),// height
				getScale(), // scale x
				getScale(), // scale y
				getRotation() // rotation
		);
		
		if (Constants.DRAW_POWERS)
			f2.draw(batch, String.valueOf(Math.round(getPower())), getCenterX(), getCenterY());

	}

	public Planet getSource()
	{
		return source;
	}

	public Planet getTarget()
	{
		return target;
	}

	public void destroy()
	{
		visible = false;
	}

}
