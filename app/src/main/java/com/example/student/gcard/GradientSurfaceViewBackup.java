package com.example.student.gcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class GradientSurfaceViewBackup extends SurfaceView implements SurfaceHolder.Callback {

    private enum TouchDetect {
        TOUCH_LEFT_SQ,
        TOUCH_RIGHT_SQ,
        UNDEFINED
    }

    private TouchDetect mStartTouchDetect = TouchDetect.UNDEFINED;

    private Path mPath;

    private ArrayList<Path> mPaths = new ArrayList<>();

    private DrawThread thread;

    private Paint mPaint;

    private Square mLeftSquare, mRightSquare;

    public GradientSurfaceViewBackup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPaintDefault();
        getHolder().addCallback(this);

        mLeftSquare = new Square(new Point[] {
                new Point(50, 50),
                new Point(150, 50),
                new Point(150, 150),
                new Point(50, 150)
            }
        );

        mLeftSquare.setFillColor(Color.YELLOW);

        mRightSquare = new Square(new Point[] {
                new Point(930, 1570),
                new Point(1030, 1570),
                new Point(1030, 1670),
                new Point(930, 1670)
            }
        );

        mRightSquare.setFillColor(Color.RED);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_DOWN:
                mPath = new Path();
                mPath.moveTo(event.getX(), event.getY());
                if (mLeftSquare.isInArea(event.getX(), event.getY())) {
                    mPaint.setColor(mLeftSquare.getFillColor());
                    mStartTouchDetect = TouchDetect.TOUCH_LEFT_SQ;
                } else if (mRightSquare.isInArea(event.getX(), event.getY())) {
                    mPaint.setColor(mRightSquare.getFillColor());
                    mStartTouchDetect = TouchDetect.TOUCH_RIGHT_SQ;
                } else {
                    mPaint.setColor(Color.WHITE);
                }
                break;
            case ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
            case ACTION_UP:
                mPath.lineTo(event.getX(), event.getY());

                switch (mStartTouchDetect) {
                    case TOUCH_LEFT_SQ:
                        if (mRightSquare.isInArea(event.getX(), event.getY())) {
                            mPaths.add(mPath);
                        }
                        break;
                    case TOUCH_RIGHT_SQ:
                        if (mLeftSquare.isInArea(event.getX(), event.getY())) {
                            mPaths.add(mPath);
                        }
                        break;
                }

                mPath = new Path();
                break;
        }

        return true;
    }

    private void setPaintDefault() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(16);
        mPaint.setColor(Color.WHITE);
        mPaint.setShader(null);
        mPaint.setPathEffect(new DashPathEffect(new float[] {60f, 10f, 5f, 10f}, 0));
    }

    public void clear() {
        mPaths.clear();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new DrawThread(holder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.isRunning = false;
    }

    private class DrawThread extends  Thread {
        boolean isRunning = true;
        SurfaceHolder holder;

        DrawThread(SurfaceHolder holder) {
            super();
            this.holder = holder;
        }

        @Override
        public void run() {
            while (isRunning) {

                Canvas c = holder.lockCanvas();

                c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                c.drawPath(mLeftSquare.getPath(), mLeftSquare.getPaint());
                c.drawPath(mRightSquare.getPath(), mRightSquare.getPaint());

                if (mPath != null) {
                    c.drawPath(mPath, mPaint);
                }

                mPaint.setPathEffect(new CornerPathEffect(15f));
                mPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), mLeftSquare.getFillColor(), mRightSquare.getFillColor(), Shader.TileMode.MIRROR));
                for (Path path : mPaths) {
                    c.drawPath(path, mPaint);
                }
                setPaintDefault();

                holder.unlockCanvasAndPost(c);
            }
        }
    }

}
