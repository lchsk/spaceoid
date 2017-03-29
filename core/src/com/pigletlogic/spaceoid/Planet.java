package com.pigletlogic.spaceoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pigletlogic.spaceoid.effects.Effect;
import com.pigletlogic.spaceoid.effects.EffectRoundabout;
import com.pigletlogic.spaceoid.effects.EffectExplosion.EFFECT_EXPLOSION_TYPE;
import com.pigletlogic.spaceoid.types.PlanetType;
import com.pigletlogic.spaceoid.types.Type;
import com.pigletlogic.util.Constants;

public class Planet extends AbstractGameObject
{
	private static final String TAG = Planet.class.getName();

	private Type type;
	private Vector2 bottomLeft;
	private float power;

	private float computatedSize;

	private float scale;
	
	protected BitmapFont f2 = Assets.instance.assetBaseFonts.defaultNormal;

	protected float rotation;

	// is player holding his finger on this planet
	private boolean isPressed;

	// Mask arbitrarly turned on/off
	private boolean maskDrawing;
	private float maskMaxAlpha;
	private float maskDrawingCurrentTime;
	private float maskDrawingMaxTime;

	// time of
	private long start;
	private long end;
	private long diff;

	private Timer timer = null;

	// for boosters (really?)
	public float timeVisible = 0;
	public float blinkTime = 0;
	// ------------------------------
	
	// how much this planet has been enlarge in last frame
	private float lastEnlargementSize = 0;

	/** Effects */

	private EffectRoundabout roundaboutEffect = null;
	private Effect currentEffect = null;

	protected boolean visible;
	protected boolean fightingObject;

	/** Enlarging smoothly */
	private boolean enlarging;
	private float enlargementTime;
	private float enlargementSize;
	private float destinationSize;
	private float stepSize;
	private float tempTime;

	public Planet(Type p_type, int p_x, int p_y, float p_power)
	{
		resetMask();

		visible = false;
		enlarging = false;

		fightingObject = true;

		currentEffect = new Effect();

		type = p_type;

		power = p_power;

		computatedSize = 0;
		scale = 1.0f;
		bottomLeft = new Vector2(0, 0);

		position.x = p_x;
		position.y = p_y;

		rotation = 0.0f;

		computeSize();
		computeScale();
		computeScale(); // must be twice in the constructor!
		setCenterCoordinates();

		// Effects
		roundaboutEffect = new EffectRoundabout();
		roundaboutEffect.initializeInterval(power);

		// Timer
		timer = new Timer();
		timer.addTimer(roundaboutEffect.getRoundaboutInterval()); // Roundabout
																	// effect
																	// time
																	// [5-10s]
	}

	private void setCenterCoordinates()
	{

		bottomLeft.x = position.x - getType().getSprite().getWidth() / 2;
		bottomLeft.y = position.y - getType().getSprite().getHeight() / 2;
	}

	public float getBottomLeftX()
	{
		return bottomLeft.x;
	}

	public float getBottomLeftY()
	{
		return bottomLeft.y;
	}

	public Circle getCircle()
	{
		return new Circle(position.x, position.y, computatedSize / 2);
	}

	public float getActualWidth()
	{
		return type.getSprite().getWidth() * power / 100;
	}

	public float getActualHeight()
	{
		return type.getSprite().getHeight() * power / 100;
	}

	public float getPower()
	{
		return power;
	}

	public void setPower(float p_power)
	{	
		//if (p_power > 0 && p_power <= Constants.MIN_PLANET_POWER)
		//	power = Constants.MIN_PLANET_POWER;
		//else
			power = p_power;

		refresh();
		//computeSize();
		//computeScale();
	}
	
	private void clampSize()
	{
		power = MathUtils.clamp(power, - Constants.MIN_PLANET_POWER, Constants.MAX_PLANET_POWER);
	}

