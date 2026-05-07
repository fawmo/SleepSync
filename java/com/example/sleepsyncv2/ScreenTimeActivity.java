package com.example.sleepsyncv2;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

public class ScreenTimeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView tvScreenTimeHours;
    private TextView tvScreenTimeLimit;
    private TextView tvScreenTimePercentage;
    private TextView tvCurrentLimit;
    private TextView tvWeeklyAverage;
    private SeekBar seekbarScreenTimeLimit;
    private Button btnEditLimit;
    private Button btnNotificationSettings;
    
    // Notification settings preferences
    private boolean notifyWhenLimitReached = true;
    private boolean notifyWhenNearLimit = true;
    private boolean showHourlyReminders = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.screentimescreen);

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupTabLayout();
        setupScreenTimeControls();
        
        // Select screen time tab by default
        tabLayout.selectTab(tabLayout.getTabAt(1));
    }

    private void initializeViews() {
        // Initialize TabLayout
        tabLayout = findViewById(R.id.tabs);
        
        // Initialize TextViews
        tvScreenTimeHours = findViewById(R.id.tv_screen_time_hours);
        tvScreenTimeLimit = findViewById(R.id.tv_screen_time_limit);
        tvScreenTimePercentage = findViewById(R.id.tv_screen_time_percentage);
        tvCurrentLimit = findViewById(R.id.tv_current_limit);
        tvWeeklyAverage = findViewById(R.id.tv_weekly_average);
        
        // Initialize SeekBar
        seekbarScreenTimeLimit = findViewById(R.id.seekbar_screen_time_limit);
        
        // Initialize Buttons
        btnEditLimit = findViewById(R.id.btn_edit_limit);
        btnNotificationSettings = findViewById(R.id.btn_notification_settings);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Home
                        navigateToActivity("MainActivity");
                        break;
                    case 1: // Screen Time (current)
                        // Already in Screen Time, do nothing
                        break;
                    case 2: // Sleep Analytics
                        navigateToActivity("SleepAnalyticsActivity");
                        break;
                    case 3: // Bedtime Routine
                        // For now, just show a toast as this screen isn't implemented yet
                        Toast.makeText(ScreenTimeActivity.this, "Bedtime Routine coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed for now
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed for now
            }
        });
    }

    /**
     * Handles navigation to different activities with animation
     */
    private void navigateToActivity(String activityName) {
        try {
            // Start the appropriate activity immediately with a smooth transition
            Intent intent;
            switch (activityName) {
                case "MainActivity":
                    intent = new Intent(ScreenTimeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "SleepAnalyticsActivity":
                    intent = new Intent(ScreenTimeActivity.this, SleepAnalyticsActivity.class);
                    break;
                default:
                    Toast.makeText(ScreenTimeActivity.this, "Activity not yet implemented", Toast.LENGTH_SHORT).show();
                    return;
            }
            
            // Create the transition animation using faster animations
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    ScreenTimeActivity.this, R.anim.fast_slide_in_right, R.anim.fast_slide_out_left);
            
            // Start the activity with animation
            startActivity(intent, options.toBundle());
            
            // Finish this activity if we're navigating away
            if (!activityName.equals("ScreenTimeActivity")) {
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error navigating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Fallback to simple navigation without animation
            if (activityName.equals("MainActivity")) {
                finish();
            }
        }
    }

    private void setupScreenTimeControls() {
        // Setup SeekBar for screen time limit
        seekbarScreenTimeLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the current limit text
                int hours = Math.max(1, progress); // Minimum 1 hour
                tvCurrentLimit.setText(hours + " hours");
                
                // Update the limit text in summary
                tvScreenTimeLimit.setText("of " + hours + "h limit");
                
                // Recalculate percentage
                updateScreenTimePercentage();
                
                // Save settings automatically when the user changes the value
                if (fromUser) {
                    saveScreenTimeSettings(hours);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Show a toast when the user finishes adjusting
                Toast.makeText(ScreenTimeActivity.this, 
                    "Screen time limit updated to " + seekbarScreenTimeLimit.getProgress() + " hours", 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        // Setup Edit Limit button
        btnEditLimit.setOnClickListener(v -> {
            // Toggle visibility of the SeekBar
            if (seekbarScreenTimeLimit.getVisibility() == View.VISIBLE) {
                seekbarScreenTimeLimit.setVisibility(View.GONE);
                btnEditLimit.setText("Edit");
                
                // Save settings when done editing
                saveScreenTimeSettings(seekbarScreenTimeLimit.getProgress());
            } else {
                seekbarScreenTimeLimit.setVisibility(View.VISIBLE);
                btnEditLimit.setText("Done");
            }
        });
        
        // Setup Notification Settings button
        btnNotificationSettings.setOnClickListener(v -> {
            showNotificationSettingsDialog();
        });
        
        // Hide the SeekBar initially
        seekbarScreenTimeLimit.setVisibility(View.GONE);
    }
    
    /**
     * Shows the notification settings dialog
     */
    private void showNotificationSettingsDialog() {
        // Create a dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notification_settings);
        
        // Make dialog background transparent to respect the card corners
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        // Set dialog width to match parent
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        // Apply animation style
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        
        // Initialize dialog views
        SwitchCompat switchLimitReached = dialog.findViewById(R.id.switch_limit_reached);
        SwitchCompat switchLimitNear = dialog.findViewById(R.id.switch_limit_near);
        SwitchCompat switchHourlyReminders = dialog.findViewById(R.id.switch_hourly_reminders);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        
        // Set initial states based on current settings
        switchLimitReached.setChecked(notifyWhenLimitReached);
        switchLimitNear.setChecked(notifyWhenNearLimit);
        switchHourlyReminders.setChecked(showHourlyReminders);
        
        // Set click listeners
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSave.setOnClickListener(v -> {
            // Save notification settings
            notifyWhenLimitReached = switchLimitReached.isChecked();
            notifyWhenNearLimit = switchLimitNear.isChecked();
            showHourlyReminders = switchHourlyReminders.isChecked();
            
            // Show confirmation
            Toast.makeText(ScreenTimeActivity.this, "Notification settings saved", Toast.LENGTH_SHORT).show();
            
            // Close dialog
            dialog.dismiss();
        });
        
        // Show the dialog with animation
        dialog.show();
    }
    
    private void saveScreenTimeSettings(int hours) {
        // In a real app, this would save the settings to shared preferences or a database
        // For now, just show a brief confirmation
        Toast.makeText(this, "Settings applied", Toast.LENGTH_SHORT).show();
    }
    
    private void updateScreenTimePercentage() {
        try {
            // Get current screen time from the text (e.g., "5h 23m")
            String screenTimeText = tvScreenTimeHours.getText().toString();
            String[] parts = screenTimeText.split(" ");
            float hours = Float.parseFloat(parts[0].replace("h", ""));
            float minutes = parts.length > 1 ? Float.parseFloat(parts[1].replace("m", "")) : 0;
            float totalHours = hours + (minutes / 60f);
            
            // Get limit from the limit text (e.g., "of 6h limit")
            String limitText = tvScreenTimeLimit.getText().toString();
            float limit = Float.parseFloat(limitText.replace("of ", "").replace("h limit", ""));
            
            // Calculate percentage
            int percentage = Math.min(100, (int)((totalHours / limit) * 100));
            tvScreenTimePercentage.setText(percentage + "%");
            
            // Update progress bar color based on percentage
            // This would require a custom drawable or programmatic color change
        } catch (Exception e) {
            // Handle parsing errors
            Toast.makeText(this, "Error updating percentage", Toast.LENGTH_SHORT).show();
        }
    }
} 