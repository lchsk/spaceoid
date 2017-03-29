package com.pigletlogic.spaceoid;

import java.util.ArrayList;
import java.util.Map;

public class LevelData
{
	private int colors = 0;
	private String humanColor = null;

	// how often booster appears
	private float boosterFreq = 0f;
	// multiply by active time of each booster
	private float boosterActiveTimeFactor = 1.0f;
	
	private float planetGrowthFactorMin = 0;
	private float planetGrowthFactorMax = 0;

	private ArrayList<Planet> planets = null;

	// the sum of the powers of all human planets
	private int totalHumanPower = 0;

	// [1-max]
	private int levelId = - 1;

	/* AI Stuff - default values*/
	private int aiSpeed = 50;
	private int aiBulletSize = 50;
	private String aiOrder = "random";
	private boolean againstHuman = false;

	public Map json = null;

	public LevelData()
	{
		planets = new ArrayList<Planet>();
		reset();
	}

	public void reset()
	{
		colors = 0;
		humanColor = null;
		planets.clear();
		totalHumanPower = 0;
		aiSpeed = 50;
		aiBulletSize = 50;
		aiOrder = "random";
	}

	/** Accessors */

	public void setColors(int p_colors)
	{
		colors = p_colors;
	}

	public void addPlanet(Planet p_planet)
	{
		planets.add(p_planet);
	}

	public ArrayList<Planet> getPlanets()
	{
		return planets;
	}

	public int getAISpeed()
	{
		return aiSpeed;
	}

	public void setAISpeed(int aiSpeed)
	{
		this.aiSpeed = aiSpeed;
	}

	public int getAIBulletSize()
	{
		return aiBulletSize;
	}

	public void setAIBulletSize(int aiBulletSize)
	{
		this.aiBulletSize = aiBulletSize;
	}

	public String getAIOrder()
	{
		return aiOrder;
	}

	public void setAIOrder(String aiOrder)
	{
		this.aiOrder = aiOrder;
	}

	public String getHumanColor()
	{
		return humanColor;
	}

	public void setHumanColor(String humanColor)
	{
		this.humanColor = humanColor;
	}

	public int getTotalHumanPower()
	{
		return totalHumanPower;
	}

	public void addHumanPower(int p_power)
	{
		totalHumanPower += p_power;
	}

	public int getLevelId()
	{
		return levelId;
	}

	public void setLevelId(int levelId)
	{
		this.levelId = levelId;
	}

	public float getBoosterFreq()
	{
		return boosterFreq;
	}

	public void setBoosterFreq(float boosterFreq)
	{
		this.boosterFreq = boosterFreq;
	}

	public float getBoosterActiveTimeFactor()
	{
		return boosterActiveTimeFactor;
	}

	public void setBoosterActiveTimeFactor(float boosterActiveTimeFactor)
	{
		this.boosterActiveTimeFactor = boosterActiveTimeFactor;
	}

	public boolean isAgainstHuman()
	{
		return againstHuman;
	}

	public void setAgainstHuman(boolean againstHuman)
	{
		this.againstHuman = againstHuman;
	}

	public float getPlanetGrowthFactorMin() {
		return planetGrowthFactorMin;
	}

	public void setPlanetGrowthFactorMin(float planetGrowthFactorMin) {
		this.planetGrowthFactorMin = planetGrowthFactorMin;
	}

	public float getPlanetGrowthFactorMax() {
		return planetGrowthFactorMax;
	}

	public void setPlanetGrowthFactorMax(float planetGrowthFactorMax) {
		this.planetGrowthFactorMax = planetGrowthFactorMax;
	}
	
	

}
