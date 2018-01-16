package ar.shadow.org.exhibition;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.google.ar.core.Frame;
import com.google.ar.core.Session;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ar.shadow.org.exhibition.render.BackgroundRenderer;

/**
 * Created by Shadow on 16.01.2018.
 */

class MyRender implements GLSurfaceView.Renderer {

    public static final String TAG = MyRender.class.getSimpleName();

    private final Session mSession;

    private final BackgroundRenderer backgroundRenderer;

    public MyRender(Session mSession) {
        this.mSession = mSession;
        backgroundRenderer = new BackgroundRenderer();
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        GLES20.glClearColor(0.0f, 0.1f, 0.0f, 1.0f);


        backgroundRenderer.createOnGlThread(MainActivity.getInstance());


        if (mSession != null) {
            mSession.setCameraTextureName(backgroundRenderer.getTextureId());
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mSession == null) {
            return;
        }

        try {
            Frame frame = mSession.update();

            backgroundRenderer.draw(frame);

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }
}
