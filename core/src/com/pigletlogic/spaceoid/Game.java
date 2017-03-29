package com.pigletlogic.spaceoid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import sun.rmi.runtime.Log;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.pigletlogic.screens.EmptyScreen;
import com.pigletlogic.screens.IntroScreen;
import com.pigletlogic.spaceoid.sfx.SoundEffects;
import com.pigletlogic.util.Constants;

public class Game implements Disposable
{
	private static final String TAG = Game.class.getName();

	private OrthographicCamera camera = null;
	private OrthographicCamera cameraGUI = null;
	private SpriteBatch batch = null;
	private Stage stage = null;
	private EmptyScreen currentScreen = null;
	private HashMap<String, EmptyScreen> screens = null;
	private SoundEffects sound = null;
	private Timer timer = null;

	public List<Level> levels = null;

	public ArrayList<Planet> planets = new ArrayList<Planet>();
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();

	// list of colors of the planets (at the beginning of the game)
	public ArrayList<String> planetColors = null;

	public HumanPlayer human = null;
	public Spaceoid baseClass = null;

	public Preferences settings = null;

	public Random random = null;

	private final float SONG_REFRESH = 1.5f;

	public Game(Spaceoid p_spaceoid)
	{
		baseClass = p_spaceoid;
		screens = new HashMap<String, EmptyScreen>();
		random = new Random();

		if (Constants.DEBUG)
		{
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}
		else
		{
			Gdx.app.setLogLevel(Application.LOG_NONE);
		}
	}

	public void initIntro()
	{
		batch = new SpriteBatch();
		SpriteBatchTransparency.getInstance().init(batch);
		camera = new OrthographicCamera(800, 480);
		camera.position.set(0, 0, 0);
		camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(false); // flip y-axis
		cameraGUI.update();

		stage = new Stage();
		sound = new SoundEffects(this);

		addScreen("intro_screen", new IntroScreen(this));

		currentScreen = getScreen("intro_screen");

		currentScreen.init(); // stage != null

		init();
	}

	public void setCurrentScreen(EmptyScreen p_screen)
	{
		currentScreen = p_screen;
	}

	public EmptyScreen getCurrentScreen()
	{
		return currentScreen;
	}

	public void init()
	{
		timer = new Timer();
		timer.addTimer(SONG_REFRESH);

		settings = Gdx.app.getPreferences("SETTINGS");

	}

	public void loadLevel()
	{
		try
		{
			Assets.instance.assetLevels.loadLevelData(Assets.instance.assetLevels.currentLevel.getLevelId() - 1);
			
		}
		catch (Exception e)
		{
			Gdx.app.error(TAG, "Level " + Assets.instance.assetLevels.currentLevel.getLevelId() + " not found");
		}
	}

	public void update(float delta)
	{
		timer.update(delta);

		if (timer.isFinished(SONG_REFRESH))
		{
			sound.updateMusic();
		}

		currentScreen.update(delta);

	}

	public void render(float delta)
	{
		currentScreen.render(delta);

	}

	public void resize(int width, int height)
	{
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / (float) height) * (float) width;
		camera.update();
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float) height) * (float) width;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();
	}

	public void log(Exception e)
	{
		if (Constants.DEBUG)
		{
			String msg = e.getMessage();

			StackTraceElement[] a = e.getStackTrace();

			for (int i = 0; i < a.length; ++i)
			{
				msg += a[i].toString();
			}
		}
	}

	@Override
	public void dispose()
	{
		batch.dispose();

	}

	public SpriteBatch getBatch()
	{
		return batch;
	}

	/**
	 * Accessors
	 */
	public ArrayList<Planet> getPlanets()
	{
		return planets;
	}

	public ArrayList<Bullet> getBullets()
	{
		return bullets;
	}

	public HumanPlayer getHumanPlayer()
	{
		return human;
	}

	public Stage getStage()
	{
		return stage;
	}

	public void addScreen(String p_identifier, EmptyScreen p_screen)
	{
		screens.put(p_identifier, p_screen);
	}

	public EmptyScreen getScreen(String p_identifier)
	{
		return screens.get(p_identifier);
	}

	public SoundEffects getSound()
	{
		return sound;
	}

}
