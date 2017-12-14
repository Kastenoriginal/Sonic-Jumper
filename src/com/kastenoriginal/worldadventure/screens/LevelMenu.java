package com.kastenoriginal.worldadventure.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kastenoriginal.worldadventure.InputController;

public class LevelMenu implements Screen {
	
	private Stage stage;
	private Table table;
	private Skin skin;
	private List list;
	private ScrollPane scrollPane;
	private TextButton play, back;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
		table.invalidateHierarchy();
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
					((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
					break;
				case Keys.ENTER:
					((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
				}
				return false;
			}
		}, stage));
		
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));
		
		table = new Table(skin);
		table.setFillParent(true);
		
		list = new List(new String[] {"one", "two", "three", "and", "so", "on", "twfafaaaaaaaaaaaaaaaaafafafafaffafo", "three", "and", "so", "on", "two", "three", "and", "so", "on", "two", "three", "and", "so", "on", "two", "three", "and", "so", "on"}, skin);
		
		scrollPane = new ScrollPane(list, skin);
		
		play = new TextButton("Play", skin, "big");
		back = new TextButton("Back", skin);
		play.pad(20);
		back.pad(12);
		play.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
			}
			
		});
		
		back.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
			}
		});
		
		table.add(new Label("Select level", skin, "big")).colspan(3).expandX().spaceBottom(50).row();
		table.add(scrollPane).uniformX().expandY().top().left();
		table.add(play).uniformX();
		table.add(back).uniformX().bottom().right();

		stage.addActor(table);
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
		skin.dispose();
	}
}
