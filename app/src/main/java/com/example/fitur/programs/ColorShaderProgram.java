package com.example.fitur.programs;
import android.content.Context;

import com.example.fitur.pruebavideo.R;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;
/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/

//We’ll use this program for drawing our mallets
//By decoupling the shader programs from the data that gets drawn with these
//programs, we’ve made it easier to reuse our code. For example, we could draw
//any object with a color attribute using our color shader program, not just the mallets.
public class ColorShaderProgram extends ShaderProgram{
    // Uniform locations
    private final int uMatrixLocation;
    // Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;


    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,
                R.raw.simple_fragment_shader);
        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
    }

    public void setUniforms(float[] matrix) {
    // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }
}
