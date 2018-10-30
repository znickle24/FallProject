package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import static com.zandernickle.fallproject_pt1.Key.SIGN_IN_FRAGMENT;
import static com.zandernickle.fallproject_pt1.ReusableUtil.loadFragment;
import static com.zandernickle.fallproject_pt1.ReusableUtil.log;

/**
 * This application's entry point. Use this class to pass data from one Fragment (or Activity) to the next. Register
 * additional modules in ModuleUtil.
 */
public class MainActivity extends CustomAppCompatActivity implements SignInFragment.OnDataPass,
        FitnessInputFragment.OnDataPass, MenuBarFragment.OnDataPass, RVAdapter.OnDataPass, BMRFragment.OnDataPass {

    private static final String AWS_ACCESS_ID = "AKIAJO4MY562M732ZGFA";
    private static final String AWS_SECRET_KEY = "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";
    private boolean mIsDownloadComplete = false;
    private String mDbPath;

    private boolean mHasEncounteredUploadError = false;

    private static final String PREV_FRAGMENT_TAG = "PREV_FRAGMENT_TAG";
    private static final HashMap<Module, Class<?>> mMappedModules = ModuleUtil.mapModuleList();

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private String mPrevFragmentTag;
    private Bundle mBundle;

    private User mUser;
    private UserRepository mUserRepo;

    private SensorManager mSensorManager;
    private Sensor mStepCounter;
    private int mSteps;

    //Sensor for custom gesture
    private Sensor mLinearAccelerometer;
    private final double mThreshold = 2.0;

    //Previous positions along x, y, and z axes - for custom gesture (shake)
    private double mPrevX, mPrevY, mPrevZ;

    //Current positions along x, y, and z axes - for custom gesture (shake)
    private double mCurrX, mCurrY,mCurrZ;

    //Used to detect custom gesture
    private boolean mNotFirstTime;

    //Flag to determine if step counter should be on or off with custom gesture
    private boolean mStepCountOn;

    private void loadModule() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AWSMobileClient.getInstance().initialize(this).execute();
        mDbPath = UserDatabase.getDatabase(this).getOpenHelper().getWritableDatabase().getPath();

        mUserRepo = new UserRepository(MainActivity.this.getApplication()); // instantiate here important!
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Get linear acceleration sensor for custom gesture (shake)
        mLinearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        boolean isTablet = isTablet();
        int viewId = isTablet ? R.id.fl_fragment_placeholder_tablet_right :
                R.id.fl_fragment_placeholder_phone;

        if (savedInstanceState == null) {

            // download backed-up file here?

            mBundle = new Bundle();
            mBundle.putBoolean(Key.IS_TABLET, isTablet);

            // get the most recent database file if an internet connection exists.
//            downloadFromCloud();

           /*
            * This is a hack of a solution but a placeholder for when a download functionality is implemented.
            */
//            do {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace(); // Just continue on with the most recent local copy.
//                }
//            } while (!mIsDownloadComplete); // see downloadFromCloud implementation.

            int activeUserId = mUserRepo.getActiveUserId();

            if (activeUserId != UserRepository.NON_EXISTENT_ID) {
                mUser = mUserRepo.getUserSync(activeUserId);
                mBundle.putSerializable(Key.MODULE, Module.HEALTH);
                mBundle.putParcelable(Key.USER, mUser);

                if (isTablet()) { // Sets up MasterView on the left for the remainder of the experience
                    Fragment masterListFragment = new MasterListFragment();
                    masterListFragment.setArguments(mBundle);
                    loadFragment(mFragmentManager, R.id.fl_fragment_placeholder_tablet_left,
                            masterListFragment, "TEST", false);
                }

                mPrevFragmentTag = Module.HEALTH.toString();
                Fragment bmrFragment = new BMRFragment();
                bmrFragment.setArguments(mBundle);
                loadFragment(mFragmentManager, viewId, bmrFragment, mPrevFragmentTag, false);

            } else {

                mPrevFragmentTag = SIGN_IN_FRAGMENT; // TODO: change this name
                Fragment signInFragment = new SignInFragment();
                signInFragment.setArguments(mBundle);
                loadFragment(mFragmentManager, viewId, signInFragment, mPrevFragmentTag, false);
            }

        } else {

            mBundle = savedInstanceState.getBundle(Key.BUNDLE);
            mUser = savedInstanceState.getParcelable(Key.USER);

            //booleans used for custom gesture (shake) and step counter
            mStepCountOn = savedInstanceState.getBoolean("StepCounter");
            mSteps = savedInstanceState.getInt("StepCount");

            mPrevFragmentTag = savedInstanceState.getString(PREV_FRAGMENT_TAG);
            Fragment currentFragment = mFragmentManager.findFragmentByTag(mPrevFragmentTag);
            loadFragment(mFragmentManager, viewId, currentFragment, mPrevFragmentTag, false);

        }
    }

