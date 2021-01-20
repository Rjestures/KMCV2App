package code.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.kmcapp.android.R;


public class CustomTextView extends TextView {
    private static final String TAG = "CustomTextView";
    private CharSequence originalText = "";
    private float spacing = 0.0f;

    public class Spacing {
        public static final float NORMAL = 0.0f;
    }

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setCustomFont(context, attrs);
        }
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            setCustomFont(context, attrs);
        }
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray mTypedArray = ctx.obtainStyledAttributes(attrs, R.styleable.CustomViewStyle);
        String customFont = mTypedArray.getString(0);
        if (customFont != null) {
            setCustomFont(ctx, customFont);
        }
        mTypedArray.recycle();
    }

    public void setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface:" + Log.getStackTraceString(e));
        }
        if (tf != null) {
            setTypeface(tf);
        }
    }

    public float getSpacing() {
        return this.spacing;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
        applySpacing();
    }

    public void setText(CharSequence text, BufferType type) {
        this.originalText = text;
        applySpacing();
    }

    public CharSequence getText() {
        return this.originalText;
    }

    private void applySpacing() {
        if (this != null && this.originalText != null) {
            int i;
            StringBuilder builder = new StringBuilder();
            for (i = 0; i < this.originalText.length(); i++) {
                builder.append(this.originalText.charAt(i));
                if (i + 1 < this.originalText.length()) {
                    builder.append(" ");
                }
            }
            SpannableString finalText = new SpannableString(builder.toString());
            if (builder.toString().length() > 1) {
                for (i = 1; i < builder.toString().length(); i += 2) {
                    finalText.setSpan(new ScaleXSpan((this.spacing + 1.0f) / 10.0f), i, i + 1, 33);
                }
            }
            super.setText(finalText, BufferType.SPANNABLE);
        }
    }
}
