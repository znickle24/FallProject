package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

/**
 * A custom implementation of TextInputLayout. Extending TextInputLayout, rather than creating a
 * few methods in a utility class, provides better readability, ease of access, and most
 * importantly, extensibility for future features.
 */
public class CustomTextInputLayout extends TextInputLayout {

    public CustomTextInputLayout(Context context) {
        super(context);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getTextString() {
        return getEditText().getText().toString();
    }

    public void showError(String message) {
        setError(message);
        setErrorEnabled(true);
    }

    public boolean textHashMatches(int hashCode) {
        return hashCode == getEditText().getText().hashCode();
    }
}
