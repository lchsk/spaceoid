package com.pigletlogic.spaceoid.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.pigletlogic.spaceoid.IActivityRequestHandler;
import com.pigletlogic.spaceoid.Spaceoid;

public class DesktopLauncher implements IActivityRequestHandler
{

	private static DesktopLauncher application;
	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;

	public static void main(String[] arg)
	{

		if (rebuildAtlas)
		{
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.debug = drawDebugOutline;
			
			
			String baseDir = "/home/lchsk/workspace/Spaceoid/desktop/";

			// TexturePacker2.process(settings, "assets-raw/images/planets",
			// "../Planets-android/assets/images", "planets.pack");

			TexturePacker.process(settings, baseDir + "assets-raw/images/planets_small", "../../android/assets/images/game", "planets_small.pack");

			TexturePacker.process(settings, baseDir + "assets-raw/images/masks_small", "../../android/assets/images/game", "masks_small.pack");

			TexturePacker.process(settings, baseDir + "assets-raw/images/stars", "../../android/assets/images/game", "stars.pack");

			TexturePacker.process(settings, baseDir + "assets-raw/images/background", "../../android/assets/images/game", "background.pack");

			TexturePacker.process(settings, baseDir + "assets-raw/images/gui", "../../android/assets/images/game", "gui.pack");
			
			TexturePacker.process(settings, baseDir + "assets-raw/images/boosters", "../../android/assets/images/game", "boosters.pack");
		}
		
		if (application == null) {
            application = new DesktopLauncher();
        }

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		Vector2 verysmall = new Vector2(480, 320);
		Vector2 small = new Vector2(800, 480);
		Vector2 middle = new Vector2(1080, 800);
		Vector2 big = new Vector2(1920, 1080);

		Vector2 used = small;

		config.width = Math.round(used.x);
		config.height = Math.round(used.y);
		new LwjglApplication(new Spaceoid(application), config);
	}

	@Override
	public void showAds(boolean show) {
		// TODO Auto-generated method stub
		
	}
}