//    private void toast() {
//        ReusableUtil.toast(this, "SUCCESSFULL UPLOAD");
//    }

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            //PRINTING FOR DEBUGGING
            if(mStepCountOn) {
                Log.d("mStepCountOn is TRUE: ","STEP COUNTER ON");
            } else if(!mStepCountOn) {
                Log.d("mStepCountOn is FALSE: ", "STEP COUNTER OFF");
            }

            //Get the acceleration rates along the x, y, and z axes
            mCurrX = sensorEvent.values[0];
            mCurrY = sensorEvent.values[1];
            mCurrZ = sensorEvent.values[2];

            if(mNotFirstTime){
                double dx = Math.abs(mPrevX - mCurrX);
                double dy = Math.abs(mPrevY - mCurrY);
                double dz = Math.abs(mPrevZ - mCurrZ);

                //Check if the values of acceleration have changed on any pair of axes
                if((dx > mThreshold && dy > mThreshold) ||
                        (dx > mThreshold && dz > mThreshold)||
                        (dy > mThreshold && dz > mThreshold)) {

                    //toggle mStepCountOn boolean when custom gesture (shake) is activated
                    if(mStepCountOn) {
                        mStepCountOn = false;
                        if (mSensorListener != null) {
                            //unregister step counter to turn it off
                            mSensorManager.unregisterListener(mSensorListener);
                        }
                        int currentSteps = (int) sensorEvent.values[0];
                        if(currentSteps < 0) {
                            currentSteps = currentSteps * -1;
                        }
                        mSteps += currentSteps; //add to step total
                        Log.d("mSteps: ", Integer.toString(mSteps)); //printing for testing

                    } else if(!mStepCountOn) {
                        mStepCountOn = true;
                        if (mSensorListener != null) {
                            //register step counter to turn it on
                            mSensorManager.registerListener(mSensorListener, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
                        }
                    }

                }
            }
            mPrevX = mCurrX;
            mPrevY = mCurrY;
            mPrevZ = mCurrZ;
            mNotFirstTime = true;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        backupToCloud();

        if (mSensorListener != null && mUser != null) {
            mSensorManager.unregisterListener(mSensorListener);
            mUser.setSteps(mSteps);
            mUserRepo.updateUserAsync(mUser);
        }
        //for custom gesture
        if(mLinearAccelerometer!=null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mSensorListener != null) {
//            mSensorManager.registerListener(mSensorListener, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
//        }
        //for custom gesture
        if(mLinearAccelerometer!=null){
            mSensorManager.registerListener(mSensorListener, mLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // TODO: Why won't the code in onCreate work here?
        // There are no errors but no Fragment is rendered (blank screen).
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle(Key.BUNDLE, mBundle);
        outState.putParcelable(Key.USER, mUser);
        outState.putString(PREV_FRAGMENT_TAG, mPrevFragmentTag);
        outState.putBoolean("StepCounter", mStepCountOn);
        outState.putInt("StepCount", mSteps);

    }

    @Override
    public void onDataPass(Module moduleToLoad, Bundle bundle) {

        // TODO: Decide how often to update the database. // onPause?

        /*
         * For purposes of extensibility, all data is stored in a single Bundle. This Bundle is passed from one
         * module to the next, each extracting only the data it requires. Any Bundles passed into this interface
         * are added to this "global bundle".
         */

        if (bundle != null) {
            /* Some modules will return a null Bundle. For example, MenuBarFragment only passes the Module which
             * should next be loaded.
             */
            mBundle.putAll(bundle);
        }

        // Pass the name of the module to the MenuBarFragment (where implemented).
        mBundle.putSerializable(Key.MODULE, moduleToLoad);

        Fragment prevFragment = mFragmentManager.findFragmentByTag(mPrevFragmentTag);
        Fragment nextFragment = (Fragment) getNextModuleView(moduleToLoad);

        // Ensure this statement is made AFTER finding the previous Fragment.
        mPrevFragmentTag = moduleToLoad.toString();

        switch (moduleToLoad) {

            case SIGN_IN:

                backupToCloud(); // The user is about to log out.
                break;

            case FITNESS_INPUT:

                // SignInFragment -> MainActivity -> FitnessInputFragment

                int activeUserId = mUserRepo.getActiveUserId();
                User user = mBundle.getParcelable(Key.USER);
                backupToCloud(); // updated fitness data

                break;

            case HEALTH:

                backupToCloud(); // updated fitness data.

                /* TODO
                 *
                 * This is a temporary line of code corresponding with the temporary code inside the
                 * SignIn and FitnessInput Fragments. See Reusable.setPortraitOnly.
                 *
                 * Only the first two sign in screens have their orientation locked (sign in data). All
                 * others will be unlocked here.
                 */
                MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                // FitnessInputFragment -> MainActivity -> BMRFragment (health module)

                /*
                 * The user's BMI and BMR data have yet to be added to the current User. This should occur
                 * when this module returns its Bundle.
                 */

                if (isTablet()) { // Sets up MasterView on the left for the remainder of the experience
                    Fragment masterListFragment = new MasterListFragment();
                    masterListFragment.setArguments(mBundle);
                    loadFragment(mFragmentManager, R.id.fl_fragment_placeholder_tablet_left,
                            masterListFragment, "TEST", false);
                }

                break;

            case MASTER_LIST:
                break;

            case HIKES:
                break;

            case WEATHER:
                break;

            default:
                break;

        }

        if (mHasEncounteredUploadError) {
            // Try again
            // This will probably be too often for a network disconnected device but for now will suffice.
            backupToCloud();
        }

        nextFragment.setArguments(mBundle);
        loadFragment(mFragmentManager, prevFragment.getId(), nextFragment, mPrevFragmentTag, false);
    }

    /**
     * Returns the Fragment associated with the provided Module. In other words, pass a Module
     * and get back an instantiated Fragment representing that model.
     *
     * Important! The module being passed must exist in mMappedModules. It must also be a
     * Fragment. This is an important distinction for future extensions where a
     *
     *
     * ensure the module you are passing is a fragment, not an activity (extensibility)
     *
     * @param moduleToLoad
     * @return
     */
    @NonNull
    private Object getNextModuleView(Module moduleToLoad) {

        Object nextModuleView = null;
        try {
            Class<?> moduleClass = mMappedModules.get(moduleToLoad);
            Constructor<?> constructor = Class.forName(moduleClass.getName()).getConstructor();
            nextModuleView = constructor.newInstance();
        } catch (Exception e) {
            // This is a very useful log when adding additional modules.
            log("Failed to retrieve next loadable fragment: " + moduleToLoad.toString());
            e.printStackTrace();
        }
        return nextModuleView; // Don't let this be null... you'll crash in onDataPass.
    }

    private void backupToCloud() {

        BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_ID, AWS_SECRET_KEY);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        TransferObserver uploadObserver =
                transferUtility.upload("jsaS3/" + mDbPath, new File(mDbPath));

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Make sure to fix the error flag once a successful upload has occurred.
                    mHasEncounteredUploadError = false;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                // Do nothing.
            }

            @Override
            public void onError(int id, Exception ex) {
                // This flag will signal to attempt another upload before onPause.
                mHasEncounteredUploadError = true;
            }

        });
    }

    private void downloadFromCloud() {

        mIsDownloadComplete = false;

        BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_ID, AWS_SECRET_KEY);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        TransferObserver downloadObserver =
                transferUtility.download("jsaS3/" + mDbPath, new File(mDbPath));

        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    mIsDownloadComplete = true;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                // Not implemented.
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });
    }
}

