package com.pigletlogic.spaceoid;

import com.pigletlogic.spaceoid.types.PlanetType;

public class HumanPlayer
{
	private PlanetType type = null;

	public HumanPlayer(String p_color)
	{
		type = new PlanetType(p_color);
	}

	public PlanetType getHumanType()
	{
		return type;
	}
}