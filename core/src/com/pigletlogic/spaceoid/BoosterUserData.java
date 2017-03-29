package com.pigletlogic.spaceoid;

import java.util.HashMap;
import java.util.Map;

import com.pigletlogic.spaceoid.types.PlanetType;

public class BoosterUserData
{
	private static final String TAG = BoosterUserData.class.getName();

	private BoosterData boosterData = null;
	public float bulletSpeedFactor = 1.0f;
	public float planetEnlargementFactor = 1.0f;
	public Map<String, Float> timeLeft = null;
	private String color = null;

	public BoosterUserData(String p_color, BoosterData p_boosterData)
	{
		color = p_color;
		boosterData = p_boosterData;
		timeLeft = new HashMap<String, Float>();
		timeLeft.put("bullet", - 1f);
		timeLeft.put("freezing", - 1f);
		timeLeft.put("enlarge", - 1f);
	}

	// --- BULLET
	public void turnOnBulletWeak()
	{
		//bulletSpeedFactor = 2.0f;
		
		timeLeft.put("bullet", 10.0f);
	}

	public void turnOnBulletStrong()
	{
		//bulletSpeedFactor = 4.0f;
		timeLeft.put("bullet", 5.0f);
	}

	// --- PLANET ENLARGEMENT FACTOR
	public void turnOnPlanetEnlargementWeak()
	{
		planetEnlargementFactor = 2.0f;
		timeLeft.put("enlarge", 5.0f);
	}

	public void turnOnPlanetEnlargementStrong()
	{
		planetEnlargementFactor = 4.0f;
		timeLeft.put("enlarge", 3.0f);
	}

	// ----- FREEZING

	public void turnOnFreezingWeak()
	{
		timeLeft.put("freezing", 40.0f);
	}

	public void turnOnFreezingStrong()
	{
		timeLeft.put("freezing", 60.0f);
	}

	// ---------------------------------

	public void turnOffBooster(String p_b)
	{
		if (p_b.equals("bullet"))
		{
			bulletSpeedFactor = 1.0f;
			timeLeft.put("bullet", - 1f);
			boosterData.boosterAction.bulletSizeFactor.put(color, 1.0f);

			boosterData.getBoosterAction().addMessage(new Planet(new PlanetType(color), -500, 0, 0), "Bullets' power back to normal");
		}
		else if (p_b.equals("freezing"))
		{
			timeLeft.put("freezing", - 1f);

			//boosterData.getBoosterAction().addMessage(new Planet(new PlanetType(color), 0, 0, 0), "The freezing is off");
		}
		else if (p_b.equals("enlarge"))
		{
			timeLeft.put("enlarge", - 1f);

			//boosterData.getBoosterAction().addMessage(new Planet(new PlanetType(color), 0, 0, 0), "Faster planets' enlargement is off");
		}
	}
}