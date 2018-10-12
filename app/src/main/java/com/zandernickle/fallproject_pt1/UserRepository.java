package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

public class UserRepository {

    private UserDao mUserDao;
    private LiveData<User> mUser;
    private LiveData<Integer> count;

    UserRepository(Application application) {
        UserDatabase db = UserDatabase.getDatabase(application);
        mUserDao = db.userDao();
    }

    LiveData<User> getUser(int userId) {
        return mUserDao.getUser(userId);
    }

    // TODO: How?
//    int getCount() {
//        return new getCountAsyncTask(mUserDao).execute();
//    }

    public void insert(User user) {
        new InsertAsyncTask(mUserDao).execute(user);
    }

    private static class InsertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        InsertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            ReusableUtil.log("INSERTING USER " + users[0].getName());
            mAsyncTaskDao.insert(users[0]);
            return null;
        }
    }

//    private static class getCountAsyncTask extends AsyncTask<Void, Void, Integer> {
//
//        private UserDao mAsyncTaskDao;
//
//        getCountAsyncTask(UserDao dao) { mAsyncTaskDao = dao; }
//
//        @Override
//        protected Integer doInBackground(Void... voids) {
//            return mAsyncTaskDao.getCount();
//        }
//    }
}

