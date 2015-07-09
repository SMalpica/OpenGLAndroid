package com.example.fitur.objects;

import com.example.fitur.Constants;
import com.example.fitur.data.VertexArray;
import com.example.fitur.programs.TextureShaderProgram;

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
public class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
    // T component is running in the opposite direction of the y component.
    //This is so that the image is oriented with the right side up
    private static final float[] VERTEX_DATA = {
        // Order of coordinates: X, Y, S, T
        // Triangle Fan
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f };
    private final VertexArray vertexArray;

    /*Constructor for the class*/
    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    /*bind the vertex array to a shader program*/
    public void bindData(TextureShaderProgram textureProgram) {
        /*method body calls setVertexAttribPinter for each attribute, getting the location of each
        attribute from the shader program. This will bind the position data to the shader attribute
        referenced by getPositionAttributeLocation() and bind the texture coordinate data to the
        shader attribute referenced by getTextureCoordinatesAttributeLocation()*/
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
