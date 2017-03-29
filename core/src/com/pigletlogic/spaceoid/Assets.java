package com.pigletlogic.spaceoid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.json.generators.JSONGenerator;
import com.json.generators.JsonGeneratorFactory;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;
import com.pigletlogic.spaceoid.types.PlanetType;
import com.pigletlogic.util.Constants;

public class Assets implements Disposable, AssetErrorListener
{

	public static final String TAG = Assets.class.getName();

	public static final Assets instance = new Assets();

	private AssetManager assetManager = null;

	public AssetPlanets planets = null;
	public AssetBoosters boosters = null;

	public AssetMasks masks = null;

	public AssetLevels assetLevels = null;

	public AssetBaseFonts assetBaseFonts = null;
	public AssetFancyFonts assetFancyFonts = null;

	public AssetStars stars = null;
	public AssetBackground background = null;
	public AssetGui gui = null;
	public AssetSounds sounds = null;

	private FreeTypeFontGenerator generator = null;
	public AssetUI ui;

	public Loader loader = null;

	private float TO_LOAD = 9;
	private float ALREADY_LOADED = 0;

	public boolean loadedEverything = false;

	/** FILE HANDLES */
	private FileHandle fileInternal = null;
	private FileHandle fileLocal = null;
	private FileHandle lastUsedFileHandle = null;
	private FileHandle allLevelsFileHandle = null; // levels.json file

	/**
	 * TO LOAD: 1) BASE FONTS 2) GUI 3) LEVELS 4) UI 5) PLANETS 6) MASKS 7)
	 * BACKGROUND 8) STARS 9) FANCY FONTS
	 */

	private final boolean refreshFiles = false;

	private Assets()
	{
	}
	/**
	 * Loads and returns text file
	 * 
	 * @param p_filename
	 * @param p_loadFresh if true, it always loads the file from internal storage
	 * @return
	 */
	private String loadTextFile(String p_filename, boolean p_loadFresh)
	{
		fileLocal = Gdx.files.local(p_filename);

		// Local: can change files
		// Internal: cannot change files
		if (fileLocal.exists() && ! refreshFiles && ! p_loadFresh)
		{
			// load string

			Gdx.app.debug(TAG, "File " + p_filename + " found in local storage.");

			lastUsedFileHandle = fileLocal;

			return fileLocal.readString();
		}
		else
		{
			// load file from internal storage and move it to local storage

			Gdx.app.debug(TAG, "File " + p_filename + " NOT found in local storage. Moving from internal to local.");

			fileInternal = Gdx.files.internal(p_filename);
			lastUsedFileHandle = fileInternal;

			String str = fileInternal.readString();

			fileLocal.writeString(str, false);
			
			lastUsedFileHandle = Gdx.files.local(p_filename);

			return str;
		}
	}

	public class AssetLevels
	{

		public List<Level> levels = null;
		public LevelData currentLevel = null;

		private Comparator<Level> levelComparator = null;

		public boolean reloadLevelsData = false;

		public AssetLevels()
		{
			currentLevel = new LevelData();

			levelComparator = new Comparator<Level>()
			{
				@Override
				public int compare(Level o1, Level o2)
				{
					return (int) (o1.getOrder() - o2.getOrder());
				}
			};

			levels = new ArrayList<Level>();

			loadLevels();

		}

		/**
		 * Loads list of levels
		 */
		public void loadLevels()
		{
			levels.clear();

			String jsonString = loadTextFile("data/levels/levels.json", false);
			

			allLevelsFileHandle = lastUsedFileHandle;

			JsonParserFactory factory = JsonParserFactory.getInstance();
			JSONParser parser = factory.newJsonParser();

			Map json = parser.parseJson(jsonString);
			currentLevel.json = json;

			List ids = (List) json.get("levels");

			// iterating over levels
			for (int i = 0; i < ids.size(); ++i)
			{

				String id = (String) ((Map) ids.get(i)).get("id");
				String order = (String) ((Map) ids.get(i)).get("order");
				String passed = (String) ((Map) ids.get(i)).get("passed");
				String score = (String) ((Map) ids.get(i)).get("score");

				levels.add(new Level(id, order, passed, score));

			}

			// sort
			Collections.sort(levels, levelComparator);
		}

