package com.pigletlogic.spaceoid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpriteBatchTransparency
{

	private SpriteBatch batch = null;

	// for transparency in masks
	private float oldAlphaValue;
	private Color transparencyColor = null;

	private static SpriteBatchTransparency instance = null;

	public void init(SpriteBatch p_batch)
	{
		batch = p_batch;
		oldAlphaValue = 0.0f;
		transparencyColor = new Color();
	}

	public static SpriteBatchTransparency getInstance()
	{
		if (instance == null) instance = new SpriteBatchTransparency();

		return instance;
	}

	public void setTransparency(float p_value)
	{
		transparencyColor = batch.getColor();
		oldAlphaValue = transparencyColor.a;
		transparencyColor.a = oldAlphaValue * p_value;
		batch.setColor(transparencyColor);
	}

	public float getAlpha()
	{
		return transparencyColor.a;
	}

	public void removeTransparency()
	{
		transparencyColor.a = oldAlphaValue;
		batch.setColor(transparencyColor);
	}

	private SpriteBatchTransparency()
	{
	}

}
