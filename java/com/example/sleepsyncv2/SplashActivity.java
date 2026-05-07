package com.example.sleepsyncv2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500; // 1.5 seconds
    
    private ImageView logoImage;
    private TextView appNameText, quoteText;
    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loading_screen);
        
        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initializeViews();
        
        // Start animations
        startAnimations();
        
        // Navigate to login screen after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToLoginScreen, SPLASH_DURATION);
    }
    
    private void initializeViews() {
        logoImage = findViewById(R.id.logo_image);
        appNameText = findViewById(R.id.app_name_text);
        quoteText = findViewById(R.id.quote_text);
        loadingProgress = findViewById(R.id.loading_progress);
    }
    
    private void startAnimations() {
        // Load animations (use faster animations)
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fast_fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        
        // Set initial visibility
        logoImage.setVisibility(View.VISIBLE);
        appNameText.setVisibility(View.VISIBLE);
        quoteText.setVisibility(View.VISIBLE);
        
        // Apply animations with reduced delay between elements
        logoImage.startAnimation(fadeIn);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            appNameText.startAnimation(slideUp);
        }, 150);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            quoteText.startAnimation(slideUp);
        }, 250);
    }
    
    private void navigateToLoginScreen() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fast_fade_in, R.anim.fast_fade_out);
        finish();
    }
} 