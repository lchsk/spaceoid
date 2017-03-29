package com.pigletlogic.util;

public class Constants
{
	/** DEBUG & VERSION */
	
	public static final float VERSION = 0.4f;
	public final static boolean DEBUG = false; // true if this version is meant
												// for debugging
	public final static boolean DEBUG_GUI = false;
	
	// true if want to draw labels next to planets and bullets with their powers
	public final static boolean DRAW_POWERS = false;
	
	/* ---------------------------------------------------------------*/

	public static float VIEWPORT_WIDTH = 800.0f;
	public static float VIEWPORT_HEIGHT = 480.0f;
	public static float VIEWPORT_GUI_WIDTH = 800.0f;
	public static float VIEWPORT_GUI_HEIGHT = 480.0f;

	public static final String TEXTURE_ATLAS_GUI = "images/gui.pack";

	public static float RATIO = 1.0f;
	
	public static boolean HELP_SHOOT = true;
	
	//public static float PLANET_GROWTH_MIN = .5f;
	//public static float PLANET_GROWTH_MAX = 1.0f;
	public static float PLANET_DEFAULT_GROWTH_MIN = 0.5f;
	public static float PLANET_DEFAULT_GROWTH_MAX = 1.5f;
	
	public static float BULLETS_SLOW_DOWN_FACTOR = 1.9f;
	
	/**
	 * Planet's growth factor (each frame)
	 */
	//public static float GROWTH_FACTOR = 2.3f; // 0.3

	/**
	 * Min time between AI's shots
	 */
	public static float AI_MIN_SHOOTING_TIME = 0.5f;
	public static float AI_MAX_SHOOTING_TIME = 5.0f;
	
	public static float AI_MIN_BULLET_SIZE = 5f;
	public static float AI_MAX_BULLET_SIZE = 50f;
	
	public static float MIN_PLANET_POWER = 5.0f;
	public static float MAX_PLANET_POWER = 100.0f;
	
	/**
	 * Number taken into care when computing speed of a bullet from power
	 */
	public static float SPEED_FACTOR = 2000.0f;
	
	public static float MIN_PLANET_POWER_TO_SURVIVE = 5.0f; // when planet's power drops below, dies
	
	public static float MIN_PLANET_POWER_TO_SHOOT = Constants.MIN_PLANET_POWER + Constants.AI_MIN_BULLET_SIZE;
	
	
	
	public static final float BOOSTER_BLINKING_THRESHOLD = 1.5f; // time before booster disappears and starts blinking
	public static final float BOOSTER_BLINKING_FREQUENCY = 1f / 5f;
	
	public static final String AD_UNIT_ID = "";
	
	// how long message is shown by default [seconds]
	public static final float DEFAULT_MESSAGE_ACTIVE_TIME = 3f;
	
	public static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=";
	public static final String GOOGLE_PLAY_PACKAGE = "com.pigletlogic.spaceoid";
	
	// if a planet has power betwen 5 and 10, it's enlarged at a faster rate
	public static final boolean ENLARGE_WEAK_PLANETS_FASTER = true;

	
	public static void init()
	{

	}

}
