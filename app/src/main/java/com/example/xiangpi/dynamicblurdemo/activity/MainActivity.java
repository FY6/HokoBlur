package com.example.xiangpi.dynamicblurdemo.activity;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.example.xiangpi.dynamicblurdemo.R;
import com.hoko.blurlibrary.view.RsBlurLinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mMultiBlurBtn;
    private Button mOpenGLBtn;
    private Button mTexBtn;
    private Button mDynamicBtn;
    private Button mLayoutBtn;
    private Button mDrawableBtn;

    private RsBlurLinearLayout mBlurLayout;

    private boolean mHasBlurred;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mMultiBlurBtn = (Button) findViewById(R.id.multi_blur);
        mOpenGLBtn = (Button) findViewById(R.id.opengl_blur);
        mTexBtn = (Button) findViewById(R.id.tex_blur);
        mDynamicBtn = (Button) findViewById(R.id.dynamic_blur);
        mLayoutBtn = (Button) findViewById(R.id.layout_blur);
        mBlurLayout = (RsBlurLinearLayout) findViewById(R.id.blur_layout);
        mDrawableBtn = (Button) findViewById(R.id.drawable_blur);

        mMultiBlurBtn.setOnClickListener(this);
        mOpenGLBtn.setOnClickListener(this);
        mTexBtn.setOnClickListener(this);
        mDynamicBtn.setOnClickListener(this);
        mLayoutBtn.setOnClickListener(this);
        mDrawableBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        final Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.multi_blur:
                intent.setClass(MainActivity.this, MultiBlurActivity.class);
                break;
            case R.id.opengl_blur:
                intent.setClass(MainActivity.this, GLSurfaceActivity.class);
                break;
            case R.id.tex_blur:
                intent.setClass(MainActivity.this, TextureViewActivity.class);
                break;
            case R.id.dynamic_blur:
                intent.setClass(MainActivity.this, DynamicBlurActivity.class);
                break;
            case R.id.layout_blur:
                changeBg();
                break;
            case R.id.drawable_blur:
                intent.setClass(MainActivity.this, BlurDrawableActivity.class);
                break;

        }

        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivity(intent);
        }

    }

    private void changeBg() {
        mBlurLayout.setBackgroundResource(R.mipmap.sample7);
        blurBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mHasBlurred) {
            return;
        }

        mHasBlurred = true;
        blurBackground();

    }

    private void blurBackground() {
        mBlurLayout.setBlurRadius(0);
        ValueAnimator animator = ValueAnimator.ofInt(0, 8);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int r = (int) animation.getAnimatedValue();
                mBlurLayout.setBlurRadius(r);
            }
        });
        animator.setDuration(1000);
        animator.start();
    }
}