		/**
		 * Called after passing a level. Tries to currentLevel++
		 * 
		 * @return false if cannot advance to the next level
		 */
		public boolean tryAdvanceToNextLevel()
		{
			if (currentLevel.getLevelId() >= getLevelsCount())
				return false;
			else
			{
				Gdx.app.debug(TAG, "LEVEL UP");
				currentLevel.setLevelId(currentLevel.getLevelId() + 1);
				return true;
			}
		}

		private void loadLevelAI(int p_id, Map json)
		{
			Map ai = (Map) json.get("ai");

			if (ai != null)
			{

				String order = (String) (ai.get("order"));
				int speed = Integer.parseInt((String) (ai.get("speed")));
				int bulletSize = Integer.parseInt((String) (ai.get("bullet_size")));
				boolean againstHuman = Boolean.parseBoolean((String) ai.get("against_human"));

				if (currentLevel != null)
				{
					currentLevel.setAIBulletSize(bulletSize);
					currentLevel.setAIOrder(order);
					currentLevel.setAISpeed(speed);
					currentLevel.setAgainstHuman(againstHuman);
				}
			}
		}

		/**
		 * Saves current level as passed with a given grade
		 */
		public void saveLevelAsPassed(int p_grade)
		{
			try
			{
			JsonGeneratorFactory factory = JsonGeneratorFactory.getInstance();
			JSONGenerator generator = factory.newJsonGenerator();

			List ids = (List) currentLevel.json.get("levels");

			for (int i = 0; i < ids.size(); ++i)
			{
				int id = Integer.parseInt((String) ((Map) ids.get(i)).get("id"));

				if (id == currentLevel.getLevelId() - 1)
				{
					int oldScore = Integer.parseInt((String) ((Map) ids.get(i)).get("score"));

					if (p_grade > oldScore)
					{
						((Map) ids.get(i)).put("passed", "1");
						((Map) ids.get(i)).put("score", Integer.toString(p_grade));
					}
				}
			}

			String json = generator.generateJson(currentLevel.json);

			// hack needed - json has brackets at first and last character
			json = json.substring(1, json.length() - 1);

			allLevelsFileHandle.writeString(json, false);

			Assets.instance.assetLevels.reloadLevelsData = true;
			
			Gdx.app.debug(TAG, "Levels saved");
			
			}
			catch(Exception e)
			{
				Gdx.app.debug(TAG, "Error while saving levels file. " + e.getMessage());
			}
		}

		private String getValue(Map p_map, String p_key)
		{
			String val = (String) p_map.get(p_key);
			
			if (val == null)
			{
				Gdx.app.debug(TAG, "Value for " + p_key + " not found in the level file. Using default instead.");
				
				return null;
			}
			
			return val;
		}
		
		public void loadLevelData(int p_id)
		{
String tmp;
			
			// p_id [0, Max - 1]
			String jsonString = loadTextFile("data/levels/level_" + p_id + ".json", true);
			Gdx.app.debug(TAG, "level text: " + jsonString);
			Set<String> uniqueColors = new HashSet<String>();

			currentLevel.reset();

			JsonParserFactory factory = JsonParserFactory.getInstance();
			JSONParser parser = factory.newJsonParser();

			Map json = parser.parseJson(jsonString);

			Map level = (Map) json.get("level");

			// String colors = (String) level.get("colors");
			String humanColor = (String) level.get("human");
			float boosterFreq = Float.parseFloat((String) level.get("booster_freq")); // how
																						// often
																						// the
																						// boosters
																						// show
																						// up
			float boosterActiveTimeFactor = Float.parseFloat((String) level.get("booster_active_time_factor"));
			
			float planetGrowthMin = Constants.PLANET_DEFAULT_GROWTH_MIN;
			tmp = getValue(level, "planet_growth_min");
			if (tmp != null)
			{
				planetGrowthMin = Float.parseFloat(tmp);
			}
			
			float planetGrowthMax = Constants.PLANET_DEFAULT_GROWTH_MAX;
			tmp = getValue(level, "planet_growth_max");
			if (tmp != null)
			{
				planetGrowthMax = Float.parseFloat(tmp);
			}

			currentLevel.setHumanColor(humanColor);
			currentLevel.setBoosterFreq(boosterFreq);
			currentLevel.setBoosterActiveTimeFactor(boosterActiveTimeFactor);
			currentLevel.setPlanetGrowthFactorMin(planetGrowthMin);
			currentLevel.setPlanetGrowthFactorMax(planetGrowthMax);

			List planets = (List) json.get("planets");

			for (int i = 0; i < planets.size(); ++i)
			{
				String power = (String) ((Map) planets.get(i)).get("power");
				String color = (String) ((Map) planets.get(i)).get("color");
				String x = (String) ((Map) planets.get(i)).get("x");
				String y = (String) ((Map) planets.get(i)).get("y");

				uniqueColors.add(color);

				int screen_x = (int) (Float.parseFloat(x) * Constants.VIEWPORT_WIDTH);
				int screen_y = (int) (Float.parseFloat(y) * Constants.VIEWPORT_HEIGHT);

				if (color.equals(humanColor))
				{
					// human
					currentLevel.addHumanPower(Integer.parseInt(power));
				}
				else
				{
					// to do later if powers of respective computer players are
					// needed
				}

				Planet p = new Planet(new PlanetType(color), screen_x, screen_y, Float.parseFloat(power));

				currentLevel.addPlanet(p);
			}

			currentLevel.setColors(uniqueColors.size());
			loadLevelAI(p_id, json);

		}

