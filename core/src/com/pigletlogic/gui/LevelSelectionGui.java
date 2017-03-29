package com.pigletlogic.gui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pigletlogic.screens.MainMenuScreen;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Level;
import com.pigletlogic.spaceoid.PagedScrollPane;
import com.pigletlogic.spaceoid.effects.TableAccessor;
import com.pigletlogic.util.Constants;

public class LevelSelectionGui extends Gui
{
	private static final String TAG = LevelSelectionGui.class.getName();

	private MainMenuScreen parent = null;

	// private int currentPage = 0;
	private int allPages = 0;

	/**
	 * BUTTONS
	 */
	private TextButton btnBack = null;
	private TextButton btnPrevious = null;
	private TextButton btnNext = null;

	private Label lSelectLevel = null;

	/**
	 * 
	 */
	private TextButtonStyle tbsBig = null;
	private TextButtonStyle tbsNormal = null;

	private ButtonStyle btnStyle = null;

	private LabelStyle lStyle = null;
	private LabelStyle lButtonLabelStyle = null;

	private TextureRegion levelButtonTexture = null;
	private Image levelButtonImage = null;
	private TextureRegion levelButtonTextureLocked = null;
	private Image levelButtonImageLocked = null;
	private Image levelBg = null;

	// Number of level buttons shown while selecting level
	private float levelsPerPage = 0f; // do not set
	private final int rows;
	private final int cols;
	private int startPage = 0;

	private float pad = 0;

	private boolean startPositionSet = false;
	
	/**
	 * Variables the need to be calculcated, and will differ depending on the
	 * screen size
	 */
	private float pageSpacing = 0;
	private float buttonSize = 0;
	private float buttonPadTopBottom = 0; // horizontal
	private float buttonPadLeftRight = 0; // vertical
	private float starSize = 0;

	private boolean levelToPlayFound = false; // needed when finding next level
												// to play
	private int levelToPlayId = 1;
	
	private Table tLockedLevelInfo = null;

	/* */

	private PagedScrollPane scroll = null;

	/* */

	private String stringifyNumber(int p_number)
	{
		String ret = "";

		if (p_number < 10) ret += "0";

		ret += p_number;

		return ret;
	}
	
	public ClickListener handleLevelLockedClick = new ClickListener()
	{

		@Override
		public void clicked(InputEvent event, float x, float y)
		{
			try
			{
//				getGame().baseClass.getActivityHandler().showAds(false);
//				
//				int levelId = Integer.parseInt(event.getListenerActor().getName());
//	
//				Assets.instance.assetLevels.currentLevel.setLevelId(levelId);
//				game.loadLevel();
//	
//				game.addScreen("game_screen", new GameScreen(game));
//				game.setCurrentScreen(game.getScreen("game_screen"));
//				game.getCurrentScreen().init();
//	
			
//				Gdx.app.debug(TAG, "Click: " + levelId);
				
				parent.tweenManager.killTarget(tLockedLevelInfo);
				
				tLockedLevelInfo.setPosition(Constants.VIEWPORT_GUI_WIDTH, 0);
				
				Timeline.createSequence()

			    .push(Tween.to(tLockedLevelInfo, TableAccessor.POS_XY, 0.8f).target(0, 0).ease(TweenEquations.easeOutCubic))
			    .pushPause(2.0f)
			    .push(Tween.to(tLockedLevelInfo, TableAccessor.POS_XY, 0.8f).target(-Constants.VIEWPORT_GUI_WIDTH, 0).ease(TweenEquations.easeOutCubic))
			    .start(parent.tweenManager);
				
				
				Gdx.app.debug(TAG, "Level locked.");
			}
			catch(Exception e)
			{
				Gdx.app.debug(TAG, "Error handling LOCKED level button click: " + e.getMessage());
				StackTraceElement[] t = e.getStackTrace();
				
				for(int i = 0; i < t.length; ++i)
				{
					Gdx.app.debug(TAG, (String)t[i].toString());
				}
				
				parent.getGame().addScreen("main_menu", new MainMenuScreen(parent.getGame()));
				parent.getGame().setCurrentScreen(parent.getGame().getScreen("main_menu"));
				parent.getGame().getCurrentScreen().init();
			}

		}

	};

