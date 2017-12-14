package com.kastenoriginal.worldadventure;

import com.badlogic.gdx.Game;
import com.kastenoriginal.worldadventure.screens.Splash;

public class WorldAdventure extends Game {
	
	public static final String TITLE = "Sonic Jumper", VERSION = "1.0.0";
	
	@Override
	public void create() {
		setScreen(new Splash());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
