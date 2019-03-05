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

import com.example.student.gcard.Utils.PathExtended;

import java.util.ArrayList;
import java.util.List;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class GradientSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Square mStartSquare = null;
    
    private Path mPath;

    private List<PathExtended> mPaths = new ArrayList<>();

    private DrawThread thread;

    private Paint mPaint;

    private List<Square> mSquares = new ArrayList<>();

    public GradientSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPaintDefault();
        getHolder().addCallback(this);

        mSquares.add(new Square(new Point[]{
                new Point(50, 50),
                new Point(150, 50),
                new Point(150, 150),
                new Point(50, 150)
            }, Color.YELLOW
        ));

        mSquares.add(new Square(new Point[]{
                new Point(930, 1570),
                new Point(1030, 1570),
                new Point(1030, 1670),
                new Point(930, 1670)
            }, Color.RED
        ));

        mSquares.add(new Square(new Point[]{
                new Point(490, 50),
                new Point(590, 50),
                new Point(590, 150),
                new Point(490, 150)
            }, Color.GREEN
        ));

        mSquares.add(new Square(new Point[]{
                new Point(930, 50),
                new Point(1030, 50),
                new Point(1030, 150),
                new Point(930, 150)
            }, Color.BLUE
        ));

        mSquares.add(new Square(new Point[]{
                new Point(490, 1570),
                new Point(590, 1570),
                new Point(590, 1670),
                new Point(490, 1670)
            }, Color.MAGENTA
        ));

        mSquares.add(new Square(new Point[]{
                new Point(50, 1570),
                new Point(150, 1570),
                new Point(150, 1670),
                new Point(50, 1670)
            }, Color.WHITE
        ));

        mSquares.add(new Square(new Point[]{
                new Point(490, 785),
                new Point(590, 785),
                new Point(590, 885),
                new Point(490, 885)
        }, Color.LTGRAY
        ));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_DOWN:
                mPath = new Path();
                mPath.moveTo(event.getX(), event.getY());

                mPaint.setColor(Color.WHITE);
                mStartSquare = null;

                for (Square square : mSquares) {
                    if (square.isInArea(event.getX(), event.getY())) {
                        mPaint.setColor(square.getFillColor());
                        mStartSquare = square;
                    }
                }
                break;
            case ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
            case ACTION_UP:
                mPath.lineTo(event.getX(), event.getY());

                if (mStartSquare != null) {
                    for (Square square : mSquares) {
                        if (square.isInArea(event.getX(), event.getY())) {
                            PathExtended path = new PathExtended(mStartSquare, square, mPath);
                            mPaths.add(path);
                        }
                    }
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
        final SurfaceHolder holder;

        DrawThread(SurfaceHolder holder) {
            super();
            this.holder = holder;
        }

        @Override
        public void run() {
            while (isRunning) {

                Canvas c = null;
                try {
                    c = holder.lockCanvas();
                    synchronized (holder) {
                        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                        for (Square square : mSquares) {
                            c.drawPath(square.getPath(), square.getPaint());
                        }

                        if (mStartSquare != null) {
                            mPaint.setColor(mStartSquare.getFillColor());
                        } else {
                            mPaint.setColor(Color.WHITE);
                        }

                        if (mPath != null) {
                            c.drawPath(mPath, mPaint);
                        }

                        mPaint.setPathEffect(new CornerPathEffect(15f));
                        for (PathExtended path : mPaths) {
                            mPaint.setShader(new LinearGradient(path.getStartSquare().getLeftUpPoint().x, path.getStartSquare().getLeftUpPoint().y,
                                    path.getEndSquare().getRightDownPoint().x, path.getEndSquare().getRightDownPoint().y,
                                    path.getStartSquare().getFillColor(), path.getEndSquare().getFillColor(), Shader.TileMode.MIRROR));
                            c.drawPath(path.getPath(), mPaint);
                        }
                        setPaintDefault();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null)
                        holder.unlockCanvasAndPost(c);
                }
            }
        }
    }

}
