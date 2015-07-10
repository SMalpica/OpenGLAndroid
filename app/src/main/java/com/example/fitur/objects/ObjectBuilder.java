package com.example.fitur.objects;

import android.util.FloatMath;

import com.example.fitur.util.Geometry;

import static android.opengl.GLES20.*;

import java.util.ArrayList;
import java.util.List;

/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/


public class ObjectBuilder {

    public static interface DrawCommand {
        void draw();
    }

    /*holder class so that we can return both the vertex data and the draw list in a single object*/
    public static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;
        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0; //variable to keep track of the position in the array for the next vertex

    /*constructor for the class*/
    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    /* calculate the size of a cylinder top in vertices*/
    private static int sizeOfCircleInVertices(int numPoints) {
        //A cylinder top is a circle built out of a triangle fan; it has one vertex in the
        //center, one vertex for each point around the circle, and the first vertex around
        //the circle is repeated twice so that we can close the circle off.
        return 1 + (numPoints + 1);
    }

    /*method to calculate the size of a cylinder side in vertices*/
    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        //cylinder side is a rolled-up rectangle built out of a triangle strip, with two
        //vertices for each point around the circle, and with the first two vertices
        //repeated twice so that we can close off the tube
        return (numPoints + 1) * 2;
    }

    /*method to generate a puck (cylinder)*/
    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        //how many vertices we need to instantiate the puck
        int size = sizeOfCircleInVertices(numPoints)
                + sizeOfOpenCylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);
        //calculate where the top of the puck should be
        /*The puck is vertically centered at center.y, so it's fine to place the cylinder side
        there. The cylinder top, however, needs to be placed at the top of the puck.
        To do that, we move it up by half of the puck's overall height*/
        Geometry.Circle puckTop = new Geometry.Circle(
                puck.center.translateY(puck.height / 2f),
                puck.radius);
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);
        return builder.build();
    }

    /*method to generate a mallet (2 cylinders)*/
    static GeneratedData createMallet(
            Geometry.Point center, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2
                + sizeOfOpenCylinderInVertices(numPoints) * 2;
        ObjectBuilder builder = new ObjectBuilder(size);
        // First, generate the mallet base.
        float baseHeight = height * 0.25f;
        Geometry.Circle baseCircle = new Geometry.Circle(
                center.translateY(-baseHeight),
                radius);
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f),
                radius, baseHeight);
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;
        Geometry.Circle handleCircle = new Geometry.Circle(
                center.translateY(height * 0.5f),
                handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius, handleHeight);
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();
    }

    /*build a circle with a triangle fan*/
    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);
        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;
        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            vertexData[offset++] =
                    circle.center.x
                            + circle.radius * FloatMath.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] =
                    circle.center.z
                            + circle.radius * FloatMath.sin(angleInRadians);
        }
        /*To generate points around a circle, we first need a loop that will range over
        the entire circle from 0 to 360 degrees, or 0 to 2 times pi in radians. To find
        the x position of a point around the circle, we call cos(angle), and to find the z
        position, we call sin(angle); we scale both by the circle's radius.
        Since our circle is going to be lying flat on the x-z plane, the y component of
        the unit circle maps to our y position*/
        drawList.add(new DrawCommand() {
            /*only using one array for the object, we need to tell OpenGL the
            right vertex offsets for each draw command*/
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);
        //generate the triangle strip
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            float xPosition =
                    cylinder.center.x
                            + cylinder.radius * FloatMath.cos(angleInRadians);
            float zPosition =
                    cylinder.center.z
                            + cylinder.radius * FloatMath.sin(angleInRadians);
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });

    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }
}
