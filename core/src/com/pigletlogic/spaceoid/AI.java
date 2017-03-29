package com.pigletlogic.spaceoid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import sun.org.mozilla.javascript.tools.debugger.SourceProvider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pigletlogic.screens.GameScreen;
import com.pigletlogic.spaceoid.types.PlanetType;
import com.pigletlogic.util.Constants;
import com.pigletlogic.util.Util;

public class AI
{
	private static final String TAG = AI.class.getName();

	private LevelData level = Assets.instance.assetLevels.currentLevel;
	private ArrayList<Planet> planets = null;
	private ArrayList<Bullet> bullets = null;

	// list of planet colors during the game (updated)
	private ArrayList<String> currentPlanetColors = null;
	private ArrayList<String> currentNonHumanColors = null;
	private Map<String, ArrayList<AIGrade>> grades = null;

	private float timeDiff = 0;
	private float timeCounter = 0;

	private float timeMin = Constants.AI_MIN_SHOOTING_TIME;
	private float timeMax = Constants.AI_MAX_SHOOTING_TIME;

	private float bulletPowerWithoutBooster = 0;
	private float bulletPower = 0;

	private float bulletMin = Constants.AI_MIN_BULLET_SIZE;
	private float bulletMax = Constants.AI_MAX_BULLET_SIZE;

	private int ownShotsCounter = 0;
	
	private Random random = null;

	private GameScreen gameScreen = null;

	private int maxTries = 10;

	private Planet planetSource = null;
	private Planet planetTarget = null;

	private Vector2 vectorStart = null;
	private Vector2 vectorEnd = null;
	private Vector2 vectorDiff = null;
	private Vector2 direction = null;
	private Vector2 path = null;

	public AI(GameScreen p_gameScreen, ArrayList<Planet> p_planets, ArrayList<Bullet> p_bullets)
	{
		random = new Random();
		gameScreen = p_gameScreen;

		planets = p_planets;
		bullets = p_bullets;

		grades = new HashMap<String, ArrayList<AIGrade>>();
		currentPlanetColors = new ArrayList<String>();
		currentNonHumanColors = new ArrayList<String>();

		vectorDiff = new Vector2();

		timeDiff = (timeMax - timeMin) / 100 * (100 - level.getAISpeed()) + timeMin;

		bulletPower = (bulletMax - bulletMin) / 100 * (level.getAIBulletSize()) + bulletMin;
	}

	/**
	 * Prepare a list of all planet colors (updated regularly during the game)
	 */
	private void updatePlanetColors()
	{
		currentPlanetColors.clear();

		for (Planet p : gameScreen.getGame().getPlanets())
		{
			if (p.isFightingObject() && ! currentPlanetColors.contains(p.getType().getString()))
			{
				currentPlanetColors.add(p.getType().getString());
			}
			if (p.isFightingObject() && p.getType().getColor() != gameScreen.getGame().getHumanPlayer().getHumanType().getColor()
					&& ! currentNonHumanColors.contains(p.getType().getString()))
			{
				currentNonHumanColors.add(p.getType().getString());
			}
		}
	}

