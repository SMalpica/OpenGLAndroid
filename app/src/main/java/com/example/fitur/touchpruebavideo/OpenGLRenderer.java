package com.example.fitur.touchpruebavideo;

import android.content.Context;
import android.media.Image;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.fitur.objects.Mallet;
import com.example.fitur.objects.Puck;
import com.example.fitur.objects.Table;
import com.example.fitur.programs.ColorShaderProgram;
import com.example.fitur.programs.TextureShaderProgram;
import com.example.fitur.util.Geometry;
import com.example.fitur.util.MatrixHelper;
import com.example.fitur.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;

import static android.opengl.Matrix.*;
//TODO: pag 192 del pdf, 181 del libro
/**
 * http://media.pragprog.com/titles/kbogla/code/FirstOpenGLProject/src/com/firstopenglproject/android/AirHockeyRenderer.java
 **
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 **
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer{
    private final Context context;

    //matrix definitions
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];

    //object definitions
    private Table table;
    private Mallet mallet;
    private Puck puck;

    //blue mallet properties
    private boolean malletPressed = false;  // keep track of whether the mallet is currently pressed or not
    private Geometry.Point blueMalletPosition;
    private Geometry.Point previousBlueMalletPosition;
    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    //program definitions
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;



    public OpenGLRenderer(Context context) {
        this.context = context;
    }


    /*Called the first time app runs. Also when device wakes up or uses switches between apps*/
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);
        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
    }

    /*Called after the surface has been created or the size has changed*/
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);
        //create a special type of view matrix
        /* setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX,
        * float centerY, float centerZ, float upX, float upY, float upZ)
        * rm -> destination array. at least 16 elements long
        * rmOffset -> position of rm to start writing
        * eyeX, eyeY, eyeZ -> where the eye will be, everything in the scene will appear as
        *                        if we’re viewing it from this point
        * centerX, centerY, centerZ -> where the eye is looking (center of the scene)
        * upX, upY, upZ -> If we were talking about your eyes, then this is where your head would be
        *                  pointing. An upY of 1 means your head would be pointing straight up*/
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
        /*We call setLookAtM() with an eye of (0, 1.2, 2.2), meaning your eye will be 1.2 units
        above the x-z plane and 2.2 units back. In other words, everything in the scene will appear
        1.2 units below you and 2.2 units in front of you. A center of (0, 0, 0) means you’ll be
        looking down toward the origin in front of you, and an up of (0, 1, 0) means that your head
        will be pointing straight up and the scene won’t be rotated to either side.*/
    }
    /*@Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
        //set the viewPort to fill the entire surface
        //This tells OpenGL the size of the surface it has available for rendering
        glViewport(0, 0, width, height);
        *//*final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }*//*
        //field of vision of 45 degrees, frustrum begins at z=-1, ends at z=-10
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);
        setIdentityM(modelMatrix, 0);
//        translateM(modelMatrix, 0, 0f, 0f, -2f); //move 2 units along the negative z-axis
        //We push the table farther, because once we rotate it the bottom end will be closer to us
        translateM(modelMatrix, 0, 0f, 0f, -2.6f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        //Whenever we multiply two matrices, we need a temporary area to store the
        //result.If we try to write the result directly, the results are undefined !
        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }*/

    /*We clear the rendering surface, and then the first thing we do is draw the table*/
    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        /*This call will create an inverted matrix that we’ll be able to use to convert the
        two-dimensional touch point into a pair of three-dimensional coordinates. If
        we move around in our scene, it will affect which part of the scene is underneath
        our fingers, so we also want to take the view matrix into account. We
        do this by taking the inverse of the combined view and projection matrices.*/
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();
        // Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();
//        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        mallet.draw();
        // Draw the puck.
        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
        /*// Draw the table.
        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();
        // Draw the mallets.
        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();*/
    }

    private void positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
        /*Note that unlike previous lessons, we don’t also translate the table into the distance
        because we want to keep the table at (0, 0, 0) in world coordinates, and the
        view matrix is already taking care of making the table visible for us.*/
    }


    /*combine all the matrices together by multiplying viewProjectionMatrix
        and modelMatrix and storing the result in modelViewProjectionMatrix, which
        will then get passed into the shader program*/
    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Log.e("TOUCH_EVENT", "press in : " + normalizedX + ", " + normalizedY);
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        // Now test if this ray intersects with the mallet by creating a
        // bounding sphere that wraps the mallet.
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z),
                mallet.height / 2f);
        // If the ray intersects (if the user touched a part of the screen that
        // intersects the mallet's bounding sphere), then set malletPressed =
        // true.
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
        Log.e("MALLET_PRESSED", "vale " + malletPressed);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        Log.e("TOUCH_EVENT","dragged to : "+normalizedX+", "+normalizedY);
        if (malletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            // Define a plane representing our air hockey table.
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
            // Find out where the touched point intersects the plane
            // representing our table. We'll move the mallet along this plane.
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            blueMalletPosition =
//                    new Geometry.Point(touchedPoint.x, mallet.height / 2f, touchedPoint.z);
                    new Geometry.Point(
                            clamp(touchedPoint.x,
                                    leftBound + mallet.radius,
                                    rightBound - mallet.radius),
                            mallet.height / 2f,
                            clamp(touchedPoint.z,
                                    0f + mallet.radius, //middle of the table so that the mallet can't pass to the opponent's side
                                    nearBound - mallet.radius));
        }
    }


    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay =
                new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay =
                new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Geometry.Ray(nearPointRay,
                Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }
}