		public int getLevelsCount()
		{
			return levels.size();
		}

	}

	public class AssetFancyFonts
	{
		public HashMap<Integer, BitmapFont> fontDefault = null;
		public HashMap<Integer, BitmapFont> fontSans = null;

		/**
		 * FONT SIZES
		 */
		private int fSizeNormal = 0;
		private int fSizeBig = 0;
		private int fSizeExtraBig = 0;
		
		public int fontSizeSpacemanExtraBig = 40;
		public int fontSizeSpacemanBig = 36;
		public int fontSizeSpacemanNormal = 26;
		
		/**
		 * FONT SIZES 2
		 */
		private int fSansSizeText = 0;
		private int fSansSizeNormal = 0;
		
		public int fontSizeSansNormal = 18;
		public int fontSizeSansText = 14;

		// public int fSansSizeBig = 0;

		public AssetFancyFonts()
		{
			/** 1 SPACEMAN */

			generator = new FreeTypeFontGenerator(Assets.instance.ui.defaultFont);
			fontDefault = new HashMap<Integer, BitmapFont>();

			// generate actual sizes
			fSizeNormal = Math.round(Constants.VIEWPORT_GUI_HEIGHT * fontSizeSpacemanNormal / 480);
			fSizeBig = Math.round(Constants.VIEWPORT_GUI_HEIGHT * fontSizeSpacemanBig / 480);
			fSizeExtraBig = Math.round(Constants.VIEWPORT_GUI_HEIGHT * fontSizeSpacemanExtraBig / 480);

			// generate fonts
			fontDefault.put(fontSizeSpacemanNormal, generator.generateFont(fSizeNormal));
			fontDefault.put(fontSizeSpacemanBig, generator.generateFont(fSizeBig));
			fontDefault.put(fontSizeSpacemanExtraBig, generator.generateFont(fSizeExtraBig));
			BitmapFontData b = new BitmapFontData();

			/** 2 SANS */
			generator = new FreeTypeFontGenerator(Assets.instance.ui.fontProstoSansBold);
			fontSans = new HashMap<Integer, BitmapFont>();

			// generate actual sizes
			fSansSizeText = Math.round(Constants.VIEWPORT_GUI_HEIGHT * fontSizeSansText / 480);
			fSansSizeNormal = Math.round(Constants.VIEWPORT_GUI_HEIGHT * fontSizeSansNormal / 480);
			
			
			
			// fSansSizeBig = Math.round(Constants.VIEWPORT_GUI_HEIGHT * 36 /
			// 480);

			// generate fonts
			fontSans.put(fontSizeSansText, generator.generateFont(fSansSizeText));
			fontSans.put(fontSizeSansNormal, generator.generateFont(fSansSizeNormal));
			// fontSans.put(36, generator.generateFont(fSansSizeBig));
		}
	}

	public class AssetBaseFonts
	{

		public final BitmapFont defaultNormal;

