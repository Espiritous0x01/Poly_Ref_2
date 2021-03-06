package org.anddev.andengine.engine.camera.hud;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.IShape;

/**
 * While you can add a {@link HUD} to {@link Scene}, you should not do so.
 * {@link HUD}s are meant to be added to {@link Camera}s via {@link Camera#setHUD(HUD)}.
 * 
 * @author Nicolas Gramlich
 * @since 14:13:13 - 01.04.2010
 */
public class HUD extends CameraScene {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public HUD() {
		this(1);

		this.setBackgroundEnabled(false);
	}

	public HUD(final int pLayerCount) {
		super(pLayerCount);

		this.setBackgroundEnabled(false);
	}

	public void setChaseShape(Camera camera, final IShape pChaseShape) {
		camera.mChaseShape = pChaseShape;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
