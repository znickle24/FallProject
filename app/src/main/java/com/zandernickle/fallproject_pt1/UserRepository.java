package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import static com.zandernickle.fallproject_pt1.ReusableUtil.log;

public class UserRepository {

    private UserDao mUserDao;

    private LiveData<Integer> count;
    private LiveData<List<User>> allUsers;
    private LiveData<User> mUser;


    UserRepository(Application application) {
        UserDatabase db = UserDatabase.getDatabase(application);
        mUserDao = db.userDao();
    }

    public LiveData<Integer> getCount() {
        return mUserDao.getCount();
    }

    public LiveData<List<User>> getAllUsers() {
        return mUserDao.getAllUsers();
    }

    public LiveData<User> getUser(int id) {
        return mUserDao.getUser(id);
    }

    public long addUser(User user) {
        new AddUserAsyncTask(mUserDao).execute(user);
        log("THIS USER's ID: " + user.getId());
        return user.getId();
    }

    public void updateUserFitness(User user) {
        new UpdateUserFitnessAsyncTask(mUserDao).execute(user);
    }

    public void updateUserHealth(User user) {
        new UpdateUserHealthAsyncTask(mUserDao).execute(user);
    }

    private static class UpdateUserHealthAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        UpdateUserHealthAsyncTask(UserDao dao) { mAsyncTaskDao = dao; }

        @Override
        protected Void doInBackground(User... users) {
            log("UPDATING USER: " + users[0].getName());
            User user = users[0];
            mAsyncTaskDao.updateUserHealth(user.getId(), user.getBMI(), user.getBMR(), user.getCalorieIntake());
            return null;
        }
    }

    private static class UpdateUserFitnessAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        UpdateUserFitnessAsyncTask(UserDao dao) { mAsyncTaskDao = dao; }

        @Override
        protected Void doInBackground(User... users) {
            log("UPDATING USER: " + users[0].getName());
            User user = users[0];
            mAsyncTaskDao.updateUserFitness(user.getId(), user.getSex(), user.getActivityLevel(), user.getHeight(), user.getWeight(), user.getWeightGoal());
            return null;
        }
    }


    /* TODO
     * Return the user's id from the database (long).
     */
    private static class AddUserAsyncTask extends AsyncTask<User, Void, Long> {

        private UserDao mAsyncTaskDao;

        AddUserAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(User... users) {
            log("INSERTING USER " + users[0].getName());
            return mAsyncTaskDao.insert(users[0]);
        }

    }

    //    public long add(User user) {
//////        new InsertAsyncTask(mUserDao).execute(user);
////        InsertAsyncTask task = new InsertAsyncTask(mUserDao);
////        return task.execute(user);
////    }

//    public LiveData<Long> addUser(User user) {
//        new AddUserAsyncTask(mUserDao).execute(user);
//    }

//    private static class AddUserAsyncTask extends AsyncTask<User, Void, Long> {
//
//        private UserDao userDao;
//
//        AddUserAsyncTask(UserDao dao) {
//            userDao = dao;
//        }
//
//        @Override
//        protected Long doInBackground(User... users) {
//            ReusableUtil.log("INSERTING USER " + users[0].getName());
//            return userDao.insert(users[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Long aLong) {
//            super.onPostExecute(aLong);
//            if (aLong != null) { // TODO: Necessary?
//                userId = aLong;
//            }
//        }
//
//    }

//    private void loadData(){
//        new AsyncTask<String,Void,String>(){
//            @Override
//            protected String doInBackground(String... strings) {
//                String location = strings[0];
//                URL weatherDataURL = null;
//                String retrievedJsonData = null;
//                if(location!=null) {
//                    weatherDataURL = Network.buildURLFromString(location);
//                    try {
//                        retrievedJsonData = Network.getDataFromURL(weatherDataURL);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return retrievedJsonData;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                if(s != null) {
//                    try {
//                        jsonData.setValue(JSONWeather.getWeatherData(s));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }.execute(mLocation);
//    }

}

