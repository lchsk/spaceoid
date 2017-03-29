package com.pigletlogic.util;

import java.util.Random;

public class Util
{
	private static Util instance = null;
	
	public static Util getInstance()
	{
		if (instance == null)
			instance = new Util();
		
		return instance;
	}
	
	private Util()
	{
		rand = new Random();
	}
	
	// end of init
	
	// variables:
	
	public Random rand = null;
	
	// end of variables
	
	// methods
	
	public float getRandomFloat(float min, float max)
	{
		return rand.nextFloat() * (max - min) + min;
	}
	
	public float clamp(float p_val, float p_min, float p_max)
	{
		float min = p_min;
		float max = p_max;
		
		if (max < min)
			max = min;
		
		if (min > max)
			min = max;
		
		if (p_val < min)
			return min;
		
		if (p_val > max)
			return max;
		
		//if (p_val >= min && p_val <= max)
		return p_val;
	}
}
