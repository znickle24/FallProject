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

    /**
     * Returns the child EditText's Text as a String.
     */
    public String getTextString() {
        return getEditText().getText().toString();
    }

    /**
     * Displays the provided error message.
     */
    public void showError(String message) {
        setError(message);
        setErrorEnabled(true);
    }

    /**
     * Returns true if the hash code of the child EditText's Text object matches that of the
     * argument.
     */
    public boolean textHashMatches(int hashCode) {
        return hashCode == getEditText().getText().hashCode();
    }
}
