package com.pxlgenesis.AppleWatch;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.rendering.ComplicationDrawable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import static com.pxlgenesis.AppleWatch.Constants.*;
import static com.pxlgenesis.AppleWatch.ComplicationLocation.*;
import androidx.core.content.ContextCompat;
/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class AnalogAndARC extends CanvasWatchFaceService {
    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(~10);
    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;
    private static Engine engine;
    // Used by {@link ComplicationConfigActivity} to retrieve complication types supported by
    // location.
    static int[] getSupportedComplicationTypes(
            ComplicationLocation complicationLocation) {
        // Add any other supported locations here.
        if (complicationLocation == BOTTOM)
            return LARGE_COMPLICATION_TYPES;
        else
            return LARGE_COMPLICATION_TYPES;
    }
    @Override
    public Engine onCreateEngine() {
        engine = new Engine();
        return engine;
    }
    public static Engine getEngine() {
        return engine;
    }
    public class EngineHandler extends Handler {
        private final WeakReference<AnalogAndARC.Engine> mWeakReference;
        public EngineHandler(AnalogAndARC.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }
        @Override
        public void handleMessage(Message msg) {
            AnalogAndARC.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }
    public class Engine extends CanvasWatchFaceService.Engine {
        /* Handler to update the time once a second in interactive mode. */
        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        private float mCenterX;
        private float mCenterY;
        private float mComplicationMargin;
        private static final float HOUR_STROKE_WIDTH = 14f;
        private static final float MINUTE_STROKE_WIDTH = 14f;
        private static final float SECOND_TICK_STROKE_WIDTH = 2f;
        private static final float CENTER_GAP_AND_CIRCLE_RADIUS = 3f;
        private int mcirclecolor;
        private int vision2=255;
        private int vision=255;
        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        private int mWatchHandColor;
        private int mWatchHandHighlightColor;
        private int CircleAmbient;
        private int msecondcolor;
        private int mtickcolor;
        private boolean isAmbientMode;
        private boolean isHollowMode;
        private boolean isColorTextMode;
        private boolean istranslucentMode;
        public float Ambientlength=14f;
        public float Ambientlengthsmol=3f;
        private float mSecondHandLength;
        private float sMinuteHandLength;
        private float sHourHandLength;
        private float sHourExtensionHandLength;
        private float sMinuteExtensionHandLength;
        private Paint mhourextension;
        private Paint ambhourextension;
        private Paint ambminuteextension;
        private Paint mminuteextension;
        private Paint DeathBlack;
        private Paint watchhandgrey;
        private Paint mMinutePaint;
        private Paint mSecondPaint;
        private Paint mTickAndCirclePaint;
        private Paint transparentPaint;
        private Paint tickGREY;
        private Paint whitey;
        private Paint orangec;
        private Paint mHourPaint;
        private Paint mCenterPaint;
        private Paint mBottomPaint;
        private Paint mBackgroundPaint;
        private Paint AppleBlack;
        private Paint AppleGrey;
        /*
         * Whether the display supports fewer bits for each color in ambient mode.
         * When true, we disable anti-aliasing in ambient mode.
         */
        private boolean hasLowBitAmbient;
        /*
         * Whether the display supports burn in protection in ambient mode.
         * When true, remove the persistent images in ambient mode.
         */
        private boolean hasBurnInProtection;
        /* Maps complication ids to corresponding ComplicationDrawable that renders the
         * the complication data on the watch face.
         */
        private ComplicationDrawable[] mComplicationDrawables;
        // Stores the ranged complication on the edge of the screen
        private ArcComplication[] mRangedComplications;
        /* Maps active complication ids to the data for that complication. Note: Data will only be
         * present if the user has chosen a provider via the settings activity for the watch face.
         */
        private ComplicationData[] complicationData;
        // ... whatever other set up you need to do ...
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(AnalogAndARC.this)
                    .setAcceptsTapEvents(true)
                    .build());
            mCalendar = Calendar.getInstance();
            CircleAmbient=Color.argb(44, 45, 45, 1);
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);
            tickGREY = new Paint();
            tickGREY.setColor(Color.GRAY);
            DeathBlack = new Paint();
            DeathBlack.setColor(CircleAmbient);
            DeathBlack.setAntiAlias(true);
            watchhandgrey = new Paint();
            watchhandgrey.setARGB(51, 51, 60, 1);
            watchhandgrey.setAntiAlias(true);
            mCenterPaint = new Paint();
            mCenterPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mCenterPaint.setStrokeWidth(1f);
            mCenterPaint.setTextAlign(Paint.Align.CENTER);
            mCenterPaint.setColor(Color.WHITE);
            mCenterPaint.setAntiAlias(true);
            mBottomPaint = new Paint();
            mBottomPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mBottomPaint.setStrokeWidth(1f);
            mBottomPaint.setTextAlign(Paint.Align.CENTER);
            int BOTTOM_ROW_ITEM_SIZE = 24;
            mBottomPaint.setTextSize(BOTTOM_ROW_ITEM_SIZE);
            mBottomPaint.setColor(Color.WHITE);
            mBottomPaint.setAntiAlias(true);
            AppleBlack = new Paint();
            AppleBlack.setColor(Color.BLACK);
            AppleBlack.setAntiAlias(true);
            AppleGrey = new Paint();
            AppleGrey.setColor(Color.GRAY);
            AppleGrey.setAntiAlias(true);
            CircleAmbient = Color.argb(44, 45, 45, 1);
            whitey = new Paint();
            whitey.setColor(Color.argb(44, 45, 45, 1));
            whitey.setAntiAlias(true);
            initializeComplications();
            initializeWatchFace();
        }
        private void initializeWatchFace() {
            mWatchHandHighlightColor = Color.rgb(255, 165, 0);
            mtickcolor = Color.rgb(82, 84, 84);
            mcirclecolor=Color.rgb(29, 29, 29);
            mWatchHandColor=Color.WHITE;
            msecondcolor=Color.rgb(255, 165, 0);
            orangec = new Paint();
            orangec.setColor(msecondcolor);
            orangec.setAntiAlias(true);
            mHourPaint = new Paint();
            mHourPaint.setColor(mWatchHandColor);
            mHourPaint.setStrokeWidth(HOUR_STROKE_WIDTH);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);
            mHourPaint.getStrokeJoin();
            //-------
            transparentPaint = new Paint();
            transparentPaint.setColor(mcirclecolor);
            transparentPaint.setAntiAlias(true);
            //-------
            mhourextension = new Paint();
            mhourextension.setColor(mWatchHandColor);
            mhourextension.setStrokeWidth(5f);
            mhourextension.setAntiAlias(true);
            mhourextension.setStrokeCap(Paint.Cap.ROUND);
            mhourextension.getStrokeJoin();
            //--------
            ambhourextension = new Paint();
            ambhourextension.setColor(Color.BLACK);
            ambhourextension.setStrokeWidth(11f);
            ambhourextension.setAntiAlias(true);
            ambhourextension.setStrokeCap(Paint.Cap.ROUND);
            //-------
            ambminuteextension = new Paint();
            ambminuteextension.setColor(Color.BLACK);
            ambminuteextension.setStrokeWidth(8f);
            ambminuteextension.setAntiAlias(true);
            ambminuteextension.setStrokeCap(Paint.Cap.ROUND);
            //----
            mminuteextension = new Paint();
            mminuteextension.setColor(mWatchHandColor);
            mminuteextension.setStrokeWidth(5f);
            mminuteextension.setAntiAlias(true);
            mminuteextension.setStrokeCap(Paint.Cap.ROUND);
            mminuteextension.getStrokeJoin();
            mMinutePaint = new Paint();
            mMinutePaint.setColor(mWatchHandColor);
            mMinutePaint.setStrokeWidth(MINUTE_STROKE_WIDTH);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);
            mSecondPaint = new Paint();
            mSecondPaint.setColor(mWatchHandHighlightColor);
            mSecondPaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
            mTickAndCirclePaint = new Paint();
            mTickAndCirclePaint.setColor(mtickcolor);
            mTickAndCirclePaint.setStrokeWidth(3f);
            mTickAndCirclePaint.setAntiAlias(true);
            mTickAndCirclePaint.setStyle(Paint.Style.STROKE);
        }
        private void initializeComplications() {
            complicationData = new ComplicationData[COMPLICATION_IDS.length];
            mComplicationDrawables = new ComplicationDrawable[COMPLICATION_IDS.length];
            mRangedComplications = new ArcComplication[RANGE_COMPLICATION_COUNT];
            for (int i = 0; i < COMPLICATION_IDS.length; i++) {
                initializeComplication(i);
            }
            setActiveComplications(COMPLICATION_IDS);
        }
        private void initializeComplication(int complicationId) {
            ComplicationDrawable complicationDrawable =
                    (ComplicationDrawable) getDrawable(R.drawable.custom_complication_styles);
            if (complicationDrawable != null) {
                complicationDrawable.setContext(getApplicationContext());
                mComplicationDrawables[complicationId] = complicationDrawable;
            }
        }
        public boolean getHollowMode() {
            return isHollowMode;
        }
        public boolean getTranslucentMode() {return istranslucentMode;}
        public boolean getColorTextMode() {
            return isColorTextMode;
        }
        public void setColorTextMode(boolean Colortext) {
            isColorTextMode = Colortext;
            if (Colortext) {
                for (ArcComplication mRangedComplication : mRangedComplications) {
                    mRangedComplication.seticonbadge(true);
                }
            }
            else {
                for (ArcComplication mRangedComplication : mRangedComplications) {
                    mRangedComplication.seticonbadge(false);
                }
            }
            invalidate();
        }

        public void setTranslucentMode(boolean translucent) {
            istranslucentMode = translucent;
            if (translucent) {vision2=50;vision=0;} else {vision2=255;vision=255;}
            invalidate();
        }
        public void setHollowMode(boolean hollow) {
            isHollowMode = hollow;
            if (hollow) {
                mCenterPaint.setStyle(Paint.Style.STROKE);
                mBottomPaint.setStyle(Paint.Style.STROKE);
                CircleAmbient=Color.BLACK;
                for (ArcComplication mRangedComplication : mRangedComplications) {
                    mRangedComplication.setHollow(true);
                }
            } else {
                mCenterPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mBottomPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                CircleAmbient=Color.argb(44, 45, 45, 1);
                    for (ArcComplication mRangedComplication : mRangedComplications) {
                    mRangedComplication.setHollow(false);
                }
            }
            invalidate();
        }
        @Override
        public void onComplicationDataUpdate(
                int complicationId, ComplicationData complicationData) {
            // Adds/updates active complication data in the array.
            this.complicationData[complicationId] = complicationData;
            // Updates correct ComplicationDrawable with updated data.
            ComplicationDrawable complicationDrawable =
                    mComplicationDrawables[complicationId];
            complicationDrawable.setComplicationData(complicationData);
            invalidate();
        }
        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }
        public void HandColor(int Red,int Green,int Blue) {
            mWatchHandColor=Color.rgb(Red,Green,Blue);
        }
        public void SecondColor(int Red,int Green,int Blue) {
            mWatchHandHighlightColor=Color.rgb(Red,Green,Blue);
            msecondcolor=Color.rgb(Red,Green,Blue);
            orangec.setColor(msecondcolor);
        }
        public void TickColorAndRing(int Red,int Green,int Blue,int Red2,int Green2,int Blue2) {
            mcirclecolor=Color.rgb( Red2, Green2, Blue2);
            mtickcolor=Color.rgb( Red, Green, Blue);
        }
        @Override
        public void onPropertiesChanged(Bundle properties) {
            hasLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            hasBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
            updateWatchHandStyle();
            ComplicationDrawable complicationDrawable;
            for (int i = 0; i < COMPLICATION_IDS.length; i++) {
                complicationDrawable = mComplicationDrawables[i];
                if (complicationDrawable != null) {
                    complicationDrawable.setLowBitAmbient(hasLowBitAmbient);
                    complicationDrawable.setBurnInProtection(hasBurnInProtection);
                }
            }
        }
        private void updateWatchHandStyle() {
            if (isAmbientMode) {
                mHourPaint.setAntiAlias(false);
                mMinutePaint.setAntiAlias(false);
                mSecondPaint.setAntiAlias(false);
                mTickAndCirclePaint.setAntiAlias(false);
                mhourextension.setAntiAlias(false);
                mminuteextension.setAntiAlias(false);
            } else {
                mHourPaint.setAntiAlias(true);
                mMinutePaint.setAntiAlias(true);
                mSecondPaint.setAntiAlias(true);
                mTickAndCirclePaint.setAntiAlias(true);
                mhourextension.setAntiAlias(true);
                mminuteextension.setAntiAlias(true);
            }
        }
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            isAmbientMode = inAmbientMode;
            if (isAmbientMode) {
                if (hasLowBitAmbient) {
                    mCenterPaint.setAntiAlias(false);
                    mBottomPaint.setAntiAlias(false);
                }
            } else {
                if (hasLowBitAmbient) {
                    mCenterPaint.setAntiAlias(true);
                    mBottomPaint.setAntiAlias(true);
                }
            }
            setHollowMode(isHollowMode);
            setTranslucentMode(istranslucentMode);
            // Update drawable complications' ambient state.
            // Note: ComplicationDrawable handles switching between active/ambient colors, we just
            // have to inform it to enter ambient mode.
            ComplicationDrawable complicationDrawable;
            for (int i = 0; i < COMPLICATION_IDS.length; i++) {
                complicationDrawable = mComplicationDrawables[i];
                complicationDrawable.setInAmbientMode(isAmbientMode);
            }
            for (ArcComplication mRangedComplication : mRangedComplications) {
                mRangedComplication.setAmbientMode(isAmbientMode);
            }
            // Check and trigger whether or not timer should be running (only in active mode).
            updateTimer();
        }
        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);
            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode;
                invalidate();
            }
        }
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Context context = getApplicationContext();
            mCenterX = width / 2f;
            mCenterY = height / 2f;
            mSecondHandLength = (float) (mCenterX * 0.83);
            sMinuteHandLength = (float) (mCenterX * 0.74);
            sHourHandLength = (float) (mCenterX * 0.4);
            sMinuteExtensionHandLength = (float) (mCenterX * 0.2);
            sHourExtensionHandLength = (float) (mCenterX * 0.2);
            int sizeOfComplication = width / 5;
            mCenterPaint.setTextSize(width / 8f);
            mComplicationMargin = sizeOfComplication / 18f;
            mCenterPaint.setTextSize(width / 8f);
            float rangeWidthF = width / 16f;
            float rangeWidthF2 = width / 20f;
            int rangeThickness = width / 10;
            int rangeOffset = rangeThickness / 2;
            float rangeOffsetF = rangeWidthF / 2;

                //Smol ring bounds :]
                RectF rangeBoundsC = new RectF(rangeOffsetF +150, height - rangeOffsetF - 165, width - rangeOffsetF - 150,
                        height - rangeOffsetF - 85);
                RectF rangeBoundsB = new RectF(rangeOffsetF + 48, rangeOffsetF + 187, rangeOffsetF + 130,
                        height - rangeOffsetF - 187);
                RectF rangeBoundsA = new RectF(rangeOffsetF +150, rangeOffset + 80, width - rangeOffsetF - 150,
                    rangeOffsetF + 170);
                //--------------------
                RectF rangeBoundsF =new RectF(rangeOffsetF, rangeOffsetF + 36, width - rangeOffsetF ,
                        height - rangeOffsetF - 36);
                //C H O N K    B O U N D S
            int bluespace=60;
            int greenspace=90;
            int redspace=120;
                RectF rangeBoundsCBLUE = new RectF(rangeOffsetF +130, rangeOffset + 160, width - rangeOffsetF - 130,
                    rangeOffsetF + 284);
                RectF rangeBoundsBGREEN =   new RectF(rangeOffsetF +100, rangeOffset + 130, width - rangeOffsetF - 100,
                        rangeOffsetF + 314);
                RectF rangeBoundsARED =  new RectF(rangeOffsetF +70, rangeOffset + 100, width - rangeOffsetF - 70,
                        rangeOffsetF + 344);
                //-----------------------
            // region Center bounds and complications
            int midpointOfScreen = width / 2;
            int radialMarginOffset = (midpointOfScreen - sizeOfComplication) / 2;
            int verticalOffset = midpointOfScreen - (sizeOfComplication / 2);
            // Left, Top, Right, Bottom
            Rect rightBounds = new Rect(
                    (width - sizeOfComplication) -10,
                    verticalOffset + 230,
                    (width) - 15,
                    (verticalOffset + sizeOfComplication) + 225);
            ComplicationDrawable rightComplicationDrawable =
                    mComplicationDrawables[RIGHT_COMPLICATION_ID];
            rightComplicationDrawable.setBounds(rightBounds);
            Rect topRightBounds =

                    // Left, Top, Right, Bottom
                    new Rect(
                            (width - radialMarginOffset - sizeOfComplication) + 35,
                            (radialMarginOffset) - 40,
                            (width - radialMarginOffset) + 55,
                            (radialMarginOffset + sizeOfComplication) - 20);
            ComplicationDrawable topRightComplicationDrawable =
                    mComplicationDrawables[TOP_RIGHT_COMPLICATION_ID];
            topRightComplicationDrawable.setBounds(topRightBounds);
            Rect topBounds =
                    // Left, Top, Right, Bottom
                    new Rect(
                            (midpointOfScreen - radialMarginOffset),
                             80,
                            (midpointOfScreen + radialMarginOffset) - 80,
                            (sizeOfComplication + radialMarginOffset));
            ComplicationDrawable topComplicationDrawable =
                    mComplicationDrawables[TOP_COMPLICATION_ID];
            topComplicationDrawable.setBounds(topBounds);
            Rect topLeftBounds =
                    // Left, Top, Right, Bottom
                    new Rect(
                            (radialMarginOffset) - 55,
                            (radialMarginOffset) - 40,
                            (radialMarginOffset + sizeOfComplication) - 35,
                            (radialMarginOffset + sizeOfComplication) - 20);
            ComplicationDrawable topLeftComplicationDrawable =
                    mComplicationDrawables[TOP_LEFT_COMPLICATION_ID];
            topLeftComplicationDrawable.setBounds(topLeftBounds);
            Rect leftBounds =
                    // Left, Top, Right, Bottom
                    new Rect(
                            15,
                            verticalOffset + 230,
                            (sizeOfComplication)+10,
                            (verticalOffset + sizeOfComplication) + 225);
            ComplicationDrawable leftComplicationDrawable =
                    mComplicationDrawables[LEFT_COMPLICATION_ID];
            leftComplicationDrawable.setBounds(leftBounds);
            // Left, Top, Right, Bottom
            Rect bottomBounds = new Rect(
                    radialMarginOffset + 42,
                    leftBounds.bottom - 455,
                    (width - radialMarginOffset) - 42,
                    leftBounds.bottom + sizeOfComplication - 483);
            ComplicationDrawable bottomComplicationDrawable =
                    mComplicationDrawables[BOTTOM_COMPLICATION_ID];
            bottomComplicationDrawable.setBounds(bottomBounds);
            Rect centerBounds =
                    // Left, Top, Right, Bottom
                    new Rect(
                            leftBounds.right,
                            topBounds.bottom,
                            rightBounds.left,
                            bottomBounds.top);
            ComplicationDrawable centerComplicationDrawable =
                    mComplicationDrawables[CENTER_COMPLICATION_ID];
            centerComplicationDrawable.setBounds(centerBounds);
            //endregion
            //region Arc bounds and complications


            Rect topRightRangedBounds = new Rect(
                    (int) rangeBoundsF.centerX() - rangeOffset+8,
                    169,
                    (int) rangeBoundsF.centerX() + rangeOffset-8,
                    rangeThickness+150);


            Rect topLeftRangedBounds =new Rect(
                    (int) rangeBoundsF.centerX() - rangeOffset+8,
                    139,
                    (int) rangeBoundsF.centerX() + rangeOffset-8,
                    rangeThickness+120);


            Rect bottomRightRangedBounds =new Rect(
                    (int) rangeBoundsF.centerX() - rangeOffset+170,
                    320,
                    (int) rangeBoundsF.centerX() + rangeOffset+167,
                    rangeThickness+318);


            Rect bottomLeftRangedBounds = new Rect(
                    (int) rangeBoundsF.centerX() - rangeOffset+8,
                    109,
                    (int) rangeBoundsF.centerX() + rangeOffset-8,
                    rangeThickness+90);


            ArcComplication topRightRanged = new ArcComplication(context,rangeBoundsCBLUE, rangeBoundsC,
                    topRightRangedBounds, rangeWidthF,rangeWidthF2, ContextCompat.getColor(context,R.color.blue1),
                    ContextCompat.getColor(context, R.color.light_purple),ContextCompat.getColor(context,R.color.blue2), -90, 360,false);
            mRangedComplications[0] = topRightRanged;
            ArcComplication bottomRightRanged = new ArcComplication(context, rangeBoundsF,rangeBoundsF,
                    bottomRightRangedBounds, rangeWidthF,rangeWidthF2, ContextCompat.getColor(context, R.color.black),
                    ContextCompat.getColor(context, R.color.black),ContextCompat.getColor(context,R.color.black), 40, 110,true);
            mRangedComplications[1] = bottomRightRanged;
            ArcComplication bottomLeftRanged = new ArcComplication(context,rangeBoundsARED, rangeBoundsA, bottomLeftRangedBounds, rangeWidthF,rangeWidthF2,
                    ContextCompat.getColor(context, R.color.red1), ContextCompat.getColor(context, R.color.light_red),ContextCompat.getColor(context,R.color.red2),
                    -90, 360,false);
            mRangedComplications[2] = bottomLeftRanged;
            ArcComplication topLeftRanged = new ArcComplication(context,rangeBoundsBGREEN, rangeBoundsB, topLeftRangedBounds, rangeWidthF,rangeWidthF2,
                    ContextCompat.getColor(context, R.color.green1), ContextCompat.getColor(context, R.color.light_green),ContextCompat.getColor(context,R.color.green2),
                    -90, 360,false);
            mRangedComplications[3] = topLeftRanged;
            //endregion
        }
        /**
         * Captures tap event (and tap type). The {@link WatchFaceService#TAP_TYPE_TAP} case can be
         * used for implementing specific logic to handle the gesture.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            if (tapType == TAP_TYPE_TAP) {
                int tappedComplicationId = getTappedComplicationId(x, y);
                if (tappedComplicationId != -1) {
                    onComplicationTap(tappedComplicationId);
                }
            }
        }
        /*
         * Determines if tap inside a complication area or returns -1.
         */
        private int getTappedComplicationId(int x, int y) {
            ComplicationData complicationData;
            ComplicationDrawable complicationDrawable;
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < COMPLICATION_IDS.length; i++) {
                complicationData = this.complicationData[i];
                if ((complicationData != null)
                        && (complicationData.isActive(currentTimeMillis))
                        && (complicationData.getType() != ComplicationData.TYPE_NOT_CONFIGURED)
                        && (complicationData.getType() != ComplicationData.TYPE_EMPTY)) {
                    complicationDrawable = mComplicationDrawables[i];
                    Rect complicationBoundingRect = complicationDrawable.getBounds();
                    if (complicationBoundingRect.width() > 0) {
                        if (complicationBoundingRect.contains(x, y)) {
                            return i;
                        }
                    }
                }
            }
            return -1;
        }
        // Fires PendingIntent associated with complication (if it has one).
        private void onComplicationTap(int complicationId) {
            ComplicationData complicationData =
                    this.complicationData[complicationId];
            if (complicationData != null) {
                if (complicationData.getTapAction() != null) {
                    try {
                        complicationData.getTapAction().send();
                    } catch (PendingIntent.CanceledException e) {
                    }
                } else if (complicationData.getType() == ComplicationData.TYPE_NO_PERMISSION) {
                    // Watch face does not have permission to receive complication data, so launch
                    // permission request.
                    ComponentName componentName =
                            new ComponentName(
                                    getApplicationContext(), AnalogAndARC.class);
                    Intent permissionRequestIntent =
                            ComplicationHelperActivity.createPermissionRequestHelperIntent(
                                    getApplicationContext(), componentName);
                    startActivity(permissionRequestIntent);
                }
            }
        }
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            if (complicationData[CENTER_COMPLICATION_ID] != null) {
                if (complicationData[CENTER_COMPLICATION_ID].getShortText() != null)
                    canvas.drawText(complicationData[CENTER_COMPLICATION_ID].getShortText().getText(
                                    getApplicationContext(), now).toString(),
                            mCenterX, mCenterY + mComplicationMargin, mCenterPaint);
            }
            if (!isAmbientMode){
                drawBackground(canvas);
                drawComplications(canvas, now);
                mminuteextension.setColor(mWatchHandColor);
                mHourPaint.setColor(mWatchHandColor);
                mMinutePaint.setColor(mWatchHandColor);
                mhourextension.setColor(mWatchHandColor);
                mTickAndCirclePaint.setColor(mtickcolor);
                transparentPaint.setColor(mcirclecolor);
                mSecondPaint.setColor(msecondcolor);
                mminuteextension.setAntiAlias(true);
                mHourPaint.setAntiAlias(true);
                mMinutePaint.setAntiAlias(true);
                mhourextension.setAntiAlias(true);
                mTickAndCirclePaint.setAntiAlias(true);
                mTickAndCirclePaint.setStrokeWidth(3f);
                transparentPaint.setAntiAlias(true);
                mSecondPaint.setAntiAlias(true);
                canvas.drawCircle(201, 238, 170, transparentPaint);
                canvas.drawCircle(201, 238, 152, AppleBlack);
                canvas.drawCircle(201, 238, 8, DeathBlack);
                mhourextension.setAlpha(vision);
                mHourPaint.setAlpha(vision2);
                mMinutePaint.setAlpha(vision2);
                mminuteextension.setAlpha(vision);
            } else {
                drawBackground(canvas);
                drawComplications(canvas,now);
                mminuteextension.setAntiAlias(false);
                mHourPaint.setAntiAlias(false);
                mMinutePaint.setAntiAlias(false);
                mhourextension.setAntiAlias(false);
                mTickAndCirclePaint.setAntiAlias(false);
                transparentPaint.setAntiAlias(false);
                mSecondPaint.setAntiAlias(false);
                mHourPaint.setColor(Color.rgb(198, 198, 198));
                mMinutePaint.setColor(Color.rgb(198, 198, 198));
                mhourextension.setColor(Color.rgb(198, 198, 198));
                mminuteextension.setColor(Color.rgb(198, 198, 198));
                mTickAndCirclePaint.setColor(Color.rgb(198, 198, 198));
                mTickAndCirclePaint.setStrokeWidth(3f);
                mhourextension.setAlpha(vision);
                mHourPaint.setAlpha(vision2);
                mMinutePaint.setAlpha(vision2);
                mminuteextension.setAlpha(vision);
                mHourPaint.setStrokeWidth(Ambientlength);
                mMinutePaint.setStrokeWidth(Ambientlength);
                mhourextension.setStrokeWidth(Ambientlengthsmol);
                mminuteextension.setStrokeWidth(Ambientlengthsmol);
                ambhourextension.setStrokeWidth(8f);
                ambminuteextension.setStrokeWidth(8f);}
            for (int i = 0; i < mRangedComplications.length; i++) {
                ComplicationData complicationData = this.complicationData[i + RANGED_ID_OFFSET];
                mRangedComplications[i].draw(canvas, complicationData);
                drawWatchFace(canvas);
                if (hasLowBitAmbient) {
                    updateWatchHandStyle();
                }
            }
        }

        private void drawWatchFace(Canvas canvas) {
            /*
             * Draw ticks. Usually you will want to bake this directly into the photo, but in
             * cases where you want to allow users to select their own photos, this dynamically
             * creates them on top of the photo.
             */
            float innerTickRadius = 152;
            float outerTickRadius = 170;
            for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
                float tickRot = (float) (tickIndex * Math.PI * 2 / 12);
                float innerX = (float) Math.sin(tickRot) * innerTickRadius;
                float innerY = (float) -Math.cos(tickRot) * innerTickRadius;
                float outerX = (float) Math.sin(tickRot) * outerTickRadius;
                float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
                canvas.drawLine(mCenterX + innerX, mCenterY + innerY,
                        mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint);
            }
            /*
             * These calculations reflect the rotation in degrees per unit of time, e.g.,
             * 360 / 60 = 6 and 360 / 12 = 30.
             */
            final float seconds = (mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f);
            final float secondsRotation = seconds;
            final float minutesRotation = mCalendar.get(Calendar.MINUTE) * 6f;
            final float hourHandOffset = mCalendar.get(Calendar.MINUTE) / 2f;
            final float hoursRotation = (mCalendar.get(Calendar.HOUR) * 30) + hourHandOffset;
            /*
             * Save the canvas state before we can begin to rotate it.
             */
            canvas.save();
            canvas.rotate(hoursRotation, mCenterX, mCenterY);
            canvas.drawLine(
                    mCenterX,
                    mCenterY - 30 - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY - sHourHandLength,
                    mHourPaint);
            canvas.drawLine(
                    mCenterX,
                    mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY - sHourExtensionHandLength,
                    mhourextension);
            if(isAmbientMode){canvas.drawLine(
                    mCenterX,
                    mCenterY -30 - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY  - sHourHandLength,
                    ambhourextension);}
            if(isHollowMode){
                canvas.drawLine(
                        mCenterX,
                        mCenterY -30 - CENTER_GAP_AND_CIRCLE_RADIUS,
                        mCenterX,
                        mCenterY  - sHourHandLength,
                        ambhourextension);
            }
            canvas.rotate(minutesRotation - hoursRotation, mCenterX, mCenterY);
            canvas.drawLine(
                    mCenterX,
                    mCenterY - 30 - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY - sMinuteHandLength,
                    mMinutePaint);
            canvas.drawLine(
                    mCenterX,
                    mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY - sMinuteExtensionHandLength,
                    mminuteextension);
            if(isAmbientMode){canvas.drawLine(
                    mCenterX,
                    mCenterY -30- CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY  - sMinuteHandLength,
                    ambminuteextension);}
            if(isHollowMode){canvas.drawLine(
                    mCenterX,
                    mCenterY -30- CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY  - sMinuteHandLength,
                    ambminuteextension);}
            /*
             * Ensure the "seconds" hand is drawn only when we are in interactive mode.
             * Otherwise, we only update the watch face once a minute.
             */
            if (!isAmbientMode) {
                canvas.rotate(secondsRotation * 6 - minutesRotation, mCenterX, mCenterY);
                canvas.drawLine(
                        mCenterX,
                        mCenterY + 35 - CENTER_GAP_AND_CIRCLE_RADIUS,
                        mCenterX,
                        mCenterY - mSecondHandLength,
                        mSecondPaint);
                canvas.drawCircle(201, 238, 7, orangec);
            }
            canvas.drawCircle(
                    mCenterX,
                    mCenterY,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    mTickAndCirclePaint);
            /* Restore the canvas" original orientation. */
            canvas.restore();
            if (isAmbientMode) {
                mHourPaint.setStrokeWidth(Ambientlength);
                mMinutePaint.setStrokeWidth(Ambientlength);
                mhourextension.setStrokeWidth(Ambientlengthsmol);
                mminuteextension.setStrokeWidth(Ambientlengthsmol);
                canvas.drawCircle(201, 238, 8, whitey);
                canvas.drawCircle(201, 238, 4, AppleBlack);
            }else{
                mHourPaint.setStrokeWidth(12f);
                mMinutePaint.setStrokeWidth(12f);
                mhourextension.setStrokeWidth(5f);
                mminuteextension.setStrokeWidth(5f);
            }
        }
        private void drawComplications(Canvas canvas, long currentTimeMillis) {
            for (int i = 0; i < CENTER_COMPLICATION_ID; i++) {
                ComplicationDrawable complicationDrawable = mComplicationDrawables[i];
                complicationDrawable.draw(canvas, currentTimeMillis);
                complicationDrawable.setBackgroundColorActive(mcirclecolor);
                complicationDrawable.setIconColorActive(msecondcolor);
                complicationDrawable.setRangedValuePrimaryColorActive(mWatchHandColor);
                complicationDrawable.setTextTypefaceActive(Typeface.SANS_SERIF);
                complicationDrawable.setBorderColorAmbient(Color.WHITE);
                complicationDrawable.setBorderWidthAmbient(2);
                complicationDrawable.setBackgroundColorAmbient(Color.BLACK);
                complicationDrawable.setIconColorAmbient(android.R.color.background_light);
            }
        }

        private void drawBackground(Canvas canvas) {
            if (isAmbientMode && (hasLowBitAmbient || hasBurnInProtection)) {
                canvas.drawColor(Color.BLACK);
            }
            if(!isAmbientMode&& (hasLowBitAmbient || hasBurnInProtection)){
                canvas.drawColor(Color.BLACK);
            }
        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceiver();
                /* Update time zone in case it changed while we weren't visible. */
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }
            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }
        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            AnalogAndARC.this.registerReceiver(mTimeZoneReceiver, filter);
        }
        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            AnalogAndARC.this.unregisterReceiver(mTimeZoneReceiver);
        }
        /**
         * Starts/stops the {@link #mUpdateTimeHandler} timer based on the state of the watch face.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }
        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer
         * should only run in active mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isAmbientMode;
        }
        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}



