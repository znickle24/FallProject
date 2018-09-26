package com.zandernickle.fallproject_pt1;

import com.neovisionaries.i18n.CountryCode;

import java.util.ArrayList;

/**
 * Use this class to create and/or retrieve data for inclusion in Spinner Views.
 */
public class SpinnerUtil {

    /**
     * Returns an array of ages represented by integers.
     *
     * The first item in the array is the Spinner label.
     *
     * @param minAge the minimum age to show in the Spinner dropdown.
     * @param maxAge the maximum age to show in the Spinner dropdown.
     * @return the array of ages.
     */
    public static String[] getSpinnerAgeData(int minAge, int maxAge) {

        /* The age range is inclusive, as defined by 'new String(maxAge - minAge + 1)'. However,
         * an additional index is appended to support the label text. This is represented by the
         * '+2'. The additional arithmetic in the for-loop and the array access is also due to the
         * inclusion of the label.
         */
        String[] ages = new String[maxAge - minAge + 2]; // Inclusive + additional index for label.
        ages[0] = Key.AGE_SPINNER_LABEL;

        for (int i = 1; i <= maxAge - minAge + 1; i++) {
            ages[i] = Integer.toString(i + minAge - 1); // Min to max inclusive.
        }

        return ages;
    }

    /**
     * Returns an array of 2-letter country codes (ISO 3166-1 alpha-2). Supported by the
     * nv-i18n internationalization package.
     *
     * The first item in the array is the Spinner label.
     *
     * See the GitHub source here: https://github.com/TakahikoKawasaki/nv-i18n
     * Find the JavaDoc here: http://takahikokawasaki.github.io/nv-i18n/
     *
     * @return the array of country codes.
     */
    public static String[] getSpinnerCountryData() {

        ArrayList<String> countries = new ArrayList<>();
        countries.add(Key.COUNTRY_SPINNER_LABEL);

        for (CountryCode code : CountryCode.values()) {

            /* TODO: Why is the first item "UNDEFINED"?
             *
             * See the JavaDoc at http://takahikokawasaki.github.io/nv-i18n/.
             */
            if (code.getAlpha2() != "UNDEFINED") {
                countries.add(code.getAlpha2());
            }
        }

        return countries.toArray(new String[countries.size()]);
    }
}
