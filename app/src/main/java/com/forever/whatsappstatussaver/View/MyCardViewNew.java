package com.forever.whatsappstatussaver.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class MyCardViewNew extends FrameLayout {
    private static final String TAG = MyCardViewNew.class.getSimpleName();
    private double aspectRatio = 1.0; //3.5/2  //For Portrait : 0.57
    private float basicWidth;
    private float basicHeight;

    private RestartActivity restartActivity;

    private boolean isCalledRecreate = true;
//    private boolean isRightCase = true;

    public MyCardViewNew(Context context) {
        super(context);
//        ObLogger.i(TAG, "MyCardViewNew: 1");
    }

    public MyCardViewNew(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

//        int random = AdvertiseHandlerNEW.randInt(1, 3);
//        if (random == 3) {
//            isRightCase = true;
//        } else {
//            isRightCase = false;
//        }
//        ObLogger.i(TAG, "MyCardViewNew: 2");
    }

    public MyCardViewNew(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        double localRatio = aspectRatio;

        if (localRatio == 0.0) {
            super.onMeasure(widthSpec, heightSpec);

        } else {
            int lockedWidth = MeasureSpec.getSize(widthSpec);
            int lockedHeight = MeasureSpec.getSize(heightSpec);

            // todo issue generated
//            int lockedWidth = 0;
//            int lockedHeight = 0;
//
//
//            if (isRightCase) {
//                lockedWidth = 0;
//                lockedHeight = 0;
//            } else {
//                lockedWidth = MeasureSpec.getSize(widthSpec);
//                lockedHeight = MeasureSpec.getSize(heightSpec);
//            }

            if (lockedWidth == 0 && lockedHeight == 0) {

                widthSpec = MeasureSpec.makeMeasureSpec((int) (basicWidth), MeasureSpec.AT_MOST);
                heightSpec = MeasureSpec.makeMeasureSpec((int) (basicHeight), MeasureSpec.AT_MOST);

                lockedWidth = MeasureSpec.getSize(widthSpec);
                lockedHeight = MeasureSpec.getSize(heightSpec);

                callForRecreate();
            }

            // Get the padding of the border background.
            int hPadding = getPaddingLeft() + getPaddingRight();
            int vPadding = getPaddingTop() + getPaddingBottom();

            // Resize the preview frame with correct aspect ratio.
            lockedWidth -= hPadding;
            lockedHeight -= vPadding;

            if (lockedHeight > 0 && (lockedWidth > lockedHeight * localRatio)) {
                lockedWidth = (int) (lockedHeight * localRatio + .5);
            } else {
                lockedHeight = (int) (lockedWidth / localRatio + .5);
            }

            // Add the padding of the border.
            lockedWidth += hPadding;
            lockedHeight += vPadding;

            // Ask children to follow the new preview dimension.
            super.onMeasure(MeasureSpec.makeMeasureSpec(lockedWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(lockedHeight, MeasureSpec.EXACTLY));

        }
    }

    public void setRestartActivity(RestartActivity restartActivity) {
        this.restartActivity = restartActivity;
    }

    public void setCollageViewRatio(float aspectRatio, float basicWidth, float basicHeight) {
//        if (basicHeight > 0 && basicWidth > 0) {
//            this.basicWidth = basicWidth;
//            this.basicHeight = basicHeight;
//        }
        try {
            if (aspectRatio <= 0.0 || basicWidth <= 0.0 || basicHeight <= 0.0) {
                throw new IllegalArgumentException(
                        "aspect ratio must be positive");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        }


        this.basicWidth = basicWidth;
        this.basicHeight = basicHeight;

        if (this.aspectRatio != aspectRatio) {
            this.aspectRatio = aspectRatio;
            requestLayout();
        }
    }

    private void callForRecreate() {

        if (isCalledRecreate) {
            if (restartActivity != null) {
                isCalledRecreate = false;
                restartActivity.onRestartActivity();

//                AppUtils.throwFatalException(new Throwable("Measure issue Resolution : call Recreate Editor."));
            }
        }

    }

    public interface RestartActivity {
        void onRestartActivity();
    }
}