package com.pigletlogic.screens;

import com.badlogic.gdx.Screen;
import com.pigletlogic.spaceoid.Game;
import com.pigletlogic.spaceoid.Level;

public class EmptyScreen implements Screen
{

	private Level level = null;
	private Game game = null;

	public EmptyScreen(Game game)
	{
		this.game = game;

	}

	/* Methods to override */

	public void init()
	{
	}

	public void update(float delta)
	{
	}

	@Override
	public void render(float delta)
	{
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void show()
	{
	}

	@Override
	public void hide()
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public void dispose()
	{
	}

	/* Setters & Getters */

	public Level getLevel()
	{
		return level;
	}

	public void setLevel(Level level)
	{
		this.level = level;
	}

	public Game getGame()
	{
		return game;
	}

	public void setGame(Game game)
	{
		this.game = game;
	}

}
