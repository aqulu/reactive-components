package lu.aqu.reactivecomponents;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * FloatingActionButton featuring loading state
 */
public class ReactiveFloatingActionButton extends RelativeLayout implements ReactiveComponent {

    private FloatingActionButton mFab;
    private ProgressBar mProgressBar;

    private boolean mDisabledWhileLoading;
    private boolean mClickableWhileLoading;
    private float mDisabledAlpha;

    public static final int SIZE_MINI = FloatingActionButton.SIZE_MINI;
    public static final int SIZE_NORMAL = FloatingActionButton.SIZE_NORMAL;

    public ReactiveFloatingActionButton(Context context) {
        this(context, null);
    }

    public ReactiveFloatingActionButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReactiveFloatingActionButton(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(context);

        mFab = new FloatingActionButton(context, attrs, defStyle);

        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ReactiveFloatingActionButton, 0, 0);
        try {
            mClickableWhileLoading = a
                    .getBoolean(R.styleable.ReactiveFloatingActionButton_clickableWhileLoading, false);
            mDisabledWhileLoading = a
                    .getBoolean(R.styleable.ReactiveFloatingActionButton_disabledWhileLoading, false);
            mDisabledAlpha = a.getFloat(R.styleable.ReactiveFloatingActionButton_disabledAlpha, 0.65f);

            if (a.hasValue(R.styleable.ReactiveFloatingActionButton_backgroundColor)) {
                int background = a.getColor(R.styleable.ReactiveFloatingActionButton_backgroundColor,
                        getPrimaryDarkColor(context));
                mFab.setBackgroundTintList(ColorStateList.valueOf(background));
            }

            if (a.hasValue(R.styleable.ReactiveFloatingActionButton_src)) {
                Drawable drawable = a.getDrawable(R.styleable.ReactiveFloatingActionButton_src);
                mFab.setImageDrawable(drawable);
            }

            int size = a.getInt(R.styleable.ReactiveFloatingActionButton_loadingFabSize, SIZE_NORMAL);
            if (size == SIZE_MINI) {
                mProgressBar = (ProgressBar) inflater.inflate(R.layout.progress_bar_mini, null);
            } else {
                mProgressBar = (ProgressBar) inflater.inflate(R.layout.progress_bar_normal, null);
            }
            mFab.setSize(size);
        } finally {
            a.recycle();
        }

        mProgressBar.setVisibility(INVISIBLE);
        addViewCentered(mFab);
        addViewCentered(mProgressBar);

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callOnClick();
            }
        });
    }

    private void addViewCentered(View view) {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(view, layoutParams);
    }

    private int getPrimaryDarkColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    @Override
    public void onLoadingStart() {
        setIsLoading(true);
    }

    @Override
    public void onLoadingFinished() {
        setIsLoading(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mFab.setEnabled(enabled);
        mFab.setAlpha((enabled) ? 1f : mDisabledAlpha);
    }

    @Override
    public void setClickable(boolean enabled) {
        super.setClickable(enabled);
        mFab.setClickable(enabled);
    }

    @Override
    public boolean isClickable() {
        return mFab.isClickable();
    }

    @Override
    public void setIsLoading(boolean loading) {
        mProgressBar.setVisibility((loading) ? VISIBLE : INVISIBLE);

        if (mDisabledWhileLoading) {
            setEnabled(!loading);
        } else if (!mClickableWhileLoading) {
            setClickable(!loading);
        }
    }

    @Override
    public boolean isLoading() {
        return mProgressBar.getVisibility() == VISIBLE;
    }

    /**
     * specify whether the component shall be clickable while in loading state (defaults to false)
     *
     * @param clickable
     */
    public void setClickableWhileLoading(boolean clickable) {
        mClickableWhileLoading = clickable;
    }


    /**
     * specify whether the component shall be disabled while loading state is displayed
     *
     * @param disabled
     */
    public void setDisabledWhileLoading(boolean disabled) {
        mDisabledWhileLoading = disabled;
    }
}
