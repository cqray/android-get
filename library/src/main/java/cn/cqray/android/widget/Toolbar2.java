//package cn.cqray.android.widget;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.res.ColorStateList;
//import android.content.res.TypedArray;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.ColorInt;
//import androidx.annotation.DrawableRes;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.StringRes;
//import androidx.appcompat.widget.AppCompatEditText;
//import androidx.core.content.ContextCompat;
//import androidx.core.view.ViewCompat;
//import androidx.lifecycle.Lifecycle;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.LifecycleRegistry;
//import androidx.lifecycle.MutableLiveData;
//
//import com.google.android.material.shape.MaterialShapeDrawable;
//import com.google.android.material.shape.MaterialShapeUtils;
//
//import cn.cqray.android.R;
//import cn.cqray.android.util.Sizes;
//
///**
// * 标题栏
// * @author Cqray
// */
//public class Toolbar2 extends RelativeLayout {
//
//    /** 标题控件 **/
//    private TextView mTitleView;
//    private View mDividerView;
//    private IconTextView mBackView;
//    private ActionLayout mActionLayout;
//
//    private int mTitleSpace;
//    private boolean mTitleCenter;
//    private LifecycleRegistry mLifecycleRegistry;
//    private final LifecycleOwner mLifecycleOwner;
//
//    private final MutableLiveData<Integer> mPadding = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> mUseRipple = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> mTitleCenterData = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> mTitleEditable = new MutableLiveData<>();
//
//    public Toolbar2(@NonNull Context context) {
//        this(context, null);
//    }
//
//    public Toolbar2(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public Toolbar2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mLifecycleOwner = () -> mLifecycleRegistry;
//        mLifecycleRegistry = new LifecycleRegistry(mLifecycleOwner);
//        mLifecycleRegistry.setCurrentState(Lifecycle.State.INITIALIZED);
//        // 设置默认Id
//        if (getId() == NO_ID) {
//            setId(R.id.starter_toolbar);
//        }
//        // 初始化一些基础属性
//        initToolbarBasic(attrs);
//        // 初始化回退按钮
//        initToolbarBack(attrs);
//        // 初始化Action
//        initToolbarAction(attrs);
//        // 初始化标题
//        initToolbarTittle(attrs);
//        // 初始化分割线
//        initToolbarDivider(attrs);
//        // 初始化LiveData
//        initLiveData();
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        mLifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
//        MaterialShapeUtils.setParentAbsoluteElevation(this);
//
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
//    }
//
//    private void initToolbarBasic(AttributeSet attrs) {
//        Context context = getContext();
//        int size = getResources().getDimensionPixelSize(R.dimen.content);
//        int elev = getResources().getDimensionPixelSize(R.dimen.elevation);
//        // 获取默认属性
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
//        int padding = ta.getDimensionPixelSize(R.styleable.Toolbar_sPadding, size);
//        float elevation = ta.getDimension(R.styleable.Toolbar_sElevation, elev);
//        boolean useRipple = ta.getBoolean(R.styleable.Toolbar_sUseRipple, true);
//        boolean titleEditable = ta.getBoolean(R.styleable.Toolbar_sTitleEditable, false);
//        mTitleCenter = ta.getBoolean(R.styleable.Toolbar_sTitleCenter, false);
//        mTitleSpace = ta.getDimensionPixelSize(R.styleable.Toolbar_sTitleSpace, size);
//        ta.recycle();
//        // 设置标题栏背景
//        int primaryColor = ContextCompat.getColor(context, R.color.colorPrimary);
//        Drawable background = getBackground() == null ? new ColorDrawable(primaryColor) : getBackground();
//        ViewCompat.setBackground(this, createMaterialShapeDrawableBackground(background));
//        // 其他属性
//        mTitleCenterData.setValue(mTitleCenter);
//        mTitleEditable.setValue(titleEditable);
//        mUseRipple.setValue(useRipple);
//        mPadding.setValue(padding);
//        setElevation(elevation);
//    }
//
//    @Override
//    public void setBackgroundColor(int color) {
//        super.setBackgroundColor(color);
//        Log.e("数据", "33333");
//    }
//
//    private void initToolbarBack(AttributeSet attrs) {
//        Context context = getContext();
//        // 获取属性
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
//        Drawable drawable = ta.getDrawable(R.styleable.Toolbar_sBackIcon);
//        String text = ta.getString(R.styleable.Toolbar_sBackText);
//        boolean iconVisible = ta.getBoolean(R.styleable.Toolbar_sBackIconVisible, true);
//        int backTextColor = ta.getColor(R.styleable.Toolbar_sBackTextColor, Color.WHITE);
//        int backTextSize = ta.getDimensionPixelSize(R.styleable.Toolbar_sBackTextSize, getResources().getDimensionPixelSize(R.dimen.h3));
//        int backTextStyle = ta.getInt(R.styleable.Toolbar_sBackTextStyle, 0);
//        ta.recycle();
//        // 设置Nav布局
//        LayoutParams params = new LayoutParams(-2, -1);
//        mBackView = new IconTextView(context);
//        mBackView.setId(R.id.starter_toolbar_back_view);
//        mBackView.setLayoutParams(params);
//        mBackView.setText(text);
//        mBackView.setTextColor(backTextColor);
//        mBackView.setTextSize(backTextSize, TypedValue.COMPLEX_UNIT_PX);
//        mBackView.setTypeface(Typeface.defaultFromStyle(backTextStyle));
//        mBackView.getIconView().setVisibility(iconVisible ? VISIBLE : GONE);
//        if (drawable == null) {
//            mBackView.setIconResource(R.drawable.def_back_material_light);
//        } else {
//            mBackView.setIconDrawable(drawable);
//        }
//        addView(mBackView);
//    }
//
//    private void initToolbarAction(AttributeSet attrs) {
//        Context context = getContext();
//        // 设置Action布局
//        LayoutParams params = new LayoutParams(-2, -1);
//        params.addRule(Build.VERSION.SDK_INT >= 17 ? ALIGN_PARENT_END : ALIGN_PARENT_RIGHT);
//        mActionLayout = new ActionLayout(context, attrs);
//        mActionLayout.setId(R.id.starter_toolbar_action_layout);
//        mActionLayout.setLayoutParams(params);
//        addView(mActionLayout);
//        mActionLayout.requestLayout();
//    }
//
//    private void initToolbarTittle(AttributeSet attrs) {
//        Context context = getContext();
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
//        String titleText = ta.getString(R.styleable.Toolbar_sTitleText);
//        int titleTextColor = ta.getColor(R.styleable.Toolbar_sTitleTextColor, Color.WHITE);
//        int titleTextSize = ta.getDimensionPixelSize(R.styleable.Toolbar_sTitleTextSize, getResources().getDimensionPixelSize(R.dimen.h2));
//        int titleTextStyle = ta.getInt(R.styleable.Toolbar_sTitleTextStyle, 0);
//        ta.recycle();
//        // 设置标题
//        mTitleView = new AppCompatEditText(context);
//        mTitleView.setLayoutParams(new LayoutParams(-1, -1));
//        mTitleView.setId(R.id.starter_toolbar_title);
//        mTitleView.setText(titleText);
//        mTitleView.setTextColor(titleTextColor);
//        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
//        mTitleView.setTypeface(Typeface.defaultFromStyle(titleTextStyle));
//        addView(mTitleView);
//    }
//
//    private void initToolbarDivider(AttributeSet attrs) {
//        Context context = getContext();
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
//        int dividerColor = ta.getColor(R.styleable.Toolbar_sDividerColor, ContextCompat.getColor(context, R.color.divider));
//        int dividerHeight = ta.getDimensionPixelSize(R.styleable.Toolbar_sDividerHeight, 0);
//        int dividerMargin = ta.getDimensionPixelSize(R.styleable.Toolbar_sDividerMargin, 0);
//        boolean dividerVisible = ta.getBoolean(R.styleable.Toolbar_sDividerVisible, true);
//        ta.recycle();
//        // 设置分割线
//        LayoutParams params = new LayoutParams(-1, dividerHeight);
//        params.addRule(ALIGN_PARENT_BOTTOM);
//        params.setMargins(dividerMargin, 0, dividerMargin, 0);
//        mDividerView = new View(context);
//        mDividerView.setBackgroundColor(dividerColor);
//        mDividerView.setLayoutParams(params);
//        mDividerView.setVisibility(dividerVisible ? VISIBLE : INVISIBLE);
//        addView(mDividerView);
//    }
//
//    @SuppressLint("InlinedApi")
//    private void initLiveData() {
//        // 标题居中监听
//        mTitleCenterData.observe(mLifecycleOwner, center -> {
//            boolean isNewApi = Build.VERSION.SDK_INT >= 17;
//            int padding = mPadding.getValue() == null ? 0 : mPadding.getValue();
//            boolean iconVisible = mBackView.getIconView().getVisibility() == View.VISIBLE;
//            LayoutParams params = (LayoutParams) mTitleView.getLayoutParams();
//            if (center) {
//                int leftWidth = mBackView.getWidth() + (iconVisible ? mTitleSpace - padding : 0);
//                int actionWidth = mActionLayout.getWidth() + mTitleSpace - mActionLayout.getActionSpace();
//                int m = Math.max(leftWidth, actionWidth);
//                params.addRule(isNewApi ? RelativeLayout.START_OF : RelativeLayout.LEFT_OF, -1);
//                params.addRule(isNewApi ? RelativeLayout.END_OF : RelativeLayout.RIGHT_OF, -1);
//                params.addRule(RelativeLayout.CENTER_IN_PARENT);
//                params.setMargins(m, 0, m, 0);
//                mTitleView.setGravity(Gravity.CENTER);
//            } else {
//                params.addRule(isNewApi ? RelativeLayout.START_OF : RelativeLayout.LEFT_OF, R.id.starter_toolbar_action_layout);
//                params.addRule(isNewApi ? RelativeLayout.END_OF : RelativeLayout.RIGHT_OF, R.id.starter_toolbar_back_view);
//                params.leftMargin = iconVisible ? mTitleSpace - padding : 0;
//                params.rightMargin = mTitleSpace - mActionLayout.getActionSpace();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    params.setMarginStart(iconVisible ? mTitleSpace - padding : 0);
//                    params.setMarginEnd(mTitleSpace - mActionLayout.getActionSpace());
//                }
//                params.addRule(RelativeLayout.CENTER_VERTICAL);
//                mTitleView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
//            }
//            mTitleView.setLayoutParams(params);
//        });
//        // 监听设置标题栏是否可编辑
//        mTitleEditable.observe(mLifecycleOwner, aBoolean -> {
//            mTitleView.setFocusableInTouchMode(aBoolean);
//            mTitleView.setClickable(aBoolean);
//            mTitleView.setFocusable(aBoolean);
//            mTitleView.setEnabled(aBoolean);
//            if (aBoolean) {
//                TypedArray ta = getContext().obtainStyledAttributes(new int[]{android.R.attr.editTextBackground});
//                Drawable drawable = ta.getDrawable(0);
//                ta.recycle();
//                ViewCompat.setBackground(mTitleView, drawable);
//            } else {
//                ViewCompat.setBackground(mTitleView, null);
//            }
//            mBackView.bringToFront();
//            mActionLayout.bringToFront();
//        });
//        // 是否使用水波纹
//        mUseRipple.observe(mLifecycleOwner, aBoolean -> {
//            mBackView.setUseRipple(aBoolean);
//            mActionLayout.setDefaultUseRipple(aBoolean);
//        });
//        // 间隔大小监听
//        mPadding.observe(mLifecycleOwner, aInteger -> {
//            // 设置BackLayout内部间隔
//            boolean iconVisible = mBackView.getIconView().getVisibility() == View.VISIBLE;
//            mBackView.setPadding(aInteger, 0, iconVisible ? aInteger : 0, 0);
//            // 设置ActionLayout右部间隔
//            LayoutParams params = (LayoutParams) mActionLayout.getLayoutParams();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                params.setMarginEnd(aInteger - mActionLayout.getActionSpace());
//            }
//            params.rightMargin = aInteger - mActionLayout.getActionSpace();
//        });
//    }
//
//    @Override
//    public void setElevation(float elevation) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            super.setElevation(elevation);
//        }
//        MaterialShapeUtils.setElevation(this, elevation);
//    }
//
//    @Override
//    public void setGravity(int gravity) {}
//
//    public Toolbar2 setPadding(float padding) {
//        setPadding(padding, TypedValue.COMPLEX_UNIT_DIP);
//        return this;
//    }
//
//    public Toolbar2 setPadding(float padding, int unit) {
//        mPadding.setValue((int) Sizes.applyDimension(padding, unit));
//        return this;
//    }
//
//    public Toolbar2 setUseRipple(boolean useRipple) {
//        mUseRipple.setValue(useRipple);
//        return this;
//    }
//
//    public Toolbar2 setTitleCenter(boolean center) {
//        mTitleCenter = center;
//        mTitleCenterData.setValue(center);
//        return this;
//    }
//
//    public Toolbar2 setBackViewSpace(float space) {
//        mBackView.setViewSpace(space);
//        return this;
//    }
//
//    public Toolbar2 setBackIcon(@DrawableRes int resId) {
//        mBackView.setIconResource(resId);
//        return this;
//    }
//
//    public Toolbar2 setBackIcon(Drawable drawable) {
//        mBackView.setIconDrawable(drawable);
//        return this;
//    }
//
//    public Toolbar2 setBackIcon(Bitmap bitmap) {
//        mBackView.setIconBitmap(bitmap);
//        return this;
//    }
//
//    public Toolbar2 setBackIconTintColor(@ColorInt int color) {
//        mBackView.setIconTintColor(color);
//        return this;
//    }
//
//    public Toolbar2 setBackIconTintList(ColorStateList tintList) {
//        mBackView.setIconTintList(tintList);
//        return this;
//    }
//
//    public Toolbar2 setBackIconVisible(boolean visible) {
//        if (visible != (mBackView.getIconView().getVisibility() == VISIBLE)) {
//            mBackView.getIconView().setVisibility(visible ? VISIBLE : GONE);
//            mPadding.setValue(mPadding.getValue());
//        }
//        return this;
//    }
//
//    public Toolbar2 setBackText(CharSequence text) {
//        mBackView.setText(text);
//        return this;
//    }
//
//    public Toolbar2 setBackText(@StringRes int id) {
//        mBackView.setText(id);
//        return this;
//    }
//
//    public Toolbar2 setBackTextColor(@ColorInt int color) {
//        mBackView.setTextColor(color);
//        return this;
//    }
//
//    public Toolbar2 setBackTextSize(float size) {
//        mBackView.setTextSize(size);
//        return this;
//    }
//
//    public Toolbar2 setBackTextSize(float size, int unit) {
//        mBackView.setTextSize(size, unit);
//        return this;
//    }
//
//    public Toolbar2 setBackTypeface(Typeface typeface) {
//        mBackView.setTypeface(typeface);
//        return this;
//    }
//
//    public Toolbar2 setBackListener(OnClickListener listener) {
//        mBackView.setOnClickListener(listener);
//        return this;
//    }
//
//    public Toolbar2 setTitle(@StringRes int id) {
//        mTitleView.setText(id);
//        return this;
//    }
//
//    public Toolbar2 setTitle(CharSequence text) {
//        mTitleView.setText(text);
//        return this;
//    }
//
//    public Toolbar2 setTitleTextColor(@ColorInt int color) {
//        mTitleView.setTextColor(color);
//        return this;
//    }
//
//    public Toolbar2 setTitleTextSize(float size) {
//        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
//        return this;
//    }
//
//    public Toolbar2 setTitleTextSize(float size, int unit) {
//        mTitleView.setTextSize(unit, size);
//        return this;
//    }
//
//    public Toolbar2 setTitleTypeface(Typeface typeface) {
//        mTitleView.setTypeface(typeface);
//        return this;
//    }
//
//    public Toolbar2 setTitleEditable(boolean editable) {
//        mTitleEditable.setValue(editable);
//        return this;
//    }
//
//    public Toolbar2 setTitleSpace(float space) {
//        return setTitleSpace(space, TypedValue.COMPLEX_UNIT_DIP);
//    }
//
//    public Toolbar2 setTitleSpace(float space, int unit) {
//        mTitleSpace = (int) Sizes.applyDimension(space, unit);
//        mTitleCenterData.setValue(mTitleCenter);
//        return this;
//    }
//
//    public Toolbar2 setActionText(int key, CharSequence text) {
//        mActionLayout.setActionText(key, text);
//        return this;
//    }
//
//    public Toolbar2 setActionText(int key, @StringRes int resId) {
//        mActionLayout.setActionText(key, resId);
//        return this;
//    }
//
//    public Toolbar2 setDefaultActionTextColor(@ColorInt int color) {
//        mActionLayout.setDefaultActionTextColor(color);
//        return this;
//    }
//
//    public Toolbar2 setActionTextColor(int key, @ColorInt int color) {
//        mActionLayout.setActionTextColor(key, color);
//        return this;
//    }
//
//    public Toolbar2 setDefaultActionTextSize(float size) {
//        mActionLayout.setDefaultActionTextSize(size);
//        return this;
//    }
//
//    public Toolbar2 setDefaultActionTextSize(float size, int unit) {
//        mActionLayout.setDefaultActionTextSize(size, unit);
//        return this;
//    }
//
//    public Toolbar2 setActionTextSize(int key, float size) {
//        mActionLayout.setActionTextSize(key, size);
//        return this;
//    }
//
//    public Toolbar2 setActionTextSize(int key, float size, int unit) {
//        mActionLayout.setActionTextSize(key, size, unit);
//        return this;
//    }
//
//    public Toolbar2 setDefaultActionTypeface(Typeface typeface) {
//        mActionLayout.setDefaultActionTypeface(typeface);
//        return this;
//    }
//
//    public Toolbar2 setActionTypeface(int key, Typeface typeface) {
//        mActionLayout.setActionTypeface(key, typeface);
//        return this;
//    }
//
//    public Toolbar2 setActionIcon(int key, @DrawableRes int resId) {
//        mActionLayout.setActionIcon(key, resId);
//        return this;
//    }
//
//    public Toolbar2 setActionIcon(int key, @DrawableRes int resId, @ColorInt int tintColor) {
//        mActionLayout.setActionIcon(key, resId, tintColor);
//        return this;
//    }
//
//    public Toolbar2 setActionIcon(int key, Drawable drawable) {
//        mActionLayout.setActionIcon(key, drawable);
//        return this;
//    }
//
//    public Toolbar2 setActionIcon(int key, Drawable drawable,  @ColorInt int tintColor) {
//        mActionLayout.setActionIcon(key, drawable, tintColor);
//        return this;
//    }
//
//    public Toolbar2 setActionIconColor(@ColorInt int tintColor) {
//        mActionLayout.setActionIconColor(tintColor);
//        return this;
//    }
//
//    public Toolbar2 setActionIconColor(int key, @ColorInt int tintColor) {
//        mActionLayout.setActionIconColor(key, tintColor);
//        return this;
//    }
//
//    public Toolbar2 setActionVisible(boolean visible) {
//        mActionLayout.setActionVisible(visible);
//        return this;
//    }
//
//    public Toolbar2 setActionVisible(int key, boolean visible) {
//        mActionLayout.setActionVisible(key, visible);
//        return this;
//    }
//
//    public Toolbar2 setActionUseRipple(int key, boolean useRipple) {
//        mActionLayout.setUseRipple(key, useRipple);
//        return this;
//    }
//
//    public Toolbar2 setActionSpace(float space) {
//        return setActionSpace(space, TypedValue.COMPLEX_UNIT_DIP);
//    }
//
//    public Toolbar2 setActionSpace(float space, int unit) {
//        mActionLayout.setActionSpace(space, unit);
//        mPadding.setValue(mPadding.getValue());
//        mTitleCenterData.setValue(mTitleCenter);
//        return this;
//    }
//
//    public Toolbar2 setActionListener(int key, OnClickListener listener) {
//        mActionLayout.setActionListener(key, listener);
//        return this;
//    }
//
//    public Toolbar2 setDividerColor(@ColorInt int color) {
//        mDividerView.setBackgroundColor(color);
//        return this;
//    }
//
//    public Toolbar2 setDividerHeight(float height) {
//        return setDividerHeight(height, TypedValue.COMPLEX_UNIT_DIP);
//    }
//
//    public Toolbar2 setDividerHeight(float height, int unit) {
//        mDividerView.getLayoutParams().height = (int) Sizes.applyDimension(height, unit);
//        mDividerView.requestLayout();
//        return this;
//    }
//
//    public Toolbar2 setDividerMargin(float margin) {
//        return setDividerMargin(margin, TypedValue.COMPLEX_UNIT_DIP);
//    }
//
//    public Toolbar2 setDividerMargin(float margin, int unit) {
//        int m = (int) Sizes.applyDimension(margin, unit);
//        LayoutParams params = (LayoutParams) mDividerView.getLayoutParams();
//        params.setMargins(m, 0, m, 0);
//        mDividerView.requestLayout();
//        return this;
//    }
//
//    public Toolbar2 setDividerVisible(boolean visible) {
//        mDividerView.setVisibility(visible ? VISIBLE : INVISIBLE);
//        return this;
//    }
//
//    public IconTextView getBackView() {
//        return mBackView;
//    }
//
//    public ActionLayout getActionLayout() {
//        return mActionLayout;
//    }
//
//    public TextView getTitleView() {
//        return mTitleView;
//    }
//
//    @NonNull
//    private Drawable createMaterialShapeDrawableBackground(@NonNull Drawable background) {
//        if (background instanceof ColorDrawable) {
//            MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
//            materialShapeDrawable.setFillColor(
//                    ColorStateList.valueOf(((ColorDrawable) background).getColor()));
//            materialShapeDrawable.initializeElevationOverlay(getContext());
//            return materialShapeDrawable;
//        } else {
//            return background;
//        }
//    }
//}
