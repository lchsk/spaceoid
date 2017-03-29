package com.pigletlogic.spaceoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pigletlogic.gui.Text;
import com.pigletlogic.screens.GameScreen;
import com.pigletlogic.spaceoid.types.PlanetType;
import com.pigletlogic.util.CameraHelper;
import com.pigletlogic.util.Constants;

public class GameInput implements InputProcessor
{
	private static final String TAG = GameInput.class.getName();

	private Game game = null;
	private GameScreen gameScreen = null;

	private int lastPlanet = - 1;
	private int planetSourceId = - 1;
	// private long start = 0;
	// private long end = 0;

	private Vector2 vectorStart;
	private Vector2 vectorEnd;

	private int planetTargetId = - 1;

	private long diff = 0;

	private int targetX = - 1;
	private int targetY = - 1;

	private Planet planetSource = null;
	private Planet planetTarget = null;

	private Vector2 vectorDiff = null;
	private Vector2 direction = null;
	private Vector2 path = null;

	private float power = 0;

	private int mouseX = 0;
	private int mouseY = 0;

	public GameInput(Game p_game, GameScreen p_screen)
	{
		super();
		game = p_game;
		gameScreen = p_screen;
	}

	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button)
	{
		if (tryShoot(x, y, pointer, button)) return false;

		planetSourceId = gameScreen.getSelectedPlanet(x, (int) CameraHelper.convertY(y));
		Gdx.app.debug(TAG, "[touchDown] planetSourceId = " + planetSourceId);

		if (planetSourceId >= 0)
		{
			planetSource = gameScreen.getPlanet(planetSourceId);
			
			if (planetSource != null && planetSource.getPower() < Constants.MIN_PLANET_POWER_TO_SHOOT)
			{
				Gdx.app.debug(TAG, "Planet " + planetSource + " too weak... Cannot shoot");
				gameScreen.boosterAction.addMessage(planetSource, Text.too_weak_cannot_shoot, 2f);
				reset();
				return true;
			}
				
			planetSource.startPressing(); // start = System.currentTimeMillis();

			planetSource.setPressed(true);

			// check if it's player's planet
			if (planetSource.getType().getColor() != gameScreen.getGame().getHumanPlayer().getHumanType().getColor()
					&& /* risky change pt. 2 */planetSource.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE)
			{
				planetSource.resetPressing(); // start = 0;
				planetSourceId = - 1;
				return true;
			}

			vectorStart = new Vector2(planetSource.getCircle().x, // CameraHelper.convertY
					(planetSource.getCircle().y));

			if (lastPlanet == planetSourceId)
			{
				Gdx.app.debug(TAG, "SAME");
			}

			lastPlanet = planetSourceId;

			return true;
		}
		else
		{
			
			reset();
		}

		return true;
	}

	/**
	 * 
	 * @return true if errors found
	 */
	private boolean validateTouchUp()
	{
		planetTargetId = gameScreen.getSelectedPlanet(targetX, targetY);

		if (planetSourceId < 0 /*&& start <= 0*/)
		{
			return true;
		}

		if (planetTargetId == - 1 || planetTargetId == planetSourceId)
		{
			// didnt click on the planet - exit
			return true;
		}

		// no problems
		return false;
	}

	private void getTouchUpData()
	{
		planetTarget = gameScreen.getPlanet(planetTargetId);

		if (/*planetTarget.visible*/planetTarget.getPower() >= Constants.MIN_PLANET_POWER_TO_SURVIVE) // risky
																	// change
		{

			if (planetTarget.getType().getColor() == planetSource.getType().getColor())
			{
				// own planet
				planetTarget.showMaskForSomeTime(.5f, PlanetType.MASK_WHITE, 0.5f);
			}
			else
			{
				// enemy
				planetTarget.showMaskForSomeTime(.5f, PlanetType.MASK_RED, 0.5f);
			}

			planetSourceId = lastPlanet = - 1;

			planetSource.endPressing();
			diff = planetSource.getPressingDiff();

			planetSource.resetPressing();

			vectorEnd = new Vector2(planetTarget.getCircle().x, // CameraHelper.convertY
					(planetTarget.getCircle().y));

			vectorDiff = vectorEnd.sub(vectorStart);
			direction = vectorDiff.nor();
		}
		else
		{
			reset();
		}
	}

	private void reset()
	{
		if (planetSource != null)
		{
			planetSource.resetDiff();
		}
		
		planetSource = null;
		planetTarget = null;
		planetSourceId = - 1;
		planetTargetId = - 1;
	}

	private boolean computePowerAndPath()
	{
		try
		{

			float actualPower = planetSource.computePower();
			actualPower = MathUtils.clamp(actualPower, Constants.AI_MIN_BULLET_SIZE, Math.min(Constants.AI_MAX_BULLET_SIZE, planetSource.getPower() / 2));
			
			// booster
			float bulletFactor = gameScreen.boosterAction.getBulletSizeFactor(gameScreen.getGame().getHumanPlayer().getHumanType().toString());
			
			Gdx.app.debug("bullet factor:", String.valueOf(bulletFactor));
			power = actualPower * bulletFactor;
			power = MathUtils.clamp(power, Constants.AI_MIN_BULLET_SIZE, Math.min(Constants.AI_MAX_BULLET_SIZE, planetSource.getPower() / 2));
			// ---
			
			// validate
			if (planetSource.getPower() <= Constants.MIN_PLANET_POWER_TO_SHOOT)
			{
				gameScreen.boosterAction.addMessage(planetSource, Text.too_weak_cannot_shoot, 2f);
				
				return false;
			}
			
		//	power = 5;

			path = new Vector2(direction.x, direction.y);
			
			float screenSizeFactor = Constants.VIEWPORT_GUI_HEIGHT * 1f / 480f;
			//screenSizeFactor = 1f;

			float factor = Constants.SPEED_FACTOR / power * screenSizeFactor;
			Gdx.app.debug(TAG, "screenSizeFactor: " + screenSizeFactor);
					/** gameScreen.boosterAction.boosterData.data.get(planetSource.getType().getString()).bulletSpeedFactor*/;
			path.x = path.x * factor;
			path.y = path.y * factor;
			
			Gdx.app.debug(TAG, "human bulletspeed: " + path);

			planetSource.resetEnlargement();
			planetSource.enlargeSmoothly( - actualPower, 0.2f);
			
			return true;
		}
		catch (Exception e)
		{
			reset();
			return false;
		}

	}

	public float getShotPowerPercentage()
	{

		return planetSource.getPowerPercentage();
	}

	public boolean isPreparingShot()
	{
		return (planetSource != null && planetSource.getType().getColor() == gameScreen.getGame().getHumanPlayer().getHumanType().getColor());
	}

	public boolean isPressingSourcePlanet()
	{
		if (planetSource == null)
			return false;
		else
		{
			return planetSource.getCircle().contains(Gdx.input.getX(), CameraHelper.convertY(Gdx.input.getY()));
		}
	}

	private boolean tryShoot(int x, int y, int pointer, int button)
	{
		try
		{
			if (planetSource == null) return false;

			//planetSource.setPressed(false);

			targetX = x;
			targetY = Math.round(CameraHelper.convertY(y));

			if (validateTouchUp())
			{
				
				// problems -> end
				reset();
				
				planetSource = null;
				planetTarget = null;
				return false;
			}

			getTouchUpData();
			
			if ( ! computePowerAndPath())
			{
				reset();
				return false;
			}

			float centerX = planetSource.getCircle().x;
			float centerY = planetSource.getCircle().y;
			
			Gdx.app.debug(TAG, planetSource + " is shooting (" + power + ")");

			Bullet bullet = new Bullet(new PlanetType(planetSource.getType().getColor()), planetSource, planetTarget, Math.round(centerX),
					Math.round(centerY), path.x, path.y, power);

			planetSource.moveBulletOutside(bullet);

			gameScreen.addBullet(bullet);

			reset();

			return true;
		}
		catch (Exception e)
		{

			Gdx.app.error(TAG, "Exception in touchUp: " + e.getMessage());

			return false;
		}
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button)
	{
		if (planetSource != null)
		{
			planetSource.setPressed(false);
			planetSource.endPressing();
		}
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer)
	{
		return true;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{

		return true;
	}

	public Planet getSourcePlanet()
	{
		return planetSource;
	}
}