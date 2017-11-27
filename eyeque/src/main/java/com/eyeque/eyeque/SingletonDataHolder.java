package com.eyeque.eyeque;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * File:            SingletonDataHolder.java
 * Description:     The screen gives the result of quick test
 * Created:         2016/10/05
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class SingletonDataHolder {
    private static SingletonDataHolder ourInstance = new SingletonDataHolder();
    public static SingletonDataHolder getInstance() {
        return ourInstance;
    }
    private SingletonDataHolder() {}

    // Session value set
    public static int loginType = 1;
    public static Boolean newRegUser = false;
    public static String token = "";
    public static int userId = 0;
    public static String email = "";
    public static String firstName = "";
    public static String lastName = "";
    public static Integer gender = 1;
    public static boolean profileWearEyeglass = false;
    public static boolean profileWearReadingGlasses = false;
    public static String profileReadingGlassesValue = "";
    public static String profileReadingGlassesValue_selected = "";
    public static boolean profileUploadPrescription = false;
    public static Integer birthYear = Calendar.getInstance().get(Calendar.YEAR) - 20;
    public static Integer groupId = 1;
    public static String deviceSerialNum = "";
    public static String country = "";
    public static String country_selected = "";
    public static ArrayList<String> countryListItems = new ArrayList<String>();
    public static boolean accommodationOn = true;
    public static int screenProtect = 0;
    public static int wearGlasses = 0;
    public static String urlOdTracking = "";
    public static String urlOSTracking = "";
    public static String urlVisionSummary = "";
    public static int currentTestScore = 0;
    public static int freetrial = 0;
    public static int pupillaryDistance = 0;
    public static double nvadd = 0.0;
    public static String gradeOd = "High";
    public static String gradeOs = "High";
    public static Boolean eyeglassNumPurchasable= false;
    public static Boolean subscriptionStatus = false;
    public static String subscriptionExpDate = "";
    public static int subscriptionSubId = 0;
    public static String subscriptionAnnualPrice = "";
    public static String subscriptionBuyLink = "";

    // Eyeglass Number
    public static class EyeglassNumber {
        public double odSph;
        public double odCyl;
        public int odAxis;
        public double osSph;
        public double osCyl;
        public int osAxis;
        public String createdAt;
        public EyeglassNumber(double odSphVal, double odCylVal, int odAxisVal,
                              double osSphVal, double osCylVal, int osAxisVal, String timeVal) {
            odSph = odSphVal;
            odCyl = odCylVal;
            odAxis = odAxisVal;
            osSph = osSphVal;
            osCyl = osCylVal;
            osAxis = osAxisVal;
            createdAt = timeVal;
        }
    }
    public static EyeglassNumber[] eyeglassNumberList;
    public static int eyeglassNumCount = 0;
    public static int numOfTests = 0;
    public static String firstTestDate;
    public static String confLevelOd = "";
    public static String confLevelOs = "";

    // Session method
    // public static int getLoginType() { return loginType; }
    // public static void setLoginType(int val) { loginType = val; }
    // public static String getToken() { return token; }
    // public static void setToken(String val) { token = val; }
    // public static String getEmail() { return email; }
    // public static void setEmail(String val) { email = val; }
    // public static String getFirstName() { return firstName; }
    // public static void setFirstName(String val) { firstName = val; }
    // public static String getLastName() { return lastName; }
    // public static void setLastName(String val) { lastName = val; }
    // public static Integer getGender() { return gender; }
    // public static void setGender(Integer val) { gender = val; }
    // public static Integer getBirthYear() { return birthYear; }
    // public static void setBirthYear(Integer val) { birthYear = val; }
    // public static String getDeviceSerialNum() { return deviceSerialNum; }
    // public static void setDeviceSerialNum(String val) { deviceSerialNum = val; }

    // System parameters
    public static String lang = "en";
    public static int testMode = 0;                 // 0: Full Test 1: Practice TEst
    public static boolean noDevice = true;          // Practice test device mode
    public static int practiceGenericPageNum = 1;   // Practice session generic page number
    public static boolean subscriptionFlag = false; // Global subscription flag

    // Phone parameters
    public static String phoneManufacturer = "";
    public static String phoneBrand = "";
    public static String phoneProduct = "";
    public static String phoneModel = "";
    public static String phoneType = "";
    public static String phoneDevice = "";
    public static String phoneHardware = "";
    public static String phoneSerialNum = "";
    public static String phoneDisplay = "";
    public static int phonePpi = 577;
    public static int phoneDpi = 577;
    public static String deviceApiRespData;
    public static String dahsboardApiRespData;
    public static String userApiRespData;
    public static boolean correctDisplaySetting = true;
    public static String deviceName = "EQ101";
    public static double deviceWidth = 2.0f;
    public static double deviceHeight = 1.375f;

    // Pattern drawing configuration
    public static int centerX = 0;
    public static int centerY = 0;
    public static int lineLength = 0;
    public static int lineWidth = 0;
    public static int initDistance = 0;
    public static int minDistance = 0;
    public static int maxDistance = 0;
    public static int disOffset = 0;
    /*** For video shooting
     public static int initDistance = 99;
     public static int minDistance = 0;
     public static int maxDistance = 183;
     ***/
    public static final int[] patternAngleList  = {0, 320, 280, 240, 200, 160, 120, 80, 40};
    public static final double[] calcAngleList = {0.0, 320.0, 280.0, 240.0, 200.0, 160.0, 120.0, 80.0, 40.0};
    public static final int[] rotateAngleList = {180, 40, 80, 120, 160, 200, 240, 280, 320};
    // Color configuration
    public static int redDegree = 0;
    public static int greenDegree = 0;
    public static int blueDegree = 0;

    // Calculation configuration
    public static List<String> sphericalStep;
    public static double SphericalStep0 = 0.127068;
    public static double SphericalStep1 = 0.00039076;
    public static double SphericalStep2 = 0.13464368;
    public static double SphericalStep3 = 0.00019929366;

}