		public AssetBaseFonts()
		{

			defaultNormal = new BitmapFont(Gdx.files.internal("fonts/default.fnt"), false);

			defaultNormal.setScale(1.0f);

		}
	}

	public class AssetSounds
	{
		public Music trackTheme = null;
		public Music trackGuitar = null;

		public AssetSounds()
		{
			trackTheme = Gdx.audio.newMusic(Gdx.files.internal("sfx/spaceoid-theme.ogg"));
			trackGuitar = Gdx.audio.newMusic(Gdx.files.internal("sfx/spaceoid-guitar.ogg"));
		}
	}

	public class AssetUI
	{
		private FileHandle h = null;
		public Skin defaultSkin = null;

		public FileHandle defaultFont = null;
		public FileHandle fontProstoSansBold = null;

		public AssetUI()
		{
			h = Gdx.files.internal("images/uiskin.json");

			defaultSkin = new Skin(h);

			defaultFont = Gdx.files.internal("fonts/SPACEMAN.TTF");
			fontProstoSansBold = Gdx.files.internal("fonts/ProstoSansBold.otf");
		}
	}

	public class AssetBoosters
	{
		private Map<String, AtlasRegion> regions = null;
		public Map<String, Sprite> sprites = null;

		public AssetBoosters(TextureAtlas atlas)
		{
			regions = new HashMap<String, AtlasRegion>();

			regions.put("add_big", atlas.findRegion("add_big"));
			regions.put("add_weak", atlas.findRegion("add_weak"));
			regions.put("against_ev_big", atlas.findRegion("against_ev_big"));
			regions.put("against_ev_weak", atlas.findRegion("against_ev_weak"));
			regions.put("bomb_big", atlas.findRegion("bomb_big"));
			regions.put("bomb_med", atlas.findRegion("bomb_med"));
			regions.put("bomb_weak", atlas.findRegion("bomb_weak"));
			regions.put("bullet_vel_strong", atlas.findRegion("bullet_vel_strong"));
			regions.put("bullet_vel_weak", atlas.findRegion("bullet_vel_weak"));
			regions.put("enlarge_big", atlas.findRegion("enlarge_big"));
			regions.put("enlarge_med", atlas.findRegion("enlarge_med"));
			regions.put("enlarge_small", atlas.findRegion("enlarge_small"));
			regions.put("freezing_strong", atlas.findRegion("freezing_strong"));
			regions.put("freezing_weak", atlas.findRegion("freezing_weak"));
			regions.put("random", atlas.findRegion("random"));
			regions.put("sniper_big", atlas.findRegion("sniper_big"));
			regions.put("sniper_weak", atlas.findRegion("sniper_weak"));
			regions.put("timer_big", atlas.findRegion("timer_big"));
			regions.put("timer_weak", atlas.findRegion("timer_weak"));

			sprites = new HashMap<String, Sprite>();

			for (Map.Entry<String, AtlasRegion> e : regions.entrySet())
			{
				sprites.put(e.getKey(), new Sprite(e.getValue()));
			}

			for (Map.Entry<String, Sprite> e : sprites.entrySet())
			{
				e.getValue().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}
	}

	public class AssetPlanets
	{

		private Map<String, AtlasRegion> regions = null;
		public Map<String, Sprite> sprites = null;

