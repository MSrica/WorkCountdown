package com.example.workcountdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.workcountdown.R;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

// variables
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // constants
    protected final int oneHourToMilliseconds = 3600000, oneMinuteToMilliseconds = 60000, oneSecondInMilliseconds = 1000, minuteInSeconds = 60, hourInSeconds = 3600, vibrationMilliseconds = 5000;

    // variables
    protected int timeInMilliseconds = 60000, startingTimeInMilliseconds = 60000;
    protected boolean oneHour, twoHours, threeHours, fifteenMinutes, thirtyMinutes;

    // layout
    protected TextView timerTextView, clockTextView, hoursTextView, minutesTextView;
    protected Button startGiveUpButton, beginButton, resetTimesButton;
    protected SwitchCompat oneHourSwitch, twoHoursSwitch, threeHoursSwitch, fifteenMinutesSwitch, thirtyMinutesSwitch;
    protected ConstraintLayout layout;
    protected ViewTreeObserver vto;

    // miscellaneous
    protected CountDownTimer countdown = null;
    private final Handler clockHandler = new Handler();
    private final Runnable clockRunnable = new Runnable() {
        public void run() {
            String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", Calendar.getInstance().getTime().getHours(), Calendar.getInstance().getTime().getMinutes(), Calendar.getInstance().getTime().getSeconds());
            clockTextView.setText(time);

            clockHandler.postDelayed(this, 1000);
        }
    };

    protected Vibrator vibrator;

