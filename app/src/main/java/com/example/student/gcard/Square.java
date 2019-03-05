package com.example.student.gcard;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;


// Класс Square принимает на вход массив из минимум 4-х точек
// Порядок точек важен, идем от верхней левой по часовой стрелке!
public class Square {

    private Point[] mPoints;

    private int mFillColor;

    public Square() {
    }

    Square(Point[] mPoints) {
        this.mPoints = mPoints;
    }

    public Square(Point[] mPoints, int mFillColor) {
        this.mPoints = mPoints;
        this.mFillColor = mFillColor;
    }

    public void setFillColor(int mFillColor) {
        this.mFillColor = mFillColor;
    }

    public int getFillColor() {
        return mFillColor;
    }

    public Point getLeftUpPoint() {
        return mPoints[0];
    }

    public Point getRightDownPoint() {
        return mPoints[2];
    }

    public boolean isInArea(Point point) {
        return point.x < mPoints[1].x &&
                point.x > mPoints[0].x &&
                point.y < mPoints[3].y &&
                point.y > mPoints[0].y;
    }

    public boolean isInArea(float x, float y) {
        return x < mPoints[1].x &&
                x > mPoints[0].x &&
                y < mPoints[3].y &&
                y > mPoints[0].y;
    }

    public Path getPath() {
        if (mPoints.length == 0) {
            return null;
        }
        Path path = new Path();
        path.moveTo(mPoints[0].x, mPoints[0].y);
        path.lineTo(mPoints[1].x, mPoints[1].y);
        path.lineTo(mPoints[2].x, mPoints[2].y);
        path.lineTo(mPoints[3].x, mPoints[3].y);
        path.lineTo(mPoints[0].x, mPoints[0].y);
        path.close();
        return path;
    }

    public Paint getPaint() {
        Paint paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mFillColor);
        return paint;
    }

}
