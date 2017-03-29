package com.pigletlogic.gui;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pigletlogic.screens.MainMenuScreen;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Planet;
import com.pigletlogic.spaceoid.effects.TableAccessor;
import com.pigletlogic.spaceoid.types.PlanetType;
import com.pigletlogic.util.Constants;

public class MainMenuGui extends Gui
{
	private final String TAG = MainMenuGui.class.getName();
	private MainMenuScreen parent = null;

	/**
	 * MAIN MENU BUTTONS
	 */
	private TextButton btnPlay = null;
	private TextButton btnRules = null;
	private TextButton btnAbout = null;
	private TextButton btnExit = null;

	/**
	 * BUTTONS: WWW, FB, MUSIC
	 */
	private Button btnMusic = null;
	private ButtonStyle btnMusicStyle = null;
	private Button btnFacebook = null;
	private ButtonStyle btnFacebookStyle = null;
	private Button btnWWW = null;
	private ButtonStyle btnWWWStyle = null;

	/**
	 * 
	 */
	private TextButtonStyle tbsBig = null;
	private TextButtonStyle tbsNormal = null;

	/**
	 * IMAGES
	 */
	private TextureRegion logoTexture = null;
	private Image logoImage = null;
	private float scale = 1.0f;

	private ArrayList<Planet> planets = null;

	/**
	 * PADDING
	 */
	private int padMenuItems = 0;

	public MainMenuGui(MainMenuScreen p_parent)
	{
		parent = p_parent;

		padMenuItems = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 15 / 480);
		scale = Constants.VIEWPORT_GUI_HEIGHT * 0.65f / 480;

		logoTexture = new TextureRegion(Assets.instance.gui.regions.get("logo"));
		logoImage = new Image(logoTexture);

		// Planets on main menu
		planets = new ArrayList<Planet>();

		int p1x = Math.round(Constants.VIEWPORT_WIDTH * 0.52f);
		int p1y = Math.round(Constants.VIEWPORT_HEIGHT * 0.5f);
		planets.add(new Planet(new PlanetType("red"), p1x, p1y, 50));

		/** Bottom buttons */

		Table bottomButtons = new Table();
		bottomButtons.setLayoutEnabled(true);

		btnWWWStyle = new ButtonStyle();
		btnWWWStyle.up = new TextureRegionDrawable(Assets.instance.gui.regions.get("icon_www"));
		btnWWW = new Button(btnWWWStyle);
		float btnWWWSize = Constants.VIEWPORT_WIDTH * 0.12f;
		float btnWWWFactor = 0.3f;

		btnFacebookStyle = new ButtonStyle();
		btnFacebookStyle.up = new TextureRegionDrawable(Assets.instance.gui.regions.get("icon_fb"));
		btnFacebook = new Button(btnFacebookStyle);
		float btnFacebookSize = Constants.VIEWPORT_WIDTH * 0.10f;
		float btnFacebookFactor = 0.3f;

		btnMusicStyle = new ButtonStyle();
		btnMusicStyle.up = new TextureRegionDrawable(Assets.instance.gui.regions.get("icon_music_active"));
		btnMusicStyle.disabled = new TextureRegionDrawable(Assets.instance.gui.regions.get("icon_music_inactive"));
		btnMusic = new Button(btnMusicStyle);
		float btnMusicSize = Constants.VIEWPORT_WIDTH * 0.10f;
		float btnMusicFactor = 0.35f;

		// at the bottom:
		//bottomButtons.add(btnWWW).width(btnWWWSize).height(btnWWWSize).bottom().padBottom( - btnWWWFactor * btnWWWSize).padLeft(btnWWWSize * 0.15f);
		//bottomButtons.add(btnFacebook).width(btnFacebookSize).height(btnFacebookSize).bottom().padBottom( - btnFacebookFactor * btnFacebookSize);
		//bottomButtons.add(btnMusic).width(btnMusicSize).height(btnMusicSize).bottom().padBottom( - btnMusicFactor * btnMusicSize);
		
		bottomButtons.add(btnWWW)
		.width(btnWWWSize).height(btnWWWSize)
		.bottom().left()
		.padBottom(Constants.VIEWPORT_HEIGHT * 0.12f)
		.padLeft(Constants.VIEWPORT_WIDTH * 0.15f)
		;
		
		bottomButtons.add(btnFacebook).
		width(btnFacebookSize).height(btnFacebookSize).
		bottom().
		padBottom(Constants.VIEWPORT_HEIGHT * 0.4f);
		
		bottomButtons.add(btnMusic).
		width(btnMusicSize).height(btnMusicSize).
		bottom().
		padBottom(Constants.VIEWPORT_HEIGHT * 0.20f);
		
		// -------------------------------------

