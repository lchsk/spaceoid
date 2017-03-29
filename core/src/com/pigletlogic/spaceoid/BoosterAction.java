package com.pigletlogic.spaceoid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.pigletlogic.screens.GameScreen;
import com.pigletlogic.spaceoid.effects.Effect;
import com.pigletlogic.spaceoid.effects.EffectExplosion;
import com.pigletlogic.spaceoid.effects.EffectExplosion.EFFECT_EXPLOSION_TYPE;
import com.pigletlogic.spaceoid.types.BoosterType;
import com.pigletlogic.spaceoid.types.PlanetType;
import com.pigletlogic.util.Constants;

public class BoosterAction
{
	private static final String TAG = BoosterAction.class.getName();

	private Map<String, Integer> boosterProbability = null;
	private ArrayList<String> boostersForDrawing = null;
	// List of all available boosters
	public ArrayList<String> allBoosters = null;

	private GameScreen gameScreen = null;
	private Random rand = null;
	public BoosterData boosterData = null;
	private ArrayList<Effect> effects = null;

	// how long is booster visible?
	public Map<String, Float> timeVisible = null;
	
	// For bullet booster
	// <color>, <factor>
	public Map<String, Float> bulletSizeFactor = null;

	public Planet planetToAdd = null;

	public BoosterAction(GameScreen p_g)
	{
		boosterProbability = new HashMap<String, Integer>();
		boostersForDrawing = new ArrayList<String>();
		effects = new ArrayList<Effect>();
		allBoosters = new ArrayList<String>();
		bulletSizeFactor = new HashMap<String, Float>();
		initProbabilities();
		timeVisible = new HashMap<String, Float>();
		initVisibilityTimes();

		rand = new Random();
		boosterData = new BoosterData(this);
		gameScreen = p_g;

	}
	
	public float getBulletSizeFactor(String p_color)
	{
		Float f = bulletSizeFactor.get(p_color);
		
		if (f == null)
			return 1.0f;
		else
			return f.floatValue();
	}

	public void getNewBooster(ArrayList<Planet> p_planets)
	{
		int r = rand.nextInt(boostersForDrawing.size());

		String booster = boostersForDrawing.get(r);

		float freq = Assets.instance.assetLevels.currentLevel.getBoosterActiveTimeFactor();
		Gdx.app.debug(TAG, "booster active time factor: " + freq);
		Float visTime = timeVisible.get(booster) * freq;
		Gdx.app.debug(TAG, "booster visTIme: " + visTime);

		int power = 20;
		// select random power
		//int powerMin = 20;
		//int powerMax = 28;
		//int power = rand.nextInt(powerMax - powerMin) + powerMin;
		if (booster.contains("_big") || booster.contains("_strong"))
			power = 30;
		else if (booster.contains("_weak") || booster.contains("_small"))
			power = 20;
		else
			power = 25;

		Booster b = new Booster(new BoosterType(booster), - 1000, -1000, power, visTime);

		// find empty place in the screen
		int x = 0;
		int y = 0;
		int maxTries = 5;

		for (int i = 0; i < maxTries; ++i)
		{
			boolean overlaps = false;
			x = rand.nextInt(Math.round(Constants.VIEWPORT_WIDTH - b.getComputedSize() * 2)) + Math.round(b.getComputedSize());
			y = rand.nextInt(Math.round(Constants.VIEWPORT_HEIGHT - b.getComputedSize() * 2)) + Math.round(b.getComputedSize());
			b.setPosition(x, y);

			for (Planet p : p_planets)
			{
				if (p.getCircle().overlaps(b.getCircle()))
				{
					overlaps = true;
				}
			}

			if ( ! overlaps)
			{
				Gdx.app.debug(TAG, "New Booster: " + b.getType() + " [" + b.getCenterX() + ", " + b.getCenterY() + "]");
				b.setVisible(true);
				p_planets.add(b);
				break;
			}
		}

	}

	private void initProbabilities()
	{
		boosterProbability.put("add_big", 1);
		boosterProbability.put("add_weak", 3);

		boosterProbability.put("against_ev_big", 1);
		boosterProbability.put("against_ev_weak", 2);

		boosterProbability.put("bomb_big", 1);
		boosterProbability.put("bomb_med", 3);
		boosterProbability.put("bomb_weak", 4);

		boosterProbability.put("bullet_vel_strong", 1);
		boosterProbability.put("bullet_vel_weak", 3);

		boosterProbability.put("enlarge_big", 1);
		boosterProbability.put("enlarge_med", 3);
		boosterProbability.put("enlarge_small", 4);

	
		boosterProbability.put("random", 2);

		boosterProbability.put("sniper_big", 1);
		boosterProbability.put("sniper_weak", 2);

		
		// removed:
//		boosterProbability.put("freezing_strong", 0);
//		boosterProbability.put("freezing_weak", 0);
//
//		boosterProbability.put("timer_big", 0);
//		boosterProbability.put("timer_weak", 0);

		// Prepare list
		for (Map.Entry<String, Integer> e : boosterProbability.entrySet())
		{
			allBoosters.add(e.getKey());

			for (int i = 0; i < e.getValue(); ++i)
			{
				boostersForDrawing.add(e.getKey());
			}
		}
	}

