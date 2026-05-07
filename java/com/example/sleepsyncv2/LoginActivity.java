package com.example.sleepsyncv2;

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
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private CardView loginCard;
    private TextInputLayout usernameLayout, passwordLayout;
    private TextInputEditText usernameInput, passwordInput;
    private Button loginButton;
    private TextView forgotPassword, signupPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_screen);
        
        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
        
        // Start animations
        startAnimations();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Re-apply UI customizations to ensure they're visible
        applyUICustomizations();
    }
    
    private void applyUICustomizations() {
        // Re-apply all UI customizations to ensure they're visible
        findViewById(android.R.id.content).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
        loginCard.setCardBackgroundColor(getResources().getColor(R.color.white, getTheme()));
        
        // Force text color to black (0xFF000000)
        usernameInput.setTextColor(0xFF000000);
        passwordInput.setTextColor(0xFF000000);
        
        // Force hint text color to black
        usernameLayout.setHintTextColor(android.content.res.ColorStateList.valueOf(0xFF000000));
        passwordLayout.setHintTextColor(android.content.res.ColorStateList.valueOf(0xFF000000));
        
        // Also set box stroke colors
        usernameLayout.setBoxStrokeColor(0xFF000000);
        passwordLayout.setBoxStrokeColor(0xFF000000);
        
        loginButton.setBackgroundResource(R.drawable.red_button_background);
        loginButton.setTextColor(0xFF000000); // Force black color
        
        forgotPassword.setTextColor(0xFF000000); // Force black color
        
        signupPrompt.setTextColor(getResources().getColor(R.color.white, getTheme()));
        signupPrompt.setTypeface(signupPrompt.getTypeface(), android.graphics.Typeface.BOLD);
    }
    
    private void initializeViews() {
        loginCard = findViewById(R.id.login_card);
        usernameLayout = findViewById(R.id.username_layout);
        passwordLayout = findViewById(R.id.password_layout);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgot_password);
        signupPrompt = findViewById(R.id.signup_prompt);
        
        // Apply all UI customizations
        applyUICustomizations();
    }
    
    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                // In a real app, perform authentication here
                simulateLogin();
            }
        });
        
        forgotPassword.setOnClickListener(v -> {
            // Handle forgot password
            Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
        });
        
        signupPrompt.setOnClickListener(v -> {
            // Navigate to signup screen with faster animation
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fast_slide_in_right, R.anim.fast_slide_out_left);
        });
    }
    
    private void startAnimations() {
        // Animate card sliding up
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        loginCard.startAnimation(slideUp);
        
        // Animate signup text fading in
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        signupPrompt.startAnimation(fadeIn);
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate username
        String username = usernameInput.getText().toString().trim();
        if (username.isEmpty()) {
            usernameLayout.setError("Username cannot be empty");
            isValid = false;
        } else {
            usernameLayout.setError(null);
        }
        
        // Validate password
        String password = passwordInput.getText().toString().trim();
        if (password.isEmpty()) {
            passwordLayout.setError("Password cannot be empty");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }
        
        return isValid;
    }
    
    private void simulateLogin() {
        // Show loading indicator
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
        // Simulate network delay (reduced for faster response)
        new android.os.Handler().postDelayed(() -> {
            // Navigate to main activity with faster animation
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fast_fade_in, R.anim.fast_fade_out);
            finish();
        }, 800);
    }
} 