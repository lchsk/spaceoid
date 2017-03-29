package com.pigletlogic.spaceoid;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.pigletlogic.screens.EmptyScreen;
import com.pigletlogic.spaceoid.types.PlanetType;
import com.pigletlogic.util.Constants;

public class Statistics
{
	private static final String TAG = Statistics.class.getName();

	private EmptyScreen gameScreen = null;
	private HashMap<String, Float> stats = null;

	private float totalPower = 0;
	private int playersCount = 0;

	/**
	 * Stats bar parameters
	 */
	private float statsBarWidth = 0;
	private int startBarX = 0;
	private int startBarY = 0;
	private float barScaleY = 0f;

	/** Stats for grading the game (1-3 stars) */
	private float playerHealthLoss = 0;
	private String winner = null;
	
	private float humanPowerStart = 0;
	private float humanPowerEnd = 0;
	
	private float enemyPowerStart = 0;
	private float enemyEnlargementSum = 0;
	
	private float humanPlanetsStart = 0;
	private float humanPlanetsEnd = 0;
	
	private float enemyPlanetsStart = 0;

	/** End of variables */

	public Statistics(EmptyScreen p_gameScreen)
	{
		gameScreen = p_gameScreen;
		stats = new HashMap<String, Float>();

		populateWithPlayers();
		compute();
		computeHumanPowerStart();
		enemyPowerStart = computeEnemyPower();
		humanPlanetsStart = countHumanPlanets();
		enemyPlanetsStart = countEnemyPlanets();

		/**
		 * Params initialization - lots of magic numbers!
		 */
		playerHealthLoss = 0;

		statsBarWidth = Constants.VIEWPORT_WIDTH * 0.2f; // bar should be 1/5 of
															// the screen width
		startBarX = Math.round(Constants.VIEWPORT_WIDTH / 2 - statsBarWidth / 2); // center
		startBarY = Math.round(Constants.VIEWPORT_HEIGHT * 0.95f); // near the
																	// top

		barScaleY = MathUtils.clamp(0.033f * Constants.VIEWPORT_HEIGHT / 32, 0.4f, 1.0f); // can
																							// be
																							// between
																							// 0.4-1.0
	}

	/**
	 * Fill map with players
	 */
	private void populateWithPlayers()
	{
		for (String s : PlanetType.colorList)
		{
			stats.put(s, 0f);
		}

		playersCount = stats.size();
		winner = null;
	}

	private void computeTotalPower()
	{
		totalPower = 0;

		for (Planet p : gameScreen.getGame().getPlanets())
		{
			if (p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE && p.isFightingObject())
			{
				totalPower += p.getPower();
			}
		}
	}
	
	public float computeHumanPower()
	{
		float pow = 0;

		for (Planet p : gameScreen.getGame().getPlanets())
		{
			if (p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE && p.isFightingObject() 
					&& p.getType().getColor() == gameScreen.getGame().getHumanPlayer().getHumanType().getColor())
			{
				pow += p.getPower();
			}
		}
		
		Gdx.app.debug(TAG, "human power: " + pow);
		
		return pow;
	}
	
	public float countHumanPlanets()
	{
		float c = 0;

		for (Planet p : gameScreen.getGame().getPlanets())
		{
			if (p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE && p.isFightingObject() 
					&& p.getType().getColor() == gameScreen.getGame().getHumanPlayer().getHumanType().getColor())
			{
				c += 1;
			}
		}
		
		return c;
	}
	
	public float countEnemyPlanets()
	{
		float c = 0;

		for (Planet p : gameScreen.getGame().getPlanets())
		{
			if (p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE && p.isFightingObject() 
					&& p.getType().getColor() != gameScreen.getGame().getHumanPlayer().getHumanType().getColor())
			{
				c += 1;
			}
		}
		
		return c;
	}
	
	public float computeEnemyPower()
	{
		float pow = 0;

		for (Planet p : gameScreen.getGame().getPlanets())
		{
			if (p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE && p.isFightingObject() 
					&& p.getType().getColor() != gameScreen.getGame().getHumanPlayer().getHumanType().getColor())
			{
				pow += p.getPower();
			}
		}
		
		Gdx.app.debug(TAG, "enemy power: " + pow);
		
		return pow;
	}
	
	public void computeHumanPowerStart()
	{
		humanPowerStart = computeHumanPower();
	}
	
	public void computeHumanPowerEnd()
	{
		humanPowerEnd = computeHumanPower();
	}
	
	public float getHumanPowerRatio()
	{
		if (humanPowerStart > 0)
			return humanPowerEnd / humanPowerStart;
		
		return 0;
	}

	public float getTotalPower()
	{
		return totalPower;
	}

	public int getPlayersCount()
	{
		return playersCount;
	}