		public AssetPlanets(TextureAtlas atlas)
		{
			regions = new HashMap<String, AtlasRegion>();
			sprites = new HashMap<String, Sprite>();

			// 1) Regions
			regions.put("red30", atlas.findRegion("planet2_2", 30));
			regions.put("red100", atlas.findRegion("planet2_2", 100));
			regions.put("red300", atlas.findRegion("planet2_2", 300));

			regions.put("blue30", atlas.findRegion("planet3_2", 30));
			regions.put("blue100", atlas.findRegion("planet3_2", 100));
			regions.put("blue300", atlas.findRegion("planet3_2", 300));

			regions.put("green30", atlas.findRegion("planet4_2", 30));
			regions.put("green100", atlas.findRegion("planet4_2", 100));
			regions.put("green300", atlas.findRegion("planet4_2", 300));

			regions.put("yellow30", atlas.findRegion("planet5_2", 30));
			regions.put("yellow100", atlas.findRegion("planet5_2", 100));
			regions.put("yellow300", atlas.findRegion("planet5_2", 300));

			regions.put("purple30", atlas.findRegion("planet6_2", 30));
			regions.put("purple100", atlas.findRegion("planet6_2", 100));
			regions.put("purple300", atlas.findRegion("planet6_2", 300));

			regions.put("pink30", atlas.findRegion("planet7_2", 30));
			regions.put("pink100", atlas.findRegion("planet7_2", 100));
			regions.put("pink300", atlas.findRegion("planet7_2", 300));

			regions.put("brown30", atlas.findRegion("planet8_2", 30));
			regions.put("brown100", atlas.findRegion("planet8_2", 100));
			regions.put("brown300", atlas.findRegion("planet8_2", 300));

			for (Map.Entry<String, AtlasRegion> e : regions.entrySet())
			{
				sprites.put(e.getKey(), new Sprite(e.getValue()));
			}

			for (Map.Entry<String, Sprite> e : sprites.entrySet())
			{
				e.getValue().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}

		}
	}

	public class AssetMasks
	{

		// Red
		private AtlasRegion arRed30 = null;
		private AtlasRegion arRed100 = null;
		private AtlasRegion arRed300 = null;

		// White
		private AtlasRegion arWhite30 = null;
		private AtlasRegion arWhite100 = null;
		private AtlasRegion arWhite300 = null;

		// Black
		private AtlasRegion arBlack30 = null;
		private AtlasRegion arBlack100 = null;
		private AtlasRegion arBlack300 = null;

		// White part mask
		private AtlasRegion arWhitePart30 = null;
		private AtlasRegion arWhitePart100 = null;
		private AtlasRegion arWhitePart300 = null;

		/* Sprite */

		public Sprite red30 = null;
		public Sprite red100 = null;
		public Sprite red300 = null;

		public Sprite white30 = null;
		public Sprite white100 = null;
		public Sprite white300 = null;

		public Sprite black30 = null;
		public Sprite black100 = null;
		public Sprite black300 = null;

		public Sprite whitePart30 = null;
		public Sprite whitePart100 = null;
		public Sprite whitePart300 = null;

		public Map<String, Sprite> sprites = null;

		public AssetMasks(TextureAtlas atlas)
		{
			sprites = new HashMap<String, Sprite>();

			// 1) Regions

			// Red
			arRed30 = atlas.findRegion("planet2_2_red", 30);
			arRed100 = atlas.findRegion("planet2_2_red", 100);
			arRed300 = atlas.findRegion("planet2_2_red", 300);

			// White
			arWhite30 = atlas.findRegion("planet2_2_white", 30);
			arWhite100 = atlas.findRegion("planet2_2_white", 100);
			arWhite300 = atlas.findRegion("planet2_2_white", 300);

			// White Part
			arWhitePart30 = atlas.findRegion("planet2_2_white_part", 30);
			arWhitePart100 = atlas.findRegion("planet2_2_white_part", 100);
			arWhitePart300 = atlas.findRegion("planet2_2_white_part", 300);

			// Black
			arBlack30 = atlas.findRegion("planet2_2_black", 30);
			arBlack100 = atlas.findRegion("planet2_2_black", 100);
			arBlack300 = atlas.findRegion("planet2_2_black", 300);

			// 2) Sprites

			red30 = new Sprite(arRed30);
			red100 = new Sprite(arRed100);
			red300 = new Sprite(arRed300);

			white30 = new Sprite(arWhite30);
			white100 = new Sprite(arWhite100);
			white300 = new Sprite(arWhite300);

			whitePart30 = new Sprite(arWhitePart30);
			whitePart100 = new Sprite(arWhitePart100);
			whitePart300 = new Sprite(arWhitePart300);

			black30 = new Sprite(arBlack30);
			black100 = new Sprite(arBlack100);
			black300 = new Sprite(arBlack300);

			// 3) Map

			sprites.put("red30", red30);
			sprites.put("red100", red100);
			sprites.put("red300", red300);

			sprites.put("white30", white30);
			sprites.put("white100", white100);
			sprites.put("white300", white300);

			sprites.put("whitePart30", whitePart30);
			sprites.put("whitePart100", whitePart100);
			sprites.put("whitePart300", whitePart300);

			sprites.put("black30", black30);
			sprites.put("black100", black100);
			sprites.put("black300", black300);

			// Setting filtering for every sprite

			for (Map.Entry<String, Sprite> e : sprites.entrySet())
			{
				e.getValue().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}

		}
	}

