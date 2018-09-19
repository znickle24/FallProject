package com.zandernickle.fallproject_pt1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

public class ReusableUtil {

    public static void attemptImageCapture(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    @Nullable
    public static Bitmap onImageCaptureResult(Activity activity, Intent data, int requestCode,
                                              int resultCode, @Nullable String errorMessage) {

        Bitmap image = null;
        if (requestCode == Key.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            image = (Bitmap) data.getExtras().get("data");
        } else {
            String defaultError = "Oops. Something went wrong. Please try again.";
            errorMessage = errorMessage == null ? defaultError : errorMessage;
            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
        }
        return image;
    }

    public static void bitmapToBundle(Bundle outState, Bitmap bitmap, String key) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] compressedImage = outputStream.toByteArray();
        outState.putByteArray(Key.PROFILE_IMAGE, compressedImage);
    }

    /**
     * Initializes clickable Views. Use this method to add any additional View.OnClickListeners.
     */
    public static void setOnClickListeners(View.OnClickListener listener, View... views) {
        for (View v : views) {
            v.setOnClickListener(listener);
        }
    }

    /**
     * Initializes EditText TextWatcher.TextChangeListeners. Use this method to add any additional
     * TextChangeListeners.
     */
    public void setTextChangedListeners(TextWatcher textWatcher, EditText... editTexts) {
        for (EditText e : editTexts) {
            e.addTextChangedListener(textWatcher);
        }
    }

}
