package com.pigletlogic.screens;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Timer;
import com.pigletlogic.spaceoid.effects.PlanetAccessor;
import com.pigletlogic.spaceoid.effects.TableAccessor;
import com.pigletlogic.util.Constants;

class Message
{
	public String text = null;
	public float activeTime = 0f;
	public Map<String, Boolean> vars = null;
	protected boolean checkVariables = false;

	public Message(String p_text, float p_activeTime)
	{
		vars = new HashMap<String, Boolean>();
		text = p_text;
		activeTime = p_activeTime;
		checkVariables = false;
	}

}

class ActionMessage extends Message
{
	public ActionMessage(String p_varName, boolean p_value)
	{
		super("", 0);
		checkVariables = true;
		vars.put(p_varName, p_value);
	}
}

public class GameScreenGui
{
	private static final String TAG = GameScreenGui.class.getName();

	private GameScreen gameScreen = null;

	private ButtonStyle btnStyle = null;
	private Button bPause = null;
	private Table winTable = null;
	private Table starsPlaceholderTable = null;
	private Table loseTable = null;
	private Table pauseTable = null;
	private Table wonEverythingTable = null;

	// variables in messages
	private boolean showStatsIndicator = true;
	public static final String STATS_INDICATOR = "stats_indicator";
	public static final String ENLARGE_PLANETS = "enlarge_planets";

	// Message Queue Variables
	private Table tMessage = null;
	private Label lMessage = null;
	private String lastMessage = "";
	private Timer queueTimer = null;
	private final float QUEUE_CLEAR = 6f;
	
	// private static final float defaultMessageActiveTime = 3f;
	private float messageActiveTime = 0f;
	private Queue<Message> messages = null;
	private boolean showingMessage = false;
	private float timeShowingMessage = 0f;

	private Message tempMessage = null;

	/** end of variables */

	public GameScreenGui(GameScreen p_gameScreen)
	{
		gameScreen = p_gameScreen;
		queueTimer = new Timer();
		queueTimer.addTimer(QUEUE_CLEAR);
	}

	public void newMessage(String p_message, float p_length)
	{

		messages.add(new Message(p_message, p_length));
	}

	public void newActionMessage(ActionMessage m)
	{
		messages.add(m);
	}

	public void newActionMessage(String p_varName, boolean p_value)
	{
		messages.add(new ActionMessage(p_varName, p_value));
	}

	public void updateQueue(float p_delta)
	{
		if (showingMessage)
		{
			tempMessage = messages.peek();

			if (tempMessage == null)
			{
				// cancel message if it still persists
				// Gdx.app.debug(TAG, "temp message null");

				// timeShowingMessage = 0;
				// showingMessage = false;
				// lMessage.setText("");

				// tMessage.setPosition(0, Constants.VIEWPORT_HEIGHT);

				// return;
			}

			timeShowingMessage += p_delta;

			if (timeShowingMessage > messageActiveTime) // time to cancel this
														// message?
			{
				// Gdx.app.debug(TAG, "R: " + tempMessage.text + " " +
				// (System.currentTimeMillis() / 1000));
				timeShowingMessage = 0;
				showingMessage = false;

				tMessage.setPosition(0, Constants.VIEWPORT_HEIGHT);

			}

			return;
		}

		Message front = messages.poll();
		if (front != null && ! gameScreen.isGameFinished() && ! front.text.equals(lastMessage))
		{
			if (front.checkVariables)
			{
				mapVariables(front);
			}

			// Gdx.app.debug(TAG, "N: " + front.text + " " +
			// System.currentTimeMillis() / 1000);
			showingMessage = true;
			messageActiveTime = front.activeTime;
			lMessage.setText(front.text);
			lastMessage = front.text;

			Tween.to(tMessage, PlanetAccessor.POS_XY, 1f).target(0, 0).ease(TweenEquations.easeOutCubic).start(gameScreen.getTweenManager())
					.setCallback(callbackWhenMessageShown);

		}
	}

	public void update(float p_delta)
	{
		queueTimer.update(p_delta);
		if (queueTimer.isFinished(QUEUE_CLEAR))
		{
			lastMessage = "";
		}

		updateQueue(p_delta);

	}

	private void mapVariables(Message p_m)
	{
		if (p_m.vars.containsKey(STATS_INDICATOR))
		{
			showStatsIndicator = p_m.vars.get(STATS_INDICATOR);
		}
		if (p_m.vars.containsKey(ENLARGE_PLANETS))
		{
			gameScreen.setEnlargePlanets(p_m.vars.get(ENLARGE_PLANETS));
		}
	}

