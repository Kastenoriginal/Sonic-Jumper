package com.kastenoriginal.worldadventure.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kastenoriginal.worldadventure.InputController;
import com.kastenoriginal.worldadventure.WorldAdventure;

public class Settings implements Screen{
	
	private Stage stage;
	private Table table;
	private Skin skin;
	
	public static FileHandle levelDirectory() {
		String prefsDir = Gdx.app.getPreferences(WorldAdventure.TITLE).getString("leveldirectory").trim();
		if(prefsDir != null && !prefsDir.equals(""))
			return Gdx.files.absolute(prefsDir);
		else
			return Gdx.files.absolute(Gdx.files.external(WorldAdventure.TITLE + "/levels").path());
	}
	
	public static boolean vSync() {
		return Gdx.app.getPreferences(WorldAdventure.TITLE).getBoolean("vsync");
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
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
				}
				return false;
			}
		}, stage));

		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));

		table = new Table(skin);
		table.setFillParent(true);

		final CheckBox vSyncCheckBox = new CheckBox(" vSync", skin);
		vSyncCheckBox.setChecked(vSync());

		final TextField levelDirectoryInput = new TextField(levelDirectory().path(), skin); // creating a new TextField with the current level directory already written in it
		levelDirectoryInput.setMessageText("level directory"); // set the text to be shown when nothing is in the TextField

		final TextButton back = new TextButton("Back", skin);
		back.pad(10);

		ClickListener buttonHandler = new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// event.getListenerActor() returns the source of the event, e.g. a button that was clicked
				if(event.getListenerActor() == vSyncCheckBox) {
					// save vSync
					Gdx.app.getPreferences(WorldAdventure.TITLE).putBoolean("vsync", vSyncCheckBox.isChecked());

					// set vSync
					Gdx.graphics.setVSync(vSync());

					Gdx.app.log(WorldAdventure.TITLE, "vSync " + (vSync() ? "enabled" : "disabled"));
				} else if(event.getListenerActor() == back) {
					// save level directory
					String actualLevelDirectory = levelDirectoryInput.getText().trim().equals("") ? Gdx.files.getExternalStoragePath() + WorldAdventure.TITLE + "/levels" : levelDirectoryInput.getText().trim(); // shortened form of an if-statement: [boolean] ? [if true] : [else] // String#trim() removes spaces on both sides of the string
					Gdx.app.getPreferences(WorldAdventure.TITLE).putString("leveldirectory", actualLevelDirectory);

					// save the settings to preferences file (Preferences#flush() writes the preferences in memory to the file)
					Gdx.app.getPreferences(WorldAdventure.TITLE).flush();

					Gdx.app.log(WorldAdventure.TITLE, "settings saved");

					stage.addAction(sequence(moveTo(0, stage.getHeight(), .5f), run(new Runnable() {

						@Override
						public void run() {
							((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
						}
					})));
				}
			}
		};

		vSyncCheckBox.addListener(buttonHandler);

		back.addListener(buttonHandler);

		// putting everything in the table
		table.add(new Label("SETTINGS", skin, "big")).spaceBottom(50).colspan(3).expandX().row();
		table.add();
		//table.add("level directory");
		table.add().row();
		table.add(vSyncCheckBox).top().expandY();
		//table.add(levelDirectoryInput).top().fillX();
		table.add(back).bottom().right();

		stage.addActor(table);

		stage.addAction(sequence(moveTo(0, stage.getHeight()), moveTo(0, 0, .5f))); // coming in from top animation
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