		btnMusic.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				if (btnMusic.isDisabled())
				{
					turnOnMusic();
				}
				else
				{
					turnOffMusic();
				}

				parent.getGame().settings.flush();

				return true;
			}
		});

		btnWWW.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				try
				{
					Gdx.app.debug(TAG, "Opening website");
					Gdx.net.openURI("http://pigletlogic.com");
				}
				catch (Exception e)
				{
					Gdx.app.debug(TAG, "Error opening a webiste: " + e.getMessage());
				}

				return true;
			}
		});

		btnFacebook.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				try
				{
					Gdx.app.debug(TAG, "Opening fb");
					Gdx.net.openURI("http://www.facebook.com/pigletlogic");
				}
				catch (Exception e)
				{
					Gdx.app.debug(TAG, "Error opening fb: " + e.getMessage());
				}

				return true;
			}
		});

		// -------------------------------------

		tbsBig = new TextButtonStyle();
		tbsNormal = new TextButtonStyle();

		tbsBig.font = parent.fExtraBig;
		tbsNormal.font = parent.fNormal;
		
		

		btnPlay = new TextButton("PLAY", Assets.instance.ui.defaultSkin);
		btnRules = new TextButton("HELP", Assets.instance.ui.defaultSkin);
		btnAbout = new TextButton("CREDITS", Assets.instance.ui.defaultSkin);
		btnExit = new TextButton("EXIT", Assets.instance.ui.defaultSkin);
		

		btnPlay.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				parent.handlePlayButton(table);
				parent.getGame().baseClass.getActivityHandler().showAds(true);
				return false;
			}
		});

		btnRules.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				parent.handleRulesButton(table);
				return false;
			}
		});

		btnAbout.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				parent.handleAboutButton(table);
				return false;
			}
		});

		btnExit.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				parent.handleExitButton(table);
				return false;
			}
		});

		btnPlay.setStyle(tbsBig);
		btnRules.setStyle(tbsNormal);
		btnAbout.setStyle(tbsNormal);
		btnExit.setStyle(tbsNormal);

		Table tButtons = new Table(); // table with all the menu buttons
		Table tLogo = new Table();

		table = new Table();
		table.setFillParent(true);
		table.setLayoutEnabled(true);
		if (Constants.DEBUG_GUI)
		{
			table.debugCell();
			//tButtons.debugCell();
			tButtons.debug();
			tLogo.debugCell();
		}
		table.defaults().fillY();
		table.align(Align.center);

		// table.align(Align.top);

		tLogo.add(logoImage).align(Align.top | Align.left).expand().width(scale * logoImage.getWidth()).height(scale * logoImage.getHeight());
		tLogo.row();
		tLogo.add(bottomButtons);
		// tLogo.add(btnWWW).width(btnWWWSize).height(btnWWWSize);;
		// tLogo.add(btnFacebook);
		// tLogo.add(btnMusic);
		// .pad(padMenuItems, 0, padMenuItems, padMenuItems)
		
		float heightBig = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 70 / 480);
		float heightNormal = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 55 / 480);
		
		tButtons.add().expandX().right();
		tButtons.add(btnPlay).align(Align.left).height(heightBig);
		
		

		tButtons.row();
		tButtons.add().expandX();
		tButtons.add(btnRules).align(Align.left).height(heightNormal);
		tButtons.row();
		tButtons.add().expandX();
		tButtons.add(btnAbout).align(Align.left).height(heightNormal);
		tButtons.row();
		tButtons.add().expandX();
		tButtons.add(btnExit).align(Align.left).height(heightNormal);

		table.add(tLogo).expand().left();
		table.add(tButtons).expand().right().padRight(padMenuItems * 5);

		parent.getGame().getStage().addActor(table);

		initSound();
	}

	private void initSound()
	{
		if (parent.getGame().settings.getBoolean("soundOn", true))
		{
			turnOnMusic();
		}
		else
		{
			turnOffMusic();
		}
	}

	private void turnOnMusic()
	{
		// turn on music
		parent.getGame().getSound().turnOn();
		btnMusic.setDisabled(false);
		parent.getGame().getSound().playBackgroundMusic();

		// save prefs
		parent.getGame().settings.putBoolean("soundOn", true);
	}

	private void turnOffMusic()
	{
		btnMusic.setDisabled(true);
		parent.getGame().getSound().turnOff();

		parent.getGame().settings.putBoolean("soundOn", false);
	}

	public void init()
	{
		table.setPosition(Constants.VIEWPORT_WIDTH, 0);
		Tween.to(table, TableAccessor.POS_XY, 1.f).target(0, 0).ease(TweenEquations.easeOutCubic).start(parent.tweenManager);
	}

	public void render(float p_delta)
	{

		for (Planet p : planets)
		{
			p.update(p_delta);
			p.render(parent.getGame().getBatch());

		}
	}
	
	
}