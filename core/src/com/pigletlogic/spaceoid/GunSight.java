package com.pigletlogic.spaceoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.pigletlogic.screens.GameScreen;
import com.pigletlogic.util.CameraHelper;
import com.pigletlogic.util.Constants;

public class GunSight
{
	private GameScreen gameScreen = null;

	private Sprite sSight = null;
	private float scale = 1.0f;

	public GunSight(GameScreen p_gameScreen)
	{
		gameScreen = p_gameScreen;

		sSight = Assets.instance.background.sprites.get("glow2");

		// scale of gunsight sprite
		scale = MathUtils.clamp(0.5f * Constants.VIEWPORT_HEIGHT / 480f, 0.25f, 1.0f);
	}

	public void render(float delta)
	{
		if (gameScreen.getInput().isPreparingShot())
		{
			float x = Gdx.input.getX() - sSight.getWidth() / 2;
			float y = CameraHelper.convertY(Gdx.input.getY()) - sSight.getHeight() / 2;

			SpriteBatchTransparency.getInstance().setTransparency(0.5f);

			gameScreen.getGame().getBatch().draw(sSight, x, // x
					y, // y
					sSight.getWidth() / 2, // origin x
					sSight.getHeight() / 2, // origin // y
					sSight.getWidth(), // width
					sSight.getHeight(),// height
					scale, // scale x
					scale, // scale y
					0 // rotation
					);
			SpriteBatchTransparency.getInstance().removeTransparency();
		}
	}
}
