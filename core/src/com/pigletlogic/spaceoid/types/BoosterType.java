package com.pigletlogic.spaceoid.types;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.pigletlogic.spaceoid.Assets;

public class BoosterType extends Type
{
	private static final String TAG = BoosterType.class.getName();
	
	private String boosterType = null;
	private Sprite sprite = null;

	// how long the booster is visible
	private float boosterActiveTime = 0;

	public BoosterType(String p_typeName)
	{
		boosterType = p_typeName;

		setParameters();
	}

	private void setParameters()
	{

		sprite = Assets.instance.boosters.sprites.get(boosterType);

		if (boosterType.equals("add_big"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("add_weak"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("against_ev_big"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("against_ev_weak"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("bomb_big"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("bomb_med"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("bomb_weak"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("bullet_vel_strong"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("bullet_vel_weak"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("enlarge_big"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("enlarge_med"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("enlarge_small"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("freezing_strong"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("freezing_weak"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("random"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("sniper_big"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("sniper_weak"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("timer_big"))
		{
			boosterActiveTime = 5.0f;
		}
		else if (boosterType.equals("timer_weak"))
		{
			boosterActiveTime = 5.0f;
		}

	}

	public String getType()
	{
		return boosterType;
	}

	public Sprite getSprite()
	{
		return sprite;
	}

	public String getString()
	{
		return toString();
	}

	public String toString()
	{
		return boosterType;
	}
}
