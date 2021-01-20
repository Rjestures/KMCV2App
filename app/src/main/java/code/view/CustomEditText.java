package code.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

import com.kmcapp.android.R;


public class CustomEditText extends AppCompatEditText {
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            setCustomFont(context, attrs);
        }
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
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
            Log.e("ContentValues", "Could not get typeface:" + Log.getStackTraceString(e));
        }
        if (tf != null) {
            setTypeface(tf);
        }
    }
}