	public void grade()
	{
		updatePlanetColors();
		grades.clear();

		// for (String color : PlanetType.colorList)
		for (String color : currentPlanetColors)
		{
			ArrayList<AIGrade> tmp = new ArrayList<AIGrade>();

			for (Planet p : gameScreen.getGame().getPlanets())
			{
				if (p.isVisible() && p.getPower() > Constants.MIN_PLANET_POWER_TO_SURVIVE)
				{
					// higher when the target planet is further from the center of the screen
					float proximityFactor = 0;
					proximityFactor = Math.abs(0.5f - p.getCenterX() / Constants.VIEWPORT_WIDTH) + Math.abs(0.5f - p.getCenterY() / Constants.VIEWPORT_HEIGHT);
					
					proximityFactor = 1 - proximityFactor; // 1 when is in center
					float proximityValue = (20 - 1) / 1 * proximityFactor + 1;
					Gdx.app.debug(TAG, p + " proximityFactor: " + proximityFactor + " proxVal: " + proximityValue);
					
					// boosters first
					if ( ! p.isFightingObject())
					{

						if (p.getType().getString().contains("big") || p.getType().getString().contains("strong"))
						{
							tmp.add(new AIGrade(p, Util.getInstance().getRandomFloat(90, 100)));
						}
						else
						{
							tmp.add(new AIGrade(p, Util.getInstance().getRandomFloat(70, 90)));
						}
					}
					// own planets (p.color = color)
					else if (p.getType().getString().equals(color))
					{
						//float colorPower = gameScreen.getStats().getPlayerPower(color);
						
						if (p.wasEnlargedLastTime())
						{
							// gotta help weak planet
							if (p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE && p.getPower() <= 15)
							{
								tmp.add(new AIGrade(p, proximityFactor + 100 - (Util.getInstance().getRandomFloat(0.75f, 1.75f) * p.getPower())));
							}
							else if (p.getPower() > 15 && p.getPower() <= 25)
							{
								tmp.add(new AIGrade(p, 100 - (Util.getInstance().getRandomFloat(3f, 3.8f) * p.getPower())));
							}
							else if (p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE)
							{
								tmp.add(new AIGrade(p, 100 - (Util.getInstance().getRandomFloat(4f, 6f) * p.getPower())));
							}
						}
						else
						{
							// planets that cant be enlarged
							tmp.add(new AIGrade(p, 0));
						}
					}
					// all other planets
					else
					{
						if(p.getPower() < 13)
						{
							tmp.add(new AIGrade(p, Util.getInstance().getRandomFloat(87f, 100f)));
						}
						else if (Assets.instance.assetLevels.currentLevel.isAgainstHuman() && p.getType().getColor() == gameScreen.getGame().getHumanPlayer().getHumanType().getColor())
						{
							// level definition says, AI should focus more on
							// human player
							tmp.add(new AIGrade(p, MathUtils.clamp(proximityValue + p.getPower() * Util.getInstance().getRandomFloat(1.51f, 2.0f), 50, 100)));
						}
						else if (gameScreen.getStats().getPlayerPower(color) >= 0.25)
						// if this player is powerful, he should fight the
						// opponents more
						{
							float colorPower = gameScreen.getStats().getPlayerPower(color);
							
							//float formula = MathUtils.clamp((gameScreen.getStats().getPlayerPower(color)) * Util.getInstance().getRandomFloat(1.3f, 2.0f), 50, 100);
							float formula = MathUtils.clamp(proximityValue + (100 - p.getPower()) * colorPower * Util.getInstance().getRandomFloat(1.5f, 2.0f), 50, 100);
							tmp.add(new AIGrade(p, formula));
						}
						else
						{ // all other planets - default situation
							tmp.add(new AIGrade(p, proximityValue + p.getPower() * Util.getInstance().getRandomFloat(1f, 1.7f)));
						}

					}
				}
			}

			Collections.sort(tmp, new AIGradeComparator());
			grades.put(color, tmp);
		}

	}

	public void act(float p_delta)
	{
		if (isTimeToShoot(p_delta))
		{
			shoot();
		}
	}

	private Planet findRandomPlanet(boolean p_sameSide)
	{
		int _try = 0;
		Planet p = null;

		if (planets.size() <= 0) return null;

		ArrayList<Planet> candidates = new ArrayList<Planet>();

		if (p_sameSide)
		{
			for (Planet planet : planets)
			{
				Gdx.app.debug(TAG, planet.getType().getColor() + " : " + gameScreen.getGame().getHumanPlayer().getHumanType().getColor());
				if (planet.getType().getColor() != gameScreen.getGame().getHumanPlayer().getHumanType().getColor() && planet.isFightingObject()
						&& planet.getPower() > 0)
				{
					if (planet != planetSource) candidates.add(planet);
				}
			}

		}
		else
		{
			for (Planet planet : planets)
			{
				if (planet.getType().getColor() == gameScreen.getGame().getHumanPlayer().getHumanType().getColor() && planet.isFightingObject()
						&& planet.getPower() > 0)
				{
					if (planet != planetSource) candidates.add(planet);
				}
			}

		}

		if (candidates.size() <= 0) return null;

		int id = random.nextInt(candidates.size());

		p = candidates.get(id);

		if (p == null || ! p.visible)
			return null;
		else
			return p;
	}

