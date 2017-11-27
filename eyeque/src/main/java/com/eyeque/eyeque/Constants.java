package com.eyeque.eyeque;

import android.os.Build;
import android.provider.BaseColumns;

/**
 *
 * File:            Constants.java
 * Description:     This class is data holder that stores all the global constants used
 *                  by the app
 * Created:         2016/07/10
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public final class Constants {
    public final static boolean DEBUG = true;

    /*****************************************************************************
     * Restful API URL Addresses
     *****************************************************************************/
    // Development Server
    // public final static String BuildNumber = "1.4.0 Dev";
    // public static String ApiRestfulBaseURL = "http://apidev.eyeque.com/";
    // public static String UserdataRestfulBaseURL = "http://apidev.eyeque.com:8987/";
    // public static final String WebSiteBaseURL = "http://wwwdev.eyeque.com/";
    // public static final String WebSiteMobileBaseURL = "http://wwwdev.eyeque.com/m/";

    // Production Server
    public final static String BuildNumber = "1.4.0";
    public static String ApiRestfulBaseURL = "https://store.eyeque.com/";
    public static String UserdataRestfulBaseURL = "https://store.eyeque.com:8987/";
    public static final String WebSiteBaseURL = "http://www.eyeque.com/";
    public static final String WebSiteMobileBaseURL = "http://www.eyeque.com/m/";

    // Restful API Call Base URL Address
    public static final String LocalRestfulBaseURL = "http://192.168.110.122:8080";
    public static final String AwsEc2RestfulBaseURL = "http://54.191.245.62:8080";
    public static final String AccessToken = "e46cghc52pqd8kvgqmv8ovsi1ufcfetg";
    public static final String RestfulBaseURL = AwsEc2RestfulBaseURL;


    // Restful API Call URL - Web Content
    public static final String UrlBanner = WebSiteMobileBaseURL + "banner";
    public static final String UrlYoutube = WebSiteMobileBaseURL + "tutorial_video/index.html";
    public static final String UrlDeviceList = WebSiteMobileBaseURL + "devicelist/android.html";
    public static final String UrlTermsOfService = WebSiteBaseURL + "terms_of_service";
    public static final String UrlPrivacyPolicy = WebSiteBaseURL + "privacy_policy";
    public static final String UrlBuyDevice = WebSiteBaseURL + "shop";
    public static final String UrlFaq =  WebSiteBaseURL + "faq";
    public static final String UrlBlog =  WebSiteBaseURL + "blog";
    public static final String UrlLikeUsOnFacebook = "https://m.facebook.com/EyeQueCorp/";
    public static final String UrlSupport =  WebSiteBaseURL + "support";
    public static final String UrlRateApp = "https://play.google.com/store/apps/details?id=com.eyeque.eyeque";

    // Restful API Call URL - Data Exchange
    public static final String UrlTrackingDataOd = UserdataRestfulBaseURL + "index.html?hash=default&column=sphEOD";
    public static final String UrlTrackingDataOs = UserdataRestfulBaseURL + "index.html?hash=default&column=sphEOS";
    public static final String UrlSignIn = ApiRestfulBaseURL + "index.php/rest/V1/integration/customer/token";
    public static final String UrlSignUp = ApiRestfulBaseURL + "index.php/rest/V1/customers";
    public static final String UrlResendEmailConfirmation = ApiRestfulBaseURL + "customer/account/confirmation/email/";
    public static final String UrlUserProfile = ApiRestfulBaseURL + "index.php/rest/V1/customers/me";
    public static final String UrlForgotPassword = ApiRestfulBaseURL + "index.php/rest/V1/customers/password";
    public static final String UrlCheckSerialNum = ApiRestfulBaseURL + "index.php/rest/V1/sncheck/check";
    public static final String UrlVerifySocMediaLogin = ApiRestfulBaseURL + "index.php/rest/V1/socialcustomers/validate";
    public static final String UrlSocMediaSignUp = ApiRestfulBaseURL + "index.php/rest/V1/socialcustomers";
    public static final String UrlPhoneConfig = ApiRestfulBaseURL + "index.php/rest/eyecloud/api/V2/devices";
    public static final String UrlCountryList = ApiRestfulBaseURL + "index.php/rest/V1/ipgeolocation/getcountrylist";
    public static final String UrlUserSubscription = ApiRestfulBaseURL + "index.php/rest/V1/subscriptionservice/get/me";
    public static final String UrlBuySubscription = ApiRestfulBaseURL + "index.php/rest/V1/subscriptionservice/defaultproduct";
    public static final String UrlUploadTest = ApiRestfulBaseURL + "index.php/rest/eyecloud/api/V2/tests";
    public static final String UrlUploadTraining = ApiRestfulBaseURL + "index.php/rest/eyecloud/api/V2/trainings";
    public static final String UrlConfirmTest = ApiRestfulBaseURL + "index.php/rest/eyecloud/api/V2/discardtest";
    public static final String UrlDashboard = ApiRestfulBaseURL + "index.php/rest/eyecloud/api/V2/getdashboardinfo";
    public static final String UrlDashboardSummary = ApiRestfulBaseURL + "index.php/rest/eyecloud/api/V2/getdashboardsummary";
    public static final String UrlPurchaseEyeglassNumber = ApiRestfulBaseURL + "index.php/rest/eyecloud/api/V2/purchaseeyeglassnumbers";


    /*****************************************************************************
     * System level constants
     *****************************************************************************/
    public static final double PI = 3.141592653589793d;
    public static final int sdkVersion = Build.VERSION.SDK_INT;
    public static final int NETCONN_TIMEOUT_VALUE = 10000;

    // Line distance scale parameters
    public static final int MINVAL_DEVICE_1 = 270;
    public static final int MINVAL_DEVICE_3 = 227;
    public static final int MINVAL_DEVICE_5 = 227;
    public static final int MINVAL_DEVICE_6 = 130;
    public static final int MAXVAL_DEVICE_1 = 60;
    public static final int MAXVAL_DEVICE_3 = 183;
    public static final int MAXVAL_DEVICE_5 = 183;
    public static final int MAXVAL_DEVICE_6 = 140;

    // Age Offset SPHE Compensation contant values
    public static final double SPAN = 4.0;
    public static final double AGE0 = 47.0;
    public static final double OFFSET1   = 0.76;
    public static final double OFFSET2   = 0.02;

    /*****************************************************************************
     * Database constants
     *****************************************************************************/
    // Table list
    public static final String DB_NAME = "scope.db";
    public static final int DB_VERSION = 1;
    public static final String USER_ENTITY_TABLE = "user_entity";
    public static final String TEST_SUBJECT_TABLE = "test_subject";
    public static final String TEST_RESULT_TABLE = "test_result";
    public static final String TEST_MEASUREMENT_TABLE = "test_measurement";

    // Columns for user entity table
    public static final String USER_ENTITY_ID_COLUMN  = BaseColumns._ID;
    public static final String USER_ENTITY_VERSION_COLUMN  = "version";
    public static final String USER_ENTITY_TOKEN_COLUMN  = "token";

    // Columns for test_subject table
    public static final String TEST_SUBJECT_ID_COLUMN  = BaseColumns._ID;
    public static final String TEST_SUBJECT_VERSION_COLUMN  = "version";
    public static final String TEST_SUBJECT_FIRST_NAME_COLUMN  = "first_name";
    public static final String TEST_SUBJECT_LAST_NAME_COLUMN  = "last_name";
    public static final String TEST_SUBJECT_EMAIL_COLUMN  = "email";
    public static final String TEST_SUBJECT_CREATED_AT_COLUMN  = "created_at";
    public static final String TEST_SUBJECT_LAST_SYNCED_AT_COLUMN  = "last_synced_at";
    public static final String TEST_SUBJECT_STATUS  = "status";

    // Columns for test_result table
    public static final String TEST_RESULT_ID_COLUMN  = BaseColumns._ID;
    public static final String TEST_RESULT_VERSION_COLUMN  = "version";
    public static final String TEST_RESULT_SUBJECT_ID_COLUMN  = "subject_id";
    public static final String TEST_RESULT_DEVICE_ID_COLUMN  = "device_id";
    public static final String TEST_RESULT_DEVICE_NAME_COLUMN  = "device_name";
    public static final String TEST_RESULT_BEAM_SPILTTER_COLUMN  = "beam_splitter";
    public static final String TEST_RESULT_SPH_OD_COLUMN  = "sphod";
    public static final String TEST_RESULT_SPH_OS_COLUMN  = "sphos";
    public static final String TEST_RESULT_CYL_OD_COLUMN  = "cylod";
    public static final String TEST_RESULT_CYL_OS_COLUMN  = "cylos";
    public static final String TEST_RESULT_AXIS_OD_COLUMN  = "axisod";
    public static final String TEST_RESULT_AXIS_OS_COLUMN  = "axisos";
    public static final String TEST_RESULT_SE_OD_COLUMN  = "seod";
    public static final String TEST_RESULT_SE_OS_COLUMN  = "seos";
    public static final String TEST_RESULT_CREATED_AT_COLUMN  = "created_at";
    public static final String TEST_RESULT_LAST_SYNCED_AT_COLUMN  = "last_synced_at";
    public static final String TEST_RESULT_STATUS  = "status";

    // Columns for test_measurement table
    public static final String TEST_MEASUREMENT_ID_COLUMN  = BaseColumns._ID;
    public static final String TEST_MEASUREMENT_VERSION_COLUMN  = "version";
    public static final String TEST_MEASUREMENT_SUBJECT_ID_COLUMN  = "subject_id";
    public static final String TEST_MEASUREMENT_TEST_ID_COLUMN  = "test_id";  // Reference test_result ID
    public static final String TEST_MEASUREMENT_ANGLE_COLUMN  = "angle";
    public static final String TEST_MEASUREMENT_DISTANCE_COLUMN  = "distance";
    public static final String TEST_MEASUREMENT_POWER_COLUMN  = "power";
    public static final String TEST_MEASUREMENT_RIGHT_EYE_COLUMN  = "right_eye";
    public static final String TEST_MEASUREMENT_CREATED_AT_COLUMN  = "created_at";
    public static final String TEST_MEASUREMENT_LAST_SYNCED_AT_COLUMN  = "last_synced_at";
    public static final String TEST_MEASUREMENT_STATUS  = "status";
}