	public class AssetBackground
	{

		// Red
		private AtlasRegion arGlow1 = null;
		private AtlasRegion arGlow2 = null;

		/* Sprite */

		public Sprite glow1 = null;
		public Sprite glow2 = null;

		public Map<String, Sprite> sprites = null;

		public AssetBackground(TextureAtlas atlas)
		{
			sprites = new HashMap<String, Sprite>();

			// 1) Regions

			// Red
			arGlow1 = atlas.findRegion("glow1");
			arGlow2 = atlas.findRegion("glow2");

			// 2) Sprites

			glow1 = new Sprite(arGlow1);
			glow2 = new Sprite(arGlow2);

			// 3) Map

			sprites.put("glow1", glow1);
			sprites.put("glow2", glow2);

			// Setting filtering for every sprite

			for (Map.Entry<String, Sprite> e : sprites.entrySet())
			{
				e.getValue().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}

		}
	}

	public class AssetGui
	{

		public Map<String, AtlasRegion> regions = null;
		public Map<String, Sprite> sprites = null;

		public AssetGui(TextureAtlas atlas)
		{
			regions = new HashMap<String, AtlasRegion>();
			sprites = new HashMap<String, Sprite>();

			// 1) Regions

			regions.put("lines_red", atlas.findRegion("lines_red"));
			regions.put("lines_blue", atlas.findRegion("lines_blue"));
			regions.put("lines_green", atlas.findRegion("lines_green"));
			regions.put("lines_yellow", atlas.findRegion("lines_yellow"));
			regions.put("lines_purple", atlas.findRegion("lines_purple"));
			regions.put("lines_pink", atlas.findRegion("lines_pink"));
			regions.put("lines_brown", atlas.findRegion("lines_brown"));

			regions.put("shot_power_red", atlas.findRegion("shot_power_red"));
			regions.put("shot_power_blue", atlas.findRegion("shot_power_blue"));
			regions.put("shot_power_green", atlas.findRegion("shot_power_green"));
			regions.put("shot_power_yellow", atlas.findRegion("shot_power_yellow"));
			regions.put("shot_power_purple", atlas.findRegion("shot_power_purple"));
			regions.put("shot_power_pink", atlas.findRegion("shot_power_pink"));
			regions.put("shot_power_brown", atlas.findRegion("shot_power_brown"));

			regions.put("stats_red", atlas.findRegion("stats_red"));
			regions.put("stats_blue", atlas.findRegion("stats_blue"));
			regions.put("stats_green", atlas.findRegion("stats_green"));
			regions.put("stats_yellow", atlas.findRegion("stats_yellow"));
			regions.put("stats_purple", atlas.findRegion("stats_purple"));
			regions.put("stats_pink", atlas.findRegion("stats_pink"));
			regions.put("stats_brown", atlas.findRegion("stats_brown"));

			regions.put("icon_music_active", atlas.findRegion("icon_music2_active"));
			regions.put("icon_music_inactive", atlas.findRegion("icon_music2_inactive"));
			regions.put("icon_fb", atlas.findRegion("icon_fb2"));
			regions.put("icon_www", atlas.findRegion("icon_www2"));
			regions.put("pause", atlas.findRegion("pause"));
			regions.put("logo", atlas.findRegion("logo"));
			regions.put("piglet_logic_1", atlas.findRegion("piglet_logic_1"));
			regions.put("level", atlas.findRegion("level"));
			regions.put("level_b", atlas.findRegion("level_b"));
			regions.put("star_white", atlas.findRegion("star_white"));
			regions.put("star_black", atlas.findRegion("star_black"));
			
//			regions.put("arrow1", atlas.findRegion("arrow1"));
//			regions.put("arrow2", atlas.findRegion("arrow2"));
//			regions.put("arrow3", atlas.findRegion("arrow3"));
//			regions.put("arrow4", atlas.findRegion("arrow4"));
//			
//			regions.put("exclamation_red", atlas.findRegion("exclamation_red"));
//			regions.put("exclamation_blue", atlas.findRegion("exclamation_blue"));
//			regions.put("exclamation_green", atlas.findRegion("exclamation_green"));
//			regions.put("exclamation_purple", atlas.findRegion("exclamation_purple"));
//			regions.put("exclamation_pink", atlas.findRegion("exclamation_pink"));
//			regions.put("exclamation_brown", atlas.findRegion("exclamation_brown"));
//			regions.put("exclamation_yellow", atlas.findRegion("exclamation_yellow"));

			for (Map.Entry<String, AtlasRegion> e : regions.entrySet())
			{
				sprites.put(e.getKey(), new Sprite(e.getValue()));
			}

			// Setting filtering for every sprite

			for (Map.Entry<String, Sprite> e : sprites.entrySet())
			{
				e.getValue().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}

		}
	}