	public void compute()
	{
		computeTotalPower();

		// reset
		for (Map.Entry<String, Float> entry : stats.entrySet())
		{
			entry.setValue(0f);
		}

		for (Planet p : gameScreen.getGame().getPlanets())
		{
			if (p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE && p.isFightingObject())
			{
				// compute percentage
				if (stats.get(p.getType().toString()) != null)
				{
					float tmp = stats.get(p.getType().toString()).floatValue() + p.getPower() / totalPower;
					
					//tmp -= Constants.MIN_PLANET_POWER_TO_SURVIVE;
					
					//Gdx.app.debug(TAG, "power for " + p.getType().toString() + " = " + tmp);

					stats.put(p.getType().toString(), tmp);
				}
			}

		}

	}

	/**
	 * 
	 * @param delta
	 */
	public void render(float delta)
	{
		int x = startBarX;

		for (Map.Entry<String, Float> entry : stats.entrySet())
		{
			if (entry.getValue() > 0)
			{
			
				Sprite s = Assets.instance.gui.sprites.get("stats_" + entry.getKey());
				float w = entry.getValue() * statsBarWidth;
				for (int i = 0; i < w; i++)
				{
					gameScreen.getGame().getBatch().draw(s, x + i, startBarY, 0, 0, 1, 32, 1, barScaleY, 0);
	
				}
				x += w;
			}
		}
	}

	public boolean isEndGame(boolean p_waitForBullets)
	{
		if (p_waitForBullets)
		{
			int liveBullets = 0;

			for (Bullet b : gameScreen.getGame().getBullets())
			{
				if (b.isVisible()) liveBullets++;
			}

			if (liveBullets != 0) return false;
		}
		
		// check number of players
		if (numberOfAlivePlayers() > 1)
		{
			//Gdx.app.debug(TAG, "numberOfAlivePlayer: " + numberOfAlivePlayers());
			
			// check human player's health
			if (stats.get(gameScreen.getGame().getHumanPlayer().getHumanType().getString()) > 0) // values from 0-1
			{
				// player's alive so carry on
				Gdx.app.debug(TAG, "Human is ALIVE: " + stats.get(gameScreen.getGame().getHumanPlayer().getHumanType().getString()));
				return false;
			}
			else
			{
				Gdx.app.debug(TAG, "HUMAN DEAD!!!");
				
				// human player lost
				winner = "someotherplayer"; // does not really matter
				return true;
			}
		}
		else
		{
			// end game
			winner = whoWon();
			Gdx.app.debug(TAG, "Number of players <= 1!!! WINNER: " + winner);
			
			return true;
		}
	}

	/**
	 * 
	 * @param boolean whether to wait for all bullets to hit their targets
	 * 
	 * @return null if the game has not ended yet
	 * @return String winning player's color
	 */
	public String whoWon()
	{

		
//
//		if (isEndGame())
//		{
//			// only 1 player left
//		}

//		if (stats.get(gameScreen.getGame().getHumanPlayer().getHumanType().getString()) > 0) // check
//																								// if
//																								// human
//																								// player
//																								// is
//																								// ok
//			return null;
//		else
		{
			for (Map.Entry<String, Float> entry : stats.entrySet())
			{
				if (entry.getValue() > 0) return entry.getKey();
			}
		}

		return null;

	}

	private int numberOfAlivePlayers()
	{
		int players = 0;

		for (Map.Entry<String, Float> entry : stats.entrySet())
		{
			if (entry.getValue() > 0) players++;
		}
		
		Gdx.app.debug(TAG, "alive players: " + players + " statssize: " + stats.size());

		return players;
	}

	public boolean isHumanPlayerAlive()
	{
		return stats.get(gameScreen.getGame().getHumanPlayer().getHumanType().toString()).floatValue() > 0;
	}

	public void addEnemyEnlargement(float p_value)
	{
		enemyEnlargementSum += p_value;
	}
	
	public void getHumanPlanetsAtGameOver()
	{
		humanPlanetsEnd = countHumanPlanets();
	}
	
	public float getHumanPlanetsPct()
	{
		if (humanPlanetsStart > 0)
			return humanPlanetsEnd / humanPlanetsStart;
		
		return 0;
	}
	
	/** Accessors */
	
	public float getHumanPowerStart()
	{
		return humanPowerStart;
	}
	
	public float getEnemyPlanetsStart()
	{
		return enemyPlanetsStart;
	}
	
	public float getEnemyPowerStart()
	{
		return enemyPowerStart;
	}
	
	public float getEnemyEnlargementSum()
	{
		return enemyEnlargementSum;
	}

	public float getPlayerHealthLoss()
	{
		return playerHealthLoss;
	}

	public void addPlayerHealthLoss(float p_addend)
	{
		playerHealthLoss += p_addend;
	}

	public float getPlayerPower(String p_color)
	{
		if (stats.get(p_color) != null)
		{
			return stats.get(p_color);
		}

		return - 1;
	}
	
	public String getWinner()
	{
		return winner;
	}

}
