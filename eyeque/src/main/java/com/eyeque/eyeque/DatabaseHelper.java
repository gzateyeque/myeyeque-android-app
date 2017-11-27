package com.eyeque.eyeque;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;

/**
 *
 * File:            DashboardFragment.java
 * Description:     Database helper class
 * Created:         2016/05/02
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    /**
     * Instantiates a new Database helper.
     *
     * @param context the context
     * @throws IOException the io exception
     */
    public DatabaseHelper(Context context) throws IOException {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    // Called only first we create the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = String.format("create table if not exists %s (%s int primary key, " +
                        // "%s int, %s text, %s text, %s text, %s int, %s text, %s int, %s int, %s int)",
                        "%s int, %s text)",
                Constants.USER_ENTITY_TABLE,
                Constants.USER_ENTITY_ID_COLUMN,
                Constants.USER_ENTITY_VERSION_COLUMN,
                // Constants.USER_ENTITY_EMAIL_COLUMN,
                Constants.USER_ENTITY_TOKEN_COLUMN);
                // Constants.USER_ENTITY_NAME_COLUMN,
                // Constants.USER_ENTITY_GENDER_COLUMN,
                // Constants.USER_ENTITY_DEVICE_SERIAL_COLUMN,
                // Constants.USER_ENTITY_SIGNUP_STATUS,
                // Constants.USER_ENTITY_CREATED_AT_COLUMN,
                // Constants.USER_ENTITY_LAST_SYNCED_AT_COLUMN);
        Log.d(TAG, "create user_entity table");
        db.execSQL(sql);

        /******
        String sql = String.format("create table if not exists %s (%s int primary key, " +
                        "%s int, %s text, %s text, %s text, %s int, %s int, %s int)",
                Constants.TEST_SUBJECT_TABLE,
                Constants.TEST_SUBJECT_ID_COLUMN,
                Constants.TEST_SUBJECT_VERSION_COLUMN,
                Constants.TEST_SUBJECT_FIRST_NAME_COLUMN,
                Constants.TEST_SUBJECT_LAST_NAME_COLUMN,
                Constants.TEST_SUBJECT_EMAIL_COLUMN,
                Constants.TEST_SUBJECT_CREATED_AT_COLUMN,
                Constants.TEST_SUBJECT_LAST_SYNCED_AT_COLUMN,
                Constants.TEST_SUBJECT_STATUS);
        Log.d(TAG, "create test_subject table");
        db.execSQL(sql);

         sql = String.format("create table if not exists %s (%s int primary key, " +
                    "%s int, %s int, %s int, %s text, %s text, %s double, %s double, " +
                    "%s double, %s double, %s double, %s double, %s double, %s double, " +
                    "%s double, %s double, %s int, %s int, %s int)",
                Constants.TEST_RESULT_TABLE,
                Constants.TEST_RESULT_ID_COLUMN,
                Constants.TEST_RESULT_VERSION_COLUMN,
                Constants.TEST_RESULT_SUBJECT_ID_COLUMN,
                Constants.TEST_RESULT_DEVICE_ID_COLUMN,
                Constants.TEST_RESULT_DEVICE_NAME_COLUMN,
                Constants.TEST_RESULT_BEAM_SPILTTER_COLUMN,
                Constants.TEST_RESULT_AXIS_OD_COLUMN,
                Constants.TEST_RESULT_AXIS_OS_COLUMN,
                Constants.TEST_RESULT_SPH_OD_COLUMN,
                Constants.TEST_RESULT_SPH_OS_COLUMN,
                Constants.TEST_RESULT_CYL_OD_COLUMN,
                Constants.TEST_RESULT_CYL_OS_COLUMN,
                Constants.TEST_RESULT_SE_OD_COLUMN,
                Constants.TEST_RESULT_SE_OS_COLUMN,
                Constants.TEST_RESULT_RMSE_OD_COLUMN,
                Constants.TEST_RESULT_RMSE_OS_COLUMN,
                Constants.TEST_RESULT_CREATED_AT_COLUMN,
                Constants.TEST_RESULT_LAST_SYNCED_AT_COLUMN,
                Constants.TEST_RESULT_STATUS);
         Log.d(TAG, "Create test_result table");
         db.execSQL(sql);

        sql = String.format("create table if not exists %s (%s int primary key, " +
                        "%s int, %s int, %s int, %s double, %s double, %s double, " +
                        "%s int, %s int, %s int)",
                Constants.TEST_MEASUREMENT_TABLE,
                Constants.TEST_MEASUREMENT_ID_COLUMN,
                Constants.TEST_MEASUREMENT_VERSION_COLUMN,
                Constants.TEST_MEASUREMENT_SUBJECT_ID_COLUMN,
                Constants.TEST_MEASUREMENT_TEST_ID_COLUMN,
                Constants.TEST_MEASUREMENT_ANGLE_COLUMN,
                Constants.TEST_MEASUREMENT_DISTANCE_COLUMN,
                Constants.TEST_MEASUREMENT_POWER_COLUMN,
                Constants.TEST_MEASUREMENT_RIGHT_EYE_COLUMN,
                Constants.TEST_MEASUREMENT_CREATED_AT_COLUMN,
                Constants.TEST_MEASUREMENT_LAST_SYNCED_AT_COLUMN,
                Constants.TEST_MEASUREMENT_STATUS);
        Log.d(TAG, "create test_measurement table");
         ****/

        db.execSQL(sql);

    }

    /*
     * Add the database migration code here.
     *
     * Note: The database version number can only be increased, not decreased
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
