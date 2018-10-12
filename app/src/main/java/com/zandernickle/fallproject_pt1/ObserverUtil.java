package com.zandernickle.fallproject_pt1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.EditText;
import android.widget.Spinner;

public class ObserverUtil {

    public static class EditTextObserver implements Observer<String> {

        private EditText editText;

        public EditTextObserver(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onChanged(@Nullable String s) {
            editText.setText(s);
        }
    }

    public static class SpinnerObserver implements Observer<Integer> {

        private AppCompatSpinner spinner;

        public SpinnerObserver(AppCompatSpinner spinner) {
            this.spinner = spinner;
        }

        @Override
        public void onChanged(@Nullable Integer index) {
            spinner.setSelection(index);
        }
    }

}
