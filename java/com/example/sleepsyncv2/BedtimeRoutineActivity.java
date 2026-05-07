package com.example.sleepsyncv2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

public class BedtimeRoutineActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView tvBedtime;
    private TextView tvWakeTime;
    private TextView tvRecommendation1;
    private TextView tvRecommendation2;
    private Button btnEditSchedule;
    private Button btnAddActivity;
    private Button btnBedtimeSettings;
    private ImageButton btnEditActivity1;
    private ImageButton btnEditActivity2;
    private ImageButton btnEditActivity3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.bedtimeroutinescreen);

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupTabLayout();
        loadBedtimeData();
        setupButtonClickListeners();
        
        // Select bedtime routine tab by default
        tabLayout.selectTab(tabLayout.getTabAt(3));
    }

    private void initializeViews() {
        // Initialize TabLayout
        tabLayout = findViewById(R.id.tabs);
        
        // Initialize TextViews
        tvBedtime = findViewById(R.id.tv_bedtime);
        tvWakeTime = findViewById(R.id.tv_wake_time);
        tvRecommendation1 = findViewById(R.id.tv_recommendation_1);
        tvRecommendation2 = findViewById(R.id.tv_recommendation_2);
        
        // Initialize Buttons
        btnEditSchedule = findViewById(R.id.btn_edit_schedule);
        btnAddActivity = findViewById(R.id.btn_add_activity);
        btnBedtimeSettings = findViewById(R.id.btn_bedtime_settings);
        
        // Initialize ImageButtons
        btnEditActivity1 = findViewById(R.id.btn_edit_activity_1);
        btnEditActivity2 = findViewById(R.id.btn_edit_activity_2);
        btnEditActivity3 = findViewById(R.id.btn_edit_activity_3);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Home
                        navigateToActivity("MainActivity");
                        break;
                    case 1: // Screen Time
                        navigateToActivity("ScreenTimeActivity");
                        break;
                    case 2: // Sleep Analytics
                        navigateToActivity("SleepAnalyticsActivity");
                        break;
                    case 3: // Bedtime Routine (current)
                        // Already in Bedtime Routine, do nothing
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
                    intent = new Intent(BedtimeRoutineActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "ScreenTimeActivity":
                    intent = new Intent(BedtimeRoutineActivity.this, ScreenTimeActivity.class);
                    break;
                case "SleepAnalyticsActivity":
                    intent = new Intent(BedtimeRoutineActivity.this, SleepAnalyticsActivity.class);
                    break;
                default:
                    Toast.makeText(BedtimeRoutineActivity.this, "Activity not yet implemented", Toast.LENGTH_SHORT).show();
                    return;
            }
            
            // Create the transition animation using faster animations
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    BedtimeRoutineActivity.this, R.anim.fast_slide_in_right, R.anim.fast_slide_out_left);
            
            // Start the activity with animation
            startActivity(intent, options.toBundle());
            
            // Finish this activity if we're navigating away
            if (!activityName.equals("BedtimeRoutineActivity")) {
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

    private void loadBedtimeData() {
        // In a real app, this would load data from a database or API
        // For now, we'll use sample data
        
        // Bedtime schedule data
        tvBedtime.setText("10:30 PM");
        tvWakeTime.setText("Wake up: 6:30 AM");
        
        // Personalized recommendations based on sleep data
        updateRecommendations();
    }
    
    private void updateRecommendations() {
        // In a real app, these recommendations would be dynamically generated based on sleep data
        // For now, we'll use sample recommendations
        tvRecommendation1.setText("Based on your sleep patterns, try going to bed 15 minutes earlier.");
        tvRecommendation2.setText("Consider adding a warm bath to your routine for better sleep quality.");
    }
    
    private void setupButtonClickListeners() {
        // Edit schedule button
        btnEditSchedule.setOnClickListener(v -> {
            addVisualFeedback(v);
            showEditScheduleDialog();
        });
        
        // Add activity button
        btnAddActivity.setOnClickListener(v -> {
            addVisualFeedback(v);
            showAddActivityDialog();
        });
        
        // Bedtime settings button
        btnBedtimeSettings.setOnClickListener(v -> {
            addVisualFeedback(v);
            Toast.makeText(BedtimeRoutineActivity.this, "Bedtime Settings will be implemented in a future update", Toast.LENGTH_SHORT).show();
        });
        
        // Edit activity buttons
        btnEditActivity1.setOnClickListener(v -> {
            addVisualFeedback(v);
            showEditActivityDialog("No Screens", "1 hour before bedtime");
        });
        
        btnEditActivity2.setOnClickListener(v -> {
            addVisualFeedback(v);
            showEditActivityDialog("Reading", "30 minutes before bedtime");
        });
        
        btnEditActivity3.setOnClickListener(v -> {
            addVisualFeedback(v);
            showEditActivityDialog("Meditation", "15 minutes before bedtime");
        });
    }
    
    private void showEditScheduleDialog() {
        // In a real app, this would show a dialog to edit the bedtime schedule
        Toast.makeText(this, "Edit Schedule dialog will be implemented in a future update", Toast.LENGTH_SHORT).show();
    }
    
    private void showAddActivityDialog() {
        // In a real app, this would show a dialog to add a new activity
        Toast.makeText(this, "Add Activity dialog will be implemented in a future update", Toast.LENGTH_SHORT).show();
    }
    
    private void showEditActivityDialog(String activityName, String timing) {
        // In a real app, this would show a dialog to edit an existing activity
        Toast.makeText(this, "Edit " + activityName + " activity dialog will be implemented in a future update", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Adds visual feedback to a view when clicked
     */
    private void addVisualFeedback(View view) {
        // Scale down slightly
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
            .withEndAction(() -> {
                // Scale back up
                view.animate().scaleX(1f).scaleY(1f).setDuration(100);
            });
    }
} 