	private void initVisibilityTimes()
	{
		timeVisible.put("add_big", 3.0f);
		timeVisible.put("add_weak", 4.5f);

		timeVisible.put("against_ev_big", 3.0f);
		timeVisible.put("against_ev_weak", 4.0f);

		timeVisible.put("bomb_big", 3f);
		timeVisible.put("bomb_med", 4f);
		timeVisible.put("bomb_weak", 5f);

		timeVisible.put("bullet_vel_strong", 3f);
		timeVisible.put("bullet_vel_weak", 5f);

		timeVisible.put("enlarge_big", 3f);
		timeVisible.put("enlarge_med", 3.5f);
		timeVisible.put("enlarge_small", 5f);

		

		timeVisible.put("random", 6f);

		timeVisible.put("sniper_big", 3f);
		timeVisible.put("sniper_weak", 4f);

//		timeVisible.put("freezing_strong", 3f);
//		timeVisible.put("freezing_weak", 4f);
//		timeVisible.put("timer_big", 3f);
//		timeVisible.put("timer_weak", 1f);
	}

	public void act(Planet p_booster, Planet p_sourcePlanet)
	{
		String type = p_booster.getType().getString();

		if (type.equals("add_big"))
		{
			float power = MathUtils.clamp(gameScreen.getStats().getTotalPower() * 0.2f, 10, 20);

			planetToAdd = new Planet(new PlanetType(p_sourcePlanet.getType().getColor()), Math.round(p_booster
					.getCenterX()), Math.round(p_booster.getCenterY()), power);
			planetToAdd.setVisible(true);

			addMessage(p_sourcePlanet, "You have one new planet");

		}
		else if (type.equals("add_weak"))
		{
			float power = MathUtils.clamp(gameScreen.getStats().getTotalPower() * 0.1f, 10, 15);

			planetToAdd = new Planet(new PlanetType(p_sourcePlanet.getType().getColor()), Math.round(p_booster
					.getCenterX()), Math.round(p_booster.getCenterY()), power);
			planetToAdd.setVisible(true);

			addMessage(p_sourcePlanet, "You have one new planet");
		}
		else if (type.equals("against_ev_big"))
		{
			actionAgainstEveryBody(p_booster, p_sourcePlanet, 0.2f, 10, 20);

			addMessage(p_sourcePlanet, "There is a new planet");
		}
		else if (type.equals("against_ev_weak"))
		{
			actionAgainstEveryBody(p_booster, p_sourcePlanet, 0.15f, 10, 15);

			addMessage(p_sourcePlanet, "There is a new planet");
		}
		else if (type.equals("bomb_big"))
		{
			gameScreen.bomb(p_booster, 60);

			addMessage(p_sourcePlanet, "A bomb, watch out!");
		}
		else if (type.equals("bomb_med"))
		{
			gameScreen.bomb(p_booster, 40);
			addMessage(p_sourcePlanet, "A bomb, watch out!");
		}
		else if (type.equals("bomb_weak"))
		{
			gameScreen.bomb(p_booster, 20);
			addMessage(p_sourcePlanet, "A bomb, watch out!");
		}
		else if (type.equals("bullet_vel_strong"))
		{
			boosterData.data.get(p_sourcePlanet.getType().getString()).turnOnBulletStrong();
			bulletSizeFactor.put(p_sourcePlanet.getType().toString(), 2.0f);

			addMessage(p_sourcePlanet, "Your bullets hit harder");
		}
		else if (type.equals("bullet_vel_weak"))
		{
			boosterData.data.get(p_sourcePlanet.getType().getString()).turnOnBulletWeak();
			bulletSizeFactor.put(p_sourcePlanet.getType().toString(), 1.5f);
			
			addMessage(p_sourcePlanet, "Your bullets hit harder");
		}
		else if (type.equals("enlarge_big"))
		{
			gameScreen.enlargePlanetBooster(p_sourcePlanet, 30);

			addMessage(p_sourcePlanet, "Your planet has been enlarged");
		}
		else if (type.equals("enlarge_med"))
		{
			gameScreen.enlargePlanetBooster(p_sourcePlanet, 20);

			addMessage(p_sourcePlanet, "Your planet has been enlarged");
		}
		else if (type.equals("enlarge_small"))
		{
			gameScreen.enlargePlanetBooster(p_sourcePlanet, 15);

			addMessage(p_sourcePlanet, "Your planet has been enlarged");
		}
//		else if (type.equals("freezing_strong"))
//		{
//			boosterData.data.get(p_sourcePlanet.getType().getString()).turnOnFreezingStrong();
//
//			addMessage(p_sourcePlanet, "Opponents are frozen");
//		}
//		else if (type.equals("freezing_weak"))
//		{
//			boosterData.data.get(p_sourcePlanet.getType().getString()).turnOnFreezingWeak();
//
//			addMessage(p_sourcePlanet, "Opponents are frozen");
//		}
		else if (type.equals("random"))
		{
			randomBooster(p_booster, p_sourcePlanet);
		}
		else if (type.equals("sniper_big"))
		{
			gameScreen.shootBiggestEnemy(p_sourcePlanet, 14f);

			addMessage(p_sourcePlanet, "The biggest enemy just got hit");
		}
		else if (type.equals("sniper_weak"))
		{
			gameScreen.shootBiggestEnemy(p_sourcePlanet, 7f);

			addMessage(p_sourcePlanet, "The biggest enemy just got hit");
		}
//		else if (type.equals("timer_big"))
//		{
//			boosterData.data.get(p_sourcePlanet.getType().getString()).turnOnPlanetEnlargementStrong();
//
//			addMessage(p_sourcePlanet, "Your planets enlarge faster");
//		}
//		else if (type.equals("timer_weak"))
//		{
//			boosterData.data.get(p_sourcePlanet.getType().getString()).turnOnPlanetEnlargementWeak();
//
//			addMessage(p_sourcePlanet, "Your planets enlarge faster");
//		}
	}
	
