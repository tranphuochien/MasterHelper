package com.hientp.hcmus.uiwidget.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hientp.hcmus.uiwidget.R;
import com.hientp.hcmus.uiwidget.R2;
import com.hientp.hcmus.uiwidget.dialog.listener.OnDialogListener;
import com.hientp.hcmus.utility.DeviceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;


public class SweetAlertDialog extends Dialog implements View.OnClickListener {
    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int INFO_NO_ICON = 4;
    public static final int PROGRESS_TYPE = 5;
    public static final int UPDATE_TYPE = 6;
    public static final int INFO_TYPE = 7;
    public static final int CUSTOM_CONTENT_VIEW = 8;
    public static final int UPDATE = 9;
    public static final int NO_INTERNET = 10;
    public static final int STYLE = R.style.alert_dialog;
    private static final String TAG = SweetAlertDialog.class.getName();
    View mDialogView;
    @BindView(R2.id.view_line)
    View mLine;
    @BindView(R2.id.sweetDialogRootView)
    LinearLayout mRootView;
    @BindView(R2.id.layout_custom_button)
    LinearLayout mLayoutCustomButton;
    @BindView(R2.id.layout_title_ver)
    RelativeLayout mLayoutTitleVer;
    @BindView(R2.id.layout_dialog_button)
    RelativeLayout mLayoutButton;
    AnimationSet mModalInAnim;
    AnimationSet mModalOutAnim;
    Animation mOverlayOutAnim;
    Animation mErrorInAnim;
    AnimationSet mErrorXInAnim;
    AnimationSet mSuccessLayoutAnimSet;
    Animation mSuccessBowAnim;
    @BindView(R2.id.content_text)
    TextView mContentTextView;
    String mContentText;
    boolean mShowCancel;
    boolean mShowConfirm;
    boolean mShowContent;
    String mCancelText;
    String mConfirmText;
    String mUpdateText;
    String mVersionText;
    String mContentTextNointernet;
    String mCheckBoxTitle;
    int mAlertType;
    @BindView(R2.id.error_frame)
    FrameLayout mErrorFrame;
    @BindView(R2.id.success_frame)
    FrameLayout mSuccessFrame;
    @BindView(R2.id.normal_frame)
    FrameLayout mNormalFrame;
    @BindView(R2.id.progress_dialog)
    FrameLayout mProgressFrame;
    @BindView(R2.id.update_frame)
    FrameLayout mUpdateFrame;
    @BindView(R2.id.info_frame)
    FrameLayout mInfoFrame;
    @BindView(R2.id.custom_rootview)
    FrameLayout mCustomFrame;
    @BindView(R2.id.internet_frame)
    FrameLayout mInternetFrame;
    @BindView(R2.id.custom_content)
    FrameLayout mCustomContentFrame;
    @BindView(R2.id.error_x)
    ImageView mErrorX;
    Drawable mCustomImgDrawable;
    @BindView(R2.id.custom_image)
    ImageView mCustomImage;
    @BindView(R2.id.confirm_button)
    TextView mConfirmButton;
    @BindView(R2.id.cancel_button)
    TextView mCancelButton;
    @BindView(R2.id.update_button)
    TextView mUpdateButton;
    @BindView(R2.id.title_text)
    TextView mTitleTextView;
    @BindView(R2.id.version)
    TextView mVersionTextView;
    @BindView(R2.id.warning_frame)
    FrameLayout mWarningFrame;
    @BindView(R2.id.id_check_box)
    CheckBox mCheckBox;
    OnDialogListener mClickButtonArrListener;
    boolean mCloseFromCancel;
    @BindView(R2.id.layout_update_button)
    LinearLayout mLayoutUpdateButton;
    private Unbinder unbinder;
    private String mTitleText;
    private String[] mArrButton;
    private OnSweetClickListener mCancelClickListener;
    private OnSweetClickListener mConfirmClickListener;
    private OnSweetClickListener mUpdateClickListener;
    private int backgroundResource;

