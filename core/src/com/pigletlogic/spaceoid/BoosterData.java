package com.pigletlogic.spaceoid;

import java.util.HashMap;
import java.util.Map;

import com.pigletlogic.spaceoid.types.PlanetType;

public class BoosterData
{
	public BoosterAction boosterAction;
	
	// user_color, data
	public Map<String, BoosterUserData> data = null;

	public BoosterData(BoosterAction p_boosterAction)
	{
		boosterAction = p_boosterAction;
		data = new HashMap<String, BoosterUserData>();
		
		for (String s : PlanetType.colorList)
		{
			data.put(s, new BoosterUserData(s, this));
		}
	}

	// ------------------------------------------------

	public boolean isColorFrozen(String p_color)
	{
		return data.get(p_color).timeLeft.get("freezing") > 0;
	}
	
	public BoosterAction getBoosterAction()
	{
		return boosterAction;
	}
	
}