	private Button getLevelButton(int p_levelNo)
	{
		// flags for this button only
		boolean nextLevelToPlay = false; // only one button of this kind
		boolean lockedButton = false;

		Level level = parent.getGame().levels.get(p_levelNo - 1);

		if (level == null)
		{
			Gdx.app.error(TAG, "Level " + p_levelNo + " (" + (p_levelNo - 1) + ") not found");
			return null;
		}

		Label text = new Label("." + stringifyNumber(p_levelNo), lButtonLabelStyle);
		text.setAlignment(Align.center);

		Button b = new Button(btnStyle);
		b.addListener(parent.handleLevelButtonClick);
		b.setName(new Integer(p_levelNo).toString());

		starSize = Constants.VIEWPORT_HEIGHT * 0.03f;

		// stars
		int stars = level.getResult()/*MathUtils.random(0, 3)*/;
		Table starTable = new Table();
		starTable.defaults().pad(5);
		if (stars >= 0)
		{
			for (int star = 0; star < 3; star++)
			{
				if (stars > star)
				{
					starTable.add(new Image(Assets.instance.gui.sprites.get("star_white"))).width(starSize).height(starSize);

				}
				else
				{
					starTable.add(new Image(Assets.instance.gui.sprites.get("star_black"))).width(starSize).height(starSize);
				}
			}
		}

		if (level.getPassed() == 1)
		{
			levelBg = levelButtonImage;
		}
		else if (level.getPassed() == 0)
		{
			if ( ! levelToPlayFound)
			{
				levelBg = levelButtonImage;
				nextLevelToPlay = true;
				levelToPlayFound = true;
				levelToPlayId = level.getId();
			}
			else
			{
				levelBg = levelButtonImageLocked;
				lockedButton = true;
				
				// add listener that show a message
				
				b.removeListener(parent.handleLevelButtonClick);
				b.addListener(handleLevelLockedClick);
			}
		}

		if (lockedButton)
		{
			b.stack(new Image(levelBg.getDrawable())).expand().fill();
		}
		else
		{
			b.stack(new Image(levelBg.getDrawable()), text).expand().fill();
		}

		b.row();

		if (nextLevelToPlay || lockedButton)
		{
			// no stars
			b.add().height(starSize * 1.5f);
		}
		else
		{
			b.add(starTable).height(starSize * 1.5f);
		}

		return b;
	}
	
