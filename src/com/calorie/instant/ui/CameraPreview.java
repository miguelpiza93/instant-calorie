package com.calorie.instant.ui;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.calorie.instant.util.Log;

/**
 *
 * @author paul.blundell
 *
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private Camera camera;
	private SurfaceHolder holder;

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraPreview(Context context) {
		super(context);
	}

	public void init(Camera camera) {
		this.camera = camera;
		initSurfaceHolder();
	}

	@SuppressWarnings("deprecation") // needed for < 3.0
	private void initSurfaceHolder() {
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initCamera(holder);
	}

	private void initCamera(SurfaceHolder holder) {
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (Exception e) {
			Log.d("Error setting camera preview", e);
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		refreshCamera(camera);
	}

	public void refreshCamera(Camera camera) {
		if (holder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		// stop preview before making changes
		try {
			camera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (Exception e) {
			Log.d("Error starting camera preview: " + e.getMessage(), e);
		}
	}
	
}