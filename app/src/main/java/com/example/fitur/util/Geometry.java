package com.example.fitur.util;

/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/

/* we’ve defined our geometry classes as immutable; whenever we make a change, we return a new
 * object. This helps to make the code easier to work with and understand, but when you need
 * top performance, you might want to stick with simple floating-point arrays and mutate them
 * with static functions
 * */
public class Geometry {

    /*Class to represent a point in 3D space*/
    public static class Point {
        public final float x, y, z;
        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        /*translate the point along the y-axis*/
        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }
    }

    /*class to define a circle*/
    public static class Circle {
        public final Point center;
        public final float radius;
        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
        /*helper function to scale the circle's radius*/
        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }

    /*Definition of a cylinder*/
    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;
        public Cylinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }
}
