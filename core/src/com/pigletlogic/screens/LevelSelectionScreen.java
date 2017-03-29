package com.pigletlogic.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Game;
import com.pigletlogic.spaceoid.effects.SpriteAccessor;

public class LevelSelectionScreen extends EmptyScreen
{
	private static final String TAG = LevelSelectionScreen.class.getName();

	private Sprite logo = null;

	private static TweenManager tweenManager = null;
	private BitmapFont font = Assets.instance.assetBaseFonts.defaultNormal;
	private Skin skin = Assets.instance.ui.defaultSkin;

	private Label l = null;

	private Table table = null;

	private float logoScale = 1.0f;

	private Game game = null;

	/**
	 * TEXT BUTTONS STYLE
	 */
	private TextButtonStyle tbsBig = null;
	private TextButtonStyle tbsNormal = null;

	/**
	 * FONTS
	 */
	private BitmapFont fBig = null;
	private BitmapFont fNormal = null;

	/**
	 * BUTTONS
	 */
	private TextButton btnBack = null;

	public LevelSelectionScreen(Game p_game)
	{
		super(p_game);

		game = p_game;

		logoScale = 0.5f;

		initGui();

		tweenManager = new TweenManager();
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

	}

	private void initGui()
	{
		table = new Table();
		table.setFillParent(true);
		table.setLayoutEnabled(true);
		table.debugCell();

		btnBack = new TextButton("BACK", skin);

		Table top = new Table();
		top.add(btnBack);

		table.add(top);

		getGame().getStage().clear();
		getGame().getStage().addActor(table);

		fBig = Assets.instance.assetFancyFonts.fontDefault.get(36);
		fNormal = Assets.instance.assetFancyFonts.fontDefault.get(24);

		tbsBig = new TextButtonStyle();
		tbsNormal = new TextButtonStyle();

		tbsBig.font = fBig;
		tbsNormal.font = fNormal;
	}

	public void update(float delta)
	{
		tweenManager.update(delta);

	}

	@Override
	public void render(float delta)
	{
		getGame().getBatch().begin();

		getGame().getBatch().end();

		getGame().getStage().act(delta);

		getGame().getStage().draw();

	}

}
