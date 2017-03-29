package com.pigletlogic.spaceoid;

import com.pigletlogic.spaceoid.types.Type;

public class Booster extends Planet
{
	private static final String TAG = Booster.class.getName();

	public Booster(Type p_type, int p_x, int p_y, int p_power, float p_visibilityTime)
	{
		super(p_type, p_x, p_y, p_power);
		fightingObject = false;
		timeVisible = p_visibilityTime;
	}

}
