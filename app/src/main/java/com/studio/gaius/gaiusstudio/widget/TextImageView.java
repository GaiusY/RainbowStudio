package com.studio.gaius.gaiusstudio.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.studio.gaius.gaiusstudio.R;

public class TextImageView extends AppCompatTextView {

    private int mLeftWidth;
    private int mLeftHeight;
    private int mTopWidth;
    private int mTopHeight;
    private int mRightWidth;
    private int mRightHeight;
    private int mBottomWidth;
    private int mBottomHeight;

    private static final int MAX_LEVEL = 10000;
    private int arrowDrawableResId;
    private int arrowDrawableTint;
    private ObjectAnimator arrowAnimator = null;

    public TextImageView(Context context) {
        super(context);
    }

    public TextImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextImageView);

        mLeftWidth = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableLeftWidth, 0);
        mLeftHeight = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableLeftHeight, 0);
        mTopWidth = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableTopWidth, 0);
        mTopHeight = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableTopHeight, 0);
        mRightWidth = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableRightWidth, 0);
        mRightHeight = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableRightHeight, 0);
        mBottomWidth = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableBottomWidth, 0);
        mBottomHeight = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableBottomHeight, 0);
        arrowDrawableTint = typedArray.getColor(R.styleable.TextImageView_arrowTint, getResources().getColor(android.R.color.white));
        arrowDrawableResId = typedArray.getResourceId(R.styleable.TextImageView_arrowDrawable, R.drawable.arrow);
        typedArray.recycle();
        setDrawablesSize();
    }

    public void showAnimationRight(Context context, TextImageView view, boolean shouldRotateUp) {
        Drawable arrowDrawable = initArrowDrawable(context, arrowDrawableTint);
        setDrawableBounds(arrowDrawable, getResources().getDimensionPixelOffset(R.dimen.dp_30), getResources().getDimensionPixelOffset(R.dimen.dp_30));
        view.setCompoundDrawables(null, null, arrowDrawable, null);
        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
        arrowAnimator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end);
        arrowAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        arrowAnimator.start();
    }

    public void cancelAnimation() {
        if (arrowAnimator != null) arrowAnimator.cancel();
    }

    private Drawable initArrowDrawable(Context context, int drawableTint) {
        if (arrowDrawableResId == 0) return null;
        Drawable drawable = ContextCompat.getDrawable(context, arrowDrawableResId);
        if (drawable != null) {
            // Gets a copy of this drawable as this is going to be mutated by the animator
            drawable = DrawableCompat.wrap(drawable).mutate();
            if (drawableTint != Integer.MAX_VALUE && drawableTint != 0) {
                DrawableCompat.setTint(drawable, drawableTint);
            }
        }
        return drawable;
    }

    public void setArrowRotate(TextImageView view) {
        if (view == null) return;
        AnimationDrawable drawable = (AnimationDrawable) getResources().getDrawable(R.drawable.arrow_triangle);
        setDrawableBounds(drawable, getResources().getDimensionPixelOffset(R.dimen.dp_30), getResources().getDimensionPixelOffset(R.dimen.dp_30));
        //drawable.setBounds(0, 0, getResources().getDimensionPixelOffset(R.dimen.dp_30), getResources().getDimensionPixelOffset(R.dimen.dp_30));
        view.setCompoundDrawables(null, null, drawable, null);
        ((Animatable) drawable).start();
    }

    private void setDrawablesSize() {
        Drawable[] compoundDrawables = getCompoundDrawables();
        for (int i = 0; i < compoundDrawables.length; i++) {
            switch (i) {
                case 0:
                    setDrawableBounds(compoundDrawables[0], mLeftWidth, mLeftHeight);
                    break;
                case 1:
                    setDrawableBounds(compoundDrawables[1], mTopWidth, mTopHeight);
                    break;
                case 2:
                    setDrawableBounds(compoundDrawables[2], mRightWidth, mRightHeight);
                    break;
                case 3:
                    setDrawableBounds(compoundDrawables[3], mBottomWidth, mBottomHeight);
                    break;
            }
        }
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

    private void setDrawableBounds(Drawable drawable, int width, int height) {
        if (drawable != null) {
            double scale = ((double) drawable.getIntrinsicHeight()) / ((double) drawable.getIntrinsicWidth());
            drawable.setBounds(0, 0, width, height);
            Rect bounds = drawable.getBounds();
            //高宽只给一个值时，自适应
            if (bounds.right != 0 || bounds.bottom != 0) {
                if (bounds.right == 0) {
                    bounds.right = (int) (bounds.bottom / scale);
                    drawable.setBounds(bounds);
                }
                if (bounds.bottom == 0) {
                    bounds.bottom = (int) (bounds.right * scale);
                    drawable.setBounds(bounds);
                }
            }
        }
    }
}