	private float computeBulletPower(Planet p_planetSource, Planet p_planetTarget, boolean p_smart)
	{

		// first let's check if the target is a booster
		if ( ! p_planetTarget.isFightingObject())
		{
			// if it is, shoot it with a smallest possible bullet
			return bulletMin;
		}
		// else proceed...

		double r = random.nextGaussian() * 0.2 + 0.5;
		r = Math.abs(r);

		if (r > 1) r = 1.0;

		float max = Constants.MIN_PLANET_POWER;

		if (p_smart)
		{
		//	max = Math.min(p_planetSource.getPower() - Constants.MIN_PLANET_POWER, planetTarget.getPower() + 5); // used
																												// to
																												// be
																												// -1
			//max = Math.min(p_planetSource.getPower() / 2, planetTarget.getPower() + 5);
			max = Math.min(p_planetSource.getPower() / 2, planetTarget.getPower());
		}
		else
		{
			max = Math.min(p_planetSource.getPower() - Constants.MIN_PLANET_POWER, bulletMax); // used
																								// to
																								// be
																								// -1
		}

		
		float val = (float) ((max - bulletMin) / 1 * r + bulletMin);
		Gdx.app.debug(TAG, "val 1:" + val);
		
		val = MathUtils.clamp(val, Constants.AI_MIN_BULLET_SIZE, p_planetSource.getPower() / 2f);
		
		//val = Math.min(val, planetTarget.getPower());
		
		Gdx.app.debug(TAG, "val 2:" + val);
		
		// 0.1 just to make sure
		val = Util.getInstance().clamp(val, Constants.AI_MIN_BULLET_SIZE, p_planetSource.getPower() - Constants.MIN_PLANET_POWER_TO_SHOOT - 0.1f);
		//val = MathUtils.clamp(val, Constants.AI_MIN_BULLET_SIZE, p_planetSource.getPower() - Constants.MIN_PLANET_POWER_TO_SHOOT - 0.1f);
		
		//Gdx.app.debug(TAG, "val 3:" + val);
		
	//	val = 5;
		
		Gdx.app.debug(TAG, "Just computed AI bullet power: " + val);
		
		return val;
	}

	private Planet findStrongestNonHumanPlanet()
	{
		Planet chosenPlanet = null;

		for (Planet p : gameScreen.getGame().getPlanets())
		{
			if (p.isFightingObject() && p.getType().getColor() != gameScreen.getGame().getHumanPlayer().getHumanType().getColor()
					&& ! gameScreen.boosterAction.boosterData.isColorFrozen(p.getType().getString()))
			{
				if (chosenPlanet == null) chosenPlanet = p;

				if (p.getPower() > chosenPlanet.getPower()) chosenPlanet = p;
			}
		}

		return chosenPlanet;
	}

	private Planet findBestTarget(String p_color)
	{
		ArrayList<AIGrade> tmp = grades.get(p_color);
		
		if (tmp != null && tmp.size() > 0)
		{
			for (AIGrade g : tmp)
			{
				// return first found
				if (planetSource != g.planet)
					return g.planet;
			}
			
		}

		return null;
	}

	private String getRandomNonHumanColor()
	{
		if (currentNonHumanColors.size() > 0)
		{
			int r = random.nextInt(currentNonHumanColors.size());

			return currentNonHumanColors.get(r);
		}

		return null;
	}
	
	private Planet forceFindBestTarget()
	{
		int i = 0;
		int maxTries = 10;
		
		Planet p = null;
		
		while (p == null && i < maxTries)
		{
			p = findBestTarget(getRandomNonHumanColor());
			i++;
		}
		
		return p;
	}

	private void findEnemy()
	{
//		if (level.getAIOrder().equals("random"))
//		{
//			planetSource = findRandomPlanet(true);
//			planetTarget = findRandomPlanet(true);
//
//			if (planetSource == null || planetTarget == null) return;
//			if (planetSource == planetTarget) return;
//
//			bulletPower = computeBulletPower(planetSource, planetTarget, false);
//
//		}
		
	//	Gdx.app.debug(TAG, "find Enemy start");
		
		if (level.getAIOrder().equals("smart"))
		{
		//	Gdx.app.debug(TAG, "in if");
			
			planetSource = findStrongestNonHumanPlanet();
		//	Gdx.app.debug(TAG, "find Strongest Non Human Player");
			//String c = getRandomNonHumanColor();
			//planetTarget = findBestTarget(c);
			planetTarget = forceFindBestTarget();
			
			//Gdx.app.debug(TAG, "find planetTarget");
			
			//Gdx.app.debug(TAG, "color: " + c);
			
		//	Gdx.app.debug(TAG, "src: " + planetSource + " trg: " + planetTarget);

			if (planetSource == null || planetTarget == null) return;
			if (planetSource == planetTarget) return;

			bulletPowerWithoutBooster = computeBulletPower(planetSource, planetTarget, true);
			float bullfact = gameScreen.boosterAction.getBulletSizeFactor(planetSource.getType().toString());
			Gdx.app.debug("bullet factor enemy:", String.valueOf(bullfact));
			bulletPower = bulletPowerWithoutBooster * bullfact;
			bulletPowerWithoutBooster = MathUtils.clamp(bulletPower, Constants.AI_MIN_BULLET_SIZE, Constants.AI_MAX_BULLET_SIZE);
		}

	}

