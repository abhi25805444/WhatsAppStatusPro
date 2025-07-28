package com.forever.whatsappstatussaver;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    private TextView txtanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Enable edge-to-edge
        EdgeToEdge.enable(this);
        
        // Set status bar color to appColor

        // Handle window insets for edge-to-edge experience
        View contentView = findViewById(android.R.id.content);
        if (contentView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(contentView, (v, insets) -> {
                if (v == null || insets == null) {
                    return insets != null ? insets : WindowInsetsCompat.CONSUMED;
                }
                
                androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                androidx.core.graphics.Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

                if (systemBars != null && ime != null) {
                    // Apply insets as padding to maintain component visibility and interactability
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right,
                        Math.max(systemBars.bottom, ime.bottom));
                }

                return WindowInsetsCompat.CONSUMED;
            });
        }

        txtanim = findViewById(R.id.txtanim);

        String text = "\"Save It, Love It, Share It.\"";
        if (text != null) {
            SpannableString spannable = new SpannableString(text);
            int startQuote1 = text.indexOf("\"");
            int endQuote1 = text.indexOf("\"", startQuote1 + 1);

            if (spannable != null && startQuote1 >= 0 && endQuote1 >= 0) {
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#18a6b8")), startQuote1, startQuote1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#18a6b8")), endQuote1, endQuote1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (txtanim != null) {
                    txtanim.setText(spannable);
                }
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        startAnim();
    }

    public void startAnim() {
        if (txtanim != null && getResources() != null) {
            float screenWidth = getResources().getDisplayMetrics().widthPixels;

            ObjectAnimator animator = ObjectAnimator.ofFloat(txtanim, "translationX", -screenWidth, txtanim.getX());
            if (animator != null) {
                animator.setDuration(1000);
                animator.start();
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (!isFinishing()) {
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                if (intent != null) {
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1000);
                        }
                        super.onAnimationCancel(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isFinishing()) {
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                if (intent != null) {
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1000);
                        }
                        super.onAnimationEnd(animation);
                    }
                });
            }
        }
    }
}