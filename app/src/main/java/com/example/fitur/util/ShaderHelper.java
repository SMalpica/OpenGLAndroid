package com.example.fitur.util;
/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
//TODO: pdf pag 55, 41 del indice
import android.util.Log;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    /*compiles a shader from source code and creates a shader object
    * if compilation was succesful, returns the shaderObjectid(openGL object id). Otherwise
    * returns 0 */
    private static int compileShader(int type, String shaderCode) {
        //create a new shader object and check if the creation was successful
        //type can be GL_VERTEX_SHADER for a vertex shader, or GL_FRAGMENT_SHADER for a fragment shader
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.");
            }
            return 0;
        }
        // Pass in the shader source.
        //Once we have a valid shader object, we call glShaderSource(shaderObjectId, shaderCode)
        //to upload the source code. This call tells OpenGL to read in the source code
        //defined in the String shaderCode and associate it with the shader object referred to
        //by shaderObjectId. We can then call glCompileShader(shaderObjectId) to compile the shader
        glShaderSource(shaderObjectId, shaderCode);
        //This tells OpenGL to compile the source code that was previously uploaded to shaderObjectId
        glCompileShader(shaderObjectId);
        // Get the compilation status.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (LoggerConfig.ON) {
            // Print the shader info log to the Android log output.
            Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:"
                    + glGetShaderInfoLog(shaderObjectId));
        }
        return shaderObjectId;
    }

    /**
     * Links a vertex shader and a fragment shader together into an OpenGL
     * program. Returns the OpenGL program object ID, or 0 if linking failed.
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        //create a program id and check if creation was successful
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new program");
            }
            return 0;
        }
        //attach our shaders to the created program
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);
        //link our shaders together
        glLinkProgram(programObjectId);
        //check whether the link failed or succeeded
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        if (LoggerConfig.ON) {
            // Print the program info log to the Android log output.
            Log.v(TAG, "Results of linking program:\n"
                    + glGetProgramInfoLog(programObjectId));
        }
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            glDeleteProgram(programObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }
        return programObjectId;
    }
    /**
     * Validates an OpenGL program. Should only be called when developing the
     * application. According to the OpenGL ES 2.0 documentation, it also provides a way for OpenGL
     * to let us know why the current program might be inefficient, failing to run, and so on.
     */
    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }
}
