package com.pigletlogic.spaceoid;

import java.util.ArrayList;

public class Timer
{
	private ArrayList<SingleTimeOut> times;
	private final float MARGIN = 0.001f;

	public Timer()
	{
		times = new ArrayList<SingleTimeOut>();
	}

	public void update(float delta)
	{
		for (SingleTimeOut f : times)
		{
			if (f.temp >= f.time)
			{
				f.temp = 0;
				f.finished = true;

			}
			else
			{
				f.temp += delta;
				f.finished = false;
			}
		}
	}

	/**
	 * 
	 * @param endTime
	 *            [s]
	 */
	public void addTimer(float endTime)
	{
		times.add(new SingleTimeOut(endTime));
	}

	/**
	 * 
	 * @param endTime
	 *            [s]
	 * @return
	 */
	public boolean isFinished(float endTime)
	{
		for (SingleTimeOut f : times)
		{

			if ((f.time + MARGIN >= endTime) && (f.time - MARGIN <= endTime)

			&& f.finished)
			{

				return true;

			}
		}

		return false;
	}
}

class SingleTimeOut
{
	float time;
	float temp;
	boolean finished;

	public SingleTimeOut(float time)
	{
		this.time = time;
		temp = 0;
		finished = false;

	}
}
