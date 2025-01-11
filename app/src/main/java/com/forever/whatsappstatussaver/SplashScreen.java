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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private TextView txtanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        txtanim = findViewById(R.id.txtanim);

        String text = "\"Save It, Love It, Share It.\"";
        SpannableString spannable = new SpannableString(text);
        int startQuote1 = text.indexOf("\"");
        int endQuote1 = text.indexOf("\"", startQuote1 + 1);

        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#18a6b8")), startQuote1, startQuote1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#18a6b8")), endQuote1, endQuote1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if(txtanim!=null){
            txtanim.setText(spannable);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        startAnim();
    }

    public void startAnim(){
        float screenWidth = getResources().getDisplayMetrics().widthPixels;

        ObjectAnimator animator = ObjectAnimator.ofFloat(txtanim, "translationX", -screenWidth, txtanim.getX());
        animator.setDuration(1000); // Duration in milliseconds
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the splash activity
                }, 1000);
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the splash activity
                }, 1000);
                super.onAnimationEnd(animation);
            }
        });
    }
}