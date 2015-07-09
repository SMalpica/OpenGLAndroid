package com.example.fitur.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
public class TextureHelper {
    private static final String TAG = "TextureHelper";

    /*this methods takes a context and a resource id and returns de id of the loaded texture.
    * Returns 0 if the load failed*/
    public static int loadTexture(Context context, int resourceId) {
        //generate a new texture ID using the same pattern as when we’ve created other OpenGL objects
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }
        //loading in bitmap data and binding to the texture
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   //original image size, not scaled
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);
        if (bitmap == null) {   //if the decoding failed we delete the texture and return 0
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            }
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        // tell OpenGL that future texture calls should be applied to this texture object
        //we tell openGL that this texture should be treated as a 2D texture
        //and which texture object should it bind to
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        //We’ll also need to specify what should happen when the texture is expanded
        //or reduced in size, using texture filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR); //minification trilinear filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);   //magnification bilinear interpolation filtering
        //load the bitmap data into OpenGL using GLUtils
        //This call tells OpenGL to read in the bitmap data defined by bitmap and copy
        //it over into the texture object that is currently bound
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //tell the garbage collector that we no longer need the bitmap object
        bitmap.recycle();
        //generate the mipmap set of textures
        glGenerateMipmap(GL_TEXTURE_2D);
        // a good practice is to then unbind from the texture so that we don’t accidentally make
        // further changes to this texture with other texture calls
        glBindTexture(GL_TEXTURE_2D, 0);    //passing 0 unbinds the current texture
        //return the created object
        return textureObjectIds[0];
    }
}
