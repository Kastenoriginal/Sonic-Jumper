package com.kastenoriginal.worldadventure.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kastenoriginal.worldadventure.InputController;
import com.kastenoriginal.worldadventure.LevelGenerator;
import com.kastenoriginal.worldadventure.WorldAdventure;
import com.kastenoriginal.worldadventure.entities.Player;

public class Play implements Screen {

	private World world;
	private Box2DDebugRenderer debugRendered;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private FixtureDef fixtureDef;
	private ChainShape groundShape;
	private Sprite playerSprite = new Sprite(new Texture("img/sonic_left_down.png")), groundSprite = new Sprite(new Texture("img/ground.png"));

	private final float TIMESTEP = 1 / 60f; // 1 / 60 tina sekundy koli 60 FPS
	private final int VELOCITYITERATIONS = 8; // stablina konstanta z BOX2D - vyssie cislo vyssia kvalita simualcie
	private final int POSITIONITERATIONS = 3; // stabilna konsanta z BOX2D - vyssie cislo vyssia kvalita simulacie

	//private Car car;
	private Player player;
	private LevelGenerator levelGenerator;

	private Vector3 bottomRight;
	private Vector3 bottomLeft;

	float backgroundSeperatorY;
	Texture background = new Texture("img/background.png");

	private Vector3 tmp = new Vector3(0, 0, 0);

	private Array<Body> tmpBodies = new Array<Body>();

	private Table table;
	private Skin skin;
	private Stage stage;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(player.getBody().getPosition().x < bottomLeft.x)
			player.getBody().setTransform(bottomRight.x, player.getBody().getPosition().y, player.getBody().getAngle());
		else if(player.getBody().getPosition().x > bottomRight.x)
			player.getBody().setTransform(bottomLeft.x, player.getBody().getPosition().y, player.getBody().getAngle());

