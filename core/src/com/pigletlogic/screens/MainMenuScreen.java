package com.pigletlogic.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pigletlogic.gui.AboutGui;
import com.pigletlogic.gui.Gui;
import com.pigletlogic.gui.LevelSelectionGui;
import com.pigletlogic.gui.MainMenuGui;
import com.pigletlogic.gui.RulesGui;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Game;
import com.pigletlogic.spaceoid.GameBackground;
import com.pigletlogic.spaceoid.SpriteBatchTransparency;
import com.pigletlogic.spaceoid.effects.TableAccessor;
import com.pigletlogic.util.Constants;

public class MainMenuScreen extends EmptyScreen
{
	private static final String TAG = MainMenuScreen.class.getName();

	private GameBackground stars = null;
	private Game game = null;
	public static TweenManager tweenManager = null;

	private Skin skin = Assets.instance.ui.defaultSkin;

	/**
	 * FONTS
	 */
	public BitmapFont fExtraBig = null;
	public BitmapFont fBig = null;
	public BitmapFont fNormal = null;

	private MainMenuGui mainMenuGui = null;
	private LevelSelectionGui levelSelectionGui = null;
	private RulesGui rulesGui = null;
	private AboutGui aboutGui = null;
	private Gui currentGui = null;

	/**
	 * SPRITES
	 */
	private Sprite logo = null;

	public MainMenuScreen(Game p_game)
	{
		super(p_game);
		game = p_game;
		stars = new GameBackground(this, true);
		SpriteBatchTransparency.getInstance().init(getGame().getBatch());
		Gdx.input.setInputProcessor(game.getStage());
		tweenManager = new TweenManager();

		Tween.registerAccessor(Table.class, new TableAccessor());

		initMainMenuGui();
	}

	public void init()
	{

	}

	private void initMainMenuGui()
	{
		Gdx.input.setCatchBackKey(true);

		// load levels file if user won a level
		if (Assets.instance.assetLevels.reloadLevelsData)
		{
			Assets.instance.assetLevels.loadLevels();
			Assets.instance.assetLevels.reloadLevelsData = false;

			Gdx.app.debug(TAG, "LEVELS RELOADED");
		}

		fExtraBig = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanExtraBig);
		fBig = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanBig);
		fNormal = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanNormal);

		mainMenuGui = new MainMenuGui(this);
		levelSelectionGui = new LevelSelectionGui(this);
		rulesGui = new RulesGui(this);
		aboutGui = new AboutGui(this);
		currentGui = mainMenuGui;
		currentGui.init();

	}

	/**
	 * MAIN MENU FUNCTIONS
	 */

	/**
	 * Moves from main menu to level selection screen
	 * 
	 * @param p_table
	 */
	public void handlePlayButton(Table p_table)
	{
		currentGui = levelSelectionGui;
		
		Tween.to(p_table, TableAccessor.POS_XY, 1.f).target( - Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);

		Tween.to(levelSelectionGui.table, TableAccessor.POS_XY, 1.f).target(0, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);

	}

	public void handleRulesButton(Table p_table)
	{
		currentGui = rulesGui;
		
		Tween.to(p_table, TableAccessor.POS_XY, 1.f).target( - Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);
		Tween.to(rulesGui.table, TableAccessor.POS_XY, 1.f).target(0, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);

	}

	public void handleAboutButton(Table p_table)
	{
		currentGui = aboutGui;
		
		Tween.to(p_table, TableAccessor.POS_XY, 1.f).target( - Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);
		Tween.to(aboutGui.table, TableAccessor.POS_XY, 1.f).target(0, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);
	}

	public void handleExitButton(Table p_table)
	{
		try
		{
			Gdx.app.exit();
		}
		catch(Exception e)
		{
			Gdx.app.debug(TAG, "Error exiting: " + e.getMessage());
		}
	}

	/**
	 * END OF MAIN MENU FUNCTIONS
	 */

	// ABOUT GUI
	public void handleAboutBackButton()
	{
		currentGui = mainMenuGui;
		
		Tween.to(aboutGui.table, TableAccessor.POS_XY, 1.f).target(Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);

		Tween.to(mainMenuGui.table, TableAccessor.POS_XY, 1.f).target(0, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);

	}

	// RULES GUI
	public void handleRulesBackButton()
	{
		currentGui = mainMenuGui;
		
		Tween.to(rulesGui.table, TableAccessor.POS_XY, 1.f).target(Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);

		Tween.to(mainMenuGui.table, TableAccessor.POS_XY, 1.f).target(0, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);

	}

	// LEVEL SELECTION GUI
	public void handleBackButton()
	{
		getGame().baseClass.getActivityHandler().showAds(false);
		
		currentGui = mainMenuGui;
		
		Tween.to(levelSelectionGui.table, TableAccessor.POS_XY, 1.f).target(Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic)
				.start(tweenManager);

		Tween.to(mainMenuGui.table, TableAccessor.POS_XY, 1.f).target(0, 0).ease(TweenEquations.easeOutCubic).start(tweenManager);

	}

	public ClickListener handleLevelButtonClick = new ClickListener()
	{

		@Override
		public void clicked(InputEvent event, float x, float y)
		{
			try
			{
				getGame().baseClass.getActivityHandler().showAds(false);
				
				int levelId = Integer.parseInt(event.getListenerActor().getName());
	
				Assets.instance.assetLevels.currentLevel.setLevelId(levelId);
				game.loadLevel();
	
				game.addScreen("game_screen", new GameScreen(game));
				game.setCurrentScreen(game.getScreen("game_screen"));
				game.getCurrentScreen().init();
	
				Gdx.app.debug(TAG, "Click: " + levelId);
			}
			catch(Exception e)
			{
				Gdx.app.debug(TAG, "Error handling level button click: " + e.getMessage());
				StackTraceElement[] t = e.getStackTrace();
				
				for(int i = 0; i < t.length; ++i)
				{
					Gdx.app.debug(TAG, (String)t[i].toString());
				}
					
				game.addScreen("main_menu", new MainMenuScreen(game));
				game.setCurrentScreen(game.getScreen("main_menu"));
				game.getCurrentScreen().init();
			}

		}

	};

	public void update(float delta)
	{
		stars.update(delta);
		
		currentGui.update(delta);

		tweenManager.update(delta);
	}

	@Override
	public void render(float delta)
	{
		getGame().getBatch().begin();
		stars.render(getGame().getBatch());

		mainMenuGui.render(delta);
		
		if (Constants.DEBUG_GUI)
		{
			Table.drawDebug(getGame().getStage());
		}
			
		getGame().getBatch().end();

		getGame().getStage().act(delta);

		getGame().getStage().draw();

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
		Gdx.app.debug(TAG, "Paused");
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

}
