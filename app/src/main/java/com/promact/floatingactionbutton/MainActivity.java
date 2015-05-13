package com.promact.floatingactionbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private ImageView fab;

    private boolean expanded = false;

    private View fabAction1;
    private View fabAction2;
    private View fabAction3;

    private float offset1;
    private float offset2;
    private float offset3;

    private View overlay;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //resources
        final ViewGroup fabContainer = (ViewGroup) findViewById(R.id.fab_container);
        fab = (ImageView) findViewById(R.id.fab);
        fabAction1 = findViewById(R.id.fab_action_1);
        fabAction2 = findViewById(R.id.fab_action_2);
        fabAction3 = findViewById(R.id.fab_action_3);
        overlay = (View) findViewById(R.id.overlay);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Floating Action Button Example");


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded = !expanded;
                if (expanded) {
                    expandFab();
                } else {
                    collapseFab();
                }
            }
        });
        fabContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                offset1 = fab.getY() - fabAction1.getY();
                fabAction1.setTranslationY(offset1);
                offset2 = fab.getY() - fabAction2.getY();
                fabAction2.setTranslationY(offset2);
                offset3 = fab.getY() - fabAction3.getY();
                fabAction3.setTranslationY(offset3);
                return true;
            }
        });


        //listenre
        overlay.setOnClickListener(this);
    }

    private void collapseFab() {
        fab.setImageResource(R.drawable.animated_minus);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1), createCollapseAlphaAnimator(fabAction1, offset1),
                createCollapseAnimator(fabAction2, offset2), createCollapseAlphaAnimator(fabAction2, offset1),
                createCollapseAnimator(fabAction3, offset3), createCollapseAlphaAnimator(fabAction3, offset1));
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(300);
        animatorSet.start();
        animateFab();

        // Check for the version and set animation accordingly as CircularReveal is not available for lower version
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FadeOutWhiteBackground();
        }else{
            overlay.setVisibility(View.VISIBLE);
            overlay.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_background_fade_out));
        }
    }

    private void expandFab() {
        fab.setImageResource(R.drawable.animated_plus);
        fabAction1.setAlpha(0.0f);
        fabAction2.setAlpha(0.0f);
        fabAction3.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1), createExpandAlphaAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2), createExpandAlphaAnimator(fabAction2, offset1),
                createExpandAnimator(fabAction3, offset3), createExpandAlphaAnimator(fabAction3, offset1)
        );
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(300);

        animatorSet.start();
        animateFab();

        //
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FadeInWhiteBackground();
        }else{
            overlay.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_background_fade_in));
        }

    }

    private static final String TRANSLATION_Y = "translationY";
    private static final String ALPHA = "alpha";

    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private Animator createExpandAnimator(View view, float offset) {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        objectAnimator.setInterpolator(new OvershootInterpolator());
        return objectAnimator;
    }

    private Animator createExpandAlphaAnimator(View view, float offset) {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(view, ALPHA, 0.0f, 1.0f);
        return objectAnimator;
    }

    private Animator createCollapseAlphaAnimator(View view, float offset) {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(view, ALPHA, 1.0f, 0.0f);
        return objectAnimator;
    }

    private void animateFab() {
        Drawable drawable = fab.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void FadeOutWhiteBackground(){
        // get the center for the clipping circle
        int cx = overlay.getMeasuredWidth() ;
        int cy = overlay.getMeasuredHeight();
        // get the initial radius for the clipping circle
        int initialRadius = overlay.getWidth();
        // create the animation (the final radius is zero)
        Animator anim =ViewAnimationUtils.createCircularReveal(overlay, cx, cy, initialRadius, 0);
        anim.setInterpolator(new OvershootInterpolator());
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                overlay.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void FadeInWhiteBackground(){
        Animator animator = ViewAnimationUtils.createCircularReveal(
                overlay,
                overlay.getWidth(),
                overlay.getHeight(),
                0,
                (float) Math.hypot(overlay.getWidth(), overlay.getHeight()));
        // Set a natural ease-in/ease-out interpolator.
        animator.setInterpolator(new OvershootInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                overlay.setVisibility(View.VISIBLE);
            }
        });

        // Finally start the animation
        animator.start();

    }

    @Override
    public void onBackPressed() {
        if (expanded) {
            expanded = !expanded;
            collapseFab();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.overlay:
                collapseFab();
                expanded = !expanded;
            break;
        }
    }
}