	public void addMessage(Planet p_sourcePlanet, String p_message, float p_length)
	{
		if (p_sourcePlanet.getType().getColor() == gameScreen.getGame().getHumanPlayer().getHumanType().getColor())
		{
			gameScreen.getGui().newMessage(p_message, p_length);
		}
	}

	public void addMessage(Planet p_sourcePlanet, String p_message)
	{
		addMessage(p_sourcePlanet, p_message, 3f);
	}

	/**
	 * New planet of random color fighting with everyone else.
	 * 
	 * @param p_booster
	 * @param p_sourcePlanet
	 * @param p_factor
	 * @param p_min
	 * @param p_max
	 */
	private void actionAgainstEveryBody(Planet p_booster, Planet p_sourcePlanet, float p_factor, float p_min, float p_max)
	{
		float power = MathUtils.clamp(gameScreen.getStats().getTotalPower() * p_factor, p_min, p_max);

		String color = null;

		ArrayList<String> tmp = new ArrayList<String>();

		for (String s : PlanetType.colorList)
		{
			if ( ! p_sourcePlanet.getType().getString().equals(s))
			{
				tmp.add(s);
			}
		}

		if (tmp.size() <= 0) return;

		int idx = rand.nextInt(tmp.size());
		color = tmp.get(idx);

		if (color != null)
		{
			planetToAdd = new Planet(new PlanetType(color), Math.round(p_booster.getCenterX()), Math.round(p_booster.getCenterY()), power);
			planetToAdd.setVisible(true);
		}
	}

	private void randomBooster(Planet p_booster, Planet p_sourcePlanet)
	{
		if (timeVisible.size() > 0)
		{
			int r = rand.nextInt(timeVisible.size());
			int i = 0;

			for (Map.Entry<String, Float> e : timeVisible.entrySet())
			{
				if (i == r)
				{
					String booster = e.getKey();
					Gdx.app.debug(TAG, "Random booster: " + booster);

					Booster b = new Booster(new BoosterType(booster), Math.round(p_booster.getCenterX()), Math.round(p_booster.getCenterY()), 10, 10);
					act(b, p_sourcePlanet);

					break;
				}

				i++;
			}

		}
	}

	public void updateBoosterEffects(float p_delta) 
	{	
		Effect toRemove = null;
		
		for (Effect e : effects)
		{
			e.update(p_delta);
			
			if ( ! e.isVisible())
				toRemove = e;
		}
		
		if (toRemove != null)
			effects.remove(toRemove);
		
	}
	
	public void renderBoosterEffects(SpriteBatch batch) 
	{	
		for (Effect e : effects)
		{
			e.render(batch);
		}
	}
	
	public void addExplosionEffect(Planet p_p, EFFECT_EXPLOSION_TYPE p_type, Sprite p_sprite, float p_explosionSize)
	{
		effects.add(new EffectExplosion(p_p, p_type, p_sprite, p_explosionSize));
	}
	
	//--- Accessors
	
	public GameScreen getGameScreen()
	{
		return gameScreen;
	}

	
}
