package com.pigletlogic.spaceoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Spritesheet
{
	public Texture texture;
	public int columns;
	public int rows;
	
	public int[] getPosition(int spriteId)
	{
		int[] retValue = new int[2];
		
		retValue[0] = spriteId % columns;
		retValue[1] = spriteId / columns;
		
		return retValue;
	}
	
	public void load(String path, boolean linearFiltering)
	{
		texture = new Texture(Gdx.files.internal(path));
		
		if (linearFiltering)
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
	
	public void load(Texture t, boolean linearFiltering)
	{
		texture = t;
		
		if (linearFiltering)
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
}
