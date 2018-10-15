package com.zandernickle.fallproject_pt1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ReusableUtil {

    /**
     * Attempts to access the device's camera and return the resulting Bitmap when the user chooses
     * to take a photo. Must be called within a Fragment class.
     * <p>
     * Fails if camera access is denied.
     *
     * @param fragment    the Fragment from which to send the image capture intent.
     * @param requestCode the request code assigned to this intent.
     */
    public static void attemptImageCapture(Fragment fragment, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(intent, requestCode);
        }
        // TODO: Error message. Are there other causes of failure beside permissions?
    }

    /**
     * Returns the Bitmap from the onActivityResult callback. However, if the result code is
     * RESULT_CANCELLED, shows a default Toast message (unless otherwise indicated by the
     * errorMessage argument), and returns null.
     * <p>
     * Call this method from within the onActivityResult callback after checking for the
     * appropriate request code. Intended for use with the ReusableUtil.attemptImageCapture method
     * but applicable anywhere an image capture intent was sent.
     * <p>
     * The null return is useful for checking whether an image exists before starting a new
     * Activity, Fragment, or other task. Usually, the return value is stored as a member
     * variable within the Activity calling this method.
     *
     * @param activity     the Activity from which the image capture intent was sent.
     * @param data         the Intent expected to contain the Bitmap (passed from the onActivityResult
     *                     callback).
     * @param resultCode   the result code passed from the onActivityResult callback.
     * @param errorMessage an optional error message to display via Toast.
     * @return the Bitmap from the image capture, if it exists; null otherwise.
     * <p>
     * TODO: Update params docs (bitmap)
     */
    @Nullable
    public static Bitmap onImageCaptureResult(Activity activity, Intent data, Bitmap bitmap, int resultCode,
                                              @Nullable String errorMessage) {

        if (resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
        } else {

            /* TODO: Possible to distinguish the cause of RESULT_CANCELLED?
             *
             * RESULT_CANCELLED, according to the documentation, is returned whether the user opted
             * to cancel the action or a system error occurred. It would be nice to either avoid a
             * Toast or cater its message if the user simply chose to cancel the image capture.
             */
            String defaultError = "Oops. Something went wrong. Please try again.";
            errorMessage = errorMessage == null ? defaultError : errorMessage;
            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }

    /**
     * Compresses a Bitmap to a ByteArray and saves it to a Bundle. Intended for use with the
     * ReusableUtil.bitmapFromBundle method or where a ByteArray is to be decoded to a Bitmap
     * after extraction from the Bundle.
     *
     * @param bundle the Bundle object to which to save the compressed Bitmap.
     * @param bitmap the Bitmap to compress (convert to ByteArray) and save to the Bundle.
     * @param key    the Key to access the compressed Bitmap from the Bundle.
     */
    public static void bitmapToBundle(Bundle bundle, Bitmap bitmap, String key) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // The quality argument is functionally irrelevant for PNG files (loss-less compression).
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] compressedImage = outputStream.toByteArray();
        bundle.putByteArray(key, compressedImage);
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    /**
     * Extracts a compressed Bitmap from a Bundle. Intended for use with the
     * ReusableUtil.bitmapToBundle method or where the Bitmap is to be decoded from a ByteArray.
     * <p>
     * This method will fail if attempting to extract a Bitmap directly from the Bundle. The
     * Bitmap must have been saved to the Bundle as a ByteArray. If the Bundle does not contain
     * the specified key, the returned Bitmap will be null.
     *
     * @param bundle the Bundle object from which to extract the compressed Bitmap.
     * @param key    the Key to access the compressed Bitmap from the Bundle.
     * @return the Bitmap to decode and extract from the Bundle.
     */
    @Nullable
    public static Bitmap bitmapFromBundle(Bundle bundle, String key) {
        byte[] compressedImage = bundle.getByteArray(key);
        return BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.length);
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
    public static void setTextChangedListeners(TextWatcher textWatcher, EditText... editTexts) {
        for (EditText e : editTexts) {
            e.addTextChangedListener(textWatcher);
        }
    }

    /**
     * Initializes SeekBar.OnSeekBarChangeListeners. Use this method to add any additional
     * SeekBarChangeListeners.
     */
    public static void setOnSeekBarChangeListeners(SeekBar.OnSeekBarChangeListener listener, SeekBar... seekBars) {
        for (SeekBar s : seekBars) {
            s.setOnSeekBarChangeListener(listener);
        }
    }

    public static void setOnItemClickListeners(AdapterView.OnItemClickListener listener, AdapterView<?>... adapterViews) {
        for (AdapterView v : adapterViews) {
            v.setOnItemClickListener(listener);
        }
    }

    public static void setOnItemSelectedListeners(AdapterView.OnItemSelectedListener listener, AdapterView<?>... adapterViews) {
        for (AdapterView v : adapterViews) {
            v.setOnItemSelectedListener(listener);
        }
    }

    /**
     * Disables an active TextInputLayout error. Intended to be called from the
     * TextWatcher.onTextChanged callback.
     * <p>
     * If used as intended, guarantees any active TextInputLayout passed as an argument will have
     * its error disabled.
     *
     * @param hashCode the hash code of the active EditText whose text has been changed (passed from
     *                 TextWatcher.onTextChanged).
     * @param layouts  any potentially active TextInputLayouts whose error state might be set to
     *                 true (in most cases, all of them).
     */
    public static void disableTILErrorOnTextChanged(int hashCode, CustomTextInputLayout... layouts) {
        for (CustomTextInputLayout l : layouts) {
            if (l.isErrorEnabled() && l.textHashMatches(hashCode)) {
                l.setErrorEnabled(false);
            }
        }
    }

    /**
     * Replaces a View with another Fragment.
     *
     * @param fragmentManager   the FragmentManager used to manipulate the Views.
     * @param replaceableViewId the id of the View to be replaced.
     * @param fragmentToLoad    the Fragment to display.
     * @param tag               the tag to uniquely identify fragmentToLoad.
     * @param addToBackStack    whether to add fragmentToLoad to the Back Stack.
     */
    public static void loadFragment(FragmentManager fragmentManager, int replaceableViewId, Fragment fragmentToLoad, @Nullable String tag, boolean addToBackStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(replaceableViewId, fragmentToLoad, tag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public static void loadDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment, Fragment targetFragment, @Nullable Bundle arguments, @Nullable String tag, boolean addToBackStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment previousInstance = fragmentManager.findFragmentByTag(tag);
        if (previousInstance != null) {
            // ensure no other instances are visible
            transaction.remove(previousInstance);
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        if (arguments != null) {
            dialogFragment.setArguments(arguments);
        }
        dialogFragment.setTargetFragment(targetFragment, 0);
        dialogFragment.show(transaction, tag);
    }

    public static HashMap<Integer, TextInputLayout> mapTextInputLayouts(TextInputLayout... layouts) {
        HashMap<Integer, TextInputLayout> map = new HashMap<>();
        for (TextInputLayout l : layouts) {
            map.put(l.getId(), l);
        }
        return map;
    }


    /**
     * Loads an instance of MenuBarFragment. If the device is a tablet (larger than the specified
     * size as saved to the values/layout resources), the MenuBarTabletFragment (a subclass of
     * MenuBarFragment) is automatically loaded.
     *
     * @param fragment      the FragmentActivity from which to load the MenuBarFragment. This
     *                      will be the Fragment whose xml contains the placeholder.
     * @param arguments     the bundle containing the data to be passed to the MenuBarFragment. This
     *                      bundle should contain a boolean specifying whether the device is a tablet.
     * @param replaceableId the id of the xml element to be replaced by the MenuBarFragment.
     */
    public static void loadMenuBarFragment(Fragment fragment, Bundle arguments, int replaceableId) {

        boolean isTablet = arguments.getBoolean(Key.IS_TABLET);
        Fragment menuBarFragment = isTablet ? new MenuBarTabletFragment() : new MenuBarFragment();

        menuBarFragment.setArguments(arguments);
        loadFragment(fragment.getChildFragmentManager(), replaceableId, menuBarFragment, Key.MENU_BAR_FRAGMENT, false);
    }

    /**
     * A wrapper for the system-wide logger. Can be useful for some bug squishing.
     *
     * @param message the message to display in the logger.
     */
    public static void log(String message) {
        Log.d("Log", message);
    }

    /**
     * A wrapper for android.Widget.Toast. Useful for rendering messages to the emulator
     * screen such as when a button is clicked but no app functionality has yet been attached
     * to that button.
     *
     * @param context the current Context.
     * @param message the message to display on the device screen.
     */
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // Temporary
    public static void setPortraitOnly(Activity hostActivity, boolean isTablet) {
        /* TODO
         *
         * This is a potentially temporary fix for both sign-in and fitness input. There is too much
         * data on each screen to effectively present in landscape mode. The on-boarding may
         * need a redesign before removing this statement.
         */
        if (isTablet) {
            hostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        } else {
            hostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

}
