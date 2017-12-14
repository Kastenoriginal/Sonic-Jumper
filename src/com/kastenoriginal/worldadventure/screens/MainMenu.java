package com.kastenoriginal.worldadventure.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kastenoriginal.worldadventure.InputController;
import com.kastenoriginal.worldadventure.WorldAdventure;
import com.kastenoriginal.worldadventure.tween.ActorAccessor;

public class MainMenu implements Screen {

	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	private TextButton buttonPlay, buttonSettings, buttonExit;
	private Label heading;
	private TweenManager tweenManager;
	//private BitmapFont black, white;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
		
		tweenManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
		//stage.setViewport(width, height, false);
		//table.invalidateHierarchy();
	}

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(true);
		stage = new Stage();
		
		Gdx.input.setInputProcessor(new InputMultiplexer(new InputController(){
			@Override
			public boolean keyUp(int keycode) {
				switch (keycode) {
				case Keys.ESCAPE:
				case Keys.BACK:
					Gdx.app.exit();
					break;
				case Keys.ENTER:
					((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
				}
				return false;
			}
		}, stage));
		
		atlas = new TextureAtlas("ui/atlas.pack");

		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);
		
		table = new Table(skin);
		table.setFillParent(true);
		
		heading = new Label(WorldAdventure.TITLE, skin, "big");
		heading.setFontScale(1);
		
		buttonPlay = new TextButton("Play", skin, "big");
		buttonSettings = new TextButton("Settings", skin);
		buttonExit = new TextButton("Exit", skin, "big");
		buttonPlay.pad(20);
		buttonSettings.pad(12);
		buttonExit.pad(20);
		buttonPlay.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
			}
		});
		buttonSettings.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Settings());
			}
		});
		buttonExit.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		//pridavanie veci do menu
		table.add(heading);
		table.getCell(heading).spaceBottom(80);
		table.row();
		table.padBottom(50);
		table.add(buttonPlay);
		table.row();
		table.getCell(buttonPlay).spaceBottom(15);
		table.add(buttonSettings);
		table.row();
		table.getCell(buttonSettings).spaceBottom(15);
		table.add(buttonExit);
		stage.addActor(table);
		
		//vytvorenie animacie menu
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
		//animacia farieb nadpisu
		Timeline.createSequence().beginSequence()
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 0, 1))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 1, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 1, 1))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(1, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(1, 0, 1))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(1, 1, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(0, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, 1.5f).target(1, 1, 1))
			.end().repeat(Tween.INFINITY, 0).start(tweenManager);
		
		//fade in nazov + buttony
		Timeline.createSequence().beginSequence()
			.push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
			.push(Tween.from(heading, ActorAccessor.ALPHA, .25f).target(0))
			.push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .25f).target(1))
			.push(Tween.to(buttonExit, ActorAccessor.ALPHA, .25f).target(1))
			.end().start(tweenManager);
		
		//fade in tabulky
		Tween.from(table, ActorAccessor.ALPHA, 5f).target(0).start(tweenManager);
		Tween.from(table, ActorAccessor.Y, 2.5f).target(Gdx.graphics.getHeight() / 8).start(tweenManager);
		
		tweenManager.update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		stage.dispose();
		atlas.dispose();
		skin.dispose();
	}

}
