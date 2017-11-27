package com.eyeque.eyeque;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.drawable.Drawable;

/**
 *
 * File:            PatternView.java
 * Description:     A view component to render the image pattern for the test
 * Created:         2016/03/19
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class PatternView extends View {

    private static int deviceId;
    private static Pattern pattern = new Pattern(0, 0);
    // private static boolean rotateCV = false;

    private static final int START_ANGLE_POINT = 90;
    private final Paint p;
    private float aniRadius;
    private int aniColor;
    private double radiansToDraw;
    private boolean drawDeviceOnly = false;


    // Tag for log message
    private static final String TAG = PatternView.class.getSimpleName();

    /**
     * Instantiates a new Pattern view.
     *
     * @param cxt   the cxt
     * @param attrs the attrs
     */
    public PatternView(Context cxt, AttributeSet attrs) {
        super(cxt, attrs);
        setMinimumHeight(100);
        setMinimumWidth(100);
        setBackgroundColor(Color.BLACK);
        invalidate();

        // Try animation
        final int strokeWidth = 36;
        p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(strokeWidth);
        //Circle color
        p.setColor(Color.RED);

    }

    /**
     * Sets device id.
     *
     * @param id the id
     */
    public void setDeviceId(int id) {
        deviceId = id;
        pattern.setDeviceId(id);
    }

    /**
     * Sets device only.
     *
     * @param value the value
     */
    public void setdrawDeviceOnly(boolean value) {
        drawDeviceOnly = value;
    }

    /**
     * Start.
     */
    public void start() {
        if (!drawDeviceOnly)
            pattern.start();
        // pattern.setDrawAxis();
        invalidate();
    }

    /**
     * Next pattern.
     */
    public void nextPattern() {
        pattern.nextPattern();
        // pattern.setDrawAxis();
        invalidate();
    }

    /**
     * Closer draw.
     *
     * @param step the step
     */
    public void closerDraw(int step) {
        pattern.moveCloser(step);
        invalidate();
    }

    /**
     * Further draw.
     *
     * @param step the step
     */
    public void furtherDraw(int step) {
        pattern.moveFurther(step);
        invalidate();
    }

    /**
     * Redraw.
     */
    public void reDraw() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas cv) {
        int color;
        double rColor = 1;
        int sqrHalfSizeX, sqrHalfSizeY;
        // cv.drawColor(Color.WHITE);
        // Paint p = new Paint();
        // Log.i("OnDraw", "drawType = " + String.valueOf(getDrawType()));

        // Practice test no device mode
        if (SingletonDataHolder.testMode == 1 && SingletonDataHolder.noDevice) {
            Drawable d = getResources().getDrawable(R.drawable.no_device);
            double  ppiRatio = SingletonDataHolder.phonePpi / 576.00;
            d.setBounds(SingletonDataHolder.centerX - (int) (100.00 * ppiRatio),
                    SingletonDataHolder.centerY + (int) (160.00 * ppiRatio),
                    SingletonDataHolder.centerX + (int) (100.00 * ppiRatio),
                   SingletonDataHolder.centerY + (int) (420.00 * ppiRatio) );
            d.draw(cv);
        }
            color = Color.rgb(200, 200, 200);
            // Draw Square
            switch (deviceId) {
                case 2:
                    p.setColor(color);
                    p.setStrokeWidth(5);
                    // sqrHalfSize = 562;
                    sqrHalfSizeX = (int) (SingletonDataHolder.deviceWidth * SingletonDataHolder.phonePpi / 2);
                    sqrHalfSizeY = sqrHalfSizeX;
                    p.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                    cv.drawRect(SingletonDataHolder.centerX - sqrHalfSizeX, SingletonDataHolder.centerY - sqrHalfSizeY, SingletonDataHolder.centerX + sqrHalfSizeX, SingletonDataHolder.centerY + sqrHalfSizeY, p);
                    p.setColor(Color.BLACK);
                    p.setStrokeWidth(10);
                    cv.drawRect(SingletonDataHolder.centerX - sqrHalfSizeX + 10, SingletonDataHolder.centerY - sqrHalfSizeY + 10, SingletonDataHolder.centerX + sqrHalfSizeX - 10, SingletonDataHolder.centerY + sqrHalfSizeY - 10, p);
                    break;
                case 3:
                    p.setColor(color);
                    p.setStrokeWidth(5);
                    // sqrHalfSize = 562;
                    sqrHalfSizeX = (int) (SingletonDataHolder.deviceWidth * SingletonDataHolder.phonePpi / 2);
                    sqrHalfSizeY = (int) (SingletonDataHolder.deviceHeight * SingletonDataHolder.phonePpi / 2);
                    // Log.i("*******************", Integer.toString(sqrHalfSizeY));
                    p.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                    // RectF r = new RectF(SingletonDataHolder.centerX - sqrHalfSizeX, SingletonDataHolder.centerY - sqrHalfSizeX + 165,
                    // SingletonDataHolder.centerX + sqrHalfSizeX, SingletonDataHolder.centerY + sqrHalfSizeX - 165);
                    RectF r = new RectF(SingletonDataHolder.centerX - sqrHalfSizeX, SingletonDataHolder.centerY - sqrHalfSizeY,
                            SingletonDataHolder.centerX + sqrHalfSizeX, SingletonDataHolder.centerY + sqrHalfSizeY);
                    if (!SingletonDataHolder.noDevice) {
                        cv.drawRoundRect(r, 60, 60, p);
                        // cv.drawRect(SingletonDataHolder.centerX - sqrHalfSize, SingletonDataHolder.centerY - sqrHalfSize + 165, SingletonDataHolder.centerX + sqrHalfSize, SingletonDataHolder.centerY + sqrHalfSize - 165, p);
                        p.setColor(Color.BLACK);
                        p.setStrokeWidth(10);
                    }
                    break;
                case 4:
                    p.setColor(color);
                    p.setStrokeWidth(5);
                    sqrHalfSizeX = 562;
                    cv.drawRect(SingletonDataHolder.centerX - sqrHalfSizeX, SingletonDataHolder.centerY - sqrHalfSizeX + 100, SingletonDataHolder.centerX + sqrHalfSizeX, SingletonDataHolder.centerY + sqrHalfSizeX - 100, p);
                    p.setColor(Color.BLACK);
                    p.setStrokeWidth(10);
                    break;
                default:
                    break;
            }

        p.setPathEffect(null);
        if (drawDeviceOnly) {
            p.setColor(color);

            // p.setStrokeWidth(2);
            // cv.drawText("Please attach device here", 720-500, 520, p);
            int fontSize = 72 * SingletonDataHolder.phonePpi / 576;
            Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            String firstLineText, secondLineText;

            if (SingletonDataHolder.lang.equals("zh")) {
                firstLineText = "";
                secondLineText = "请将设备贴此位置";
            } else {
                firstLineText = "Remove cover and";
                secondLineText = "place miniscope here";
            }
            p.setTypeface(typeface);
            p.setTextSize(fontSize);
            p.setStrokeWidth(6 * SingletonDataHolder.phonePpi / 576);
            float textWidth = p.measureText(secondLineText);
            // int xOffset = (int)((1440-textWidth)/2f) - (int)(fontSize/2f);
            int xOffset = (int)(SingletonDataHolder.centerX-(textWidth/2f));
            cv.drawText(firstLineText, xOffset+50, SingletonDataHolder.centerY - 45, p);
            cv.drawText(secondLineText, xOffset, SingletonDataHolder.centerY + 45, p);
        } else {
            if ((deviceId != 4 && pattern.getPattenIndex() > 0) || (deviceId == 4)) {
                int angle = pattern.getRotateAngle();
                cv.save();
                cv.rotate(angle, SingletonDataHolder.centerX, SingletonDataHolder.centerY);
            } else {
                cv.save();
                cv.rotate(0);
            }

            if (SingletonDataHolder.accommodationOn) {
                for (float ii = 380 * SingletonDataHolder.phonePpi / 562; ii >= 230 * SingletonDataHolder.phonePpi / 562; ii -= 0.5) {
                    // cv.save();
                    aniRadius = ii + 20 * SingletonDataHolder.phonePpi / 562;
                    color = Color.rgb(127, 70, 0);
                    p.setColor(color);
                    cv.drawCircle(SingletonDataHolder.centerX, SingletonDataHolder.centerY, ii, p);
                }
            }

            /****
            // Draw Accormodation Pattern
             p.setColor(Color.BLUE);  // p.setColor(getAniColor());
             p.setStyle(Paint.Style.STROKE);
             if (deviceId != 2) {
             p.setStrokeWidth(5);
             if (getAniRadius() > 20)
             cv.drawCircle(720, 520, getAniRadius()-20, p);
             cv.drawCircle(720, 520, getAniRadius(), p);
             }
             else {
             p.setStrokeWidth(24);
             cv.drawCircle(855, 520, getAniRadius(), p);
             }

            for (float ii = 150*SingletonDataHolder.phonePpi/562; ii >= 50; ii -= 0.5) {
                // cv.save();
                aniRadius = ii + 20;
                color = Color.rgb(127,114,0;
                p.setColor(color);
                cv.drawCircle(SingletonDataHolder.centerX, SingletonDataHolder.centerY, ii, p);
            }

            for (float ii = 150*SingletonDataHolder.phonePpi/562; ii >= 0; ii -= 0.5) {
                // cv.save();
                aniRadius = ii + 20;
                rColor = 0.6 * 0.5 * (0.4 + ii / 125f) * (1.0 - 1.0 * (Math.cos((double) (2 * Constants.PI * (radiansToDraw + ii * ((1 - ii / 125f) * 0.005 + 0.015))))));
                rColor = rColor * 255;
                if (rColor > 255)
                    rColor = 255;
                color = Color.rgb(0, 0, (int) (rColor));
                p.setColor(color);
                // Log.i("DEBUG", String.valueOf(ii));

                if (deviceId >= 2 && deviceId != 4)
                    // cv.drawCircle(855, 520, ii, p);
                    cv.drawCircle(pattern.getGreenStartX() - SingletonDataHolder.phonePpi*100/562, SingletonDataHolder.centerY, ii, p);
                else
                    cv.drawCircle(SingletonDataHolder.centerX, SingletonDataHolder.centerY, ii, p);
            }
             ****/

            p.setColor(Color.RED);
            if (deviceId == 2 || deviceId == 3) {
                p.setStrokeWidth(SingletonDataHolder.lineWidth);
                cv.drawLine(pattern.getRedStartX(), pattern.getRedStartY(),
                        pattern.getRedEndX(), pattern.getRedEndY(), p);
                // cv.drawLine(pattern.getRedStartX() + 36, pattern.getRedStartY() + 148,
                // pattern.getRedEndX() + 36, pattern.getRedEndY(), p);
                // cv.drawLine(pattern.getRedStartX() - 50, pattern.getRedStartY(),
                // pattern.getRedEndX()-1, pattern.getRedStartY(), p);
            } else if (deviceId == 4) {
                p.setStrokeWidth(30);
                cv.drawLine(pattern.getRedStartX(), pattern.getRedStartY(),
                        pattern.getRedEndX(), pattern.getRedEndY(), p);
            } else {
                p.setStrokeWidth(1);
                cv.drawLine(pattern.getRedStartX(), pattern.getRedStartY(),
                        pattern.getRedEndX(), pattern.getRedEndY(), p);
            }

            /***
            int yellowColor = Color.rgb(240, 230, 140);
            p.setStrokeWidth(120);
            p.setColor(yellowColor);
            cv.drawCircle(SingletonDataHolder.centerX, SingletonDataHolder.centerY, 280, p);
             ***/

            // Draw GREEN line
            p.setColor(Color.GREEN);
            if (deviceId == 2 || deviceId == 3) {
                if (SingletonDataHolder.noDevice) {
                    int yellowColor = Color.rgb(255, 255, 0);
                    // Log.i("***Width11***", String.valueOf(pattern.getGreenStartX()));
                    // Log.i("***Width12***", String.valueOf(pattern.getRedStartX()));
                    // Log.i("***Width13***", String.valueOf(SingletonDataHolder.lineWidth));

                    p.setStrokeWidth(SingletonDataHolder.lineWidth);
                    cv.drawLine(pattern.getGreenStartX(), pattern.getGreenStartY(),
                            pattern.getGreenEndX(), pattern.getGreenEndY(), p);

                    // Draw yellow lines in the overlapped area
                    int yellowWidth;
                    if ((pattern.getGreenStartX() - pattern.getRedStartX() < SingletonDataHolder.lineWidth)
                            && (pattern.getGreenStartX() - pattern.getRedStartX()) > 0) {
                        yellowWidth = SingletonDataHolder.lineWidth - (pattern.getGreenStartX() - pattern.getRedStartX());
                        p.setStrokeWidth(yellowWidth);
                        p.setColor(yellowColor);
                        int offset = -SingletonDataHolder.lineWidth / 2 + yellowWidth / 2;
                        cv.drawLine(pattern.getGreenStartX() + offset, pattern.getGreenStartY(),
                                pattern.getGreenEndX() + offset, pattern.getGreenEndY(), p);
                    } else if ((pattern.getRedStartX() - pattern.getGreenStartX() < SingletonDataHolder.lineWidth)
                            && (pattern.getRedStartX() - pattern.getGreenStartX()) > 0) {
                        yellowWidth = SingletonDataHolder.lineWidth - (pattern.getRedStartX() - pattern.getGreenStartX());
                        p.setStrokeWidth(yellowWidth);
                        p.setColor(yellowColor);
                        int offset = -SingletonDataHolder.lineWidth / 2 + yellowWidth / 2;
                        cv.drawLine(pattern.getRedStartX() + offset, pattern.getRedStartY(),
                                pattern.getRedEndX() + offset, pattern.getRedEndY(), p);
                    } else if (pattern.getGreenStartX() == pattern.getRedStartX()
                                && pattern.getGreenStartY() == pattern.getRedStartY()) {
                            p.setColor(yellowColor);
                            cv.drawLine(pattern.getGreenStartX(), pattern.getGreenStartY(),
                                    pattern.getGreenEndX(), pattern.getGreenEndY(), p);
                    }

                } else {
                    p.setStrokeWidth(SingletonDataHolder.lineWidth);
                    cv.drawLine(pattern.getGreenStartX(), pattern.getGreenStartY(),
                            pattern.getGreenEndX(), pattern.getGreenEndY(), p);
                    // cv.drawLine(pattern.getGreenStartX() - 36, pattern.getGreenStartY(),
                    // pattern.getGreenEndX() - 36, pattern.getGreenEndY() - 148, p);
                    // cv.drawLine(pattern.getGreenStartX() + 1, pattern.getGreenStartY(),
                    // pattern.getGreenEndX() + 50, pattern.getGreenStartY(), p);
                }
            } else if (deviceId == 4) {
                color = Color.rgb(0, 127, 0);
                p.setColor(color);
                p.setStrokeWidth(30);
                cv.drawLine(pattern.getGreenStartX(), pattern.getGreenStartY(),
                        pattern.getGreenEndX(), pattern.getGreenEndY(), p);
            } else {
                p.setStrokeWidth(1);
                cv.drawLine(pattern.getGreenStartX(), pattern.getGreenStartY(),
                        pattern.getGreenEndX(), pattern.getGreenEndY(), p);
            }
        }
    }

    /**
     * Gets angle.
     *
     * @return the angle
     */
    public int getAngle() {
        return pattern.getAngle();
    }

    /**
     * Gets ani radius.
     *
     * @return the ani radius
     */
    public float getAniRadius() {
        return aniRadius;
    }

    /**
     * Sets ani radius.
     *
     * @param radius the radius
     */
    public void setAniRadius(float radius) {
        this.aniRadius = radius;
    }

    /**
     * Gets ani color.
     *
     * @return the ani color
     */
    public int getAniColor() {
        return aniColor;
    }

    /**
     * Sets ani color.
     *
     * @param value the value
     */
    public void setAniColor(int value) {
        this.aniColor = value;
    }

    /**
     * Sets .
     *
     * @param value the value
     */
    public void setradians(double value) {

        this.radiansToDraw = value;
    }

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public int getDistance() {
        return pattern.getDistance();
    }

    /**
     * Gets device id.
     *
     * @return the device id
     */
    public int getDeviceId() { return deviceId; }

    /**
     * Gets pattern instance.
     *
     * @return the pattern instance
     */
    public Pattern getPatternInstance() {
        return pattern;
    }
}
