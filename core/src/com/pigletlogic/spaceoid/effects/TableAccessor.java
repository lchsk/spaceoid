package com.pigletlogic.spaceoid.effects;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TableAccessor implements TweenAccessor<Table>
{
	public static final int POS_XY = 1;

	@Override
	public int getValues(Table target, int tweenType, float[] returnValues)
	{
		switch (tweenType)
		{
		case POS_XY:
			returnValues[0] = target.getX();
			returnValues[1] = target.getY();
			return 2;

		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues(Table target, int tweenType, float[] newValues)
	{
		switch (tweenType)
		{

		case POS_XY:
			target.setPosition(newValues[0], newValues[1]);
			break;

		default:
			assert false;
		}

	}

}
