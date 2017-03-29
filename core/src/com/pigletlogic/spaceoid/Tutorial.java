package com.pigletlogic.spaceoid;

import com.pigletlogic.gui.Text;
import com.pigletlogic.screens.GameScreen;
import com.pigletlogic.screens.GameScreenGui;

public class Tutorial
{
	private GameScreen gameScreen = null;

	// [1-]
	private int levelId = - 1;
	private boolean tutorial = false;

	public Tutorial(GameScreen p_gameScreen)
	{
		gameScreen = p_gameScreen;
		levelId = Assets.instance.assetLevels.currentLevel.getLevelId();

		init();
	}

	/**
	 * LEVEL 1 Welcome to Spaceoid It's an abstract strategy game, which demands
	 * logical thinking Spaceoid is set in space and depicts planets fighting
	 * for dominance Here are your planets (STRZALKI ON) And here is the enemy
	 * It is really easy to shoot him: Hold your finger on your planet You'see
	 * power indicator at the bottom of the screen Then drag your finger to the
	 * enemy planet and let go to shoot Remember not to release your finger
	 * before it is over the enemy planet! Good luck
	 */

	private void init()
	{
		if (levelId == 1)
		{
			tutorial = true;

			// gameScreen.getGui().newActionMessage(GameScreenGui.STATS_INDICATOR,
			// false);
			gameScreen.getGui().newActionMessage(GameScreenGui.ENLARGE_PLANETS, false);
			gameScreen.getGui().newMessage(Text.t1_1, 5);
			gameScreen.getGui().newMessage(Text.t1_2, 5);
			gameScreen.getGui().newMessage(Text.t1_3, 8);
			gameScreen.getGui().newMessage(Text.t1_4, 6);
			gameScreen.getGui().newMessage(Text.t1_5, 6);
			gameScreen.getGui().newMessage(Text.t1_6, 6);
			gameScreen.getGui().newMessage(Text.t1_7, 6);
			gameScreen.getGui().newMessage(Text.t1_8, 6);
			gameScreen.getGui().newMessage(Text.t1_9, 8);
			gameScreen.getGui().newMessage(Text.t1_10, 5);
			gameScreen.getGui().newMessage(Text.t1_11, 8);
			gameScreen.getGui().newMessage(Text.t1_12, 5);
		}
		else if (levelId == 2)
		{
			tutorial = true;

			gameScreen.getGui().newMessage(Text.t2_1, 5);
			gameScreen.getGui().newMessage(Text.t2_2, 7);
			gameScreen.getGui().newMessage(Text.t2_3, 6);
			gameScreen.getGui().newMessage(Text.t2_4, 7);
			gameScreen.getGui().newMessage(Text.t2_5, 8);
			gameScreen.getGui().newMessage(Text.t2_6, 7);
			gameScreen.getGui().newMessage(Text.t2_7, 6);
			gameScreen.getGui().newMessage(Text.t2_8, 8);
			gameScreen.getGui().newMessage(Text.t2_9, 7);
			gameScreen.getGui().newMessage(Text.t2_10, 7);
			gameScreen.getGui().newMessage(Text.t2_11, 6);

		}
		else if (levelId == 3)
		{
			tutorial = true;

			gameScreen.getGui().newMessage(Text.t3_1, 5);
			gameScreen.getGui().newMessage(Text.t3_2, 7);
			gameScreen.getGui().newMessage(Text.t3_3, 7);
			gameScreen.getGui().newMessage(Text.t3_4, 6);
			gameScreen.getGui().newMessage(Text.t3_5, 8);
			gameScreen.getGui().newMessage(Text.t3_6, 6);
			gameScreen.getGui().newMessage(Text.t3_7, 6);
			gameScreen.getGui().newMessage(Text.t3_8, 8);
			gameScreen.getGui().newMessage(Text.t3_9, 8);
			gameScreen.getGui().newMessage(Text.t3_10, 7);
			gameScreen.getGui().newMessage(Text.t3_11, 8);
			gameScreen.getGui().newMessage(Text.t3_12, 6);
			gameScreen.getGui().newMessage(Text.t3_13, 7);
			gameScreen.getGui().newMessage(Text.t3_14, 5);
		}
	}

	public boolean isTutorial()
	{
		return tutorial;
	}
}
