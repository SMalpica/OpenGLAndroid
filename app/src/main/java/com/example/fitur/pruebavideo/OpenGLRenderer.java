package com.example.fitur.pruebavideo;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.fitur.objects.Table;
import com.example.fitur.programs.ColorShaderProgram;
import com.example.fitur.programs.TextureShaderProgram;
import com.example.fitur.util.LoggerConfig;
import com.example.fitur.util.MatrixHelper;
import com.example.fitur.util.ShaderHelper;
import com.example.fitur.util.TextResourceReader;
import com.example.fitur.util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;
//TODO: pag 127 del pdf, 114 del libro
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
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private Table table;
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
        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    /*Called after the surface has been created or the size has changed*/
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
        //set the viewPort to fill the entire surface
        //This tells OpenGL the size of the surface it has available for rendering
        glViewport(0, 0, width, height);
        /*final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }*/
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
    }

    /*We clear the rendering surface, and then the first thing we do is draw the table*/
    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        // Draw the table.
        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();
        /*// Draw the mallets.
        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();*/
    }
}
