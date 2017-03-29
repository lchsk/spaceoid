package com.pigletlogic.spaceoid;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pigletlogic.screens.EmptyScreen;
import com.pigletlogic.screens.GameScreen;
import com.pigletlogic.util.Constants;

public class GameBackground
{
	private ArrayList<GameBackgroundElement> stars = null;
	private ArrayList<GameBackgroundElement> glows = null;

	private EmptyScreen gameScreen = null;
	private Random random = null;
	private int starsCount = 0;
	private int glowsCount = 0;

	/** MIN & MAX */
	private int starsMin = 0;
	private int starsMax = 0;
	private int glowsMin = 0;
	private int glowsMax = 0;
	
	private float screenSizeFactor = 1.0f;
	
	private boolean movingBackground = false;

	public GameBackground(EmptyScreen p_gameScreen, boolean p_movingBackground)
	{
		movingBackground = p_movingBackground;
		
		starsMin = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 50 / 480);
		starsMax = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 100 / 480);
		glowsMin = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 2 / 480);
		glowsMax = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 5 / 480);

		stars = new ArrayList<GameBackgroundElement>();
		glows = new ArrayList<GameBackgroundElement>();
		gameScreen = p_gameScreen;
		random = new Random();
		starsCount = random.nextInt(starsMax - starsMin + 1) + starsMin;
		glowsCount = random.nextInt(glowsMax - glowsMin + 1) + glowsMin;

		// default value, it can be changed for smaller objects
		screenSizeFactor = Constants.VIEWPORT_GUI_HEIGHT * 0.5f / 480f;
		
		populateBackgroundWithStars();
		populateBackgroundWithGlows();
		
		
	}

	private void populateBackgroundWithStars()
	{
		int direction = 1;
		
		if (random.nextInt(2) == 0)
			direction = 1; // move right
		else
			direction = -1; // left
		
		for (int i = 0; i < starsCount; ++i)
		{
			String name = "star" + Integer.toString(random.nextInt(Assets.instance.stars.sprites.size()) + 1);
			Float size = Assets.instance.stars.starSizes.get(name);
			Sprite s = Assets.instance.stars.sprites.get(name);
			
			GameBackgroundElement g = new GameBackgroundElement(s);
			
			if (size != null && movingBackground)
			{
				// bigger star are closer so they move faster
				if (size <= 1f) 
				{
					g.setScale(1f);
					g.speed.x = 10f;
				}
				else if (size <= 1.5f)
				{
					g.setScale(1f);
					g.speed.x = 15f;
				}
				else if (size <= 2f)
				{
					g.setScale(1f);
					g.speed.x = 20;
				}
				else if (size <= 2.5f)
				{
					g.setScale(1f);
					g.speed.x = 25;
				}
				else if (size <= 3f)
				{
					g.setScale(1f);
					g.speed.x = 25;
				}
				else if (size <= 4f)
				{
					g.setScale(screenSizeFactor);
					g.speed.x = 30;
				}
				else if (size <= 5f)
				{
					g.setScale(screenSizeFactor);
					g.speed.x = 35f;
				}
				else if (size <= 6f)
				{
					g.setScale(screenSizeFactor);
					g.speed.x = 40f;
				}
				else
				{
					g.setScale(screenSizeFactor);
					g.speed.x = 40f;
				}
					
				// screen size
				g.speed.x *= (Constants.VIEWPORT_WIDTH / 1080f);
				
				g.speed.x *= direction;
			}
			
			stars.add(g);
		}
	}

	private void populateBackgroundWithGlows()
	{
		for (int i = 0; i < glowsCount; ++i)
		{
			String name = "glow" + Integer.toString(random.nextInt(Assets.instance.background.sprites.size()) + 1);
			GameBackgroundElement e = new GameBackgroundElement(Assets.instance.background.sprites.get(name));
			e.alpha = MathUtils.clamp(random.nextFloat(), 0.6f, 1.0f);
			glows.add(e);
		}
	}

	public void render(SpriteBatch batch)
	{
		// stars
		for (GameBackgroundElement s : stars)
		{
			SpriteBatchTransparency.getInstance().setTransparency(s.alpha);
			//batch.draw(s.sprite, s.position.x, s.position.y);
			batch.draw(s.sprite, s.position.x, s.position.y, 0, 0, s.sprite.getWidth(), s.sprite.getHeight(), s.scale.x, s.scale.y, s.rotation);
			SpriteBatchTransparency.getInstance().removeTransparency();
		}

		// glows
		for (GameBackgroundElement s : glows)
		{
			SpriteBatchTransparency.getInstance().setTransparency(s.alpha);
			batch.draw(s.sprite, s.position.x, s.position.y, 0, 0, s.sprite.getWidth(), s.sprite.getHeight(), 1, 1, s.rotation);
			SpriteBatchTransparency.getInstance().removeTransparency();
		}
	}

	public void update(float delta)
	{
		for (GameBackgroundElement s : stars)
		{
			s.position.x += delta * s.speed.x;
			//s.position.y += delta * s.speed.y;
			
			if (s.position.x > Constants.VIEWPORT_WIDTH)
				s.position.x = -s.sprite.getWidth();
			else if (s.position.x + s.sprite.getWidth() < 0)
				s.position.x = Constants.VIEWPORT_WIDTH;
		}
	}
}

class GameBackgroundElement
{
	public Sprite sprite = null;
	public Vector2 position = null;
	public Vector2 speed = null;
	private Vector2 center = null;
	public float alpha = 1.0f;
	public float rotation = 0;
	public Vector2 scale = null;

	public GameBackgroundElement(Sprite p_sprite)
	{
		sprite = p_sprite;

		Random r = new Random();
		position = new Vector2();
		speed = new Vector2();
		center = new Vector2(sprite.getWidth() / 2.0f, sprite.getHeight() / 2.0f);

		position.x = r.nextInt((int) Constants.VIEWPORT_WIDTH) - center.x;
		position.y = r.nextInt((int) Constants.VIEWPORT_HEIGHT) - center.y;
		alpha = r.nextFloat();
		rotation = r.nextInt(361);
		scale = new Vector2(1f, 1f);
	}
	
	public void setScale(float p_scale)
	{
		scale.x = scale.y = p_scale;
	}
}