	private void initLockedLevelInfoTable()
	{
		TextButton btnLevelLockedOK = new TextButton("ok", tbsBig);

		btnLevelLockedOK.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				Gdx.app.debug(TAG, "level locked OK button click");
			//	tLockedLevelInfo.setPosition(Constants.VIEWPORT_WIDTH, 0);
				
				parent.tweenManager.killTarget(tLockedLevelInfo);
				
				Tween.to(tLockedLevelInfo, TableAccessor.POS_XY, 1f).target( - Constants.VIEWPORT_WIDTH, 0).ease(TweenEquations.easeOutCubic)
				.start(parent.tweenManager);
				
				
				
				return true;
			}
		});
		
		tLockedLevelInfo = new Table();
		tLockedLevelInfo.setFillParent(true);
		
		Label lInfo = new Label("This level is locked", lStyle);
		
		tLockedLevelInfo.add(lInfo);
		tLockedLevelInfo.row();
		tLockedLevelInfo.add().height(pad);
		tLockedLevelInfo.row();
		tLockedLevelInfo.add(btnLevelLockedOK);
		
		tLockedLevelInfo.setPosition(Constants.VIEWPORT_WIDTH, 0);
		parent.getGame().getStage().addActor(tLockedLevelInfo);
	}

	public LevelSelectionGui(MainMenuScreen p_parent)
	{
		super();

		parent = p_parent;

		rows = 2;
		cols = 5;
		
		levelsPerPage = rows * cols;
		
		levelButtonTexture = new TextureRegion(Assets.instance.gui.regions.get("level"));
		levelButtonImage = new Image(levelButtonTexture);
		levelButtonTextureLocked = new TextureRegion(Assets.instance.gui.regions.get("level_b"));
		levelButtonImageLocked = new Image(levelButtonTextureLocked);

		pad = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 15 / 480);

		tbsBig = new TextButtonStyle();
		tbsNormal = new TextButtonStyle();
		lStyle = new LabelStyle();
		lButtonLabelStyle = new LabelStyle();
		btnStyle = new ButtonStyle();

		tbsBig.font = parent.fBig;
		tbsNormal.font = parent.fNormal;
		lStyle.font = parent.fBig;
		lButtonLabelStyle.font = Assets.instance.assetFancyFonts.fontSans.get(18); // level
																					// selection
																					// button

		btnBack = new TextButton("BACK", Assets.instance.ui.defaultSkin);

		lSelectLevel = new Label("SELECT LEVEL", lStyle);

		btnBack.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				parent.handleBackButton();
				return false;
			}
		});

		btnBack.setStyle(tbsNormal);

		Table tMiddle = new Table();
		

		Table tTopRight = new Table();
		tTopRight.debug();
		Table tTopLeft = new Table();

		table = new Table();
		table.setFillParent(true);
		table.setLayoutEnabled(true);
		if (Constants.DEBUG_GUI)
		{
			table.debugCell();
			tMiddle.debugCell();
		}
		table.defaults().fill();
		table.align(Align.center);

		tTopLeft.add(lSelectLevel).pad(pad).expandX().left();
		
		float topHeight = lSelectLevel.getHeight() + 2 * pad;
		
		tTopRight.add(btnBack).expandX().right().padRight(pad).height(topHeight);
		
		//Gdx.app.debug(TAG, "selectH: " + lSelectLevel.getHeight());
		//Gdx.app.debug(TAG, "selectHAll: " + (lSelectLevel.getHeight() + 2 * pad));
		
		float topRowHeight = lSelectLevel.getHeight() + 2 * pad; // heading height (select level label)
		float levelRowHeight = 0; // levels menu height

		table.add(tTopLeft);
		table.add(tTopRight);

		table.row();
		table.add(tMiddle).expand().colspan(2);
		table.row();

		// calculate variables
		pageSpacing = Constants.VIEWPORT_WIDTH * 0.2f;
		
		Gdx.app.debug(TAG, "pageSpacing " + pageSpacing);

		buttonSize = Constants.VIEWPORT_WIDTH * 0.1f;

		buttonPadTopBottom = Constants.VIEWPORT_HEIGHT * 0.025f;
		buttonPadLeftRight = Constants.VIEWPORT_WIDTH * 0.05f / 1.5f;

		scroll = new PagedScrollPane();

		scroll.setFlingTime(0.5f);
		scroll.setPageSpacing(pageSpacing);
		scroll.setScrollingDisabled(false, true);
		

		int levelsNo = parent.getGame().levels.size();
		allPages = (int) Math.ceil(levelsNo / levelsPerPage);
		boolean allAdded = false;
		
		
		
		

		int c = 1;
		for (int l = 0; l < allPages; l++)
		{
			Table levels = new Table();
			if (Constants.DEBUG_GUI)
			{
				levels.debug();
			}
			//levels.setFillParent(true); // not a good idea
			
			
			
			levels.defaults().pad(buttonPadTopBottom, buttonPadLeftRight * 1.5f, buttonPadTopBottom, buttonPadLeftRight  * 1.5f);

			for (int y = 0; y < rows; y++) // rows
			{
				levels.row();
				for (int x = 0; x < cols; x++) // columns
				{
					Button b = getLevelButton(c++);
					levelRowHeight = rows * (buttonSize + starSize * 1.5f) + (buttonPadTopBottom * rows * 2);
//					Gdx.app.debug(TAG, "h: " + h);
//					Gdx.app.debug(TAG, "bW: " + buttonSize);
//					Gdx.app.debug(TAG, "bH: " + (buttonSize + starSize * 1.5f));
//					Gdx.app.debug(TAG, "star: " + starSize);
					if (b != null) levels.add(b).width(buttonSize).height(buttonSize + starSize * 1.5f);

					if (c > levelsNo)
					{
						allAdded = true;
						break;
					}
					if (allAdded) break;
				}
				if (allAdded) break;
			}
			levels.row();
			levels.add().height(Constants.VIEWPORT_HEIGHT - (topRowHeight + levelRowHeight));
			
			scroll.addPage(levels);
			if (allAdded) break;
		}
		
		Gdx.app.debug(TAG, "scroll: " + scroll.getScrollX());
		Gdx.app.debug(TAG, "height: " + (topRowHeight + levelRowHeight));
		
		//scroll.scrollToCenter(1, 1, 10, 10);
		
		//scroll.updateVisualScroll();
		//scroll.setSmoothScrolling(true);
		
		tMiddle.add(scroll).top().expandY();

		table.setPosition(Constants.VIEWPORT_WIDTH, 0);
		parent.getGame().getStage().addActor(table);
		
		initLockedLevelInfoTable();
		
		setStartPage();

	}
	
	private void setStartPage()
	{
		try
		{
		//levelToPlayId = 5;
		Gdx.app.debug(TAG, "levelToPlayId " + levelToPlayId + " levelsPerPage " + levelsPerPage);
		startPage = (int) Math.floor((levelToPlayId /*- 1*/) / levelsPerPage);
		startPage = MathUtils.clamp(startPage, 0, allPages - 1);
		Gdx.app.debug(TAG, "startPage " + startPage);
		}
		catch(Exception e)
		{
			Gdx.app.error(TAG, "Error in setStartPage: " + e.getMessage());
			startPage = 0;
		}
		
	}
	
	private void moveToStartPage(int p_page)
	{
		if ( ! startPositionSet)
		{
			scroll.setSmoothScrolling(false);
		//	Gdx.app.debug(TAG, "current: " + scroll.getScrollX() + " w: " + scroll.getScrollWidth());
			float x = p_page * scroll.getScrollWidth() + p_page * pageSpacing;
			//Gdx.app.debug(TAG, "x " + x + " " + scroll.getScrollPercentX());
			//scroll.setVisible(false);
			scroll.setScrollX(x);
			
			//if (scroll.getScrollX()+1 >= x)
			//	scroll.setVisible(true);
			
			startPositionSet = true;
			
		}
		else
		{
			scroll.setSmoothScrolling(true);
		}
		
		
	}
	
	@Override
	public void update(float p_delta)
	{
		moveToStartPage(startPage);
		//scroll.setScrollX( 1000);
		//scroll.act(p_delta);
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			parent.handleBackButton();
		}
	}

	public void init()
	{

	}
}