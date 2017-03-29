package com.pigletlogic.spaceoid.effects;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.graphics.Color;
import com.pigletlogic.spaceoid.Planet;

public class PlanetAccessor implements TweenAccessor<Planet>
{
	public static final int POS_XY = 1;
	public static final int SCALE = 2;
	public static final int POWER = 3;

	@Override
	public int getValues(Planet target, int tweenType, float[] returnValues)
	{
		switch (tweenType)
		{
		case POS_XY:
			returnValues[0] = target.getCircle().x;
			returnValues[1] = target.getCircle().y;
			return 2;

		case SCALE:
			returnValues[0] = target.getScale();
			return 1;
			
		case POWER:
			returnValues[0] = target.getPower();
			return 1;

		default:
			assert false;
			return - 1;
		}
	}

	@Override
	public void setValues(Planet target, int tweenType, float[] newValues)
	{
		switch (tweenType)
		{
		case POS_XY:
			target.setPosition(newValues[0], newValues[1]);

			break;

		case SCALE:
			target.setScale(newValues[0]);
			break;
			
		case POWER:
			target.setPower(newValues[0]);
			break;

		default:
			assert false;
		}

	}

}