	public void addPower(float p_addend)
	{
		
		
		if (p_addend > 0)
		{
			if (power < 100)
			{
				float p = p_addend / 10;

				for (int i = 0; i < 10; ++i)
				{
					power += p;

					if (isCloseToScreenEdge()) break;
				}

			}
		}
		else if (p_addend < 0)
		{
			power += p_addend;
		}
		

		computeSize();
		computeScale();

		// Planet DIES
		// moved to GameScreen#killPlanets
//		if (power < Constants.MIN_PLANET_POWER_TO_SURVIVE) // used to be <= 0
//		{
//			visible = false;
//			Gdx.app.debug(TAG, "Visibility of " + this + " to false, because power (" + power + ") < " + Constants.MIN_PLANET_POWER_TO_SURVIVE);
//					
//		}

	}
	
	

	public Type getType()
	{
		return type;
	}

	public float getCenterX()
	{
		return position.x;
	}

	public float getCenterY()
	{
		return position.y;
	}

	public float getScale()
	{
		return scale;
	}

	public void setScale(float p_scale)
	{
		scale = p_scale;
	}

	/**
	 * Returns actual size of the planet
	 * 
	 * @return
	 */
	public float getComputedSize()
	{
		return computatedSize;
	}

	private void computeSize()
	{
		computatedSize = power * 1 / 200.0f * Constants.VIEWPORT_HEIGHT;
		setCenterCoordinates();
	}

	public void refresh()
	{
		computeSize();
		computeScale();
	}

	private void computeScale()
	{
		//float oldScale = scale;
		scale = computatedSize / type.getSprite().getWidth();
		
		float oldSize = type.getSprite().getHeight();

		if (computatedSize > 150)
		{
			type.setSpriteSize(300);
		}
		else if (computatedSize > 35)
		{
			type.setSpriteSize(100);
		}
		else
		{
			type.setSpriteSize(30);
		}
		
		//float scale3 = computatedSize / type.getSprite().getWidth();
		
		if (Math.abs(oldSize - type.getSprite().getHeight()) > 0.001f)
		{
			// Sprite was changed, need to update scale and position
			scale = computatedSize / type.getSprite().getWidth();
			setCenterCoordinates();
			
			//Gdx.app.debug("sprite", toString() + " old: " + oldSize + " new: " + type.getSprite().getHeight() + " oldscale: " + oldScale + " newScale: " + scale + " 3rdScale: " + scale3);
		}
		
		
	}

	@Override
	public void render(SpriteBatch batch)
	{
		if (visible)
		{
//			if (getPower() <= 10)
//			{
//				SpriteBatchTransparency.getInstance().setTransparency(0.6f);
//			}
			
				// draw planet
				batch.draw(getType().getSprite(), getBottomLeftX(), // x
					getBottomLeftY(), // y
					getType().getSprite().getWidth() / 2, // origin x
					getType().getSprite().getHeight() / 2, // origin // y
					getType().getSprite().getWidth(), // width
					getType().getSprite().getHeight(),// height
					getScale(), // scale x
					getScale(), // scale y
					rotation // rotation	
			);
			
//			if (getPower() <= 10)
//			{
//				SpriteBatchTransparency.getInstance().removeTransparency();
//				
//				// draw exclamation mark
//				
//				// draw planet
//				batch.draw(getType().getExclamation(), getBottomLeftX(), // x
//					getBottomLeftY(), // y
//					getType().getExclamation().getWidth() / 2, // origin x
//					getType().getExclamation().getHeight() / 2, // origin // y
//					getType().getExclamation().getWidth(), // width
//					getType().getExclamation().getHeight(),// height
//					.3f, // scale x
//					.3f, // scale y
//					0f // rotation	
//					);
//			}
			
			//Gdx.app.debug("scale", String.valueOf(getScale()));

			if (getType().isMaskDrawingOn() || getType().getPartMask())
			{
				// setTransparency(batch, currentEffect.getAlpha());
				SpriteBatchTransparency.getInstance().setTransparency(getType().getMaskAlpha());

				// draw mask
				batch.draw(getType().getCurrentMask(), getBottomLeftX(), // x
						getBottomLeftY(), // y
						getType().getSprite().getWidth() / 2, // origin x
						getType().getSprite().getHeight() / 2, // origin // y
						getType().getSprite().getWidth(), // width
						getType().getSprite().getHeight(),// height
						getScale(), // scale x
						getScale(), // scale y
						currentEffect.getRotation() // rotation
				);

				SpriteBatchTransparency.getInstance().removeTransparency();
			}

			if (Constants.DRAW_POWERS)
				f2.draw(batch, String.valueOf(Math.round(getPower())), getCenterX(), getCenterY());
		}

	}

