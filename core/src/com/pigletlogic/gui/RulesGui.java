package com.pigletlogic.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.pigletlogic.screens.MainMenuScreen;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.util.Constants;

public class RulesGui extends Gui
{
	private MainMenuScreen parent = null;
	private TextureRegion logoTexture = null;
	private Image logoImage = null;
	private float scale = 1.0f;
	private TextButton btnBack = null;
	private TextButtonStyle tbsNormal = null;
	private float pad = 0;
	private LabelStyle headingStyle = null;
	private LabelStyle textStyle = null;

	public RulesGui(MainMenuScreen p_parent)
	{
		super();

		parent = p_parent;

		scale = Constants.VIEWPORT_GUI_HEIGHT * 0.35f / 480;
		pad = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 15 / 480);

		logoTexture = new TextureRegion(Assets.instance.gui.regions.get("logo"));
		logoImage = new Image(logoTexture);
		headingStyle = new LabelStyle();
		headingStyle.font = parent.fBig;
		textStyle = new LabelStyle();
		textStyle.font = Assets.instance.assetFancyFonts.fontSans.get(18);

		table = new Table();
		Table tTopRight = new Table();
		tTopRight.debug();
		Table tTopLeft = new Table();
		table.setFillParent(true);
		table.setLayoutEnabled(true);
		table.debugCell();
		table.defaults().fill();
		table.align(Align.center);

		Table scrollTable = new Table();

		tbsNormal = new TextButtonStyle();
		tbsNormal.font = parent.fNormal;

		btnBack = new TextButton("BACK", Assets.instance.ui.defaultSkin);
		btnBack.setStyle(tbsNormal);