	public class AssetStars
	{

		private AtlasRegion arStar1 = null;
		private AtlasRegion arStar2 = null;
		private AtlasRegion arStar3 = null;
		private AtlasRegion arStar4 = null;
		private AtlasRegion arStar5 = null;
		private AtlasRegion arStar6 = null;
		private AtlasRegion arStar7 = null;
		private AtlasRegion arStar8 = null;
		private AtlasRegion arStar9 = null;
		private AtlasRegion arStar10 = null;
		private AtlasRegion arStar11 = null;
		private AtlasRegion arStar12 = null;
		private AtlasRegion arStar13 = null;
		private AtlasRegion arStar14 = null;
		private AtlasRegion arStar15 = null;
		private AtlasRegion arStar16 = null;
		private AtlasRegion arStar17 = null;

		/* Sprite */

		public Sprite star1 = null;
		public Sprite star2 = null;
		public Sprite star3 = null;
		public Sprite star4 = null;
		public Sprite star5 = null;
		public Sprite star6 = null;
		public Sprite star7 = null;
		public Sprite star8 = null;
		public Sprite star9 = null;
		public Sprite star10 = null;
		public Sprite star11 = null;
		public Sprite star12 = null;
		public Sprite star13 = null;
		public Sprite star14 = null;
		public Sprite star15 = null;
		public Sprite star16 = null;
		public Sprite star17 = null;

		public Map<String, Sprite> sprites = null;
		public Map<String, Float> starSizes = null;

		public AssetStars(TextureAtlas atlas)
		{
			sprites = new HashMap<String, Sprite>();
			starSizes = new HashMap<String, Float>();
			
			// Sizes
			starSizes.put("star1", 2f);
			starSizes.put("star2", 1f);
			starSizes.put("star3", 2f);
			starSizes.put("star4", 1f);
			starSizes.put("star5", 2f);
			starSizes.put("star6", 1.5f);
			starSizes.put("star7", 1f);
			starSizes.put("star8", 1.5f);
			starSizes.put("star9", 1f);
			starSizes.put("star10", 1f);
			starSizes.put("star11", 1.5f);
			starSizes.put("star12", 2f);
			starSizes.put("star13", 4f);
			starSizes.put("star14", 2.5f);
			starSizes.put("star15", 2f);
			starSizes.put("star16", 6f);
			starSizes.put("star17", 4f);
			
			// 1) Regions

			// Red
			arStar1 = atlas.findRegion("star1");
			arStar2 = atlas.findRegion("star2");
			arStar3 = atlas.findRegion("star3");
			arStar4 = atlas.findRegion("star4");
			arStar5 = atlas.findRegion("star5");
			arStar6 = atlas.findRegion("star6");
			arStar7 = atlas.findRegion("star7");
			arStar8 = atlas.findRegion("star8");
			arStar9 = atlas.findRegion("star9");
			arStar10 = atlas.findRegion("star10");
			arStar11 = atlas.findRegion("star11");
			arStar12 = atlas.findRegion("star12");
			arStar13 = atlas.findRegion("star13");
			arStar14 = atlas.findRegion("star14");
			arStar15 = atlas.findRegion("star15");
			arStar16 = atlas.findRegion("star16");
			arStar17 = atlas.findRegion("star17");
			
			

			// 2) Sprites

			star1 = new Sprite(arStar1);
			star2 = new Sprite(arStar2);
			star3 = new Sprite(arStar3);
			star4 = new Sprite(arStar4);
			star5 = new Sprite(arStar5);
			star6 = new Sprite(arStar6);
			star7 = new Sprite(arStar7);
			star8 = new Sprite(arStar8);
			star9 = new Sprite(arStar9);
			star10 = new Sprite(arStar10);
			star11 = new Sprite(arStar11);
			star12 = new Sprite(arStar12);
			star13 = new Sprite(arStar13);
			star14 = new Sprite(arStar14);
			star15 = new Sprite(arStar15);
			star16 = new Sprite(arStar16);
			star17 = new Sprite(arStar17);

			// 3) Map

			sprites.put("star1", star1);
			sprites.put("star2", star2);
			sprites.put("star3", star3);
			sprites.put("star4", star4);
			sprites.put("star5", star5);
			sprites.put("star6", star6);
			sprites.put("star7", star7);
			sprites.put("star8", star8);
			sprites.put("star9", star9);
			sprites.put("star10", star10);
			sprites.put("star11", star11);
			sprites.put("star12", star12);
			sprites.put("star13", star13);
			sprites.put("star14", star14);
			sprites.put("star15", star15);
			sprites.put("star16", star16);
			sprites.put("star17", star17);

			// Setting filtering for every sprite

			for (Map.Entry<String, Sprite> e : sprites.entrySet())
			{
				e.getValue().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}

		}
	}

