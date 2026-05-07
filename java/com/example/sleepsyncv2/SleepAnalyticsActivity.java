package com.example.sleepsyncv2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

public class SleepAnalyticsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView tvSleepDuration;
    private TextView tvSleepGoal;
    private TextView tvSleepQualityScore;
    private TextView tvDeepSleepDuration;
    private TextView tvLightSleepDuration;
    private TextView tvRemSleepDuration;
    private TextView tvWeeklySleepAverage;
    private TextView tvRecommendation1;
    private TextView tvRecommendation2;
    private TextView tvRecommendation3;
    private Button btnSleepSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sleepanalyticsscreen);

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupTabLayout();
        loadSleepData();
        setupSleepSettingsButton();
        
        // Select sleep analytics tab by default
        tabLayout.selectTab(tabLayout.getTabAt(2));
    }

    private void initializeViews() {
        // Initialize TabLayout
        tabLayout = findViewById(R.id.tabs);
        
        // Initialize TextViews
        tvSleepDuration = findViewById(R.id.tv_sleep_duration);
        tvSleepGoal = findViewById(R.id.tv_sleep_goal);
        tvSleepQualityScore = findViewById(R.id.tv_sleep_quality_score);
        tvDeepSleepDuration = findViewById(R.id.tv_deep_sleep_duration);
        tvLightSleepDuration = findViewById(R.id.tv_light_sleep_duration);
        tvRemSleepDuration = findViewById(R.id.tv_rem_sleep_duration);
        tvWeeklySleepAverage = findViewById(R.id.tv_weekly_sleep_average);
        tvRecommendation1 = findViewById(R.id.tv_recommendation_1);
        tvRecommendation2 = findViewById(R.id.tv_recommendation_2);
        tvRecommendation3 = findViewById(R.id.tv_recommendation_3);
        
        // Initialize Button
        btnSleepSettings = findViewById(R.id.btn_sleep_settings);
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
                    case 2: // Sleep Analytics (current)
                        // Already in Sleep Analytics, do nothing
                        break;
                    case 3: // Bedtime Routine
                        // Navigate to BedtimeRoutineActivity
                        navigateToActivity("BedtimeRoutineActivity");
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
                    intent = new Intent(SleepAnalyticsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "ScreenTimeActivity":
                    intent = new Intent(SleepAnalyticsActivity.this, ScreenTimeActivity.class);
                    break;
                case "BedtimeRoutineActivity":
                    intent = new Intent(SleepAnalyticsActivity.this, BedtimeRoutineActivity.class);
                    break;
                default:
                    Toast.makeText(SleepAnalyticsActivity.this, "Activity not yet implemented", Toast.LENGTH_SHORT).show();
                    return;
            }
            
            // Create the transition animation using faster animations
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    SleepAnalyticsActivity.this, R.anim.fast_slide_in_right, R.anim.fast_slide_out_left);
            
            // Start the activity with animation
            startActivity(intent, options.toBundle());
            
            // Finish this activity if we're navigating away
            if (!activityName.equals("SleepAnalyticsActivity")) {
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

    private void loadSleepData() {
        // In a real app, this would load data from a database or API
        // For now, we'll use sample data
        
        // Last night's sleep data
        tvSleepDuration.setText("7h 45m");
        tvSleepGoal.setText("of 8h goal");
        tvSleepQualityScore.setText("92%");
        
        // Sleep phases
        tvDeepSleepDuration.setText("1h 45m");
        tvLightSleepDuration.setText("4h 20m");
        tvRemSleepDuration.setText("1h 40m");
        
        // Weekly average
        tvWeeklySleepAverage.setText("Avg: 7.2h");
        
        // Personalized recommendations based on sleep data
        updateRecommendations();
    }
    
    private void updateRecommendations() {
        // In a real app, these recommendations would be dynamically generated based on sleep data
        // For now, we'll use sample recommendations
        
        // Parse the sleep duration
        String sleepDurationText = tvSleepDuration.getText().toString();
        String[] parts = sleepDurationText.split(" ");
        float hours = Float.parseFloat(parts[0].replace("h", ""));
        float minutes = parts.length > 1 ? Float.parseFloat(parts[1].replace("m", "")) : 0;
        float totalHours = hours + (minutes / 60f);
        
        // Get deep sleep percentage
        String deepSleepText = tvDeepSleepDuration.getText().toString();
        String[] deepParts = deepSleepText.split(" ");
        float deepHours = Float.parseFloat(deepParts[0].replace("h", ""));
        float deepMinutes = deepParts.length > 1 ? Float.parseFloat(deepParts[1].replace("m", "")) : 0;
        float deepTotalHours = deepHours + (deepMinutes / 60f);
        
        float deepSleepPercentage = (deepTotalHours / totalHours) * 100;
        
        // Generate recommendations based on sleep metrics
        if (deepSleepPercentage < 25) {
            tvRecommendation1.setText("Try going to bed 30 minutes earlier to improve your deep sleep percentage.");
        } else {
            tvRecommendation1.setText("Your deep sleep percentage is good. Keep maintaining your current bedtime routine.");
        }
        
        if (totalHours < 7) {
            tvRecommendation2.setText("You're getting less than the recommended 7-9 hours of sleep. Try to sleep longer for better health.");
        } else {
            tvRecommendation2.setText("Reduce screen time by 1 hour before bedtime to help you fall asleep faster.");
        }
        
        // Check weekly pattern (this would be more sophisticated in a real app)
        tvRecommendation3.setText("Your weekend sleep pattern is excellent. Try maintaining a similar schedule during weekdays.");
    }
    
    private void setupSleepSettingsButton() {
        btnSleepSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add visual feedback for button press
                addVisualFeedback(v);
                
                // In a real app, this would open sleep settings
                Toast.makeText(SleepAnalyticsActivity.this, "Sleep Settings will be implemented in a future update", Toast.LENGTH_SHORT).show();
            }
        });
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