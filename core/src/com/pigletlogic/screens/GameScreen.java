package com.pigletlogic.screens;

import java.util.ArrayList;
import java.util.Map;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pigletlogic.spaceoid.AI;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.BoosterAction;
import com.pigletlogic.spaceoid.BoosterUserData;
import com.pigletlogic.spaceoid.Bullet;
import com.pigletlogic.spaceoid.Game;
import com.pigletlogic.spaceoid.GameBackground;
import com.pigletlogic.spaceoid.GameInput;
import com.pigletlogic.spaceoid.GunSight;
import com.pigletlogic.spaceoid.HumanPlayer;
import com.pigletlogic.spaceoid.LevelData;
import com.pigletlogic.spaceoid.Planet;
import com.pigletlogic.spaceoid.SpriteBatchTransparency;
import com.pigletlogic.spaceoid.Statistics;
import com.pigletlogic.spaceoid.Timer;
import com.pigletlogic.spaceoid.Tutorial;
import com.pigletlogic.spaceoid.effects.EffectExplosion.EFFECT_EXPLOSION_TYPE;
import com.pigletlogic.spaceoid.effects.PlanetAccessor;
import com.pigletlogic.util.Constants;
import com.pigletlogic.util.Util;

public class GameScreen extends EmptyScreen
{
	private static final String TAG = GameScreen.class.getName();

	/**
	 * CONSTANTS
	 */
	private final float REFRESH_TIME = 1.0f;
	private final float GARBAGE_PLANETS = 4f;
	private final float GARBAGE_BULLETS = 0.5f;

	private final int THREE_STARS_PCT = 20; // loss percentage to give best
											// grade to the human player
	private final int TWO_STARS_PCT = 60;

	/**
	 * BOOLEANS
	 */
	private boolean introPhase = true;
	private boolean gameFinished = false;
	private boolean first = true;
	private boolean enlargePlanets = true;
	private boolean backButtonOn = false;

	/**
	 * OBJECTS
	 */
	private Timer timer = null;
	private GameInput input = null;
	private ShapeRenderer shapeRenderer = null; // for testing purposes
	private BitmapFont f2 = Assets.instance.assetBaseFonts.defaultNormal;
	private AI ai = null;
	private Statistics statistics = null;
	private GunSight gunSight = null;
	private GameBackground stars = null;
	private GameScreenGui gameScreenGui = null;
	public BoosterAction boosterAction = null;
	private Tutorial tutorial = null;

	private static TweenManager tweenManager = null;
	private InputMultiplexer im = new InputMultiplexer();

	/**
	 * GAME GUI
	 */

	private float timeSinceLastBooster = 0f;

	/** END OF VARIABLES **/

	public GameScreen(Game p_game)
	{
		super(p_game);
		Gdx.app.debug(TAG, "co: " + System.currentTimeMillis());
	}

