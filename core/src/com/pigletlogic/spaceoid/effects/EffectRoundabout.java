package com.pigletlogic.spaceoid.effects;

import com.badlogic.gdx.math.MathUtils;
import com.pigletlogic.spaceoid.Timer;

public class EffectRoundabout extends Effect implements IEffect
{
	private float roundaboutInterval = -1;
	private boolean isRoundaboutEffect = false;
	
	private float startRotation = 0.0f;
	
	private final float speed = 2.5f;
	
	
	public void initializeInterval(float p_factor)
	{
		roundaboutInterval = MathUtils.clamp(p_factor / 10, 0.0f, 1.0f) + random.nextInt(2); 
		rotation = startRotation = random.nextInt(1);
	}
	
	public boolean checkStartCondition(Timer p_timer)
	{
		if (isRoundaboutEffect) return false;
		
		if (p_timer.isFinished(roundaboutInterval))
		{
			isRoundaboutEffect = true;
			rotationCounter = 0.0f;
			
			return true;	
		}
		
		return false;
	}
	
	public boolean checkStopCondition()
	{
		rotation += speed;
		rotationCounter += speed;
		
		rotation %= 360;
		
		if (rotationCounter > 358)
		{
			isRoundaboutEffect = false;		
			
			return true;
		}
		
		return false;
	}
	
	public float getRoundaboutInterval()
	{
		return roundaboutInterval;
	}
	public void setRoundaboutInterval(float roundaboutEffectTime)
	{
		this.roundaboutInterval = roundaboutEffectTime;
	}
	public boolean isRoundaboutEffect()
	{
		return isRoundaboutEffect;
	}
	public void setRoundaboutEffect(boolean isRoundaboutEffect)
	{
		this.isRoundaboutEffect = isRoundaboutEffect;
	}
	
	
	
	
}
