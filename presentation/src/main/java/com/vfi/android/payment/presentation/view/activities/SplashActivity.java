package com.vfi.android.payment.presentation.view.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.SplashPresenter;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseMvpActivity;
import com.vfi.android.payment.presentation.view.contracts.SplashUI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseMvpActivity<SplashUI> implements SplashUI {
    @BindView(R.id.iv_bg)
    ImageView iv_bg;
    @BindView(R.id.iv_bg_reflective_light)
    ImageView iv_bg_reflective_light;
    @BindView(R.id.iv_bg_down_hexagon)
    ImageView iv_bg_down_hexagon;
    @BindView(R.id.iv_bg_point)
    ImageView iv_bg_point;
    @BindView(R.id.iv_bg_up_hexagon)
    ImageView iv_bg_up_hexagon;
    @BindView(R.id.iv_logo)
    ImageView iv_logo;
    @BindView(R.id.iv_mid_point1)
    ImageView iv_mid_point1;
    @BindView(R.id.iv_little_point1)
    ImageView iv_little_point1;
    @BindView(R.id.iv_mid_point2)
    ImageView iv_mid_point2;
    @BindView(R.id.iv_little_point2)
    ImageView iv_little_point2;

    @Inject
    SplashPresenter splashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        animate();
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(splashPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    public void animate() {
        ObjectAnimator animatorUpHexagon = ObjectAnimator.ofFloat(iv_bg_up_hexagon, "rotationY", 0, 360);
        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
        animatorUpHexagon.setDuration(2000).setInterpolator(accelerateInterpolator);
        animatorUpHexagon.start();

        ObjectAnimator animatorDownHexagon = ObjectAnimator.ofFloat(iv_bg_down_hexagon, "rotationY", 0, 360);
        accelerateInterpolator = new AccelerateInterpolator();
        animatorDownHexagon.setDuration(2000).setInterpolator(accelerateInterpolator);
        animatorDownHexagon.start();

        ObjectAnimator animator = ObjectAnimator.ofFloat(iv_bg_reflective_light, "translationX", 0, -200).setDuration(1900);
        animator.setStartDelay(3000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                splashPresenter.doAnimationFinishedProcess();
            }
        });
        animator.start();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(iv_bg_point, "scaleX", 0, 1),
                ObjectAnimator.ofFloat(iv_bg_point, "scaleY", 0, 1),
                ObjectAnimator.ofFloat(iv_bg_point, "alpha", 0, 1)
        );
        animatorSet.setDuration(1200);
        animatorSet.setStartDelay(300);
        animatorSet.start();

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(iv_logo, "scaleX", 0, 1.5f),
                ObjectAnimator.ofFloat(iv_logo, "scaleY", 0, 1.5f),
                ObjectAnimator.ofFloat(iv_logo, "alpha", 0, 1)
        );
        animatorSet.setDuration(1200);
        animatorSet.setStartDelay(1900);
        animatorSet.start();

//        animatorSet = new AnimatorSet();
//        animatorSet.playTogether(
//                ObjectAnimator.ofFloat(iv_mid_point2, "translationX", 0, 150),
//                ObjectAnimator.ofFloat(iv_mid_point2, "translationY", 0, -85),
//                ObjectAnimator.ofFloat(iv_mid_point2, "alpha", 0, 1)
//        );
//        animatorSet.setDuration(700);
//        animatorSet.setStartDelay(1900);
//        animatorSet.start();
    }

    @Override
    public void navigatorToNextStep() {
        AndroidUtil.startActivity(this, MainMenuActivity.class);
        finish();
    }
}
