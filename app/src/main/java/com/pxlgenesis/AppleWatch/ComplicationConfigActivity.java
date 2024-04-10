/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pxlgenesis.AppleWatch;

import static com.pxlgenesis.AppleWatch.Constants.*;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.SeekBar;

import java.text.BreakIterator;
import java.util.concurrent.Executors;

public class ComplicationConfigActivity extends Activity implements View.OnClickListener {

    static final int COMPLICATION_CONFIG_REQUEST_CODE = 1001;
    // Selected complication id by user.
    private int mSelectedComplicationId;
    // ComponentName used to identify a specific service that renders the watch face.
    private ComponentName mWatchFaceComponentName;
    // Required to retrieve complication data from watch face for preview.
    private ProviderInfoRetriever mProviderInfoRetriever;
    private ImageView mRightComplicationBackground;
    private ImageView mTopRightComplicationBackground;
    private ImageView mTopRightRangedComplicationBackground;
    private ImageView mTopComplicationBackground;
    private ImageView mTopLeftComplicationBackground;
    private ImageView mTopLeftRangedComplicationBackground;
    private ImageView mLeftComplicationBackground;
    private ImageView mBottomComplicationBackground;
    private ImageView mBottomLeftRangedComplicationBackground;
    private ImageView mBottomRightRangedComplicationBackground;
    private ImageView mCenterComplicationBackground;
    private ImageButton mRightComplication;
    private ImageButton mTopRightComplication;
    private ImageButton mTopRightRangedComplication;
    private ImageButton mTopComplication;
    private ImageButton mTopLeftComplication;
    private ImageButton mTopLeftRangedComplication;
    private ImageButton mLeftComplication;
    private ImageButton mBottomComplication;
    private ImageButton mBottomLeftRangedComplication;
    private ImageButton mBottomRightRangedComplication;
    private ImageButton mCenterComplication;
    private Drawable mDefaultAddComplicationDrawable;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        //-----------------------------------------------------------
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar2);
        seekBar.setProgress(0);
        seekBar.incrementProgressBy(1);
        seekBar.setMax(4);
        //-----------------------------------------------------------
        final SeekBar seekBar1 =(SeekBar) findViewById(R.id.seekBar);
        seekBar1.setProgress(0);
        seekBar1.incrementProgressBy(1);
        seekBar1.setMax(4);
        seekBar1.setMax(255);
        //-----------------------------------------------------------
        TextView Text =(TextView) findViewById(R.id.textView4);
        TextView Text2 =(TextView) findViewById(R.id.textView5);
        TextView Text3 =(TextView) findViewById(R.id.textView6);
        //-----------------------------------------------------------
        final LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout1);
        final LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout2);
        final LinearLayout layout3 = (LinearLayout) findViewById(R.id.layout3);
        //-----------------------------------------------------------
        final SeekBar seekBar2 =(SeekBar) findViewById(R.id.seekBar3);
        seekBar2.setProgress(0);
        seekBar2.incrementProgressBy(1);
        seekBar2.setMax(4);
        //animation for text bounce--------------------------------------------------
        final Animation animation = new AlphaAnimation(0.9f,0.6f);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        //---------------------------------------------------------------------------
        final Animation animation2 = new ScaleAnimation(0.97f,1,0.97f,1,50,50);
        animation2.setDuration(500);
        animation2.setInterpolator(new LinearInterpolator());
        animation2.setRepeatMode(Animation.REVERSE);
        //---------------------------------------------------------------------------
        final Animation animationforseekbar = new ScaleAnimation(0.97f,1,0.97f,1,30,30);
        animation2.setDuration(3000);
        animation2.setInterpolator(new LinearInterpolator());
        animation2.setRepeatMode(Animation.REVERSE);
        //---------------------------------------------------------------------------
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress3, boolean b) {
                if(progress3==0){
                    AnalogAndARC.getEngine().SecondColor(208, 249, 212);
                    Text3.setText("GREEN");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress3==1){
                    AnalogAndARC.getEngine().SecondColor(248, 145, 3);
                    Text3.setText("DEFAULT");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress3==2){

                    AnalogAndARC.getEngine().SecondColor(211, 253, 253);
                    Text3.setText("BLUE");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress3==3){
                    AnalogAndARC.getEngine().SecondColor(255, 147, 166);
                    Text3.setText("RED");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress3==4) {
                    AnalogAndARC.getEngine().SecondColor(249, 208, 216);
                    Text3.setText("PINK");
                    seekBar.startAnimation(animationforseekbar);

                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.startAnimation(animationforseekbar);

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.startAnimation(animationforseekbar);
            }
        });
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress2, boolean b) {
                if(progress2==0){
                    AnalogAndARC.getEngine().TickColorAndRing(163,244,3,34, 55, 33);
                    Text2.setText("GREEN");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress2==1){
                    AnalogAndARC.getEngine().TickColorAndRing(82, 84, 84,29, 29, 29);
                    Text2.setText("DEFAULT");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress2==2){
                    AnalogAndARC.getEngine().TickColorAndRing(4,192,214,4, 42, 43);
                    Text2.setText("BLUE");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress2==3){
                    AnalogAndARC.getEngine().TickColorAndRing(212,72,72,43, 10, 4);
                    Text2.setText("RED");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress2==4){
                    AnalogAndARC.getEngine().TickColorAndRing(216,135,169,59, 47, 59);
                    Text2.setText("PINK");
                    seekBar.startAnimation(animationforseekbar);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.startAnimation(animationforseekbar);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.startAnimation(animationforseekbar);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==0){
                    AnalogAndARC.getEngine().HandColor(152, 199, 96);
                    Text.setText("GREEN");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress==1){
                    AnalogAndARC.getEngine().HandColor(255,255,255);
                    Text.setText("DEFAULT");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress==2){
                    AnalogAndARC.getEngine().HandColor(122, 193, 208);
                    Text.setText("BLUE");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress==3){
                    AnalogAndARC.getEngine().HandColor(241, 91, 75);
                    Text.setText("RED");
                    seekBar.startAnimation(animationforseekbar);
                }
                if(progress==4){
                    AnalogAndARC.getEngine().HandColor(216,135,169);
                    Text.setText("PINK");
                    seekBar.startAnimation(animationforseekbar);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.startAnimation(animationforseekbar);
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.startAnimation(animationforseekbar);
            }
        });
        final ImageView view1 =(ImageView) findViewById(R.id.top_left_complication_background);
        view1.startAnimation(animation);
        final ImageView view2 =(ImageView) findViewById(R.id.top_right_complication_background);
        view2.startAnimation(animation);
        final ImageView view3 =(ImageView) findViewById(R.id.right_complication_background);
        view3.startAnimation(animation);
        final ImageView view4 =(ImageView) findViewById(R.id.left_complication_background);
        view4.startAnimation(animation);
        final ImageView view5 =(ImageView) findViewById(R.id.bottom_right_ranged_complication_background);
        view5.startAnimation(animation);
        final ImageView view6 =(ImageView) findViewById(R.id.bottom_left_ranged_complication_background);
        view6.startAnimation(animation);
        final ImageView view7 =(ImageView) findViewById(R.id.top_left_ranged_complication_background);
        view7.startAnimation(animation);
        final ImageView view8 =(ImageView) findViewById(R.id.top_right_ranged_complication_background);
        view8.startAnimation(animation);
        final ImageView view9 =(ImageView) findViewById(R.id.bottom_complication_background);
        view9.startAnimation(animation);
        final TextView view10 =(TextView) findViewById(R.id.textView);
        view10.startAnimation(animation2);
        //---------------------------------------------------------------------------------------
        mDefaultAddComplicationDrawable = getDrawable(R.drawable.add_complication);

        mSelectedComplicationId = -1;

        mWatchFaceComponentName =
                new ComponentName(getApplicationContext(), AnalogAndARC.class);

        mRightComplicationBackground = findViewById(R.id.right_complication_background);
        mRightComplication = findViewById(R.id.right_complication);
        setUpComplication(mRightComplicationBackground, mRightComplication);

        mTopRightComplicationBackground = findViewById(R.id.top_right_complication_background);
        mTopRightComplication = findViewById(R.id.top_right_complication);
        setUpComplication(mTopRightComplicationBackground, mTopRightComplication);

        mTopRightRangedComplicationBackground = findViewById(R.id.top_right_ranged_complication_background);
        mTopRightRangedComplication = findViewById(R.id.top_right_ranged_complication);
        setUpComplication(mTopRightRangedComplicationBackground, mTopRightRangedComplication);

        mTopComplicationBackground = findViewById(R.id.top_complication_background);
        mTopComplication = findViewById(R.id.top_complication);
        setUpComplication(mTopComplicationBackground, mTopComplication);

        mTopLeftComplicationBackground = findViewById(R.id.top_left_complication_background);
        mTopLeftComplication = findViewById(R.id.top_left_complication);
        setUpComplication(mTopLeftComplicationBackground, mTopLeftComplication);

        mTopLeftRangedComplicationBackground = findViewById(R.id.top_left_ranged_complication_background);
        mTopLeftRangedComplication = findViewById(R.id.top_left_ranged_complication);
        setUpComplication(mTopLeftRangedComplicationBackground, mTopLeftRangedComplication);

        mLeftComplicationBackground = findViewById(R.id.left_complication_background);
        mLeftComplication = findViewById(R.id.left_complication);
        setUpComplication(mLeftComplicationBackground, mLeftComplication);

        mBottomComplicationBackground = findViewById(R.id.bottom_complication_background);
        mBottomComplication = findViewById(R.id.bottom_complication);
        setUpComplication(mBottomComplicationBackground, mBottomComplication);

        mBottomRightRangedComplicationBackground = findViewById(R.id.bottom_right_ranged_complication_background);
        mBottomRightRangedComplication = findViewById(R.id.bottom_right_ranged_complication);
        setUpComplication(mBottomRightRangedComplicationBackground, mBottomRightRangedComplication);

        mBottomLeftRangedComplicationBackground = findViewById(R.id.bottom_left_ranged_complication_background);
        mBottomLeftRangedComplication = findViewById(R.id.bottom_left_ranged_complication);
        setUpComplication(mBottomLeftRangedComplicationBackground, mBottomLeftRangedComplication);

        mCenterComplicationBackground = findViewById(R.id.center_complication_background);
        mCenterComplication = findViewById(R.id.center_complication);
        setUpComplication(mCenterComplicationBackground, mCenterComplication);

        AnalogAndARC.Engine e = AnalogAndARC.getEngine();
        Switch mHollowSwitch;
        mHollowSwitch = findViewById(R.id.hollow_switch);
        mHollowSwitch.setChecked(e.getHollowMode());
        mHollowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AnalogAndARC.Engine e = AnalogAndARC.getEngine();
                e.setHollowMode(b);
                layout1.startAnimation(animation2);
            }
        });
        Switch translucent;
        translucent = findViewById(R.id.switch1);
        translucent.setChecked(e.getTranslucentMode());
        translucent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AnalogAndARC.Engine e = AnalogAndARC.getEngine();
                e.setTranslucentMode(b);
                layout2.startAnimation(animation2);
            }
        });
        Switch Textgradient1;
        Textgradient1 = findViewById(R.id.switch2);
        Textgradient1.setChecked(e.getColorTextMode());
        Textgradient1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AnalogAndARC.Engine e = AnalogAndARC.getEngine();
                e.setColorTextMode(b);
                layout3.startAnimation(animation2);
            }
        });
        // Initialization of code to retrieve active complication data for the watch face.
        mProviderInfoRetriever =
                new ProviderInfoRetriever(getApplicationContext(), Executors.newCachedThreadPool());
        mProviderInfoRetriever.init();
        retrieveInitialComplicationsData();
    }
    // Used by {@link ComplicationConfigActivity} to retrieve id for complication locations and
    // to check if complication location is supported.
    static int getComplicationId(
            ComplicationLocation complicationLocation) {
        // Add any other supported locations here you would like to support. In our case, we are
        // only supporting a left and right complication.
        switch (complicationLocation) {
            case RIGHT:
                return RIGHT_COMPLICATION_ID;
            case TOP_RIGHT:
                return TOP_RIGHT_COMPLICATION_ID;
            case TOP_RIGHT_RANGED:
                return TOP_RIGHT_RANGED_COMPLICATION_ID;
            case TOP:
                return TOP_COMPLICATION_ID;
            case TOP_LEFT:
                return TOP_LEFT_COMPLICATION_ID;
            case TOP_LEFT_RANGED:
                return TOP_LEFT_RANGED_COMPLICATION_ID;
            case LEFT:
                return LEFT_COMPLICATION_ID;
            case BOTTOM:
                return BOTTOM_COMPLICATION_ID;
            case BOTTOM_RIGHT_RANGED:
                return BOTTOM_RIGHT_RANGED_COMPLICATION_ID;
            case BOTTOM_LEFT_RANGED:
                return BOTTOM_LEFT_RANGED_COMPLICATION_ID;
            case CENTER:
                return CENTER_COMPLICATION_ID;
            default:
                return -1;
        }
    }
    private void setUpComplication(ImageView complicationBackground, ImageButton complication) {
        // Sets up left complication preview.
        complication.setOnClickListener(this);
        // Sets default as "Add Complication" icon.
        complication.setImageDrawable(mDefaultAddComplicationDrawable);
        complicationBackground.setVisibility(View.INVISIBLE);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Required to release retriever for active complication data.
        mProviderInfoRetriever.release();
    }
    public void retrieveInitialComplicationsData() {

        final int[] complicationIds = COMPLICATION_IDS;

        mProviderInfoRetriever.retrieveProviderInfo(
                new ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
                    @Override
                    public void onProviderInfoReceived(
                            int watchFaceComplicationId,
                            @Nullable ComplicationProviderInfo complicationProviderInfo) {
                        updateComplicationViews(watchFaceComplicationId, complicationProviderInfo);
                    }
                },
                mWatchFaceComponentName,
                complicationIds);
    }
    @Override
    public void onClick(View view) {
        if (view.equals(mRightComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.RIGHT);
        } else if (view.equals(mTopRightComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.TOP_RIGHT);
        } else if (view.equals(mTopRightRangedComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.TOP_RIGHT_RANGED);
        } else if (view.equals(mTopComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.TOP);
        } else if (view.equals(mTopLeftComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.TOP_LEFT);
        } else if (view.equals(mTopLeftRangedComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.TOP_LEFT_RANGED);
        } else if (view.equals(mLeftComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.LEFT);
        } else if (view.equals(mBottomComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.BOTTOM);
        } else if (view.equals(mBottomRightRangedComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.BOTTOM_RIGHT_RANGED);
        } else if (view.equals(mBottomLeftRangedComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.BOTTOM_LEFT_RANGED);
        } else if (view.equals(mCenterComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.CENTER);
        }
    }
    // Verifies the watch face supports the complication location, then launches the helper
    // class, so user can choose their complication data provider.
    private void launchComplicationHelperActivity(ComplicationLocation complicationLocation) {
        mSelectedComplicationId =
                getComplicationId(complicationLocation);
        if (mSelectedComplicationId >= 0) {
            int[] supportedTypes =
                    AnalogAndARC.getSupportedComplicationTypes(
                            complicationLocation);
            startActivityForResult(
                    ComplicationHelperActivity.createProviderChooserHelperIntent(
                            getApplicationContext(),
                            mWatchFaceComponentName,
                            mSelectedComplicationId,
                            supportedTypes),
                    ComplicationConfigActivity.COMPLICATION_CONFIG_REQUEST_CODE);
        }
    }
    private void updateComplicationView(ComplicationProviderInfo complicationProviderInfo, ImageButton complication, ImageView complicationBackground) {
        if (complicationProviderInfo != null) {
            complication.setImageIcon(complicationProviderInfo.providerIcon);
            complicationBackground.setVisibility(View.VISIBLE);
            complication.setScaleType(ImageView.ScaleType.CENTER);
        }
        else {
            complication.setImageDrawable(mDefaultAddComplicationDrawable);
            complicationBackground.setVisibility(View.INVISIBLE);
            complication.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
    public void updateComplicationViews(
            int watchFaceComplicationId, ComplicationProviderInfo complicationProviderInfo) {
        if (watchFaceComplicationId == RIGHT_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mRightComplication, mRightComplicationBackground);
        } else if (watchFaceComplicationId == TOP_RIGHT_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mTopRightComplication, mTopRightComplicationBackground);
        } else if (watchFaceComplicationId == TOP_RIGHT_RANGED_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mTopRightRangedComplication, mTopRightRangedComplicationBackground);
        } else if (watchFaceComplicationId == TOP_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mTopComplication, mTopComplicationBackground);
        } else if (watchFaceComplicationId == TOP_LEFT_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mTopLeftComplication, mTopLeftComplicationBackground);
        } else if (watchFaceComplicationId == TOP_LEFT_RANGED_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mTopLeftRangedComplication, mTopLeftRangedComplicationBackground);
        } else if (watchFaceComplicationId == LEFT_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mLeftComplication, mLeftComplicationBackground);
        } else if (watchFaceComplicationId == BOTTOM_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mBottomComplication, mBottomComplicationBackground);
        } else if (watchFaceComplicationId == BOTTOM_LEFT_RANGED_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mBottomLeftRangedComplication, mBottomLeftRangedComplicationBackground);
        } else if (watchFaceComplicationId == BOTTOM_RIGHT_RANGED_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mBottomRightRangedComplication, mBottomRightRangedComplicationBackground);
        } else if (watchFaceComplicationId == CENTER_COMPLICATION_ID) {
            updateComplicationView(complicationProviderInfo, mCenterComplication, mCenterComplicationBackground);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPLICATION_CONFIG_REQUEST_CODE && resultCode == RESULT_OK) {
            // Retrieves information for selected Complication provider.
            ComplicationProviderInfo complicationProviderInfo =
                    data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO);
            if (mSelectedComplicationId >= 0) {
                updateComplicationViews(mSelectedComplicationId, complicationProviderInfo);
            }
        }
    }

}
