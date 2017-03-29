package com.pigletlogic.gui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Gui
{
	private static String TAG = Gui.class.getName();
	
	public Table table = null;
	protected ArrayList<Table> levelTables = new ArrayList<Table>();

	public void init()
	{
	}

	public Table getTable()
	{
		return table;
	}

	public ArrayList<Table> getLevelTables()
	{
		return levelTables;
	}
	
	public void update(float p_delta)
	{

	}

}