	private TweenCallback callbackAtBegin = new TweenCallback()
	{

		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			if (type == TweenCallback.COMPLETE)
			{
				if (introPhase)
				{
					GameScreen.this.initGame();
				}

			}

		}
	};

	private void initGame()
	{
		im.addProcessor(getGame().getStage());
		im.addProcessor(input);
		

		Gdx.input.setInputProcessor(im);
		Gdx.input.setCatchBackKey(true);
		introPhase = false;
	}

	private void beginGame()
	{
		getGame().bullets.clear();
		
		getGame().human = new HumanPlayer(Assets.instance.assetLevels.currentLevel.getHumanColor());
		getGame().planets = Assets.instance.assetLevels.currentLevel.getPlanets();

		getGame().planetColors = new ArrayList<String>();

		for (Planet p : getGame().planets)
		{
			if (p.isFightingObject() && ! getGame().planetColors.contains(p.getType().getString()))
			{
				getGame().planetColors.add(p.getType().getString());
			}
		}
		
		timeSinceLastBooster = Util.getInstance().getRandomFloat(0, 30);
		

		//
		// gameScreenGui.newActionMessage(GameScreenGui.STATS_INDICATOR, false);
		// gameScreenGui.newMessage("Test", 2);
		//
		// gameScreenGui.newActionMessage(GameScreenGui.STATS_INDICATOR, true);
	}

	private void placePlanets()
	{

		for (Planet p : getGame().getPlanets())
		{
			tweenManager.killTarget(p);

			float x = p.getCircle().x;
			float y = p.getCircle().y;

			if (p.getCircle().x < Constants.VIEWPORT_WIDTH / 2)
			{
				// left side
				p.setPosition( - p.getCircle().radius + 50, y);
			}
			else
			{
				p.setPosition(Constants.VIEWPORT_WIDTH + p.getCircle().radius + 100, y);
			}

			Tween.to(p, PlanetAccessor.POS_XY, 1.5f).target(x, y).ease(TweenEquations.easeOutCubic).start(tweenManager).setCallback(callbackAtBegin);
		}

	}

	/**
	 * Overridden
	 * 
	 */

	public void init()
	{
		Constants.BULLETS_SLOW_DOWN_FACTOR = Util.getInstance().getRandomFloat(2f, 2.5f);
		
		tweenManager = new TweenManager();
		
		gameScreenGui = new GameScreenGui(this);
		gameScreenGui.init(); // initGui();
		tutorial = new Tutorial(this);
		
		shapeRenderer = new ShapeRenderer();

		
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Planet.class, new PlanetAccessor());

		timer = new Timer();
		timer.addTimer(REFRESH_TIME);
		timer.addTimer(GARBAGE_BULLETS);
		timer.addTimer(GARBAGE_PLANETS);

		SpriteBatchTransparency.getInstance().init(getGame().getBatch());

		

		// intro animation
		beginGame();
		placePlanets();

		input = new GameInput(getGame(), this);
		ai = new AI(this, getGame().getPlanets(), getGame().getBullets());
		statistics = new Statistics(this);
		stars = new GameBackground(this, true);
		gunSight = new GunSight(this);
		boosterAction = new BoosterAction(this);
		
		//Planet p = getGame().planets.get(2);
		
		//boosterAction.addExplosionEffect(p);

	}

	public int getSelectedPlanet(int p_x, int p_y)
	{
		for (int i = 0; i < getGame().getPlanets().size(); ++i)
		{
			if (getGame().getPlanets().get(i).getPower() > 0)
			// needs to be done to avoid freezing the game when (planets with
			// power <= 0 will be removed)
			{
				if (getGame().getPlanets().get(i).getCircle().contains(p_x, p_y) // clicked
																					// right
				// on the circle

				)
				{
					return i;
				}
				else if (getGame().getPlanets().get(i).getCircle().radius < 50) // the
																				// planet
																				// is
				// small so have
				// to take
				// bigger circle
				{
					Circle tmp = new Circle(getGame().getPlanets().get(i).getCircle().x, getGame().getPlanets().get(i).getCircle().y, 50);

					if (tmp.contains(p_x, p_y)) return i;
				}
			}
		}

		return - 1;
	}

	public boolean isBulletOnScreen(Bullet b)
	{
		return false;
	}

	/**
	 * Is planet overlapping with another planet.
	 * 
	 * @param p_planet
	 * @return
	 */
	private boolean isPlanetOverlapping(Planet p_planet)
	{
		for (Planet p : getGame().getPlanets())
		{
			if (p_planet != p && p_planet.getCircle().overlaps(p.getCircle()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Updates end time for a shot (and therefore shot power) only when the
	 * players holds the planet
	 */
	private void checkShotPowerIncrease()
	{
		if (input.isPreparingShot())
		{
			if (input.isPressingSourcePlanet() && input.getSourcePlanet().isPressed())
			{
				input.getSourcePlanet().updateEndTime();

			}
		}
	}

	private void updateActors(float delta)
	{
		for (Planet p : getGame().getPlanets())
		{
			if (p.isVisible())
			{
				p.update(delta);
			}
		}

		for (Bullet b : getGame().getBullets())
		{
			if (b.isVisible())
			{
				b.update(delta);
			}
		}
	}

	public void update(float delta)
	{
		//Gdx.app.debug(TAG, "update - start");
		
		tweenManager.update(delta);
		
		//Gdx.app.debug(TAG, "update - after tween");
		
		updateActors(delta);
		
		//Gdx.app.debug(TAG, "update - after updateActors");

		if (introPhase) return;

		timer.update(delta);
		
	//	Gdx.app.debug(TAG, "update - timerUpdate");

		checkShotPowerIncrease();
		//Gdx.app.debug(TAG, "update - checkShotPowerIncrease");
		
		stars.update(delta);
		
	//	Gdx.app.debug(TAG, "update - starsUpdate");

		if (timer.isFinished(REFRESH_TIME))
		{
			if (enlargePlanets)
			{
				for (Planet p : getGame().getPlanets())
				{
					float growth = 0f;
					
					if (getCurrentLevel().getPlanetGrowthFactorMax() < 0 || getCurrentLevel().getPlanetGrowthFactorMin() < 0)
					{
						if (Constants.ENLARGE_WEAK_PLANETS_FASTER && 
								(p.getPower() >= Constants.MIN_PLANET_POWER && p.getPower() < Constants.MIN_PLANET_POWER_TO_SHOOT))
							growth = 2f;
						else
							growth = 0f;
					}
					else
						growth = getGrowthFactor(p);
					
					if (p.getType().getColor() != getGame().getHumanPlayer().getHumanType().getColor())
					{
						statistics.addEnemyEnlargement(growth);
					}
					
					enlargePlanet(p, growth);
				}
			}
		//	Gdx.app.debug(TAG, "update - enlargePlanets");

			statistics.compute();
			//Gdx.app.debug(TAG, "update - statisticsCompute");
			checkEndGameConditions();
		//	Gdx.app.debug(TAG, "update - checkEndGameCOnd");

			
			ai.grade();
		//	Gdx.app.debug(TAG, "update - ai Grade");
		}
		
		// check if it's time to show booster
					showBooster(delta);
			//		Gdx.app.debug(TAG, "update - showBooster");

		if (timer.isFinished(GARBAGE_BULLETS))
		{
			removeDestroyedBullets();
		//	Gdx.app.debug(TAG, "update - removeDestr Bull");
		}

		if (timer.isFinished(GARBAGE_PLANETS))
		{
			removeDestroyedPlanets();
			
		//	Gdx.app.debug(TAG, "update - remove Dest Pla");
		}

		checkForHits();
	//	Gdx.app.debug(TAG, "update - check for hits");
		updateBoosters(delta);
	//	Gdx.app.debug(TAG, "update - update boosters");
		
		tryStoppingEnlargement();
	//	Gdx.app.debug(TAG, "update - try stopping enlargement");
		
		killPlanets();
		
		updateBullets(delta);
		
		//Gdx.app.debug(TAG, "update - killplanets");

		gameScreenGui.update(delta);
		
		//Gdx.app.debug(TAG, "update - gamescreengui update");

		//if ( ! gameFinished)
		{
			ai.act(delta);
			
		//	Gdx.app.debug(TAG, "update - ai act");
		}
		
		//Gdx.app.debug(TAG, "update - end");
	}
	
	private void updateBullets(float p_delta)
	{
		// power of bullets deceases
		
		for (Bullet b : getGame().bullets)
		{
			if (b.isVisible() && b.getPower() > /*Constants.AI_MIN_BULLET_SIZE*/ 2f)
			{
				// decrease
				float value = Constants.BULLETS_SLOW_DOWN_FACTOR * p_delta;
				b.setPower(b.getPower() - value);
			}
		}
	}
	
	private void killPlanets()
	{
		for (Planet p : getGame().planets)
		{
			if (p.isVisible() && p.getPower() < Constants.MIN_PLANET_POWER_TO_SURVIVE)
			{
				p.setVisible(false);
				Gdx.app.debug(TAG, "Visibility of " + p + " to false, because power (" + p.getPower() + ") < " + Constants.MIN_PLANET_POWER_TO_SURVIVE);
						
				planetExplosion(p, 0 /* does not matter*/);
			}
		}
	}
	
	private void tryStoppingEnlargement()
	{
		for (Planet p : getGame().planets)
		{
			// stepSize > 0 means it only works when shooting to own planets
			if (p.isEnlargin() && p.getStepSize() > 0)
			{
				if (isPlanetOverlapping(p) && p.canEnlarge())
				{
					p.resetEnlargementWithoutDestSize();
					p.refresh();
				}
			}
		}
	}

	private void updateBoosters(float p_delta)
	{
		for (Map.Entry<String, BoosterUserData> d : boosterAction.boosterData.data.entrySet())
		{

			for (Map.Entry<String, Float> e : boosterAction.boosterData.data.get(d.getKey()).timeLeft.entrySet())
			{
				// subtract time on every active booster
				if (e.getValue() >= 0)
				{
					e.setValue(e.getValue() - p_delta);

					// check if we can turn off booster
					if (e.getValue() < 0)
					{
						boosterAction.boosterData.data.get(d.getKey()).turnOffBooster(e.getKey());
					}
				}
			}
		}

		for (Planet p : getPlanets())
		{

			// only boosters, please
			if ( ! p.isFightingObject())
			{
				p.timeVisible -= p_delta;

				if (p.timeVisible <= Constants.BOOSTER_BLINKING_THRESHOLD && p.timeVisible > 0)
				{
					// blink
					p.blinkTime += p_delta;

					if (p.blinkTime > Constants.BOOSTER_BLINKING_FREQUENCY)
					{
						p.setVisible( ! p.isVisible());
						p.blinkTime = 0;
					}
				}

				if (p.timeVisible <= 0)
				{
					p.setPower(0);
					p.setVisible(false);
				}
			}
			else
			{
				// planets blink when they're weak
				
				if (p.getPower() < Constants.MIN_PLANET_POWER_TO_SHOOT && p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE)
				{
					p.blinkTime += p_delta;
					
					if (p.blinkTime > Constants.BOOSTER_BLINKING_FREQUENCY)
					{
						p.setVisible( ! p.isVisible());
						p.blinkTime = 0;
					}
				}
			}
		}
		
		boosterAction.updateBoosterEffects(p_delta);
	}

	private void showBooster(float p_delta)
	{
		//timeSinceLastBooster += REFRESH_TIME;
		timeSinceLastBooster += p_delta;
		
		float max = 60;
		float min = 1;
		float baseWaitTime = (max - min) / 100 * (Assets.instance.assetLevels.currentLevel.getBoosterFreq()) + min;

		// randomize a bit
		int r = Math.round(baseWaitTime * 0.1f);
		if (r <= 1)
			r = 2;
		float timeDiff = baseWaitTime + getGame().random.nextInt(r); 
																			

		if (Assets.instance.assetLevels.currentLevel.getBoosterFreq() > 0) // check if boosters are turned on
		{
			if (timeSinceLastBooster > timeDiff)
			{
				// booster time
				// draw new booster

				timeSinceLastBooster = 0f;

				boosterAction.getNewBooster(getGame().planets);
			}
		}
	}

	private float getPlanetEnlargementFactor(Planet p_planet)
	{
		if (p_planet.isFightingObject()) return boosterAction.boosterData.data.get(p_planet.getType().getString()).planetEnlargementFactor;

		return 1.0f;
	}

	private float getGrowthFactor(Planet p_planet)
	{
		// enlarge weak planets at a maximum growth level
		if (Constants.ENLARGE_WEAK_PLANETS_FASTER && 
				(p_planet.getPower() >= Constants.MIN_PLANET_POWER && p_planet.getPower() < Constants.MIN_PLANET_POWER_TO_SHOOT))
			return getCurrentLevel().getPlanetGrowthFactorMax();
		
		// if they're not weak, it's standard
		
		return (getCurrentLevel().getPlanetGrowthFactorMax() - getCurrentLevel().getPlanetGrowthFactorMin()) * (p_planet.getPower() / 100) + getCurrentLevel().getPlanetGrowthFactorMin() * getPlanetEnlargementFactor(p_planet);
	}
	
	public LevelData getCurrentLevel()
	{
		return Assets.instance.assetLevels.currentLevel;
	}

	private int getHumanPlayerGrade()
	{
		// A) Percentage of Alive Human Planets
		
		statistics.getHumanPlanetsAtGameOver();
		float a = statistics.getHumanPlanetsPct();
		a = MathUtils.clamp(a, 0, 1);
		
		Gdx.app.debug(TAG, "A: " + a);
		
		// B) Percentage of enemy growth
		
		float enemyPowerStart = statistics.getEnemyPowerStart();
		float enemyEnlargementSum = statistics.getEnemyEnlargementSum();
		float growthAvg = (getCurrentLevel().getPlanetGrowthFactorMax() + getCurrentLevel().getPlanetGrowthFactorMin()) / 2f;
		float enemyEnlargementNormalized = enemyEnlargementSum / growthAvg;
		
		Gdx.app.debug(TAG, "enemy pow: " + enemyPowerStart
				+ " enlar: " + enemyEnlargementSum
				+ " growthAvg: "+ growthAvg
				+ " enl Norm: " + enemyEnlargementNormalized
				);
		
		float b = enemyEnlargementNormalized / enemyPowerStart;
		b = MathUtils.clamp(b, 0, 2);
		
		// consider how many enemy planets were at the beginning
		float enemyPlanets = MathUtils.clamp(statistics.getEnemyPlanetsStart(), 0, 10);
		float enemyCountFactor = (1f - 0f) / 10 * enemyPlanets + 0f;
		Gdx.app.debug(TAG, "enemyPlanets: + " + enemyPlanets + " EnemyCountFactor: " + enemyCountFactor);
		b = b - enemyCountFactor;
		
		b = b * 0.5f;
		
		
		
		b = 1 - b;
		
		Gdx.app.debug(TAG, "B: " + b);
		
		// C) Player health loss
		
		float c = statistics.getPlayerHealthLoss() / statistics.getHumanPowerStart();
		Gdx.app.debug(TAG, "Pure C: " + c);
		c = MathUtils.clamp(c, 0, 1);
		c = 1 - c;
		//float lossPct = (100 - loss) / 100f;
		
		Gdx.app.debug(TAG, "C: " + c);
		
		// D) Human power ratio
		
		statistics.computeHumanPowerEnd();
		float d = statistics.getHumanPowerRatio();
		d = MathUtils.clamp(d, 0, 1);
		
		Gdx.app.debug(TAG, "D: " + d);
		
		
		float E = (a + b + c + d) / 4f;
		
		float levelId = getCurrentLevel().getLevelId();
		float levelFactor = (1.2f - 0.9f) / 60f * levelId + 1.0f;
		float Eacc = E * levelFactor;
		
		Gdx.app.debug(TAG, "E: " + E + " level: " + levelId + " levelFactor: " + levelFactor + " Eacc: " + Eacc);
		
		
		//Gdx.app.debug(TAG, "GRADE: " + grade);
		
		//return 1;
		
		if (Eacc > 0.6)
			return 3;
		else if (Eacc > 0.4)
			return 2;
		else
			return 1;
		
//		if (grade <= 0.4)
//			return 3;
//		else if (grade <= 0.65)
//			return 2;
//		else
//			return 1;
		
		//
		//float loss = statistics.getPlayerHealthLoss();
		
		//float lossPct = (100 - loss) / 100f;
		
		//float grade = pct * lossPct;
		
		//Gdx.app.debug(TAG, "GRADE PCT: " + grade);
		
		
		
		/*
		float loss = statistics.getPlayerHealthLoss();

		if (loss <= THREE_STARS_PCT) // human player lost 20% or less of his
										// initial power
		{
			return 3; // best mark
		}
		else if (loss <= TWO_STARS_PCT)
		{
			return 2;
		}
		else
		{
			return 1;
		}*/
	}

	private void checkEndGameConditions()
	{
		if ( ! gameFinished)
		{
			//String winner = statistics.whoWon(true);
			//Gdx.app.debug(TAG, "won: " + winner);
			//if (winner != null)
			if (statistics.isEndGame(true))
			{
				
				// end of game
				gameFinished = true;
				
				String winner = statistics.getWinner();

				if (winner != null && winner.equals(getGame().getHumanPlayer().getHumanType().getString()))
				{
					// human player won

					getInputMultiplexer().removeProcessor(getInput()); // remove
																		// player's
																		// control

					// give mark (1-3)

					int grade = getHumanPlayerGrade();
					gameScreenGui.addStars(grade);

					Tween.to(gameScreenGui.getWinTable(), PlanetAccessor.POS_XY, 1.0f).target(0, 0).ease(TweenEquations.easeOutCubic).delay(0.5f)
							.start(tweenManager);

					Assets.instance.assetLevels.saveLevelAsPassed(grade);
				}
				else
				{

					getInputMultiplexer().removeProcessor(getInput());

					// computer won
					Tween.to(gameScreenGui.getLoseTable(), PlanetAccessor.POS_XY, 1.0f).target(0, 0).ease(TweenEquations.easeOutCubic).delay(0.5f)
							.start(tweenManager);
				}

				Gdx.app.debug(TAG, "HUMAN LOSS: " + statistics.getPlayerHealthLoss());

			}
		}
	}

	public void enlargePlanet(Planet p, float p_factor)
	{
		if (p.isVisible() && p.getPower() > 0 && p.canEnlarge() && ! isPlanetOverlapping(p) && p.isFightingObject())
		{		
			p.resetEnlargement();
			p.enlargeSmoothly(p_factor, 0.2f);
		}
	}
	
	public void enlargePlanetBooster(Planet p, float p_factor)
	{
		if (p.isVisible() && p.getPower() > 0 && p.canEnlarge() && ! isPlanetOverlapping(p) && p.isFightingObject())
		{
			float plus = MathUtils.clamp(p_factor / 100 * p.getPower(), 10, 25);
			Gdx.app.debug(TAG, "Enlarging " + p.getPower() + " for: " + plus);
			float newSize = p.getPower() + plus;
			Tween.to(p, PlanetAccessor.POWER, 1f).target(newSize).ease(TweenEquations.easeOutCubic).start(tweenManager);
		}
	}
	
	public Sprite getPlanetSprite(Planet p, int size)
	{
		return Assets.instance.planets.sprites.get(p.getType().getString() + String.valueOf(size));
	}

	// BOOSTERS STUFF
	public void shootBiggestEnemy(Planet p_sourcePlanet, float p_shotPower)
	{
		Planet planetToShoot = null;

		for (Planet p : getGame().planets)
		{

			if (p.getType().getColor() != p_sourcePlanet.getType().getColor() && p.isFightingObject())
			{
				if (planetToShoot == null)
					planetToShoot = p;
				
				if (p.getPower() > planetToShoot.getPower()) 
					planetToShoot = p;
			}
		}

		if (planetToShoot != null && planetToShoot.getType().getColor() != p_sourcePlanet.getType().getColor())
		{
			Sprite s = getPlanetSprite(planetToShoot, 300);
			
			if (s != null)
			{
				boosterAction.addExplosionEffect(planetToShoot, EFFECT_EXPLOSION_TYPE.BOMB, s, 2f);
				Gdx.app.debug(TAG, "sniper " + p_sourcePlanet + " SHOT " + planetToShoot + " NEW POWER: " + (planetToShoot.getPower() - p_shotPower));
			}
			
			//planetToShoot.resetEnlargement();
			//planetToShoot.setPower(planetToShoot.getPower() - p_shotPower);
			float newPower = planetToShoot.getPower() - p_shotPower;
			Tween.to(planetToShoot, PlanetAccessor.POWER, 0.2f).target(newPower).ease(TweenEquations.easeOutCubic).start(tweenManager);
			
			planetToShoot = null;
		}
	}

	public void bomb(Planet p_booster, float p_factor)
	{
		boosterAction.addExplosionEffect(p_booster, EFFECT_EXPLOSION_TYPE.BOMB, Assets.instance.masks.white300, 3f);
		
		for (Planet p : getGame().planets)
		{
			if (p.isFightingObject())
			{
				float distance = (float) Math.sqrt(Math.pow(p.getCenterX() - p_booster.getCenterX(), 2)
						+ Math.pow(p.getCenterY() - p_booster.getCenterY(), 2));

				float maxDistance = Constants.VIEWPORT_WIDTH / 2;
				float po = (maxDistance - distance) / maxDistance * p_factor / 100;
				float ptsTaken = MathUtils.clamp(p.getPower() * po, 3, 25);

				//p.setPower(p.getPower() - ptsTaken);
				Tween.to(p, PlanetAccessor.POWER, 0.2f).target(p.getPower() - ptsTaken).ease(TweenEquations.easeOutCubic).start(tweenManager);
				
			}
		}
	}

	private void planetExplosion(Planet p_p, float p_pointsTaken)
	{
		Gdx.app.debug(TAG, "try planet explosion: " + p_p + " pointstaken: " + p_pointsTaken);
		
		// p_pointsTaken is already negative
		//if (p_p.getPower() + p_pointsTaken >= Constants.MIN_PLANET_POWER_TO_SURVIVE) return;
		
		Sprite s = getPlanetSprite(p_p, 300);
		
		if (s != null)
		{
			//p_p.setVisible(false); -- cant be here, something wrong with Statistics#whoWon
			
			Gdx.app.debug(TAG, "Explode planet " + p_p);
			boosterAction.addExplosionEffect(p_p, EFFECT_EXPLOSION_TYPE.BOMB, s, 5f);		
		}
		else
		{
			Gdx.app.debug(TAG, "No sprite for explosion");
		}
	}
	
	// -------------------------------------

	private void checkForHits()
	{

		for (Planet p : getGame().getPlanets()) // planets + boosters
		{
			for (Bullet b : getGame().getBullets()) // bullets
			{
				if (p.getPower() > 0 && b.isVisible())
				{

					if (b.getCircle().overlaps(p.getCircle()))
					{

						// cannot hit itself && can hit only target
						if (b.getSource() != p && b.getTarget() == p)
						{
							// BOOSTER!
							if ( ! p.isFightingObject() && p.timeVisible > 0)
							{
								p.setPower(0);
								p.setVisible(false);
								b.destroy();

								boosterAction.act(p, b.getSource());
							}
							else if (/*p.isVisible() &&*/ p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE && b.getSource().getType().getColor() == p.getType().getColor())
							{
								// friendly hit

								 
								if (p.canEnlarge() && ! isPlanetOverlapping(p))
								{
									p.setVisible(true);
									p.resetEnlargement();
									p.enlargeSmoothly(b.getPower(), 0.2f);

								}
								b.destroy();

							}
							else if (/*p.isVisible()*/ p.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE)
							{
								p.setVisible(true);
								
								// enemy hit
								

								p.resetEnlargement();
							
								// make sure that the planet is left with at least 5 hp
								float enemyPower = p.getPower() - b.getPower();
								
								Gdx.app.debug(TAG, "ENEMY HIT " + p + " with bullet_power: " + b.getPower());
								
								// check if DEAD
								if ((p.getPower() - b.getPower()) < Constants.MIN_PLANET_POWER_TO_SURVIVE)
								{
									// DEAD
									
								}
								p.enlargeSmoothly( - b.getPower(), 0.2f);
							
//								if (enemyPower > 0 && enemyPower < Constants.MIN_PLANET_POWER)
//								{
//									p.enlargeSmoothly( - (p.getPower() - Constants.MIN_PLANET_POWER), 0.2f);
//									//Gdx.app.debug("POWER", enemyPower + " " + String.valueOf("FIVE"));
//								}
//								else
								{
									
									//Gdx.app.debug("POWER", String.valueOf(enemyPower));
								}
								

								// Human player hit by enemy computer player
								if (p.getType().getString().equals(getGame().getHumanPlayer().getHumanType().getString()))
								{
									statistics.addPlayerHealthLoss(b.getPower());
								}

								
								b.destroy();

								statistics.compute();
								checkEndGameConditions();
							}
						}

					}
				}
			}
		}

		if (boosterAction.planetToAdd != null)
		{
			getGame().planets.add(boosterAction.planetToAdd);
			boosterAction.planetToAdd = null;
		}
	}

	/**
	 * Draws line showing how strong the shot is
	 */
	private void drawShotPowerIndicator()
	{
		// Show Power Indicator
		if (input.isPreparingShot())
		{
			float pct = input.getShotPowerPercentage();
			Sprite s = Assets.instance.gui.sprites.get("shot_power_" + getGame().getHumanPlayer().getHumanType().toString());

			int pixels = Math.round(Constants.VIEWPORT_WIDTH * pct);

			for (int i = 0; i < pixels; i += 2)
			{
				getGame().getBatch().draw(s, i, 0/*Constants.VIEWPORT_HEIGHT - s.getHeight()*/);
			}
		}
	}
	
	private void backButtonAction()
	{
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			
			if (gameScreenGui.getPauseTable() != null && ! backButtonOn)
			{
				Gdx.app.debug(TAG, "Back button");
				gameScreenGui.pauseButtonAction(gameScreenGui.getPauseTable());
			}
			backButtonOn = true;
		}
		else
		{
			backButtonOn = false;
		}
	}

	@Override
	public void render(float delta)
	{
		backButtonAction(); // has to be there as it cannot be in update()
		
		getGame().getBatch().begin();

		stars.render(getGame().getBatch());

		gunSight.render(delta);

		for (Planet p : getGame().getPlanets())
		{

			p.render(getGame().getBatch());

		}

		for (Bullet b : getGame().getBullets())
		{
			if (b.isVisible())
			{
				b.render(getGame().getBatch());
			}
		}
		
		boosterAction.renderBoosterEffects(getGame().getBatch());

		if (Constants.DEBUG)
		{
			f2.draw(getGame().getBatch(), String.valueOf(Gdx.graphics.getFramesPerSecond()), 10, 20);
		}

		drawShotPowerIndicator();

		if ( ! gameFinished && gameScreenGui.isShowingStatsIndicator())
		{
			statistics.render(delta);
		}
		
		// draw arrows
//		for (Planet p : getGame().planets)
//		{
//			Sprite s = Assets.instance.gui.sprites.get("arrow3");
//			
//			getGame().getBatch().draw(s, p.getCenterX() - p.getComputedSize() / 2, p.getCenterY() - p.getComputedSize() / 2, 0, 0, s.getWidth(), s.getHeight(), 1, 1, 0);
////			
////			getGame().getBatch().draw(s.getTexture(), 110f, // x
////					110f, // y
////					s.getWidth() / 2f, // origin x
////					s.getHeight() / 2f, // origin // y
////					s.getWidth(), // width
////					s.getHeight(),// height
////					1f, // scale x
////					1f, // scale y
////					2f, 0, 0, Math.round(s.getWidth()), Math.round(s.getHeight()), false, false); // rotation
////			
//			//p.render(batch)
//		}

		getGame().getBatch().end();

		if (Constants.DEBUG_GUI)
		{
			Table.drawDebug(getGame().getStage());
		}
			
		// STAGE
		getGame().getStage().act(delta);
		getGame().getStage().draw();

		if (first)
		{
			for (Planet p : getGame().getPlanets())
			{
				p.setVisible(true);
			}
			first = false;
		}
	}

	public void removeDestroyedPlanets()
	{
		for (int i = 0; i < getGame().getPlanets().size(); ++i)
		{
			if ( ! getGame().getPlanets().get(i).isVisible() && getGame().getPlanets().get(i).getPower() < Constants.MIN_PLANET_POWER_TO_SURVIVE)
			{
				getGame().getPlanets().get(i).resetEnlargement();
				Gdx.app.debug(TAG, getGame().getPlanets().get(i) + " : planet removed");
				getGame().getPlanets().remove(i);
			}
		}
	}

	private void removeDestroyedBullets()
	{
		for (int i = 0; i < getGame().getBullets().size(); ++i)
		{
			// remove those which are invisible
			if ( ! getGame().getBullets().get(i).isVisible())
			{
				getGame().getBullets().remove(i);
				break;
			}

			// remove those out of the screen

			float x = getGame().getBullets().get(i).getXPos();
			float y = getGame().getBullets().get(i).getYPos();
			if (x > Constants.VIEWPORT_WIDTH || x < 0 || y < 0 || y > Constants.VIEWPORT_HEIGHT)
			{
				getGame().getBullets().remove(i);
				Gdx.app.debug(TAG, "BULLET REMOVED (out of the screen)");
			}
		}
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
//		if (gameScreenGui != null)
//		{
//			Gdx.app.debug(TAG, "Pause gameScreen");
//			gameScreenGui.pauseButtonAction(gameScreenGui.getPauseTable());
//		}
	}

	@Override
	public void resume()
	{
		Gdx.app.debug(TAG, "Resumed");
	}

	@Override
	public void dispose()
	{
	}

	/** Getters/Setters */

	public Planet getPlanet(int p_id)
	{
		return getGame().getPlanets().get(p_id);
	}

	public Bullet getBullet(int p_id)
	{
		return getGame().getBullets().get(p_id);
	}

	public void addBullet(Bullet p_bullet)
	{
		getGame().getBullets().add(p_bullet);
	}

	public ArrayList<Planet> getPlanets()
	{
		return getGame().getPlanets();
	}

	public GameScreenGui getGui()
	{
		return gameScreenGui;
	}

	public GameInput getInput()
	{
		return input;
	}

	public TweenManager getTweenManager()
	{
		return tweenManager;
	}

	public boolean isGameFinished()
	{
		return gameFinished;
	}

	public void setGameFinished(boolean p_gameFinished)
	{
		gameFinished = p_gameFinished;
	}

	public InputMultiplexer getInputMultiplexer()
	{
		return im;
	}

	public Statistics getStats()
	{
		return statistics;
	}
	
	public boolean isEnlargingPlanets()
	{
		return enlargePlanets;
	}
	
	public void setEnlargePlanets(boolean b)
	{
		enlargePlanets = b;
	}

}
