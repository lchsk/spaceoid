package com.pigletlogic.spaceoid;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.pigletlogic.util.Constants;

public class Spaceoid implements ApplicationListener
{
	private static final String TAG = Spaceoid.class.getName();
	private IActivityRequestHandler myRequestHandler;

	private Game game;

	public boolean paused;
	private Random r = null;
	private int R = 0;
	private int G = 0;
	private int B = 0;

	public Spaceoid(IActivityRequestHandler handler)
	{
		myRequestHandler = handler;
	}

	@Override
	public void create()
	{

		Constants.VIEWPORT_GUI_WIDTH = Gdx.graphics.getWidth();
		Constants.VIEWPORT_WIDTH = Gdx.graphics.getWidth();

		Constants.VIEWPORT_GUI_HEIGHT = Gdx.graphics.getHeight();
		Constants.VIEWPORT_HEIGHT = Gdx.graphics.getHeight();

		Constants.init();

		r = new Random();
		R = r.nextInt(15);
		G = r.nextInt(15);
		B = r.nextInt(15);

		Assets.instance.init(new AssetManager());
		Assets.instance.loadBase();

		game = new Game(this);
		game.initIntro();

	}

	@Override
	public void render()
	{
		float delta = Gdx.graphics.getDeltaTime();
		if ( ! paused)
		{

			game.update(delta);
		}

		Gdx.gl.glClearColor(R / 255.0f, G / 255.0f, B / 255.0f, 0xff / 255.0f);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.render(delta);
	}

	@Override
	public void resize(int width, int height)
	{
		game.resize(width, height);
		Gdx.app.debug(TAG, "Resized");
		
	}

	@Override
	public void pause()
	{
		Gdx.app.debug(TAG, "Paused");
	//	game.getScreen("game_screen").g
		paused = true;
	}

	@Override
	public void resume()
	{
		Gdx.app.debug(TAG, "Resumed");
		//Assets.instance.init(new AssetManager());
		paused = false;
	}

	@Override
	public void dispose()
	{
		game.dispose();
		Assets.instance.dispose();
	}
	
	// ---
	
	public IActivityRequestHandler getActivityHandler()
	{
		return myRequestHandler;
	}

}
