package com.kastenoriginal.worldadventure;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class LevelGenerator {

	private World world;
	private Sprite ground;
	private float leftEdge, rightEdge, minGap, maxGap, minWidth, maxWidth, height, angle, y;

	public LevelGenerator(World world, Sprite ground, float leftEdge, float rightEdge, float minGap, float maxGap, float minWidth, float maxWidth, float height, float angle) {
		this.world = world;
		this.ground = ground;
		this.leftEdge = leftEdge;
		this.rightEdge = rightEdge;
		this.minGap = minGap;
		this.maxGap = maxGap;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.height = height;
		this.angle = angle;
	}

	public void generate(float topEdge) {
		if(y + MathUtils.random(minGap, maxGap) > topEdge)
			return;

		y = topEdge;
		float width = MathUtils.random(minWidth, maxWidth);
		float x = MathUtils.random(leftEdge, rightEdge - width);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, height / 2, Vector2.Zero, MathUtils.random(-angle / 2, angle / 2));

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(new Vector2(x + width / 2, y + height / 2));

		Sprite platformSprite = new Sprite(ground);
		platformSprite.setSize(width, height);
		platformSprite.setOrigin(width / 2, height / 2);

		Body platform = world.createBody(bodyDef);
		platform.createFixture(shape, 0);
		platform.setUserData(platformSprite);

		shape.dispose();
	}

	/** @return the {@link #world} */
	public World getWorld() {
		return world;
	}

	/** @param world the {@link #world} to set */
	public void setWorld(World world) {
		this.world = world;
	}

	/** @return the {@link #leftEdge} */
	public float getLeftEdge() {
		return leftEdge;
	}

	/** @param leftEdge the {@link #leftEdge} to set */
	public void setLeftEdge(float leftEdge) {
		this.leftEdge = leftEdge;
	}

	/** @return the {@link #rightEdge} */
	public float getRightEdge() {
		return rightEdge;
	}

	/** @param rightEdge the {@link #rightEdge} to set */
	public void setRightEdge(float rightEdge) {
		this.rightEdge = rightEdge;
	}

	/** @return the {@link #minGap} */
	public float getMinGap() {
		return minGap;
	}

	/** @param minGap the {@link #minGap} to set */
	public void setMinGap(float minGap) {
		this.minGap = minGap;
	}

	/** @return the {@link #maxGap} */
	public float getMaxGap() {
		return maxGap;
	}

	/** @param maxGap the {@link #maxGap} to set */
	public void setMaxGap(float maxGap) {
		this.maxGap = maxGap;
	}

	/** @return the {@link #minWidth} */
	public float getMinWidth() {
		return minWidth;
	}

	/** @param minWidth the {@link #minWidth} to set */
	public void setMinWidth(float minWidth) {
		this.minWidth = minWidth;
	}

	/** @return the {@link #maxWidth} */
	public float getMaxWidth() {
		return maxWidth;
	}

	/** @param maxWidth the {@link #maxWidth} to set */
	public void setMaxWidth(float maxWidth) {
		this.maxWidth = maxWidth;
	}

	/** @return the {@link #height} */
	public float getHeight() {
		return height;
	}

	/** @param height the {@link #height} to set */
	public void setHeight(float height) {
		this.height = height;
	}

}
