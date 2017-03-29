package com.pigletlogic.spaceoid;


public class Level
{
	public static final String TAG = Level.class.getName();

	private int id;
	private int order;
	private int passed;
	private int result;

	//  "id" : 0, "order" : 0, "passed" : 0, "result" : 0

	public Level(String p_id, String p_order, String p_passed, String p_result)
	{
		id = Integer.parseInt(p_id);
		order = Integer.parseInt(p_order);
		passed = Integer.parseInt(p_passed);
		result = Integer.parseInt(p_result);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getOrder()
	{
		return order;
	}

	public void setOrder(int order)
	{
		this.order = order;
	}

	public int getPassed()
	{
		return passed;
	}

	public void setPassed(int passed)
	{
		this.passed = passed;
	}

	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}



}
