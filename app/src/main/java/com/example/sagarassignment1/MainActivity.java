package com.example.sagarassignment1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sagarassignment1.databinding.ActivityMainBinding;

/**
 * Entry point for the Stopwatch
 * Handles all the functionality and interactions
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding mainBinding;

    // Ref: https://stackoverflow.com/questions/61023968/what-do-i-use-now-that-handler-is-deprecated
    private final Handler timerHandler = new Handler(Looper.getMainLooper());

    // Enumeration for the time state
    private enum TimerState {
        STOPPED,
        RUNNING
    }

    private TimerState currentState = TimerState.STOPPED;

    private static final long DEFAULT_TIME = 0;
    private long startTime = DEFAULT_TIME;
    private long timeWhenStopped = DEFAULT_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing view binding
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        initializeClickListeners();
    }

    // Interface for handling click event
    private void initializeClickListeners() {
        mainBinding.startStopButton.setOnClickListener(this);
        mainBinding.resetButton.setOnClickListener(this);
    }

    /**
     * This method is called whenever click event happened in UI
     * @param clickedView The view that was clicked
     */
    @Override
    public void onClick(View clickedView) {
        // Getting the id of the button when it is clicked
        int clickedViewId = clickedView.getId();

        // Checking which button was clicked and calling its handler
        if(clickedViewId == R.id.startStopButton) {
            handleStartStopClick();
        } else if(clickedViewId == R.id.resetButton) {
            handleResetClick();
        }
    }

    /**
     * Handling start and stop button click
     */
    private void handleStartStopClick() {
        if(currentState == TimerState.STOPPED) {
            // Starting a timer
            startTimer();
            currentState = TimerState.RUNNING;

            updateButtonStates();
        } else {
            // Stop the timer
            stopTimer();
            currentState = TimerState.STOPPED;
            updateButtonStates();
        }
    }

    // Updating button states based on current time state
    private void updateButtonStates() {
        if(currentState == TimerState.RUNNING) {
            // When timer is running
            mainBinding.startStopButton.setImageResource(R.drawable.pause);
            mainBinding.resetButton.setEnabled(false);
        } else {
            // When timer is stopped
            mainBinding.startStopButton.setImageResource(R.drawable.play);
            mainBinding.resetButton.setEnabled(true);
        }
    }

    /**
     * Starting a timer
     * <a href="https://developer.android.com/reference/android/os/SystemClock#elapsedRealtime()">Documentation for ElapsedRealTime</a>
     */
    private void startTimer() {
        // If timer was stopped before, adding the previous elapsed time
        startTime = SystemClock.elapsedRealtime() - timeWhenStopped;
        timerHandler.post(updateTimerRunnable);
    }

    // Runnable that update the timer display
    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            // Calculating the elapsed time
            long timeInMilliSecond = SystemClock.elapsedRealtime() - startTime;
            updateTimerDisplay(timeInMilliSecond);
            // Scheduling next update in 1 second
            timerHandler.postDelayed(this, 1000);
        }
    };

    // Updating the timer text view
    private void updateTimerDisplay(long timeInMilliSecond) {
        // Converting milliseconds to hours, minutes, and seconds
        int seconds = (int) (timeInMilliSecond / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        // Formating time string
        String timeString = String.format(getString(R.string.timer_format), hours, minutes, seconds);

        // Updating view with formatted time
        mainBinding.textView.setText(timeString);
    }

    /**
     * Stopping the timer by removing callback from the handler
     */
    private void stopTimer() {
        // Remove any pending timer
        timerHandler.removeCallbacks(updateTimerRunnable);
        // Storing elapsed time in case timer is resumed
        timeWhenStopped = SystemClock.elapsedRealtime() - startTime;
    }

    /**
     * Resetting timer to initial state
     */
    private void handleResetClick() {
        // Stopping any running timer
        timerHandler.removeCallbacks(updateTimerRunnable);
        // Resetting all variable of time
        startTime = 0L;
        timeWhenStopped = 0L;
        mainBinding.textView.setText(R.string.initial_timer);
    }

    /**
     * Cleanup the resources when activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Removing any pending timer updates
        timerHandler.removeCallbacks(updateTimerRunnable);
        // Cleaning up binding reference
        mainBinding = null;
    }
}