	public void init()
	{
		gameScreen.getGame().getStage().clear();
		messages = new LinkedList<Message>();

		// other tables
		final Table pauseTable = createPauseTable();
		final Table winTable = createWinTable();
		final Table loseTable = createLoseTable();
		final Table wonEverythingTable = createWonEverythingTable();

		float scale = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 45 / 480);

		LabelStyle ls = new LabelStyle();
		ls.font = Assets.instance.assetFancyFonts.fontSans.get(Assets.instance.assetFancyFonts.fontSizeSansText);
		lMessage = new Label("", ls);
		tMessage = new Table();
		tMessage.debugCell();
		tMessage.setFillParent(true);
		tMessage.add(lMessage).align(Align.top | Align.right).pad(scale).expand();
		tMessage.setPosition(0, Constants.VIEWPORT_HEIGHT);

		// Pause Button Init

		btnStyle = new ButtonStyle();
		bPause = new Button(btnStyle);
		bPause.stack(new Image(Assets.instance.gui.regions.get("pause"))).expand().fill();

		bPause.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				pauseButtonAction(pauseTable);

				return true;
			}
		});

		Table table = new Table();
		table.debugCell();
		table.setFillParent(true);
		table.add(bPause).align(Align.left | Align.top).expand().width(scale).height(scale).pad(scale * 0.2f);
		
		

		gameScreen.getGame().getStage().addActor(table);
		gameScreen.getGame().getStage().addActor(tMessage);
		
		// test
			//	Tween.to(table, TableAccessor.POS_XY, .5f).delay(1).target(100, 0).ease(TweenEquations.easeInBounce).repeatYoyo(1, 0.3f).start(gameScreen.getTweenManager())
			//	;
		/*
		Timeline.createSequence()

	    // Wait 1s
	    .pushPause(1.0f)

	    // Move the objects around, one after the other
	    .push(Tween.to(table, TableAccessor.POS_XY, .1f).target(100, 0).ease(TweenEquations.easeInBounce))
	    .push(Tween.to(table, TableAccessor.POS_XY, .1f).target(-100, 0).ease(TweenEquations.easeInBounce))
	    .push(Tween.to(table, TableAccessor.POS_XY, .1f).target(0, 0).ease(TweenEquations.easeInBounce))



	    // Let's go!
	    .start(gameScreen.getTweenManager());
*/
	}

	private Table createWinTable()
	{
		/** Pause menu */
		winTable = new Table();
		winTable.debugCell();
		winTable.setFillParent(true);
		
		starsPlaceholderTable = new Table();

		LabelStyle ls = new LabelStyle();
		ls.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanExtraBig);
		Label heading = new Label("You won!", ls);

		TextButtonStyle tsNormal = new TextButtonStyle();
		tsNormal.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanNormal);
		TextButtonStyle tsBig = new TextButtonStyle();
		tsBig.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanBig);
		
		TextButton nextLevelButton = new TextButton("Next Level", tsBig);
		TextButton menuButton = new TextButton("Menu", tsNormal);
		TextButton rateButton = new TextButton("Rate this game", tsNormal);
		TextButton replayButton = new TextButton("Replay", tsNormal);
		
		replayButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				replayButtonAction();

				return true;
			}
		});

		menuButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				goToMainMenu();

				return true;
			}
		});

		rateButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				try
				{
					Gdx.app.debug(TAG, "Opening rate game");
					Gdx.net.openURI(Constants.GOOGLE_PLAY_URL + Constants.GOOGLE_PLAY_PACKAGE);
				}
				catch (Exception e)
				{
					Gdx.app.debug(TAG, "Error opening a webiste: " + e.getMessage());
				}

				return true;
			}
		});

		nextLevelButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				boolean canAdvance = Assets.instance.assetLevels.tryAdvanceToNextLevel();
				if (canAdvance)
				{
					goToNextLevel();
				}
				else
				{
					Tween.to(winTable, TableAccessor.POS_XY, 1f).target( - Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic)
							.start(gameScreen.getTweenManager());
					Tween.to(wonEverythingTable, TableAccessor.POS_XY, 1f).target(0, 0).ease(TweenEquations.easeOutCubic)
							.start(gameScreen.getTweenManager());
				}
				return true;
			}
		});


		
			
			winTable.add(heading); // HEADING
			winTable.row();
			
			winTable.add(starsPlaceholderTable); // STARS
			winTable.row();
			
			winTable.add().height(Constants.VIEWPORT_HEIGHT * 0.05f); // EXTRA SPACE
			winTable.row();
			
			winTable.add(nextLevelButton); // NEXT LEVEL
			winTable.row();
			winTable.add().height(Constants.VIEWPORT_HEIGHT * 0.05f); // EXTRA SPACE
			
			winTable.row();
			winTable.add(rateButton); // RATE
			winTable.row();
			winTable.add().height(Constants.VIEWPORT_HEIGHT * 0.02f);
			
			winTable.row();
			
			winTable.add(replayButton);
			winTable.row();
			winTable.add().height(Constants.VIEWPORT_HEIGHT * 0.02f);
			winTable.row();
			
			winTable.add(menuButton);	// MENU

		winTable.setPosition(Constants.VIEWPORT_WIDTH, 0);
		gameScreen.getGame().getStage().addActor(winTable);

		return winTable;

	}

	private Table createWonEverythingTable()
	{

		wonEverythingTable = new Table();
		wonEverythingTable.debugCell();
		wonEverythingTable.setFillParent(true);

		LabelStyle ls = new LabelStyle();
		ls.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanExtraBig);
		Label heading = new Label("Well done", ls);

		LabelStyle ls2 = new LabelStyle();
		ls2.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanNormal);
		Label writing1 = new Label("You have completed", ls2);
		Label writing2 = new Label("all levels!", ls2);

		TextButtonStyle ts = new TextButtonStyle();
		ts.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanNormal);
		TextButton menuButton = new TextButton("Go to menu", ts);

		menuButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				goToMainMenu();

				return true;
			}
		});

		wonEverythingTable.add(heading);
		wonEverythingTable.row();
		wonEverythingTable.add().height(Constants.VIEWPORT_HEIGHT * 0.05f);
		wonEverythingTable.row();
		wonEverythingTable.add(writing1);
		wonEverythingTable.row();
		wonEverythingTable.add(writing2);
		wonEverythingTable.row();
		wonEverythingTable.add().height(Constants.VIEWPORT_HEIGHT * 0.05f);
		wonEverythingTable.row();
		wonEverythingTable.add(menuButton);

		wonEverythingTable.setPosition(Constants.VIEWPORT_WIDTH, 0);
		gameScreen.getGame().getStage().addActor(wonEverythingTable);

		return wonEverythingTable;
	}

	/**
	 * Adds stars to the winTable
	 * 
	 * @param p_stars
	 */
	public void addStars(int p_stars)
	{
		//winTable.row();

		Table stars = new Table();
		for (int i = 0; i < p_stars; ++i)
		{
			Image star = new Image(Assets.instance.gui.sprites.get("star_white"));
			stars.add(star).pad(Constants.VIEWPORT_WIDTH * 0.01f).width(Constants.VIEWPORT_WIDTH * 0.06f).height(Constants.VIEWPORT_WIDTH * 0.06f);
		}

		starsPlaceholderTable.add(stars);
		
		//winTable.add(stars).colspan(3);
	}

	private void goToNextLevel()
	{
		gameScreen.getGame().loadLevel();

		gameScreen.getGame().addScreen("game_screen", new GameScreen(gameScreen.getGame()));
		gameScreen.getGame().setCurrentScreen(gameScreen.getGame().getScreen("game_screen"));
		gameScreen.getGame().getCurrentScreen().init();
	}

	private Table createLoseTable()
	{
		/** Lose menu */
		loseTable = new Table();
		loseTable.setFillParent(true);

		LabelStyle ls = new LabelStyle(); // heading
		ls.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanExtraBig);
		Label heading = new Label("You lost!", ls);

		TextButtonStyle ts = new TextButtonStyle();
		ts.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanNormal);
		TextButton menuButton = new TextButton("Menu", ts);
		TextButton replayButton = new TextButton("Try again", ts);

		replayButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				replayButtonAction();

				return true;
			}
		});

		menuButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{

				goToMainMenu();

				return true;
			}
		});

		loseTable.add(heading).colspan(3);
		loseTable.row();
		loseTable.add().height(Constants.VIEWPORT_HEIGHT * 0.05f);
		loseTable.row();
		loseTable.add(menuButton);
		loseTable.add().width(Constants.VIEWPORT_WIDTH * 0.1f);
		loseTable.add(replayButton);
		loseTable.setPosition(Constants.VIEWPORT_WIDTH, 0);
		gameScreen.getGame().getStage().addActor(loseTable);

		return loseTable;

	}

	/**
	 * Creates table with buttons and labels. Shown where user hits pause
	 * button.
	 * 
	 * @return
	 */
	private Table createPauseTable()
	{
		/** Pause menu */
		pauseTable = new Table();
		pauseTable.setFillParent(true);

		LabelStyle ls = new LabelStyle();
		ls.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanExtraBig);
		Label heading = new Label("Game paused", ls);

		TextButtonStyle ts = new TextButtonStyle();
		ts.font = Assets.instance.assetFancyFonts.fontDefault.get(Assets.instance.assetFancyFonts.fontSizeSpacemanBig);
		TextButton resumeButton = new TextButton("Resume", ts);
		TextButton menuButton = new TextButton("Menu", ts);
		TextButton replayButton = new TextButton("Replay", ts);

		replayButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				pauseButtonAction(pauseTable);
				replayButtonAction();

				return true;
			}
		});
		
		resumeButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				pauseButtonAction(pauseTable);

				return true;
			}
		});

		menuButton.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{

				goToMainMenu();

				pauseButtonAction(pauseTable);

				return true;
			}
		});

		pauseTable.add(heading).colspan(3);
		pauseTable.row();
		pauseTable.add().height(Constants.VIEWPORT_HEIGHT * 0.05f);
		pauseTable.row();
		pauseTable.add(replayButton);
		pauseTable.add().width(Constants.VIEWPORT_WIDTH * 0.1f);
		pauseTable.add(resumeButton);
		pauseTable.row();
		pauseTable.add().height(Constants.VIEWPORT_HEIGHT * 0.05f);
		pauseTable.row();
		pauseTable.add(menuButton).colspan(3);
		
		pauseTable.setPosition(Constants.VIEWPORT_WIDTH, 0);
		gameScreen.getGame().getStage().addActor(pauseTable);

		return pauseTable;
		/* End of Pause Menu */
	}

	/** Action Function */

	public void pauseButtonAction(Table p_table)
	{
		if ( ! gameScreen.isGameFinished())
		{
			if (gameScreen.getGame().baseClass.paused)
			{
				// resume game
				gameScreen.getGame().baseClass.paused = false;
				Tween.to(p_table, TableAccessor.POS_XY, 1f).target(Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic)
						.start(gameScreen.getTweenManager());
				gameScreen.getInputMultiplexer().addProcessor(gameScreen.getInput());
			}
			else
			{
				// pause game
				Tween.to(p_table, TableAccessor.POS_XY, .5f).target(0, 0).ease(TweenEquations.easeOutCubic).start(gameScreen.getTweenManager())
						.setCallback(callbackAtPauseBegin);
				gameScreen.getInputMultiplexer().removeProcessor(gameScreen.getInput());
			}
		}
	}

	private void replayButtonAction()
	{
		gameScreen.getGame().loadLevel();

		gameScreen.getGame().addScreen("game_screen", new GameScreen(gameScreen.getGame()));
		gameScreen.getGame().setCurrentScreen(gameScreen.getGame().getScreen("game_screen"));
		gameScreen.getGame().getCurrentScreen().init();
	}

	/** End of Action Functions */

	private void goToMainMenu()
	{
		gameScreen.getGame().getStage().clear();

		gameScreen.getGame().addScreen("main_screen", new MainMenuScreen(gameScreen.getGame()));
		gameScreen.getGame().setCurrentScreen(gameScreen.getGame().getScreen("main_menu"));
		gameScreen.getGame().getCurrentScreen().init();
	}

	private TweenCallback callbackAtPauseBegin = new TweenCallback()
	{

		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			if (type == TweenCallback.COMPLETE)
			{
				gameScreen.getGame().baseClass.paused = true;
			}

		}
	};

	private TweenCallback callbackWhenMessageShown = new TweenCallback()
	{

		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			if (type == TweenCallback.COMPLETE)
			{

			}

		}
	};

	/** accessors */

	public Button getbPause()
	{
		return bPause;
	}

	public Table getWinTable()
	{
		return winTable;
	}
	
	public Table getPauseTable()
	{
		return pauseTable;
	}

	public Table getLoseTable()
	{
		return loseTable;
	}

	public boolean isShowingStatsIndicator()
	{
		return showStatsIndicator;
	}

	/** end of accessors */
}
