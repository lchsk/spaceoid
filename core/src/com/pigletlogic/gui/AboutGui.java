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

public class AboutGui extends Gui
{
	private MainMenuScreen parent = null;

	private TextureRegion logoTexture = null;
	private Image logoImage = null;
	private TextureRegion PLlogoTexture = null;
	private Image PLlogoImage = null;

	private float scale = 1.0f;
	private TextButton btnBack = null;
	private TextButtonStyle tbsNormal = null;
	private float pad = 0;
	private LabelStyle headingStyle = null;
	private LabelStyle textBigStyle = null;
	private LabelStyle textStyle = null;

	public AboutGui(MainMenuScreen p_parent)
	{
		super();

		parent = p_parent;

		scale = Constants.VIEWPORT_GUI_HEIGHT * 0.4f / 480;
		pad = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 15 / 480);

		logoTexture = new TextureRegion(Assets.instance.gui.regions.get("logo"));
		logoImage = new Image(logoTexture);
		PLlogoTexture = new TextureRegion(Assets.instance.gui.regions.get("piglet_logic_1"));
		PLlogoImage = new Image(PLlogoTexture);

		headingStyle = new LabelStyle();
		headingStyle.font = parent.fBig;
		textBigStyle = new LabelStyle();
		textBigStyle.font = Assets.instance.assetFancyFonts.fontSans.get(18);
		textStyle = new LabelStyle();
		textStyle.font = Assets.instance.assetFancyFonts.fontSans.get(14);

		table = new Table();
		Table tTopRight = new Table();
		tTopRight.debug();
		Table tTopLeft = new Table();
		table.setFillParent(true);
		table.setLayoutEnabled(true);
		table.debug();
		table.defaults().fill();
		table.align(Align.center);

		Table scrollTable = new Table();
		scrollTable.debugCell();

		tbsNormal = new TextButtonStyle();
		tbsNormal.font = parent.fNormal;

		btnBack = new TextButton("BACK", Assets.instance.ui.defaultSkin);
		btnBack.setStyle(tbsNormal);

		btnBack.addListener(new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				parent.handleAboutBackButton();
				return false;
			}
		});

		Label heading = new Label("Credits", headingStyle);

		tTopLeft.add(heading).pad(pad).align(Align.left).expand();
		
		float topHeight = heading.getHeight() + 2 * pad;

		tTopRight.add(btnBack).align(Align.center | Align.right).expand().right().height(topHeight).padRight(pad);

		table.add(tTopLeft).expandX();
		table.add(tTopRight).expand();

		// scroll
		final ScrollPane scroll = new ScrollPane(scrollTable);
		scroll.setScrollingDisabled(true, false);
		scroll.setFlingTime(0);

		Label h1 = new Label("Game Design and Programming", textBigStyle);
		Label t1 = new Label("Maciek Lechowski", textStyle);
		t1.setWrap(true);
		t1.setAlignment(Align.center);
		
		Label h2 = new Label("Music", textBigStyle);
		Label t2 = new Label("'resonating very far away' by burning-mir\nwww.freesound.org/people/burning-mir/sounds/149069/", textStyle);
		t2.setWrap(true);
		t2.setAlignment(Align.center);
		
		Label t3 = new Label("'Happy Guitar' by edtijo\nwww.freesound.org/people/edtijo/sounds/207558/", textStyle);
		t3.setWrap(true);
		t3.setAlignment(Align.center);
		
		Label h3 = new Label("Contact", textBigStyle);
		Label t4 = new Label("www.pigletlogic.com\nhq@pigletlogic.com", textStyle);
		t4.setWrap(true);
		t4.setAlignment(Align.center);
		
		Label t5 = new Label("Carefully crafted in Krakow, Poland", textStyle);
		t5.setWrap(true);
		t5.setAlignment(Align.center);

		// Addint to scrollTable
		scrollTable.add(logoImage).align(Align.center | Align.top).width(scale * logoImage.getWidth()).height(scale * logoImage.getHeight());
		
		//scrollTable.row();
		scrollTable.add(new Label("by", textStyle)).pad(pad);
		//scrollTable.row();
		scrollTable.add(PLlogoImage).align(Align.center | Align.top).width(scale * PLlogoImage.getWidth())
				.height(scale * PLlogoImage.getHeight());
		
		Table credits = new Table();
		credits.debugCell();
		
//		scrollTable.row();
//		scrollTable.add(h1);
//		scrollTable.row();
//		scrollTable.add(t1);
//		
//		scrollTable.row().height(scale);
//		
//		scrollTable.row();
//		scrollTable.add(h2);
//		scrollTable.row();
//		scrollTable.add(t2);
		
		credits.row();
		credits.add(h1);
		credits.row();
		credits.add(t1);
		
		credits.row();
		credits.add().height(pad);
		credits.row();
		
		credits.row();
		credits.add(h3);
		credits.row();
		credits.add(t4);
		
		credits.row();
		credits.add().height(pad);
		credits.row();
		
		credits.row();
		credits.add(h2);
		credits.row();
		credits.add(t2);
		credits.row();
		credits.add(t3);
		
		credits.row();
		credits.add().height(pad);
		credits.row();
		
		credits.add(t5);
		
		//scrollTable.add(text).width(Constants.VIEWPORT_WIDTH - 5 * pad).align(Align.center | Align.top).expand().pad(pad, pad * 2, pad * 2, pad * 2);

		//scrollTable.add(textMusic).width(Constants.VIEWPORT_WIDTH - 5 * pad).align(Align.center | Align.top).expand().pad(pad, pad * 2, pad * 2, pad * 2);

		scrollTable.row().colspan(3);
		scrollTable.add(credits).expand().colspan(3);
		// end of scrollTable
		
		table.row();
		table.add(scroll).colspan(2).expand().fill().pad(pad);

		table.setPosition(Constants.VIEWPORT_WIDTH, 0);
		parent.getGame().getStage().addActor(table);
	}
	
	public void update(float p_delta)
	{
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			parent.handleAboutBackButton();
		}
	}
}