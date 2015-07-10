package com.example.fitur.touchpruebavideo;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.fitur.util.LoggerConfig;
import com.example.fitur.util.MatrixHelper;
import com.example.fitur.util.ShaderHelper;
import com.example.fitur.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

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
public class OpenGLRendererViejo implements GLSurfaceView.Renderer{
    private static final int POSITION_COMPONENT_COUNT = 3;  //vertix coord. dimension
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    //space tp skip the colors of each vertex in the array
    //it is very important that the stride is represented in terms of bytes
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final FloatBuffer vertexData;
    private final Context context;
    private int program;
//    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";
//    private int uColorLocation;
    private int aPositionLocation;
    private int aColorLocation;
    private int uMatrixLocation;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    public OpenGLRendererViejo(Context context){
        this.context = context;
        /*float[] tableVertices = {

                //triangle1 down
                -0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,

                //triangle2 down
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,

                //triangle1 up
                -0.5f, -0.5f, 0.7f,
                -0.5f, 0.5f, 0.7f,
                0.5f, 0.5f, 0.7f,

                //triangle2 up
                -0.5f, -0.5f, 0.7f,
                0.5f, -0.5f, 0.7f,
                0.5f, 0.5f, 0.7f,

                //triangle1 side1
                -0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, 0.7f,

                //triangle2 side1
                -0.5f, -0.5f, 0.7f,
                -0.5f, 0.5f, 0.7f,
                -0.5f, 0.5f, -0.5f,

                //triangle1 side2
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.7f,

                //triangle2 side2
                -0.5f, -0.5f, 0.7f,
                0.5f, -0.5f, 0.7f,
                0.5f, -0.5f, -0.5f,

                //triangle1 side1p
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, 0.7f,

                //triangle2 side1p
                0.5f, -0.5f, 0.7f,
                0.5f, 0.5f, 0.7f,
                0.5f, 0.5f, -0.5f,

                //triangle1 side2p
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, 0.7f,

                //triangle2 side2p
                -0.5f, 0.5f, 0.7f,
                0.5f, 0.5f, 0.7f,
                -0.5f, 0.5f, -0.5f,

                 //punto
                0.5f, 0.75f, 0.5f,
                 // linea
                -0.5f, 0.75f, 0.75f,
                -0.5f, -0.75f,0.75f
                 };*/
        float[] tableVertices = {
                // Order of coordinates: X, Y, Z, R, G, B
                //triangle fan
                0, 0, -0.8f, 1f, 1f, 1f,
                -0.5f, -0.8f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, -0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, -0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, -0.5f, 0.7f, 0.7f, 0.7f,

                //triangle1 up
                -0.5f, -0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f,1f, 1f, 1f,

                //triangle2 up
                -0.5f, -0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f,1f, 1f, 1f,

                //triangle1 side1
                -0.5f, -0.8f, -0.5f,0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, -0.5f,0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f,1f, 1f, 1f,

                //triangle2 side1
                -0.5f, -0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, -0.5f,1f, 1f, 1f,

                //triangle1 side2
                -0.5f, -0.8f, -0.5f,0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, -0.5f,0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f,1f, 1f, 1f,

                //triangle2 side2
                -0.5f, -0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, -0.5f,1f, 1f, 1f,

                //triangle1 side1p
                0.5f, -0.8f, -0.5f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, -0.5f,0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f,1f, 1f, 1f,

                //triangle2 side1p
                0.5f, -0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, -0.5f,1f, 1f, 1f,

                //triangle1 side2p
                -0.5f, 0.8f, -0.5f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, -0.5f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f,1f, 1f, 1f,

                //triangle2 side2p
                -0.5f, 0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f,0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, -0.5f,1f, 1f, 1f};
        /*float[] tableVertices = {
                // Order of coordinates: X, Y, Z, W, R, G, B
                //triangle fan
                0, 0, -0.8f, 1.5f, 1f, 1f, 1f,
                -0.5f, -0.8f, -0.5f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, -0.5f,  1f,0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, -0.5f,  2f,0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, -0.5f, 2f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, -0.5f, 1f, 0.7f, 0.7f, 0.7f,

                //triangle1 up
                -0.5f, -0.8f, 0.7f, 1f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 2f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 2f, 1f, 1f, 1f,

                //triangle2 up
                -0.5f, -0.8f, 0.7f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 2f, 1f, 1f, 1f,

                //triangle1 side1
                -0.5f, -0.8f, -0.5f, 1f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, -0.5f, 2f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 1f, 1f, 1f, 1f,

                //triangle2 side1
                -0.5f, -0.8f, 0.7f, 1f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 2f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, -0.5f, 2f, 1f, 1f, 1f,

                //triangle1 side2
                -0.5f, -0.8f, -0.5f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f,  -0.5f, 1f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 1f, 1f, 1f, 1f,

                //triangle2 side2
                -0.5f, -0.8f, 0.7f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, -0.5f, 1f, 1f, 1f, 1f,

                //triangle1 side1p
                0.5f, -0.8f, -0.5f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, -0.5f, 2f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 1f, 1f, 1f, 1f,

                //triangle2 side1p
                0.5f, -0.8f, 0.7f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 2f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, -0.5f, 2f, 1f, 1f, 1f,

                //triangle1 side2p
                -0.5f, 0.8f, -0.5f, 2f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, -0.5f, 2f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 2f, 1f, 1f, 1f,

                //triangle2 side2p
                -0.5f, 0.8f, 0.7f, 2f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f,2f,0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, -0.5f, 2f, 1f, 1f, 1f};*/

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
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //retrieve the shaders source code
        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);
        //create the shader objects
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        //link shaders together in a program
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        //validate our program before we start using it
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        //enable the openGL program
        glUseProgram(program);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        //take uniform and attribute location
//        uColorLocation = glGetUniformLocation(program, U_COLOR);    //use this to update uniform's value
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        vertexData.position(0); //ensure data will be read from the starting position
        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
         //enlable the attribute before qe can start drawing
        glEnableVertexAttribArray(aPositionLocation);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_COLOR_LOCATION.
        //set position to first color of the array
        //Had we set the position to 0 instead, OpenGL would be reading in the position as the color.
        vertexData.position(POSITION_COMPONENT_COUNT);
        //associate our color data with a_color in the shaders
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        //enable vertex attribute for the color attribute
        glEnableVertexAttribArray(aColorLocation);
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
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        //Whenever we multiply two matrices, we need a temporary area to store the
        //result.If we try to write the result directly, the results are undefined !
        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    public void onDrawFrame(GL10 glUnused){
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        // send the orthographic projection matrix to the shader
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        /*//we dont need it because we have already associated each vertex with a color
        //update the value of uColor in our shader code
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);    //white*/
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
//        glDrawArrays(GL_TRIANGLES,6,30);
        /*//draw triangles, start at the beginning of our array, read 36 vertices
        glDrawArrays(GL_TRIANGLES, 0, 36);

        glUniform4f(uColorLocation,0.0f,1.0f,0.0f,1.0f);
        glDrawArrays(GL_POINTS, 36, 1);

        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 37, 2);*/
    }
}
