package com.zandernickle.fallproject_pt1;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.neovisionaries.i18n.CountryCode;

public class TypeConverterUtil {

    public static class CountryCodeTypeConverter {

        @TypeConverter
        public static String toString(CountryCode countryCode) {
            return countryCode == null ? null : countryCode.getAlpha2();
        }

        @TypeConverter
        public static CountryCode toCountryCode(String alpha2) {
            return alpha2 == null ? null : CountryCode.getByCode(alpha2);
        }
    }

    public static class SexTypeConverter {

        @TypeConverter
        public static String toString(Sex sex) {
            return sex == null ? null : sex.toString();
        }

        @TypeConverter
        public static Sex toSex(String name) {
            return name == null ? null : Sex.valueOf(name);
        }
    }

    public static class ActivityLevelTypeConverter {

        @TypeConverter
        public static String toString(ActivityLevel activityLevel) {
            return activityLevel == null ? null : activityLevel.toString();
        }

        @TypeConverter
        public static ActivityLevel toActivityLevel(String name) {
            return name == null ? null : ActivityLevel.valueOf(name);
        }
    }


}
