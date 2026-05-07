package com.example.sleepsyncv2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private CardView signupCard;
    private TextInputLayout usernameLayout, emailLayout, passwordLayout, confirmPasswordLayout;
    private TextInputEditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button signupButton;
    private TextView loginPrompt;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
            "(" +
            "." +
            "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
            ")+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup_screen);
        
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
    
    private void initializeViews() {
        signupCard = findViewById(R.id.signup_card);
        usernameLayout = findViewById(R.id.username_layout);
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        signupButton = findViewById(R.id.signup_button);
        loginPrompt = findViewById(R.id.login_prompt);
    }
    
    private void setupClickListeners() {
        signupButton.setOnClickListener(v -> {
            if (validateInputs()) {
                // In a real app, perform registration here
                simulateSignup();
            }
        });
        
        loginPrompt.setOnClickListener(v -> {
            // Navigate back to login screen
            onBackPressed();
        });
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fast_slide_in_left, R.anim.fast_slide_out_right);
    }
    
    private void startAnimations() {
        // Animate card sliding up
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        signupCard.startAnimation(slideUp);
        
        // Animate login prompt fading in
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        loginPrompt.startAnimation(fadeIn);
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
        
        // Validate email
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            emailLayout.setError("Email cannot be empty");
            isValid = false;
        } else if (!isValidEmail(email)) {
            emailLayout.setError("Please enter a valid email");
            isValid = false;
        } else {
            emailLayout.setError(null);
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
        
        // Validate confirm password
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Please confirm your password");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }
        
        return isValid;
    }
    
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    private void simulateSignup() {
        // Show loading indicator
        signupButton.setEnabled(false);
        signupButton.setText("Creating Account...");
        
        // Simulate network delay (reduced for faster response)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            
            // Navigate to main activity with faster animation
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fast_fade_in, R.anim.fast_fade_out);
                finishAffinity(); // Close all activities in the stack
            }, 500);
        }, 800);
    }
} 