	public void update(float deltaTime)
	{
		super.update(deltaTime);
		timer.update(deltaTime);

		turnOffMask(deltaTime);

		enlarge(deltaTime);

		setCenterCoordinates();

		computeRotation();

		//if (isPressed)
		if (diff > 0 || isPressed)
		{
			getType().setMask(PlanetType.MASK_WHITE);
			getType().setMaskAlpha(MathUtils.clamp(getPowerPercentage(), 0.15f, 0.4f));

		}
		else
		{
			if (maskDrawing)
			{
				// ...
			}
			else
			{
				getType().setMaskDrawing(false);

				if (roundaboutEffect.checkStartCondition(timer))
				{
					getType().setPartMask();
					currentEffect = roundaboutEffect;
					getType().setMaskAlpha(0.07f);
				}

				if (roundaboutEffect.checkStopCondition())
				{
					getType().setMaskDrawing(false);
					getType().setPartMaskDrawing(false);
				}
			}

		}
		
		clampSize();
	}

	private void turnOffMask(float p_delta)
	{
		if (maskDrawing)
		{
			maskDrawingCurrentTime += p_delta;

			if (maskDrawingCurrentTime >= maskDrawingMaxTime)
			{
				resetMask();
			}
			else
			{
				float a = 0.0f;

				// increase alpha
				if (maskDrawingCurrentTime / maskDrawingMaxTime < 0.5f)
				{
					a = maskDrawingCurrentTime / maskDrawingMaxTime / 2 / maskMaxAlpha;
				}
				else
				{
					// decrease alpha
					a = 1 - (maskDrawingCurrentTime / maskDrawingMaxTime / 2 / maskMaxAlpha);
				}
				getType().setMaskAlpha(a);
			}
		}
	}

	public void showMaskForSomeTime(float p_seconds, int p_maskType, float p_maxAlpha)
	{
		maskDrawing = true;
		maskDrawingCurrentTime = 0.0f;
		maskDrawingMaxTime = p_seconds;
		maskMaxAlpha = p_maxAlpha;
		getType().setMaskDrawing(true);
		getType().setMask(p_maskType);

	}

	public void resetMask()
	{
		maskDrawing = false;
		maskDrawingCurrentTime = 0;
		maskDrawingMaxTime = 0;
		maskMaxAlpha = 0.0f;
	}

	private void computeRotation()
	{
		float r = MathUtils.clamp(power / 20, 1.0f, 5.0f);

		rotation += r / 3.0f;
		rotation %= 360;
	}

	protected float getRotation()
	{
		return rotation;
	}

	private boolean isCloseToScreenEdge()
	{
		float right = this.getCircle().x + this.getCircle().radius;
		float left = this.getCircle().x - this.getCircle().radius;
		float top = this.getCircle().y + this.getCircle().radius;
		float bottom = this.getCircle().y - this.getCircle().radius;

		if (right >= Constants.VIEWPORT_WIDTH) return true;
		if (left <= 0) return true;
		if (top >= Constants.VIEWPORT_HEIGHT) return true;
		if (bottom <= 0) return true;

		return false;
	}

	public float computePower()
	{
		return MathUtils.clamp((diff) / 50, 5, Math.min(getPower() - Constants.MIN_PLANET_POWER, Constants.AI_MAX_BULLET_SIZE)); // used
																															// to
																															// -1
	}

	public float getPowerPercentage()
	{
		float c = MathUtils.clamp((end - start) / 50, 1, Math.min(getPower(), Constants.AI_MAX_BULLET_SIZE));
		return c / Math.min(getPower(), Constants.AI_MAX_BULLET_SIZE);
	}
	