    public SweetAlertDialog(Context context) {
        this(context, NORMAL_TYPE, STYLE);
    }

    public SweetAlertDialog(Context context, int alertType, int style) {
        super(context, style);

        setCancelable(true);
        setCanceledOnTouchOutside(false);

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        backgroundResource = typedArray.getResourceId(0, 0);
        //TypedArray should be recycled after use with '#recycle()'
        typedArray.recycle();

        mAlertType = alertType;
        mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.error_frame_in);
        mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.error_x_in);
        // 2.3.x system don't support alpha-animation on layer-list drawable
        // remove it from animation set

        mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.success_bow_roate);
        mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.success_mask_layout);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            SweetAlertDialog.super.cancel();
                        } else {
                            SweetAlertDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // dialog overlay fade out

        if (mAlertType == SweetAlertDialog.PROGRESS_TYPE)
            return;

        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                Window window = getWindow();
                if (window == null) {
                    return;
                }
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                window.setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sweet);


        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mContentTextView.setMovementMethod(new ScrollingMovementMethod());
        mUpdateButton.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mConfirmButton.setBackgroundResource(backgroundResource);
        mCancelButton.setBackgroundResource(backgroundResource);
        setTitleText(mTitleText);
        setCustomViewContent(mContentTextNointernet);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        setUpdatetext(mUpdateText);
        setVersionText(mVersionText);
        setCheckBoxTitle(mCheckBoxTitle);
        setArrButton(mArrButton);
        changeAlertType(mAlertType, true);
        setContentHtmlText(mContentText);
        setWidthDialog();
    }

    /**
     * Set width dialog
     */
    public void setWidthDialog()

    {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        int densityDpi = display.getWidth();
        ViewGroup.LayoutParams params = mRootView.getLayoutParams();

        if (!DeviceUtil.isTablet(getContext())) {
            params.width = (int) (densityDpi * 0.85);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mRootView.setLayoutParams(params);
        } else {
            params.width = (int) (densityDpi * 0.5);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mRootView.setLayoutParams(params);
        }
    }

    public void setBackground(int color) {
        if (mRootView != null) {
            mRootView.setBackgroundColor(color);
        }
    }

    private void restore() {
        mCustomImage.setVisibility(View.GONE);
        mErrorFrame.setVisibility(View.GONE);
        mInfoFrame.setVisibility(View.GONE);
        mCustomFrame.setVisibility(View.GONE);
        mSuccessFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mUpdateFrame.setVisibility(View.GONE);
        mProgressFrame.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.VISIBLE);
        mUpdateButton.setVisibility(View.GONE);
        mErrorFrame.clearAnimation();

    }

    private void playAnimation() {
        if (mAlertType == ERROR_TYPE) {

        }

    }

    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case NORMAL_TYPE:
                    mNormalFrame.setVisibility(View.VISIBLE);
                    break;
                case ERROR_TYPE:
                    mErrorFrame.setVisibility(View.VISIBLE);
                    break;
                case UPDATE_TYPE:
                    mLine.setVisibility(View.GONE);
                    mVersionTextView.setVisibility(View.VISIBLE);
                    mUpdateFrame.setVisibility(View.VISIBLE);
                    mConfirmButton.setVisibility(View.GONE);
                    mUpdateButton.setVisibility(View.VISIBLE);
                    break;
                case UPDATE:
                    mLine.setVisibility(View.VISIBLE);
                    mVersionTextView.setVisibility(View.VISIBLE);
                    mUpdateFrame.setVisibility(View.VISIBLE);
                    mLayoutUpdateButton.setGravity(Gravity.CENTER);
                    mLayoutUpdateButton.setWeightSum(10);
                    mLayoutUpdateButton.setPadding(4, 8, 4, 8);
                    mUpdateButton.setBackground(null);
                    mUpdateButton.setTextColor(Color.parseColor("#008fe5"));
                    mUpdateButton.setGravity(Gravity.RIGHT);
                    mUpdateButton.setVisibility(View.VISIBLE);
                    mUpdateButton.setTypeface(Typeface.DEFAULT_BOLD);
                    break;
                case SUCCESS_TYPE:
                    mSuccessFrame.setVisibility(View.VISIBLE);
                    // initial rotate layout of success mask
                    break;
                case WARNING_TYPE:
                    mWarningFrame.setVisibility(View.VISIBLE);
                    break;
                case INFO_NO_ICON:
                    setCustomImage(mCustomImgDrawable);
                    break;
                case INFO_TYPE:
                    mInfoFrame.setVisibility(View.VISIBLE);
                    break;
                case PROGRESS_TYPE:
                    mProgressFrame.setVisibility(View.VISIBLE);
                    mConfirmButton.setVisibility(View.GONE);
                    mLine.setVisibility(View.GONE);
                    break;
                case CUSTOM_CONTENT_VIEW:
                    mCustomFrame.setVisibility(View.GONE);
                    mNormalFrame.setVisibility(View.GONE);
                    break;
                case NO_INTERNET:
                    mInternetFrame.setVisibility(View.VISIBLE);
                    break;

            }

        }
    }

    public String getTitleText() {
        return mTitleText;
    }

    public SweetAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && !TextUtils.isEmpty(mTitleText) && mTitleText != null) {
            mTitleTextView.setText(Html.fromHtml(mTitleText));
            mTitleTextView.setVisibility(View.VISIBLE);
            mLayoutTitleVer.setVisibility(View.VISIBLE);
        } else if (mLayoutTitleVer != null) {

            mLayoutTitleVer.setVisibility(View.GONE);
        }
        return this;
    }

    public SweetAlertDialog setCustomViewContent(String pTextView) {
        mContentTextNointernet = pTextView;
        if (mCustomFrame != null && pTextView != null) {
            TextView textViewContentNointernet = (TextView) mCustomFrame.findViewById(R.id.text_content_nointernet);
            if (textViewContentNointernet != null) {
                textViewContentNointernet.setText(Html.fromHtml(pTextView));
            }
            mCustomFrame.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public SweetAlertDialog setVersionText(String text) {
        mVersionText = text;
        if (mVersionText != null && mVersionTextView != null) {
            mVersionTextView.setText(Html.fromHtml(mVersionText));
            mVersionTextView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public SweetAlertDialog setCustomImage(Drawable drawable) {
        mCustomImgDrawable = drawable;
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mNormalFrame.setVisibility(View.GONE);
            mCustomImage.setVisibility(View.VISIBLE);
            mCustomImage.setImageDrawable(mCustomImgDrawable);
        }
        return this;
    }

    public SweetAlertDialog setCheckBoxTitle(String pText) {
        mCheckBoxTitle = pText;
        if (mCheckBox != null && mCheckBoxTitle != null) {
            mCheckBox.setVisibility(View.VISIBLE);
            mCheckBox.setText(mCheckBoxTitle);
        }
        return this;
    }

    public SweetAlertDialog setCustomImage(int resourceId) {
        return setCustomImage(getContext().getResources().getDrawable(resourceId));
    }

    public String getContentText() {
        return mContentText;
    }

    public SweetAlertDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(mContentText);
        }
        return this;
    }

    public SweetAlertDialog setContentHtmlText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(Html.fromHtml(mContentText));
        }
        return this;
    }

    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public boolean isShowConfirmButton() {
        return mShowConfirm;
    }

    public SweetAlertDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public SweetAlertDialog showConfirmButton(boolean isShow) {
        mShowConfirm = isShow;
        if (mConfirmButton != null) {
            mConfirmButton.setVisibility(mShowConfirm ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public boolean isShowContentText() {
        return mShowContent;
    }

    public SweetAlertDialog showContentText(boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public SweetAlertDialog setCancelText(String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            // if cancel button show , Confirm button set blod
            mConfirmButton.setTypeface(null, Typeface.BOLD);
            showCancelButton(true);
            mCancelButton.setText(Html.fromHtml(mCancelText));
        }
        return this;
    }

    public boolean CheckBoxIsChecked(View v) {
        //code to check if this checkbox is checked!
        if (v != null && v instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) v;

            return checkBox.isChecked();
        }

        return false;
    }

    public boolean getCheckBox() {
        return CheckBoxIsChecked(mCheckBox);
    }

    public String getConfirmText() {
        return mConfirmText;
    }

    public SweetAlertDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(Html.fromHtml(mConfirmText));
        } else if (mConfirmButton != null) {
            mConfirmButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCancelButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mCancelButton.setLayoutParams(params);
        }
        return this;
    }

    public SweetAlertDialog setUpdatetext(String text) {
        mUpdateText = text;
        if (mUpdateButton != null && mUpdateText != null) {
            mUpdateButton.setText(Html.fromHtml(mUpdateText));
        }
        return this;
    }

    public SweetAlertDialog setCancelClickListener(OnSweetClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public SweetAlertDialog setConfirmClickListener(OnSweetClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    public SweetAlertDialog setCustomClickListener(OnDialogListener listener) {
        mClickButtonArrListener = listener;
        return this;
    }

    public SweetAlertDialog setUpdateClickListener(OnSweetClickListener listener) {
        mUpdateClickListener = listener;
        return this;
    }

    protected void onStart() {
        if (mAlertType == SweetAlertDialog.PROGRESS_TYPE)
            return;

        mDialogView.startAnimation(mModalInAnim);
        playAnimation();
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mConfirmButton.startAnimation(mOverlayOutAnim);
        mDialogView.startAnimation(mModalOutAnim);
    }

    //set arr Button
    public SweetAlertDialog setArrButton(String... pArrButton) {
        mArrButton = pArrButton;

        if (mLayoutCustomButton != null && mArrButton != null) {
            try {
                mLayoutCustomButton.setVisibility(View.VISIBLE);
                int paddingTop = (int) getContext().getResources().getDimension(R.dimen.zpw_padding_6);
                int paddingRight = (int) getContext().getResources().getDimension(R.dimen.zpw_padding_12);
                int paddingLeft = (int) getContext().getResources().getDimension(R.dimen.zpw_padding_12);
                int paddingBottom = (int) getContext().getResources().getDimension(R.dimen.zpw_padding_6);
                for (int i = 0; i < mArrButton.length; i++) {
                    TextView tv = new TextView(getContext());
                    tv.setId(i);
                    tv.setText(mArrButton[i]);
                    tv.setBackgroundResource(backgroundResource);
                    tv.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                    tv.setTextColor(getContext().getResources().getColor(R.color.text_color_bold));
                    tv.setTextAppearance(getContext(), R.style.dialog_blue_button);
                    if (i == mArrButton.length - 1) {
                        tv.setTypeface(null, Typeface.BOLD);
                    } else {
                        tv.setTypeface(null, Typeface.NORMAL);

                    }

                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mClickButtonArrListener.onCloseDialog(SweetAlertDialog.this, view.getId());
                        }
                    });
                    mLayoutCustomButton.addView(tv);
                }
            } catch (Exception e) {
                Timber.d(e);
            }
        }
        if (mLayoutButton != null && mArrButton != null) {
            mLayoutButton.setVisibility(View.GONE);
        }

        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(SweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null && mAlertType != UPDATE) {
                mConfirmClickListener.onClick(SweetAlertDialog.this);
            } else if (mAlertType == UPDATE && mUpdateClickListener != null) {
                mUpdateClickListener.onClick(SweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.update_button) {
            if (mUpdateClickListener != null)
                mUpdateClickListener.onClick(SweetAlertDialog.this);
            else {
                dismissWithAnimation();
            }
        }
    }

    public interface OnSweetClickListener {
        public void onClick(SweetAlertDialog sweetAlertDialog);
    }
}