package com.pxlgenesis.AppleWatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationText;

public class ArcComplication{
    //For gradient and PC1 for ambient only.
    private final int primaryColor;
    private final int primaryColor2;

    //Does not correspond to actual width
    private final float width;

    //Secondary color for the fixed range
    private final int secondaryColor;

    private final Context context;

    //C H O N K    B O U N D S
    private final RectF complicationBounds;

    //Icon boundary for watch
    private final Rect iconBounds;

    //Paints for colors
    private final Paint primaryPaint;
    private final Paint secondaryPaint;

    //Paint for text
    private Paint textPaint;

    //Defined path for text
    private final Path textPath;

    //start and sweep angles for arc
    private final int startAngle;
    private final int sweepAngle;

    //boolean for ambient and hollow
    private boolean ambientmrm;
    private boolean hollowmrm;

    //Paint for ambient arc
    private final Paint ambarcpaint;

    //Conditional Color for text
    private int textcolor;

    //smol ring bound
    private final RectF bound2;

    //conditional bound
    private RectF bound;

    //rangewidth smaller
    private float width2;

    //conditional  width

    private float wd=14f;

    //icon color
    private int iconcolor;

    //textraster
    private boolean textraster;


    public ArcComplication(Context context, RectF complicationBounds,RectF bound2, Rect iconBounds, float width, float width2,
                           int primaryColor, int secondaryColor,int primaryColor2, int startAngle, int sweepAngle,boolean textraster) {
        //Defining the function contents
        this.context = context;
        this.complicationBounds = complicationBounds;
        this.iconBounds = iconBounds;
        this.bound2 = bound2;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        this.width = width;
        this.width2= width2;
        this.primaryColor2 = primaryColor2;
        this.textraster = textraster;



        //----
        primaryPaint = createPaint(primaryColor);
        primaryPaint.setShader(new LinearGradient(0,50,201,50, primaryColor, primaryColor2,Shader.TileMode.CLAMP));
        //----
        secondaryPaint = createPaint(secondaryColor);
        //----
        ambarcpaint = createPaint(Color.BLACK);
        //----
        ambarcpaint.setAntiAlias(true);
        ambarcpaint.setStrokeWidth(22f);
        //----
        textPaint = createPaint(textcolor);
        textPaint.setStrokeWidth(1f);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setAntiAlias(true);
        //----
        iconcolor=Color.TRANSPARENT;

        //----
        textcolor=Color.WHITE;
        bound=complicationBounds;
        //Path defined
        textPath = new Path();
        textPath.addArc(bound, startAngle+3, sweepAngle);
    }
    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(31);
        paint.setTypeface(Typeface.SANS_SERIF);
        return paint;
        }
    public void draw(Canvas canvas, ComplicationData complicationData) {
        if(complicationData == null)
            return;
        if(complicationData.getType() == ComplicationData.TYPE_RANGED_VALUE) {
            float minValue = complicationData.getMinValue();
            float maxValue = complicationData.getMaxValue();
            float currentValue = complicationData.getValue();
            // Translate the current progress to a percentage value between 0 and 1.
            float percent = 0;
            float range = Math.abs(maxValue - minValue);
            if (range > 0) {
                percent = (currentValue - minValue) / range;

                //We don't want to deal progress values outside 0-100.
                percent = Math.max(0f, percent);
                percent = Math.min(2f, percent);
            }
                canvas.drawArc(complicationBounds, startAngle, sweepAngle, false, secondaryPaint);
                canvas.drawArc(complicationBounds, startAngle, sweepAngle * percent, false, primaryPaint);
            //Draw it on the canvas.

            //Used to draw the hollow active range in ambient mode

            if(ambientmrm) {
                if(hollowmrm){}
                else{
                canvas.drawArc(complicationBounds, startAngle * 9999.9f / 10000, sweepAngle * percent * 9999.9f / 10000, false, ambarcpaint);}
            }
        } else{
            String textToDraw = "";
            long currentTime = System.nanoTime();
            ComplicationText complicationText = complicationData.getLongTitle();
            if(complicationText != null) {
                textToDraw =  complicationText.getText(context, currentTime).toString();
                textToDraw.toUpperCase();
            }
            complicationText = complicationData.getLongText();
            if(complicationText != null) {
                textToDraw += " " + complicationText.getText(context, currentTime).toString();
            }
            complicationText = complicationData.getLongText();
            if(complicationText != null) {
                textToDraw += " " + complicationText.getText(context, currentTime).toString();
            }
            complicationText = complicationData.getImageContentDescription();
            if(complicationText != null) {
                textToDraw += " " + complicationText.getText(context, currentTime).toString();
            }

            if (textToDraw.length()>24){
                textToDraw=textToDraw.substring(0,23);
                textToDraw+="...";
            }
            if(textToDraw.length()<=24){
                if(textToDraw==""){
                    textToDraw="";
                }
                else{
                textToDraw+="...";}

            }
            if(textraster) {
                canvas.drawArc(complicationBounds, startAngle, sweepAngle, false, primaryPaint);
                canvas.drawTextOnPath(textToDraw, textPath, -width / 2, width / 4, textPaint);
            }
            }
        drawRangeIcon(canvas, complicationData);
    }
    private void drawRangeIcon(Canvas canvas, ComplicationData complicationData) {
        Icon i = null;
        if (complicationData.getIcon() != null) {
            i = complicationData.getIcon();
        } else if (complicationData.getSmallImage() != null) {
            i = complicationData.getSmallImage();
        } else if (complicationData.getLargeImage() != null) {
            i = complicationData.getLargeImage();
        } else if (complicationData.getBurnInProtectionSmallImage() != null) {
            i = complicationData.getBurnInProtectionSmallImage();
        } else if (complicationData.getBurnInProtectionIcon() != null) {
            i = complicationData.getBurnInProtectionIcon();
        }

        if (i != null) {
            if(ambientmrm){
                i.setTint(Color.BLACK);}
            else{
                i.setTint(iconcolor);}
            Drawable icon = i.loadDrawable(context);
            icon.setBounds(iconBounds);
            icon.draw(canvas);
        }
    }

    public void setAmbientMode(boolean ambientMode) {
        if(ambientMode) {ambientmrm=true;
            primaryPaint.setAntiAlias(true);
            primaryPaint.setShader(null);
            primaryPaint.setColor(primaryColor);
            secondaryPaint.setAntiAlias(true);
            secondaryPaint.setColor(Color.BLACK);
            textPaint.setAntiAlias(true);
            textPaint.setColor(textcolor);
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setStrokeWidth(1f);
            if(hollowmrm){textcolor=Color.BLACK;}
            else {textcolor=Color.WHITE;}
        } else{ambientmrm=false;
            primaryPaint.setAntiAlias(true);
            primaryPaint.setColor(primaryColor);
            primaryPaint.setShader(new LinearGradient(0,80,201,80, primaryColor, primaryColor2,Shader.TileMode.MIRROR));
            textPaint.setAntiAlias(true);
            textPaint.setColor(textcolor);
            secondaryPaint.setAntiAlias(true);
            secondaryPaint.setColor(secondaryColor);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setStrokeWidth(1f);
        }
    }
    public void setHollow(boolean hollow) {
        if(hollow) {
            if(ambientmrm) {
                primaryPaint.setStrokeWidth(width);
                secondaryPaint.setStrokeWidth(width);
            }
            else{
                primaryPaint.setStrokeWidth(3f);
                secondaryPaint.setStrokeWidth(3f);
            }
        } else {
            hollowmrm=false;
            primaryPaint.setStrokeWidth(width);
            secondaryPaint.setStrokeWidth(width);
        }
    }
    public void seticonbadge(boolean iconbadge) {
        if(iconbadge){
            iconcolor=Color.WHITE;
        }
        else{
            iconcolor=Color.TRANSPARENT;
        }

    }
}