	public boolean wasEnlargedLastTime()
	{
		return lastEnlargementSize > 0;
	}

	public boolean canEnlarge()
	{
		return ! isCloseToScreenEdge();
	}

	public boolean isPressed()
	{
		return isPressed;
	}

	public void setPressed(boolean p_pressed)
	{
		isPressed = p_pressed;
	}

	public void startPressing()
	{
		start = System.currentTimeMillis();
	}

	public void resetPressing()
	{
		start = 0;
		end = 0;
		setPressed(false);
	}

	public long getPressingStart()
	{
		return start;
	}

	public void endPressing()
	{
		diff = end - start;
	}

	public long getPressingEnd()
	{
		return end;
	}

	public void updateEndTime()
	{
		end = System.currentTimeMillis();
	}

	public long getPressingDiff()
	{
		return diff;
	}

	@Override
	public void destroy()
	{
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean p_visible)
	{
		visible = p_visible;
	}

	public boolean isFightingObject()
	{
		return fightingObject;
	}

	/**
	 * Moves the bullet just outside the planet
	 * 
	 * @param p_b
	 *            Bullet to move
	 * @param p_path
	 *            Path of the bullet
	 */
	public void moveBulletOutside(Bullet p_b)
	{
		Vector2 path = new Vector2(p_b.getVelocity().x / 10, p_b.getVelocity().y / 10);

		int i = 0;
		while (getCircle().overlaps(p_b.getCircle()) && i < 100)
		{
			p_b.setPosition(p_b.getCircle().x + path.x, p_b.getCircle().y + path.y);

			i++;
		}
		p_b.refresh();
	}

	public void moveVectorOutside(Vector2 p_v, Vector2 p_path)
	{
		Vector2 path = new Vector2(p_path.x / 1, p_path.y / 1);

		int i = 0;
		while (getCircle().contains(p_v) && i < 1000)
		{

			p_v.x += path.x;
			p_v.y += path.y;

			i++;
		}

	}

	private void enlarge(float p_delta)
	{
		if (enlarging)
		{
			tempTime += p_delta;
			addPower(stepSize * p_delta);
			refresh();

			if (tempTime >= enlargementTime)
			{
				enlarging = false;
			}
		}
	}

	/**
	 * MUST be called before {@link #enlargeSmoothly(float, float)}
	 */
	public void resetEnlargement()
	{
		if (enlarging)
		{
			power = destinationSize;
			enlarging = false;
			enlargementSize = 0;
			destinationSize = 0;
			enlargementTime = 0;
			stepSize = 0;

		}
	}
	
	public void resetEnlargementWithoutDestSize()
	{
		if (enlarging)
		{
			enlarging = false;
			enlargementSize = 0;
			destinationSize = 0;
			enlargementTime = 0;
			stepSize = 0;

		}
	}

	public void enlargeSmoothly(float p_value, float p_seconds)
	{
		enlarging = true;
		enlargementSize = p_value;
		destinationSize = power + p_value;
		enlargementTime = p_seconds;
		stepSize = enlargementSize / (enlargementTime * 1000) * 1000;
		tempTime = 0;
		
		lastEnlargementSize = p_value;
	}
	
	public void resetDiff()
	{
		diff = 0;
	}

	public String toString()
	{
		String what = isFightingObject() ? "F" : "B";
		String vis = isVisible() ? "VI" : "NV";	
		String enl = enlarging ? "+" : "=";
		
		return "PL " + enl + " [" + what + "] [" + getType().getString() + "] [" + Math.round(getPower()) + "]";
	}
	
	public boolean isEnlargin()
	{
		return enlarging;
	}
	
	public float getStepSize()
	{
		return stepSize;
	}
	
	public float getCenterXNorm()
	{
		return getCenterX() / Constants.VIEWPORT_WIDTH;
	}
	
	public float getCenterYNorm()
	{
		return getCenterY() / Constants.VIEWPORT_HEIGHT;
	}
}