	private void increaseProgress()
	{
		ALREADY_LOADED++;

	}

	public void loadBase()
	{
		assetBaseFonts = new AssetBaseFonts();
		increaseProgress();

		assetManager.load("images/game/gui.pack", TextureAtlas.class);
		assetManager.finishLoading();
		// gui
		TextureAtlas atlas5 = assetManager.get("images/game/gui.pack");
		gui = new AssetGui(atlas5);
		increaseProgress();

		ui = new AssetUI();
		increaseProgress();
		//
	}

	public class Loader implements Runnable
	{
		public Thread thread = null;

		public Loader()
		{
			thread = new Thread(this);
		}

		@Override
		public void run()
		{

		}

	}

	public void loadAll()
	{
		assetManager.load("images/game/planets_small.pack", TextureAtlas.class);
		assetManager.load("images/game/masks_small.pack", TextureAtlas.class);
		assetManager.load("images/game/stars.pack", TextureAtlas.class);
		assetManager.load("images/game/background.pack", TextureAtlas.class);
		assetManager.load("images/game/boosters.pack", TextureAtlas.class);

		assetManager.finishLoading();

		// load levels
		assetLevels = new AssetLevels();
		increaseProgress();

		assetFancyFonts = new AssetFancyFonts();
		increaseProgress();

		sounds = new AssetSounds();
		increaseProgress();

		TextureAtlas atlas = assetManager.get("images/game/planets_small.pack");
		planets = new AssetPlanets(atlas);
		increaseProgress();

		TextureAtlas atlas2 = assetManager.get("images/game/masks_small.pack");
		masks = new AssetMasks(atlas2);
		increaseProgress();

		// stars
		TextureAtlas atlas3 = assetManager.get("images/game/stars.pack");
		stars = new AssetStars(atlas3);
		increaseProgress();

		// background
		TextureAtlas atlas4 = assetManager.get("images/game/background.pack");
		background = new AssetBackground(atlas4);
		increaseProgress();

		// background
		TextureAtlas atlas5 = assetManager.get("images/game/boosters.pack");
		boosters = new AssetBoosters(atlas5);
		increaseProgress();

		loadedEverything = true;
	}

	public void init(AssetManager assetManager)
	{
		this.assetManager = assetManager;

		assetManager.setErrorListener(this);
		loader = new Loader();

	}

	public float getLoadingPercentage()
	{
		return ALREADY_LOADED / TO_LOAD;
	}

	@Override
	public void dispose()
	{
		assetManager.dispose();
		generator.dispose();
		sounds.trackTheme.dispose();
	}

	public void error(String filename, Class type, Throwable throwable)
	{
		Gdx.app.error(TAG, "Couldn't load asset '" + filename + "'", (Exception) throwable);
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable)
	{
		// TODO Auto-generated method stub

	}

}
