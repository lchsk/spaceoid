package com.pigletlogic.spaceoid.effects;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pigletlogic.spaceoid.Timer;

public class Effect implements IEffect
{
	protected Random random = null;
	protected float rotation = 0.0f;
	protected float rotationCounter = 0.0f;
	protected boolean visible = true;
	
	// alpha value for this effect
	protected float alpha = 1.0f;
	
	public Effect()
	{
		random = new Random();
	}
	
	public float getRotation()
	{
		return rotation;
	}
	
	public void update(float p_delta)
	{
		
	}
	
	public float getAlpha()
	{
		return alpha;
	}
	
	public void setAlpha(float p_alpha)
	{
		alpha = p_alpha;
	}
	
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void initializeInterval(float p_factor)
	{
		
	}

	@Override
	public boolean checkStartCondition(Timer p_timer)
	{
		return false;
	}

	@Override
	public boolean checkStopCondition()
	{
		return false;
	}

	public void render(SpriteBatch batch) 
	{
		
		
	}
}
