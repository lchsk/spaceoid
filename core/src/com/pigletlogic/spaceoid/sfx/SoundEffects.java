package com.pigletlogic.spaceoid.sfx;

import java.util.ArrayList;

import com.badlogic.gdx.audio.Music;
import com.pigletlogic.spaceoid.Assets;
import com.pigletlogic.spaceoid.Game;

public class SoundEffects
{
	private static final String TAG = SoundEffects.class.getName();
	
	// List of music files to play in background
	private ArrayList<Music> backgroundMusic = null;
	private Game game = null;
	
	private int currentSongId = 0;
	
	// Music and sound effects
	private boolean isSoundOn = false; // default is false
	
	public SoundEffects(Game p_game)
	{
		game = p_game;
	}
	
	public void playBackgroundMusic()
	{
		if (backgroundMusic == null)
		{
			backgroundMusic = new ArrayList<Music>();
			backgroundMusic.add(Assets.instance.sounds.trackTheme);
			backgroundMusic.add(Assets.instance.sounds.trackGuitar);
		}
		
		playCurrentSong();
	}
	
	public void turnOn()
	{
		isSoundOn = true;
	}
	
	public void turnOff()
	{
		if (isSoundOn && backgroundMusic != null && backgroundMusic.get(currentSongId) != null)
		{
			isSoundOn = false;
			backgroundMusic.get(currentSongId).stop();
		}
	}
	
	private void playCurrentSong()
	{
		if (isSoundOn && backgroundMusic != null && backgroundMusic.get(currentSongId) != null)
		{
			backgroundMusic.get(currentSongId).play();
		}
	}
	
	public void updateMusic()
	{
		if (isSoundOn && backgroundMusic != null)
		{
			if ( ! backgroundMusic.get(currentSongId).isPlaying())
			{
				if ((currentSongId + 1) < backgroundMusic.size())
				{
					currentSongId++;
				}
				else
				{
					currentSongId = 0;
				}
				
				playCurrentSong();
			}
		}
	}
	
	public boolean isSoundOn()
	{
		return isSoundOn;
	}
	
	public void toggleSound()
	{
		isSoundOn = ! isSoundOn;
	}
}
