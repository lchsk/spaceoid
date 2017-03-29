package com.pigletlogic.spaceoid.effects;

import com.pigletlogic.spaceoid.Timer;

public interface IEffect
{
	public void initializeInterval(float p_factor);
	
	public boolean checkStartCondition(Timer p_timer);
	
	public boolean checkStopCondition();
}
