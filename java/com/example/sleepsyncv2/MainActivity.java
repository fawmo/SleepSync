package com.example.sleepsyncv2;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.app.ActivityOptions;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private CardView screenTimeCard, sleepAnalyticsCard, bedtimeRoutineCard;
    private Button screenTimeSettingsBtn, viewSleepDataBtn, bedtimeSettingsBtn;
    private TextView screenTimeToday, sleepQualityText, nextBedtimeText;
    private TextView welcomeMessage, sleepGoalText, sleepProgressText;
    private TextView greetingText, currentTimeText;
    private FloatingActionButton fabQuickActions;
    
    // Constants for Philippine Time API
    private static final String WORLD_TIME_API_URL = "http://worldtimeapi.org/api/timezone/Asia/Manila";
    private static final String FALLBACK_TIMEZONE = "Asia/Manila";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homescreen);
        
        // Set up edge-to-edge display
        try {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up edge-to-edge display: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Initialize UI components
        try {
            initializeViews();
            setupTabLayout();
            setupCardClickListeners();
            setupButtonClickListeners();
            setupWelcomeMessage();
            setupFloatingActionButton();
            
            // Select home tab by default
            tabLayout.selectTab(tabLayout.getTabAt(0));
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        try {
            // Initialize TabLayout
            tabLayout = findViewById(R.id.tabs);
            
            // Initialize CardViews
            screenTimeCard = findViewById(R.id.screen_time_card);
            sleepAnalyticsCard = findViewById(R.id.sleep_analytics_card);
            bedtimeRoutineCard = findViewById(R.id.bedtime_routine_card);
            
            // Initialize Buttons
            screenTimeSettingsBtn = findViewById(R.id.screen_time_settings_btn);
            viewSleepDataBtn = findViewById(R.id.view_sleep_data_btn);
            bedtimeSettingsBtn = findViewById(R.id.bedtime_settings_btn);
            
            // Initialize TextViews
            screenTimeToday = findViewById(R.id.screen_time_today);
            sleepQualityText = findViewById(R.id.sleep_quality_text);
            nextBedtimeText = findViewById(R.id.next_bedtime_text);

            // Initialize Welcome Card Views
            welcomeMessage = findViewById(R.id.welcome_message);
            sleepGoalText = findViewById(R.id.sleep_goal_text);
            sleepProgressText = findViewById(R.id.sleep_progress_text);
            
            // Initialize Dashboard Header
            greetingText = findViewById(R.id.greeting_text);
            currentTimeText = findViewById(R.id.current_time_text);
            
            // Initialize Floating Action Button
            fabQuickActions = findViewById(R.id.fab_quick_actions);
            
            // Set initial data (this would come from a real data source in production)
            updateDashboardData();
            
            // Animate the sleep chart bars after a short delay to ensure views are laid out
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::animateSleepChartBars, 500);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Animates the sleep chart bars with a growing effect
     */
    private void animateSleepChartBars() {
        try {
            // Find the chart container view
            View chartContainer = findViewById(R.id.dashboard_content);
            if (chartContainer == null) return;
            
            // Find all views with sleep_quality_bar drawable backgrounds
            List<View> sleepBars = findSleepBars(chartContainer);
            
            // Apply animations with staggered delay
            AnimatorSet animatorSet = new AnimatorSet();
            List<Animator> animators = new ArrayList<>();
            
            for (int i = 0; i < sleepBars.size(); i++) {
                View bar = sleepBars.get(i);
                
                // Reset the pivot point to bottom center for proper growth animation
                bar.setPivotY(bar.getHeight());
                bar.setPivotX(bar.getWidth() / 2f);
                
                // Start with height of 0
                bar.setScaleY(0f);
                
                // Create the animation
                AnimatorSet barAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.anim.bar_reveal);
                barAnim.setTarget(bar);
                barAnim.setStartDelay(i * 100); // Stagger by 100ms per bar
                
                animators.add(barAnim);
            }
            
            animatorSet.playTogether(animators);
            animatorSet.start();
        } catch (Exception e) {
            Toast.makeText(this, "Error animating sleep chart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Recursively finds all View objects with sleep_quality_bar drawable backgrounds
     */
    private List<View> findSleepBars(View root) {
        List<View> bars = new ArrayList<>();
        
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                
                // Check if this view has a sleep bar background
                if (child.getBackground() != null && 
                    (child.getBackground().toString().contains("sleep_bar_0_3_hours") ||
                     child.getBackground().toString().contains("sleep_bar_3_6_hours") ||
                     child.getBackground().toString().contains("sleep_bar_6_8_hours") ||
                     child.getBackground().toString().contains("sleep_quality_bar_good") ||
                     child.getBackground().toString().contains("sleep_quality_bar_average") ||
                     child.getBackground().toString().contains("sleep_quality_bar_poor"))) {
                    bars.add(child);
                }
                
                // Recursively check children
                bars.addAll(findSleepBars(child));
            }
        }
        
        return bars;
    }
    
    private void updateDashboardData() {
        try {
            // In a real app, these values would come from your data layer
            screenTimeToday.setText("Today: 3h 25m");
            sleepQualityText.setText("Last night's sleep quality: Good");
            nextBedtimeText.setText("Next bedtime: 10:30 PM");
        } catch (Exception e) {
            Toast.makeText(this, "Error updating dashboard data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupWelcomeMessage() {
        try {
            // Fetch Philippine time in a background thread
            new Thread(() -> {
                try {
                    // Get current time in Philippines
                    final Date philippineTime = getPhilippineTime();
                    
                    // Update UI on main thread
                    runOnUiThread(() -> updateWelcomeMessageWithTime(philippineTime));
                } catch (Exception e) {
                    // In case of error, fallback to device time with Manila timezone
                    runOnUiThread(() -> {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(FALLBACK_TIMEZONE));
                        updateWelcomeMessageWithTime(calendar.getTime());
                        Toast.makeText(MainActivity.this, 
                                "Using device time: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up welcome message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            // Fallback to device time
            Calendar calendar = Calendar.getInstance();
            updateWelcomeMessageWithTime(calendar.getTime());
        }
    }
    
    /**
     * Updates the welcome message with the provided time
     */
    private void updateWelcomeMessageWithTime(Date time) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            
            String greeting;
            if (hourOfDay >= 5 && hourOfDay < 12) {
                greeting = "Good morning";
            } else if (hourOfDay >= 12 && hourOfDay < 18) {
                greeting = "Good afternoon";
            } else {
                greeting = "Good evening";
            }
            
            // Format time to display
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
            String formattedTime = timeFormat.format(time);
            
            if (welcomeMessage != null) {
                welcomeMessage.setText(greeting + ", Alex!");
            }
            

            
            // Set current time in dashboard header
            if (currentTimeText != null) {
                currentTimeText.setText("• " + formattedTime);
            }
            
            // Set sleep goal text
            if (sleepGoalText != null) {
                sleepGoalText.setText("Your sleep goal tonight is 8 hours. Bedtime at 10:30 PM.");
            }
            
            // Set sleep progress
            if (sleepProgressText != null) {
                sleepProgressText.setText("Sleep goal progress: 75%");
            }
            
            // Update the sleep chart to highlight current day
            highlightCurrentDayInSleepChart(calendar);
        } catch (Exception e) {
            Toast.makeText(this, "Error updating welcome message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Highlights the current day in the sleep chart
     */
    private void highlightCurrentDayInSleepChart(Calendar calendar) {
        try {
            // Get day of week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            
            // Convert to our chart's format (0 = Monday, 1 = Tuesday, ..., 6 = Sunday)
            int chartDayIndex;
            if (dayOfWeek == Calendar.SUNDAY) {
                chartDayIndex = 6; // Sunday is last in our chart
            } else {
                chartDayIndex = dayOfWeek - 2; // Monday (2) becomes index 0
            }
            
            // In a real app with proper IDs for each day container, we would do this:
            // First, let's log which day we're highlighting
            String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            Toast.makeText(this, "Current day: " + dayNames[chartDayIndex], Toast.LENGTH_SHORT).show();
            
            // For now, we've hardcoded the highlight to Friday in the layout
            // In a real implementation, we would find all day containers and update them dynamically
        } catch (Exception e) {
            Toast.makeText(this, "Error highlighting current day: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Fetches the current time in the Philippines from World Time API
     * @return Date object with the current Philippines time
     * @throws Exception if there's an error fetching or parsing the time
     */
    private Date getPhilippineTime() throws Exception {
        URL url = new URL(WORLD_TIME_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            JSONObject jsonResponse = new JSONObject(response.toString());
            String datetime = jsonResponse.getString("datetime");
            
            // Parse the ISO 8601 datetime format (2023-05-25T12:34:56.123456+08:00)
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.US);
            return isoFormat.parse(datetime);
        } else {
            throw new Exception("Failed to fetch time: HTTP " + responseCode);
        }
    }
    
    private void setupFloatingActionButton() {
        fabQuickActions.setOnClickListener(v -> {
            playButtonAnimation(v);
            showQuickActions();
        });
    }
    
    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Home
                        showHomeView();
                        break;
                    case 1: // Screen Time
                        showScreenTimeView();
                        break;
                    case 2: // Sleep Analytics
                        showSleepAnalyticsView();
                        break;
                    case 3: // Bedtime Routine
                        showBedtimeRoutineView();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection if needed
            }
        });
    }

    private void setupCardClickListeners() {
        // Set click listeners for feature cards with visual feedback
        screenTimeCard.setOnClickListener(v -> {
            addVisualFeedback(v);
            // Select the Screen Time tab
            tabLayout.selectTab(tabLayout.getTabAt(1));
        });

        sleepAnalyticsCard.setOnClickListener(v -> {
            addVisualFeedback(v);
            // Select the Sleep Analytics tab
            tabLayout.selectTab(tabLayout.getTabAt(2));
        });

        bedtimeRoutineCard.setOnClickListener(v -> {
            addVisualFeedback(v);
            // Select the Bedtime Routine tab
            tabLayout.selectTab(tabLayout.getTabAt(3));
        });
    }

    private void setupButtonClickListeners() {
        try {
            screenTimeSettingsBtn.setOnClickListener(v -> {
                addVisualFeedback(v);
                simulateScreenTransition("ScreenTimeActivity");
            });
            
            viewSleepDataBtn.setOnClickListener(v -> {
                addVisualFeedback(v);
                simulateScreenTransition("SleepAnalyticsActivity");
            });
            
            bedtimeSettingsBtn.setOnClickListener(v -> {
                addVisualFeedback(v);
                simulateScreenTransition("BedtimeRoutineActivity");
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up button listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
    
    /**
     * Plays button press and release animation
     */
    private void playButtonAnimation(View view) {
        Animation pressAnim = AnimationUtils.loadAnimation(this, R.anim.button_press);
        view.startAnimation(pressAnim);
        
        pressAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                Animation releaseAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_release);
                view.startAnimation(releaseAnim);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    
    /**
     * Simulates a screen transition with animation
     * This would be used when we add more screens to the app
     */
    private void simulateScreenTransition(String activityName) {
        try {
            // Start the appropriate activity immediately with a smooth transition
            Intent intent;
            switch (activityName) {
                case "ScreenTimeActivity":
                    intent = new Intent(MainActivity.this, ScreenTimeActivity.class);
                    break;
                case "SleepAnalyticsActivity":
                    intent = new Intent(MainActivity.this, SleepAnalyticsActivity.class);
                    break;
                case "BedtimeRoutineActivity":
                    intent = new Intent(MainActivity.this, BedtimeRoutineActivity.class);
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Activity not yet implemented", Toast.LENGTH_SHORT).show();
                    return;
            }
            
            // Create the transition animation using faster animations
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    MainActivity.this, R.anim.fast_fade_in, R.anim.fast_fade_out);
            
            // Start the activity with animation
            startActivity(intent, options.toBundle());
        } catch (Exception e) {
            Toast.makeText(this, "Error transitioning screens: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    // These methods would show the appropriate view for each tab
    // In a real app, these might be separate fragments or activities
    
    private void showHomeView() {
        // Show the dashboard content
        View homeContent = findViewById(R.id.dashboard_content);
        homeContent.setVisibility(View.VISIBLE);
        
        // Animate the dashboard content
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        homeContent.startAnimation(fadeIn);
    }
    
    private void showScreenTimeView() {
        // Launch ScreenTimeActivity with faster animation
        Intent screenTimeIntent = new Intent(MainActivity.this, ScreenTimeActivity.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(
                MainActivity.this, R.anim.fast_slide_in_right, R.anim.fast_fade_out);
        startActivity(screenTimeIntent, options.toBundle());
    }
    
    private void showSleepAnalyticsView() {
        try {
            // Highlight the sleep analytics card
            highlightFeatureCard(sleepAnalyticsCard);
            
            // In a real app with proper navigation, we would navigate to the Sleep Analytics screen
            // For now, we'll just simulate a transition
            simulateScreenTransition("SleepAnalyticsActivity");
        } catch (Exception e) {
            Toast.makeText(this, "Error showing sleep analytics view: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showBedtimeRoutineView() {
        try {
            // Launch BedtimeRoutineActivity with faster animation
            Intent bedtimeRoutineIntent = new Intent(MainActivity.this, BedtimeRoutineActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    MainActivity.this, R.anim.fast_slide_in_right, R.anim.fast_fade_out);
            startActivity(bedtimeRoutineIntent, options.toBundle());
        } catch (Exception e) {
            Toast.makeText(this, "Error showing bedtime routine view: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void highlightFeatureCard(CardView cardToHighlight) {
        // Reset all cards
        screenTimeCard.setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        sleepAnalyticsCard.setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        bedtimeRoutineCard.setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        
        // Highlight the selected card
        cardToHighlight.setCardElevation(getResources().getDimension(R.dimen.card_elevation_highlighted));
        
        // Scroll to the highlighted card
        final NestedScrollView scrollView = findViewById(R.id.nested_scroll_view);
        scrollView.post(() -> scrollView.smoothScrollTo(0, cardToHighlight.getTop()));
        
        // Add a pulse animation to the highlighted card
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        cardToHighlight.startAnimation(pulse);
    }

    private void showQuickActions() {
        try {
            // This would show a menu or dialog with quick actions
            Snackbar.make(findViewById(R.id.main), "Quick actions menu", Snackbar.LENGTH_SHORT)
                    .setAction("Sleep Now", v -> Toast.makeText(this, "Sleep mode activated", Toast.LENGTH_SHORT).show())
                    .setBackgroundTint(getResources().getColor(R.color.primary_dark, getTheme()))
                    .setActionTextColor(getResources().getColor(R.color.white, getTheme()))
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Error showing quick actions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}