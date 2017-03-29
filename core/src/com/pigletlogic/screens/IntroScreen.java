package com.pigletlogic.screens;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Bullet;
import com.pigletlogic.spaceoid.Game;
import com.pigletlogic.spaceoid.HumanPlayer;
import com.pigletlogic.spaceoid.SpriteBatchTransparency;
import com.pigletlogic.spaceoid.effects.SpriteAccessor;
import com.pigletlogic.util.Constants;

public class IntroScreen extends EmptyScreen
{
	private static final String TAG = IntroScreen.class.getName();

	private Sprite logo = null;

	private static TweenManager tweenManager = null;
	private BitmapFont font = Assets.instance.assetBaseFonts.defaultNormal;

	private boolean showLoadingLabel = false;
	private Label l = null;

	private Table table = null;

	private float logoScale = 1.0f;

	private Game game = null;

	private float FIRST_ANIMATION_TIME = 0f; // 2.5
	private float SECOND_ANIMATION_TIME = 0f; // 1.5

	public IntroScreen(Game p_game)
	{
		super(p_game);
		
		if (Constants.DEBUG)
		{
			FIRST_ANIMATION_TIME = 0f;
			SECOND_ANIMATION_TIME = 0;
		}
		else
		{
			// production version
			FIRST_ANIMATION_TIME = 2.5f;
			SECOND_ANIMATION_TIME = 1.5f;
		}

		game = p_game;

		logoScale = Constants.VIEWPORT_HEIGHT * 0.5f / 480f;

		table = new Table();
		table.setFillParent(true);
		table.setLayoutEnabled(true);
		table.debugTable();

		LabelStyle ls = new LabelStyle();
		ls.font = font;
		l = new Label("Loading...", ls);

		getGame().getStage().addActor(table);

		logo = Assets.instance.gui.sprites.get("logo");

		tweenManager = new TweenManager();
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		logo.setColor(logo.getColor().r, logo.getColor().g, logo.getColor().b, 0.0f);

		Tween.to(logo, SpriteAccessor.ALPHA, FIRST_ANIMATION_TIME).target(1.0f).ease(TweenEquations.easeInQuart).start(tweenManager)
				.setCallback(callbackAtEndFirstLogoAnim);

	}

	/**
	 * At the end of 1st logo animation
	 */
	private TweenCallback callbackAtEndFirstLogoAnim = new TweenCallback()
	{

		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			if (type == TweenCallback.COMPLETE)
			{
				Assets.instance.loadAll();
				getGame().levels = Assets.instance.assetLevels.levels;

				getGame().getSound().playBackgroundMusic();

				Gdx.input.setInputProcessor(getGame().getStage());

				getGame().bullets = new ArrayList<Bullet>();
			}
		}
	};

	/**
	 * At the end of 2nd logo animation
	 */
	private TweenCallback callbackAtEndSecondLogoAnim = new TweenCallback()
	{

		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			if (type == TweenCallback.COMPLETE)
			{
				game.addScreen("main_menu", new MainMenuScreen(game));
				game.setCurrentScreen(game.getScreen("main_menu"));
				game.getCurrentScreen().init();

			}
		}
	};

	public void update(float delta)
	{
		tweenManager.update(delta);

		if (Assets.instance.loadedEverything)
		{
			Assets.instance.loadedEverything = false;
			l.remove();
			Tween.to(logo, SpriteAccessor.ALPHA, SECOND_ANIMATION_TIME).delay(1.0f).target(0.0f).ease(TweenEquations.easeInOutQuart)
					.start(tweenManager).setCallback(callbackAtEndSecondLogoAnim);
		}
	}

	@Override
	public void render(float delta)
	{
		getGame().getBatch().begin();

		// Table.drawDebug(getGame().getStage());

		SpriteBatchTransparency.getInstance().setTransparency(logo.getColor().a);
		getGame().getBatch().draw(logo, Constants.VIEWPORT_WIDTH * 0.5f - logo.getWidth() * 0.5f * logoScale,
				Constants.VIEWPORT_HEIGHT * 0.5f - logo.getHeight() * 0.5f * logoScale, 0, 0, logo.getWidth(), logo.getHeight(), logoScale,
				logoScale, 0);
		SpriteBatchTransparency.getInstance().removeTransparency();

		getGame().getBatch().end();

		getGame().getStage().act(delta);

		getGame().getStage().draw();

	}

}