		player.getBody().applyForceToCenter(-Gdx.input.getAccelerometerX() * 10, 0, true);

		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);

		if(player.getBody().getPosition().y > camera.position.y)
			camera.position.y = player.getBody().getPosition().y;

		levelGenerator.generate(camera.position.y + camera.viewportHeight / 2);

		player.update();
		camera.update();

		table.clear();
		table.top().left().add("Score: " + String.valueOf((int) camera.position.y)).expandX();
		//table.top().right().add("High score: " + "8").expandX();

		stage.act(delta);

		if(checkLose())
			return;

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// begin drawing the background
		tmp.set(0, Gdx.graphics.getHeight(), 0);
		camera.unproject(tmp);
		float backgroundWidth = camera.viewportWidth;
		float backgroundHeight = camera.viewportHeight;

		if(backgroundSeperatorY < tmp.y)
			backgroundSeperatorY += backgroundHeight;
		batch.draw(background, tmp.x, backgroundSeperatorY + backgroundHeight, backgroundWidth, backgroundHeight);
		batch.draw(background, tmp.x, backgroundSeperatorY, backgroundWidth, backgroundHeight);
		batch.draw(background, tmp.x, backgroundSeperatorY - backgroundHeight, backgroundWidth, backgroundHeight); // only necessary if background image has a greater height than the screen
		// end drawing the background

		world.getBodies(tmpBodies);
		for(Body body : tmpBodies)
			if(body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite) body.getUserData();
				sprite.setPosition(body.getWorldCenter().x - sprite.getWidth() / 2, body.getWorldCenter().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(batch);
			}

		batch.end();
		//debugRendered.render(world, camera.combined);
		stage.draw();
	}

	/** @return if the game is lost and the screen changed to the {@link MainMenu} */
	 private boolean checkLose() {
		 if(player.getBody().getPosition().y < camera.position.y - 20) {
			 ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
			 return true;
		 }
		 return false;
	 }

	public static FileHandle levelDirectory() {
		String prefsDir = Gdx.app.getPreferences(WorldAdventure.TITLE).getString("leveldirectory").trim();
		if(prefsDir != null && !prefsDir.equals(""))
			return Gdx.files.absolute(prefsDir);
		else
			return Gdx.files.absolute(Gdx.files.external(WorldAdventure.TITLE + "/levels").path()); // return default level directory
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width / 25;
		camera.viewportHeight = height / 25;
	}

	@Override
	public void show() {
		if(Gdx.app.getType() == ApplicationType.Desktop)
			Gdx.graphics.setDisplayMode((int) (Gdx.graphics.getHeight() / 1.5f), Gdx.graphics.getHeight(), false);

		stage = new Stage();

		Gdx.input.setCatchBackKey(true);

		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));
		table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);

		world = new World(new Vector2(0, -9.81f), true); //svet s gravitaciou y = -9.81 Newton (konstanta g)
		debugRendered = new Box2DDebugRenderer();
		batch = new SpriteBatch();

		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 25, Gdx.graphics.getHeight() / 25);

		BodyDef bodyDef = new BodyDef(); // vytvorenie vlastnosti tela objektu
		fixtureDef = new FixtureDef(); //vytvorenie vlastnosti objektu

		fixtureDef.density = 10;
		fixtureDef.friction = .4f;
		fixtureDef.restitution = .2f;

		player = new Player(world, 0, 0, 1);
		world.setContactFilter(player);
		world.setContactListener(player);
		player.getBody().applyLinearImpulse(0, 145, player.getBody().getWorldCenter().x, player.getBody().getWorldCenter().y, true);

		playerSprite.setSize(player.WIDTH, player.HEIGHT);
		player.getBody().setUserData(playerSprite);

		Gdx.input.setInputProcessor(new InputMultiplexer(new InputController() {
			@Override
			public boolean keyUp(int keycode) {
				switch(keycode) {
				case Keys.ESCAPE:
				case Keys.BACK:
					//Gdx.graphics.setDisplayMode(Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), false);
					Gdx.graphics.setDisplayMode(480, 800, false);
					((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
					break;
				}
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				camera.zoom += amount / 25f; // to iste ako camera.zoom = camera.zoom + amount / 25f
				return true;
			}
		}, player));

		//POVRCH ZEMSKY :D
		//vlastnosti zeme
		bodyDef.type = BodyType.StaticBody; // staticky objekt - neda sa s nim pohnut a nic ho nemoze donutit ho hybat sa
		bodyDef.position.set(0, 0); //nastavenie pozicie objektu

		// shape zeme
		groundShape = new ChainShape();
		bottomLeft = new Vector3(0, Gdx.graphics.getHeight(), 0);
		bottomRight = new Vector3(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);

		camera.unproject(bottomLeft);
		camera.unproject(bottomRight);

		groundShape.createChain(new float[] {bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y});

		// fixture zeme
		fixtureDef.shape = groundShape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0;

		Body ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);

		groundShape.dispose();

		levelGenerator = new LevelGenerator(world, groundSprite, bottomLeft.x, bottomRight.x, player.HEIGHT * 2f, player.HEIGHT * 2f, player.WIDTH * 2, player.WIDTH * 3, player.WIDTH / 3, 0);

		backgroundSeperatorY = bottomLeft.y;
	}

	/**PRIKLAD AKO SA TO ROBI AJ S KOMENTARMI ATD
	bodyDef.type = BodyType.DynamicBody; // nastavenie dynamickeho typu
	bodyDef.position.set(2.25f, 5); // pozicia
	PolygonShape boxShape = new PolygonShape(); // typ shape-u (polygon)
	boxShape.setAsBox(.5f, 1); //parametre su polovice skto�nej �irky, vy�ky (hx - halfx, hy - halfy), setasbox - obdlznik
	fixtureDef.shape = boxShape; // priradenie shape-u fixture
	fixtureDef.friction = 0.75f; // drsnost objektu 0-1, 0 - hladky, 1 - drsny
	fixtureDef.restitution = .1f; // odrazavost 0-1, cislo*100 urcuje kolko % sa odrazi
	fixtureDef.density = 5; // kolko kg je v 1 metri stvorcovom
	
	//
	boxSprite = new Sprite(new Texture("img/frajeris.png")); // nacitanie textury
	
	box = world.createBody(bodyDef);
	box.createFixture(fixtureDef);
	
	boxSprite.setSize(1, 2); //sirka a vyska je dvojnasobok vysky a sirky boxShape.setAsBox(hx, hy); lebo hx a hy su polovicne parametre
	boxSprite.setOrigin(boxSprite.getWidth() / 2, boxSprite.getHeight() / 2); // rotacia obrazka
	box.setUserData(boxSprite);
	
	box.applyAngularImpulse(46, true); //pociatocna rotacia
	
	boxShape.dispose();
	
	//DALSIA KRABICA
	// vlastnosti krabice
	bodyDef.position.x = 0;
	bodyDef.position.y = 7;
	bodyDef.type = BodyType.StaticBody;
	
	// shape krabice
	PolygonShape secondBoxShape = new PolygonShape();
	secondBoxShape.setAsBox(.25f, .25f);
	
	fixtureDef.shape = secondBoxShape;
	
	Body secondBox = world.createBody(bodyDef);
	secondBox.createFixture(fixtureDef);
	
	boxShape.dispose();
	
	//Distance Joint medzi boxom a second boxom
	DistanceJointDef distanceJointDef = new DistanceJointDef();
	distanceJointDef.bodyA = secondBox;
	distanceJointDef.bodyB = box;
	distanceJointDef.length = 5;
	
	world.createJoint(distanceJointDef);
	
	//Rope Joint medzi zemou a boxom
	RopeJointDef ropeJointDef = new RopeJointDef();
	ropeJointDef.bodyA = box;
	ropeJointDef.bodyB = ground;
	ropeJointDef.maxLength = 4;
	ropeJointDef.localAnchorA.set(0, 0);
	ropeJointDef.localAnchorB.set(0, 0);
	
	world.createJoint(ropeJointDef);
	
	//LOPTA
	//tvar objektu kruh (shape)
	CircleShape ballShape = new CircleShape(); // vytvorenie objektu tvaru kruhu
	ballShape.setPosition(new Vector2(0, 1.5f));
	ballShape.setRadius(0.5f); //polomer kruhu
	
	//vlastnosti objektu lopty
	fixtureDef.shape = ballShape; //Ak� tvar m� objekt (hviezda, kruh, stvorec, ...)
	fixtureDef.density = 2.5f; //kolko kilogramov masy je v 1 metri stvorcovom
	fixtureDef.friction = 0.25f; //drsnost objektu nastavuje sa medzi 0-1, 1 - drsny, 0 - hladky
	fixtureDef.restitution = 0.75f; //elastickost medzi 0-1, cislo*100 urcuje do kolkych % sa odrazi objekt pri naraze so zemou, 1-skace do nekonecna
	
	box.createFixture(fixtureDef); //pridanie objektu do sveta a priranie vlastnosti objektu lopte
	
	ballShape.dispose();
	
	}*/

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
		world.dispose();
		debugRendered.dispose();
		skin.dispose();
		stage.dispose();
		playerSprite.getTexture().dispose();
		groundSprite.getTexture().dispose();
	}

}