// functions
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // initialization
    protected void viewsInitialization(){
    layout = findViewById(R.id.mainLayout);

    timerTextView = findViewById(R.id.timer);
    clockTextView = findViewById(R.id.clock);
    hoursTextView = findViewById(R.id.hours);
    minutesTextView = findViewById(R.id.minutes);

    oneHourSwitch = findViewById(R.id.oneHourSwitch);
    twoHoursSwitch = findViewById(R.id.twoHoursSwitch);
    threeHoursSwitch = findViewById(R.id.threeHoursSwitch);
    fifteenMinutesSwitch = findViewById(R.id.fifteenMinutesSwitch);
    thirtyMinutesSwitch = findViewById(R.id.thirtyMinutesSwitch);

    startGiveUpButton = findViewById(R.id.startGiveUpButton);
    beginButton = findViewById(R.id.beginButton);
    resetTimesButton = findViewById(R.id.resetTime);

    vto = layout.getViewTreeObserver();
    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
}
    protected void layoutSetup(){
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final ConstraintLayout layout = findViewById(R.id.mainLayout);

            @Override
            public void onGlobalLayout() {
                this.layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final int layoutWidth  = layout.getMeasuredWidth();
                final int layoutHeight = layout.getMeasuredHeight();

                ViewGroup.LayoutParams startGiveUpButtonParams = startGiveUpButton.getLayoutParams();

                startGiveUpButtonParams.width = (int) (layoutWidth / 3);
                startGiveUpButtonParams.height = (int) (layoutHeight * 0.2);

                startGiveUpButton.setLayoutParams(startGiveUpButtonParams);

                clockTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (layoutHeight * 0.1));
                timerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (layoutHeight * 0.3));
            }
        });
    }
    protected void countdownInitialization(){
        countdown = new CountDownTimer(timeInMilliseconds, oneSecondInMilliseconds) {
            public void onTick(long millisecondsUntilFinished) { printTime(millisecondsUntilFinished); }
            public void onFinish() {
                countdownStop();
                vibrate();
            }
        };
    }
    protected void hideSetup(){
        hoursTextView.setVisibility(View.INVISIBLE);
        minutesTextView.setVisibility(View.INVISIBLE);

        oneHourSwitch.setVisibility(View.INVISIBLE);
        twoHoursSwitch.setVisibility(View.INVISIBLE);
        threeHoursSwitch.setVisibility(View.INVISIBLE);
        fifteenMinutesSwitch.setVisibility(View.INVISIBLE);
        thirtyMinutesSwitch.setVisibility(View.INVISIBLE);

        beginButton.setVisibility(View.INVISIBLE);

        timerTextView.setVisibility(View.VISIBLE);
        clockTextView.setVisibility(View.VISIBLE);
        startGiveUpButton.setVisibility(View.VISIBLE);
        resetTimesButton.setVisibility(View.VISIBLE);
    }
    protected void showSetup(){
        timerTextView.setVisibility(View.INVISIBLE);
        clockTextView.setVisibility(View.INVISIBLE);
        startGiveUpButton.setVisibility(View.INVISIBLE);
        resetTimesButton.setVisibility(View.INVISIBLE);

        hoursTextView.setVisibility(View.VISIBLE);
        minutesTextView.setVisibility(View.VISIBLE);

        oneHourSwitch.setVisibility(View.VISIBLE);
        twoHoursSwitch.setVisibility(View.VISIBLE);
        threeHoursSwitch.setVisibility(View.VISIBLE);
        fifteenMinutesSwitch.setVisibility(View.VISIBLE);
        thirtyMinutesSwitch.setVisibility(View.VISIBLE);

        beginButton.setVisibility(View.VISIBLE);
    }

    // countdown
    protected void startCountdown(){
        countdownInitialization();
        countdown.start();
        startGiveUpButton.setText(getString(R.string.giveUpString));
        startGiveUpButton.setBackgroundColor(startGiveUpButton.getContext().getResources().getColor(R.color.pink));
    }
    protected void resetCountdown(){
        if(countdown != null){
            countdown.cancel();
            countdown = null;
        }

        printTime(startingTimeInMilliseconds);
        startGiveUpButton.setText(getString(R.string.startString));
        startGiveUpButton.setBackgroundColor(startGiveUpButton.getContext().getResources().getColor(R.color.cyan));
    }

    protected void countdownStop(){
        countdown.cancel();
        countdown = null;
    }

    // buttons
    protected void timerActions(){
        if(startGiveUpButton.getText().equals("Start")) startCountdown();
        else if(startGiveUpButton.getText().equals("Give Up")) resetCountdown();
    }
    protected void setupFinished(){
        timeInMilliseconds = 0;
        if(fifteenMinutes) timeInMilliseconds += 15*oneMinuteToMilliseconds;
        if(thirtyMinutes) timeInMilliseconds += 30*oneMinuteToMilliseconds;

        if(oneHour) timeInMilliseconds += oneHourToMilliseconds;
        if(twoHours) timeInMilliseconds += 2*oneHourToMilliseconds;
        if(threeHours) timeInMilliseconds += 3*oneHourToMilliseconds;

        if(timeInMilliseconds != 0){
            hideSetup();

            printTime(timeInMilliseconds);
            startingTimeInMilliseconds = timeInMilliseconds;
        }

        oneHour = false;
        twoHours = false;
        threeHours = false;
        fifteenMinutes = false;
        thirtyMinutes = false;
    }
    protected void resetTimes(){
        resetCountdown();
        oneHourSwitch.setChecked(false);
        twoHoursSwitch.setChecked(false);
        threeHoursSwitch.setChecked(false);
        fifteenMinutesSwitch.setChecked(false);
        thirtyMinutesSwitch.setChecked(false);
        showSetup();
    }

    // display values
    protected void printTime(long millisecondsUntilFinished){
        int seconds = (int) (millisecondsUntilFinished / 1000);
        final int hours = seconds / hourInSeconds;
        final int minutes = (seconds % hourInSeconds) / minuteInSeconds;
        final int secs = seconds % minuteInSeconds;

        final String time = String.format(Locale.getDefault(), "%d:%02d:%02d",  hours, minutes, secs);
        timerTextView.setText(time);
    }

    protected void vibrate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(vibrationMilliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        else vibrator.vibrate(vibrationMilliseconds);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        viewsInitialization();
        layoutSetup();

        clockRunnable.run();
        startGiveUpButton.setOnClickListener(new View.OnClickListener() {public void onClick(View v) { timerActions(); }});
        beginButton.setOnClickListener(new View.OnClickListener() {public void onClick(View v) { setupFinished(); }});
        resetTimesButton.setOnClickListener(new View.OnClickListener() {public void onClick(View v) { resetTimes(); }});

        oneHourSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) oneHour = true;
            }
        });
        twoHoursSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) twoHours = true;
            }
        });
        threeHoursSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) threeHours = true;
            }
        });
        fifteenMinutesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) fifteenMinutes = true;
            }
        });
        thirtyMinutesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) thirtyMinutes = true;
            }
        });
    }
}