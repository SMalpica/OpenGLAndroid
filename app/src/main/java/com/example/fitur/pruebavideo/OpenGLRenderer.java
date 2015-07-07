package com.example.fitur.pruebavideo;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.fitur.util.TextResourceReader;

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
    private static final int POSITION_COMPONENT_COUNT = 3;  //vertix coord. dimension
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private final Context context;

    public OpenGLRenderer(Context context){
        this.context = context;
        float[] tableVertices = {
                //triangle1 down
                0f, 0f, 0f,
                0f, 14f, 0f,
                9f, 14f, 0f,

                //triangle2 down
                0f, 0f, 0f,
                9f, 0f, 0f,
                9f, 14f, 0f,

                //triangle1 up
                0f, 0f, 7f,
                0f, 14f, 7f,
                9f, 14f, 7f,

                //triangle2 up
                0f, 0f, 7f,
                9f, 0f, 7f,
                9f, 14f, 7f,

                //triangle1 side1
                0f, 0f, 0f,
                0f, 14f, 0f,
                0f, 0f, 7f,

                //triangle2 side1
                0f, 0f, 7f,
                0f, 14f, 7f,
                0f, 14f, 0f,

                //triangle1 side2
                0f, 0f, 0f,
                9f, 0f, 0f,
                0f, 0f, 7f,

                //triangle2 side2
                0f, 0f, 7f,
                9f, 0f, 7f,
                9f, 0f, 0f,

                //triangle1 side1p
                9f, 0f, 0f,
                9f, 14f, 0f,
                9f, 0f, 7f,

                //triangle2 side1p
                9f, 0f, 7f,
                9f, 14f, 7f,
                9f, 14f, 0f,

                //triangle1 side2p
                0f, 14f, 0f,
                9f, 14f, 0f,
                9f, 14f, 7f,

                //triangle2 side2p
                0f, 14f, 7f,
                9f, 14f, 7f,
                0f, 14f, 0f,
                                };

        //allocate a block of native memory. This memory will not be managed by the garbage collector
        //how large the block of memory will be,
        //organize bytes in native order (same order the platform uses)
        //work with floats
        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        //copy data from Dalvik's VM memory to native memory
        //the memory will be freed when the process is killed, fine if we don't use lots of vertexData
        vertexData.put(tableVertices);
    }


    /*Called the first time app runs. Also when device wakes up or uses switches between apps*/
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config){
        // Set the background clear color to red. The first component is
        // red, the second is green, the third is blue, and the last
        // component is alpha, for transparency
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);
    }

    /*Called after the surface has been created or the size has changed*/
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
        //set the viewPort to fill the entire surface
        //This tells OpenGL the size of the surface it has available for rendering
        glViewport(0, 0, width, height);
    }

    public void onDrawFrame(GL10 glUnused){
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
    }
}