	private void shoot()
	{
		//Gdx.app.debug(TAG, "shootStart");
		findEnemy();
		//Gdx.app.debug(TAG, "findEnemy");
		

		boolean b = prepareShot();
		//Gdx.app.debug(TAG, "prepareShot: " + b);

		if (b)
		{
		//	Gdx.app.debug(TAG, "in B");
			
			float centerX = planetSource.getCircle().x;
			float centerY = planetSource.getCircle().y;
			
			//Gdx.app.debug(TAG, "after setting center");

			Bullet bullet = new Bullet(new PlanetType(planetSource.getType().getColor()), planetSource, planetTarget, Math.round(centerX),
					Math.round(centerY), path.x, path.y, bulletPower);
			
		//	Gdx.app.debug(TAG, "creating bullet");

			planetSource.moveBulletOutside(bullet);
			//Gdx.app.debug(TAG, "moving bullet outside");

			
				gameScreen.addBullet(bullet);
			
			//Gdx.app.debug(TAG, "shoot end");
		}
	}

	private boolean prepareShot()
	{
		if (planetSource == null || planetTarget == null) return false;
		
		// AI's shots to their own
		if (planetSource.getType().getColor() == planetTarget.getType().getColor())
		{
			if (Math.abs(planetSource.getPower() - planetTarget.getPower()) <= 7) return false;
			
			if (planetSource.getPower() < 15) return false;
			if (planetTarget.getPower() > 45) return false;
			
			// prevent long-distance
			if (Math.sqrt(Math.pow(planetSource.getCenterXNorm() + planetTarget.getCenterXNorm(), 2f)
					+ Math.pow(planetSource.getCenterYNorm() + planetTarget.getCenterYNorm(), 2f)
					) > 0.7f)
				return false;
			
			// count own shots
			ownShotsCounter++;
			
			// max 2 own shots in a row possible
			if (ownShotsCounter > 1) return false;
		}
		else
		{
			ownShotsCounter = 0;
		}

		vectorStart = new Vector2(planetSource.getCenterX(), planetSource.getCenterY());
		vectorEnd = new Vector2(planetTarget.getCenterX(), planetTarget.getCenterY());

		vectorDiff = vectorEnd.sub(vectorStart);

		direction = vectorDiff.nor();

		path = new Vector2(direction.x, direction.y);

		float screenSizeFactor = Constants.VIEWPORT_GUI_HEIGHT * 1f / 480f;
		
		float factor = Constants.SPEED_FACTOR / bulletPower * screenSizeFactor;
		

		path.x = path.x * factor;
		path.y = path.y * factor;

		if (planetSource.getPower() > bulletPowerWithoutBooster 
				&& (planetSource.getPower() - bulletPowerWithoutBooster) > Constants.MIN_PLANET_POWER_TO_SURVIVE /*Constants.MIN_PLANET_POWER_TO_SHOOT*/ /*(Constants.MIN_PLANET_POWER + Constants.AI_MIN_BULLET_SIZE)*/)
		{

			planetSource.resetEnlargement();
			planetSource.enlargeSmoothly( - bulletPowerWithoutBooster, 0.2f);

			return true;
		}
		else
		{
			Gdx.app.debug(TAG, "Not sending bullet. source pow: " + planetSource.getPower() + " bulletPowerWithoutBooster: " + bulletPowerWithoutBooster);
			
			return false;
		}
	}

	private boolean isTimeToShoot(float p_delta)
	{
		if (level.getAISpeed() <= 0) return false;
		
		timeCounter += p_delta;

		if (timeCounter > timeDiff)
		{
			// Shoot!
			timeCounter = 0;

			return true;
		}

		return false;
	}
}

class AIGrade
{
	public Planet planet = null;
	public float grade = 0;

	public AIGrade(Planet p_planet, float p_grade)
	{
		planet = p_planet;
		grade = p_grade;
	}

	public String toString()
	{
		return grade + " : " + planet;
	}
}

class AIGradeComparator implements Comparator<AIGrade>
{
	@Override
	public int compare(AIGrade o1, AIGrade o2)
	{

		if (o1.grade < o2.grade)
			return 1;
		else
			return - 1;
	}
}
