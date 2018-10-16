package com.zandernickle.fallproject_pt1;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;


@Database(entities = {User.class, ActiveUser.class}, version = 1)
@TypeConverters({TypeConverterUtil.CountryCodeTypeConverter.class, TypeConverterUtil.SexTypeConverter.class,
        TypeConverterUtil.ActivityLevelTypeConverter.class})
public abstract class UserDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    private static volatile UserDatabase INSTANCE;

    static UserDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    ReusableUtil.log("INSTANTIATING DATABASE");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserDatabase.class, "user_database").addCallback(userDataBaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static UserDatabase.Callback userDataBaseCallback = new UserDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            ReusableUtil.log("ONOPEN SUCCESSFULLY CALLED");
//            new PopulateDbAsync(INSTANCE).execute();
        }
    };

//    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
//
//        private final UserDao mDao;
//
//        PopulateDbAsync(UserDatabase db) {
//            mDao = db.userDao();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            User user = new User("User1", new byte[] {'c'}, 0, 0, CountryCode.US);
//            ReusableUtil.log("INSERTING USER: " + user.getName());
//            mDao.insert(user);
//            return null;
//        }
//    }

}