		btnBack.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				parent.handleRulesBackButton();
				return false;
			}
		});

		tTopLeft.add(logoImage).align(Align.top | Align.left).expand().width(scale * logoImage.getWidth()).height(scale * logoImage.getHeight());
		
		float topHeight = (scale * logoImage.getHeight());
		
		tTopRight.add(btnBack).align(Align.center | Align.right).expand().right().height(topHeight).padRight(pad);

		table.add(tTopLeft).expandX();
		table.add(tTopRight).expandX();

		// scroll
		final ScrollPane scroll = new ScrollPane(scrollTable);
		scroll.setScrollingDisabled(true, false);
		scroll.setFlingTime(0.5f);

		Label heading = new Label("Help", headingStyle);

		// "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed felis sapien, commodo at augue eu, consequat vehicula mauris. Fusce id tortor id arcu interdum pellentesque. Maecenas porttitor rhoncus ligula et elementum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse id fermentum felis. Maecenas blandit suscipit pulvinar. Maecenas feugiat purus in orci lacinia porta. Morbi cursus suscipit erat in scelerisque. Nullam ullamcorper sollicitudin fringilla. Sed tortor leo, vehicula nec elit a, tincidunt eleifend felis. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed felis sapien, commodo at augue eu, consequat vehicula mauris. Fusce id tortor id arcu interdum pellentesque. Maecenas porttitor rhoncus ligula et elementum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse id fermentum felis. Maecenas blandit suscipit pulvinar. Maecenas feugiat purus in orci lacinia porta. Morbi cursus suscipit erat in scelerisque. Nullam ullamcorper sollicitudin fringilla. Sed tortor leo, vehicula nec elit a, tincidunt eleifend felis. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed felis sapien, commodo at augue eu, consequat vehicula mauris. Fusce id tortor id arcu interdum pellentesque. Maecenas porttitor rhoncus ligula et elementum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse id fermentum felis. Maecenas blandit suscipit pulvinar. Maecenas feugiat purus in orci lacinia porta. Morbi cursus suscipit erat in scelerisque. Nullam ullamcorper sollicitudin fringilla. Sed tortor leo, vehicula nec elit a, tincidunt eleifend felis. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed felis sapien, commodo at augue eu, consequat vehicula mauris. Fusce id tortor id arcu interdum pellentesque. Maecenas porttitor rhoncus ligula et elementum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse id fermentum felis. Maecenas blandit suscipit pulvinar. Maecenas feugiat purus in orci lacinia porta. Morbi cursus suscipit erat in scelerisque. Nullam ullamcorper sollicitudin fringilla. Sed tortor leo, vehicula nec elit a, tincidunt eleifend felis."
		
		String p1 = Text.r1;
		
		String n = "\n";
		String n2 = "\n\n";
		
		String p2 = Text.r2;
		String p3 = Text.r3;
		String p4 = Text.r4;
		String p5 = Text.r5;
		String p6 = Text.r6;
		String p7 = Text.r7;
		String p8 = Text.r8;
		
		Label text = new Label(p1 + n2 + p2 + n2 + p3 + n + p4 + n2 + p5 + n + p6 + n2 + p7 + n2 + p8, textStyle);
		text.setWrap(true);

		scrollTable.add(heading).align(Align.left | Align.top).expand().padLeft(pad * 2);
		
		scrollTable.row();
		scrollTable.add(text).width(Constants.VIEWPORT_WIDTH - 5 * pad).align(Align.left | Align.top).expand().pad(pad, pad * 2, pad * 2, pad * 2);

		// boosters
		//TextureRegion r = new TextureRegion(Assets.instance.gui.regions.get("logo"));
		float boosterSize = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 60f / 480f);
		Image s = null;
		
		
		// BOOSTER 1
		s = new Image(Assets.instance.boosters.sprites.get("enlarge_big"));
		scrollTable.row();
		scrollTable.add(s).width(boosterSize).height(boosterSize);
		Label b1 = new Label(Text.r9, textStyle);
		scrollTable.row();
		scrollTable.add(b1).center();
		// ---
		
		scrollTable.row();
		scrollTable.add().height(2 * pad);
		scrollTable.row();
		
		// BOOSTER 2
		s = new Image(Assets.instance.boosters.sprites.get("bullet_vel_strong"));
		scrollTable.row();
		scrollTable.add(s).width(boosterSize).height(boosterSize);
		Label b2 = new Label(Text.r10, textStyle);
		scrollTable.row();
		scrollTable.add(b2).center();
		// ---
		
		scrollTable.row();
		scrollTable.add().height(2 * pad);
		scrollTable.row();
		
		// BOOSTER 3
		s = new Image(Assets.instance.boosters.sprites.get("add_big"));
		scrollTable.row();
		scrollTable.add(s).width(boosterSize).height(boosterSize);
		Label b3 = new Label(Text.r11, textStyle);
		scrollTable.row();
		scrollTable.add(b3).center();
		// ---
		
		scrollTable.row();
		scrollTable.add().height(2 * pad);
		scrollTable.row();
		
		// BOOSTER 4
		s = new Image(Assets.instance.boosters.sprites.get("against_ev_big"));
		scrollTable.row();
		scrollTable.add(s).width(boosterSize).height(boosterSize);
		Label b4 = new Label(Text.r12, textStyle);
		scrollTable.row();
		scrollTable.add(b4).center();
		// ---
		
		scrollTable.row();
		scrollTable.add().height(2 * pad);
		scrollTable.row();
		
		// BOOSTER 5
		s = new Image(Assets.instance.boosters.sprites.get("bomb_big"));
		scrollTable.row();
		scrollTable.add(s).width(boosterSize).height(boosterSize);
		Label b5 = new Label(Text.r13, textStyle);
		scrollTable.row();
		scrollTable.add(b5).center();
		// ---
		
		scrollTable.row();
		scrollTable.add().height(2 * pad);
		scrollTable.row();
		
		// BOOSTER 6
		s = new Image(Assets.instance.boosters.sprites.get("sniper_big"));
		scrollTable.row();
		scrollTable.add(s).width(boosterSize).height(boosterSize);
		Label b6 = new Label(Text.r14, textStyle);
		scrollTable.row();
		scrollTable.add(b6).center();
		// ---
		
		scrollTable.row();
		scrollTable.add().height(2 * pad);
		scrollTable.row();
		
		// BOOSTER 7
		s = new Image(Assets.instance.boosters.sprites.get("random"));
		scrollTable.row();
		scrollTable.add(s).width(boosterSize).height(boosterSize);
		Label b7 = new Label(Text.r15, textStyle);
		scrollTable.row();
		scrollTable.add(b7).center();
		// ---
		
		table.row();
		table.add(scroll).colspan(2).expand().fill().pad(pad);

		table.setPosition(Constants.VIEWPORT_WIDTH, 0);
		parent.getGame().getStage().addActor(table);
	}

	public void update(float p_delta)
	{
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			parent.handleRulesBackButton();
		}
	}
}