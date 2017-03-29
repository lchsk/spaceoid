package com.pigletlogic.spaceoid.types;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Game;

public class PlanetType extends Type
{
	private static final String TAG = PlanetType.class.getName();

	private int currentSize = 0;

	public final static int RED = 0;
	public final static int BLUE = 1;
	public final static int GREEN = 2;
	public final static int YELLOW = 3;
	public final static int PURPLE = 4;
	public final static int PINK = 5;
	public final static int BROWN = 6;

	private int color = - 1;

	public final static int MASK_RED = 0;
	public final static int MASK_WHITE = 1;
	public final static int MASK_BLACK = 2;
	private int maskColor = - 1;

	private Sprite sprite30 = null;
	private Sprite sprite100 = null;
	private Sprite sprite300 = null;

	// private Sprite sprite55 = null;

	private Sprite current = null;
	private Sprite currentMask = null;
	
	private Sprite exclamation = null;

	private boolean drawMask = false;
	private float alpha;

	private boolean partMask = false;

	// All possible colors of the planets
	public static ArrayList<String> colorList = null;

	private void init(String p_str)
	{
		colorList = new ArrayList<String>();
		colorList.add("red"); // 2_2
		colorList.add("blue"); // 3_2
		colorList.add("green"); // 4_2
		colorList.add("yellow"); // 5_2
		colorList.add("purple"); // 6_2
		colorList.add("pink"); // 7_2
		colorList.add("brown"); // 8_2

		if (p_str.equals("red"))
		{
			color = RED;

			sprite30 = Assets.instance.planets.sprites.get("red30");
			sprite100 = Assets.instance.planets.sprites.get("red100");
			sprite300 = Assets.instance.planets.sprites.get("red300");
			
			//exclamation = Assets.instance.gui.sprites.get("exclamation_red");

		}
		else if (p_str.equals("blue"))
		{
			color = BLUE;

			sprite30 = Assets.instance.planets.sprites.get("blue30");
			sprite100 = Assets.instance.planets.sprites.get("blue100");
			sprite300 = Assets.instance.planets.sprites.get("blue300");
			
			//exclamation = Assets.instance.gui.sprites.get("exclamation_blue");

		}
		else if (p_str.equals("green"))
		{
			color = GREEN;

			sprite30 = Assets.instance.planets.sprites.get("green30");
			sprite100 = Assets.instance.planets.sprites.get("green100");
			sprite300 = Assets.instance.planets.sprites.get("green300");
			
			//exclamation = Assets.instance.gui.sprites.get("exclamation_green");

		}
		else if (p_str.equals("yellow"))
		{
			color = YELLOW;

			sprite30 = Assets.instance.planets.sprites.get("yellow30");
			sprite100 = Assets.instance.planets.sprites.get("yellow100");
			sprite300 = Assets.instance.planets.sprites.get("yellow300");
			
			//exclamation = Assets.instance.gui.sprites.get("exclamation_yellow");
		}

		else if (p_str.equals("purple"))
		{
			color = PURPLE;

			sprite30 = Assets.instance.planets.sprites.get("purple30");
			sprite100 = Assets.instance.planets.sprites.get("purple100");
			sprite300 = Assets.instance.planets.sprites.get("purple300");
			
			//exclamation = Assets.instance.gui.sprites.get("exclamation_purple");
		}
		else if (p_str.equals("pink"))
		{
			color = PINK;

			sprite30 = Assets.instance.planets.sprites.get("pink30");
			sprite100 = Assets.instance.planets.sprites.get("pink100");
			sprite300 = Assets.instance.planets.sprites.get("pink300");
			
			//exclamation = Assets.instance.gui.sprites.get("exclamation_pink");
		}
		else if (p_str.equals("brown"))
		{
			color = BROWN;

			sprite30 = Assets.instance.planets.sprites.get("brown30");
			sprite100 = Assets.instance.planets.sprites.get("brown100");
			sprite300 = Assets.instance.planets.sprites.get("brown300");
			
			//exclamation = Assets.instance.gui.sprites.get("exclamation_brown");
		}

		if (current == null)
		{
			current = sprite30;

		}

		alpha = 0.5f;

	}

	public void setPartMask()
	{
		partMask = true;

		if (currentSize == 30)
			currentMask = Assets.instance.masks.whitePart30;
		else if (currentSize == 100)
			currentMask = Assets.instance.masks.whitePart100;
		else if (currentSize == 300) currentMask = Assets.instance.masks.whitePart300;

		if (currentMask != null) drawMask = true;
	}

	public boolean getPartMask()
	{
		return partMask;
	}

	public void setMask(int p_maskColor)
	{
		maskColor = p_maskColor;
		partMask = false;

		switch (p_maskColor)
		{
		case MASK_RED:

			if (currentSize == 30)
				currentMask = Assets.instance.masks.red30;
			else if (currentSize == 100)
				currentMask = Assets.instance.masks.red100;
			else if (currentSize == 300) currentMask = Assets.instance.masks.red300;
			break;
		case MASK_WHITE:

			if (currentSize == 30)
				currentMask = Assets.instance.masks.white30;
			else if (currentSize == 100)
				currentMask = Assets.instance.masks.white100;
			else if (currentSize == 300) currentMask = Assets.instance.masks.white300;
			break;
		case MASK_BLACK:

			if (currentSize == 30)
				currentMask = Assets.instance.masks.black30;
			else if (currentSize == 100)
				currentMask = Assets.instance.masks.black100;
			else if (currentSize == 300) currentMask = Assets.instance.masks.black300;
			break;
		}

		if (currentMask != null) drawMask = true;
	}

	public PlanetType(int p_color)
	{
		color = p_color;
		init(getString());

	}

	public boolean isMaskDrawingOn()
	{
		return drawMask;
	}

	public void setMaskDrawing(boolean p_drawMask)
	{
		drawMask = p_drawMask;
	}

	public void setPartMaskDrawing(boolean p_b)
	{
		partMask = p_b;
	}

	public PlanetType(String p_str)
	{
		init(p_str);
	}

	public Sprite getSprite()
	{
		return current;
	}

	public Sprite getCurrentMask()
	{
		return currentMask;
	}

	public void setSpriteSize(int p_size)
	{
		currentSize = p_size;

		switch (p_size)
		{
		case 30:
			current = sprite30;
			break;

		case 100:
			current = sprite100;
			break;

		case 300:
			current = sprite300;
			break;

		}
	}

	public int getColor()
	{
		return color;
	}

	public String getString()
	{
		return toString();
	}

	public String toString()
	{
		switch (color)
		{
		case RED:
			return ("red");

		case GREEN:
			return ("green");

		case BLUE:
			return ("blue");

		case YELLOW:
			return ("yellow");

		case PURPLE:
			return ("purple");

		case PINK:
			return ("pink");

		case BROWN:
			return ("brown");

		}

		return null;
	}

	public void setMaskAlpha(float p_alpha)
	{
		alpha = p_alpha;
	}

	public float getMaskAlpha()
	{
		return alpha;
	}
	
	public Sprite getExclamation()
	{
		return exclamation;
	}
}
