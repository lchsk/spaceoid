package com.pigletlogic.spaceoid.effects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Planet;
import com.pigletlogic.spaceoid.SpriteBatchTransparency;
import com.pigletlogic.util.Constants;

public class EffectExplosion extends Effect implements IEffect
{
	private float centerX = 0;
	private float centerY = 0;
	private float leftBottomX = 0;
	private float leftBottomY = 0;
	private Sprite sprite = null;
	private Planet planet = null;
	private float scale = 1f;
	private float explosionSize = 3f;
	//private float alpha = 1f;
	//private boolean visible = true;
	
	public enum EFFECT_EXPLOSION_TYPE { BOMB, SNIPER };
	private EFFECT_EXPLOSION_TYPE type;
	
	public EffectExplosion(Planet p_planet, EFFECT_EXPLOSION_TYPE p_type, Sprite p_sprite, float p_explosionSize)
	{
		super();
		planet = p_planet;
		type = p_type;
		sprite = p_sprite;
		explosionSize = p_explosionSize;
		
		if (type == EFFECT_EXPLOSION_TYPE.BOMB)
		{
			
			scale = planet.getType().getSprite().getWidth() / sprite.getWidth();
		}
		
		
		visible = true;
		alpha = 1f;
		centerX = planet.getCenterX();
		centerY = planet.getCenterY();
		leftBottomX = planet.getBottomLeftX() - sprite.getWidth() / 2 + planet.getType().getSprite().getWidth() / 2;
		leftBottomY = planet.getBottomLeftY() - sprite.getHeight() / 2 + planet.getType().getSprite().getHeight() / 2;
		
		
	}
	
	public void render(SpriteBatch batch)
	{
		if (visible)
		{
			SpriteBatchTransparency.getInstance().setTransparency(alpha);
			batch.draw(sprite, leftBottomX, // x
				leftBottomY, // y
				sprite.getWidth() / 2, // origin x
				sprite.getHeight() / 2, // origin // y
				sprite.getWidth(), // width
				sprite.getHeight(),// height
				scale, // scale x
				scale, // scale y
				0// rotation
				);
			SpriteBatchTransparency.getInstance().removeTransparency();
		}
	}
	
	public void update(float p_delta)
	{
//		double factor = Math.pow(scale, 1/4f);
//		Gdx.app.debug("costam", scale + " " + String.valueOf(factor));
//		scale += p_delta * MathUtils.clamp((float)factor, 3f, 10000f);
		
		if (type == EFFECT_EXPLOSION_TYPE.BOMB)
		{
			scale += p_delta * explosionSize * Constants.VIEWPORT_HEIGHT / 480f;
			alpha -= p_delta * scale * 480f / Constants.VIEWPORT_HEIGHT;
		}
		
		if (alpha <= 0)
		{
			visible = false;
		}
	}
}
