/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.settings.cyanogenmod.colorpicker.ColorPickerPreference;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_CLOCK_CATEGORY = "status_bar_clock";
    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";
    private static final String TAG = "StatusBar";
    private static final String STATUS_BAR_BATTERY = "status_bar_battery";
    private static final String STATUS_BAR_CIRCLE_BATTERY_COLOR = "status_bar_circle_battery_color";
    private static final String STATUS_BAR_CIRCLE_BATTERY_TEXT_COLOR = "status_bar_circle_battery_text_color";
    private static final String STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED = "status_bar_circle_battery_animationspeed";

    private static final String PREF_CIRCLE_COLOR_RESET = "circle_battery_reset";
    private static final String PREF_STATUS_BAR_CIRCLE_BATTERY_COLOR = "circle_battery_color";
    private static final String PREF_STATUS_BAR_CIRCLE_BATTERY_TEXT_COLOR = "circle_battery_text_color";
    private static final String PREF_STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED = "circle_battery_animation_speed";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String STATUS_BAR_AUTO_HIDE = "status_bar_auto_hide"; 
    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";
    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String STATUS_BAR_CATEGORY_GENERAL = "status_bar_general";
    private static final String PREF_FULLSCREEN_STATUSBAR = "fullscreen_statusbar";
    private static final String FREF_FULLSCREEN_STATUSBAR_TIMEOUT = "fullscreen_statusbar_timeout"; 
    private static final String PREF_ENABLE = "clock_style";
    private static final String PREF_BATT_BAR = "battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";

    private ListPreference mStatusBarBattery;
    private ColorPickerPreference mCircleColor;
    private ColorPickerPreference mCircleTextColor;
    private ListPreference mCircleAnimSpeed;
    private Preference mCircleColorReset;
    private ListPreference mStatusBarCmSignal;
    private ListPreference mStatusBarClock;
    private ListPreference mStatusBarAmPm;
    private ListPreference mBatteryBar;
    private ListPreference mBatteryBarStyle;
    private ListPreference mBatteryBarThickness;
    private PreferenceScreen mClockStyle;
    private CheckBoxPreference mStatusBarBrightnessControl;
    private CheckBoxPreference mStatusBarNotifCount;
    private CheckBoxPreference mBatteryBarChargingAnimation;
    private PreferenceCategory generalCategory;
    private ColorPickerPreference mBatteryBarColor;
    private CheckBoxPreference mStatusBarAutoHide; 
    private CheckBoxPreference mFullScreenStatusBar;
    ListPreference mFullScreenStatusBarTimeout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	int defaultColor;
        int intColor;
        String hexColor;

        addPreferencesFromResource(R.xml.status_bar);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarBrightnessControl = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarBattery = (ListPreference) prefSet.findPreference(STATUS_BAR_BATTERY);

        mCircleColor = (ColorPickerPreference) findPreference(PREF_STATUS_BAR_CIRCLE_BATTERY_COLOR);
        mCircleColor.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_blue_dark);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCircleColor.setSummary(hexColor);

        mCircleTextColor = (ColorPickerPreference) findPreference(PREF_STATUS_BAR_CIRCLE_BATTERY_TEXT_COLOR);
        mCircleTextColor.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_blue_dark);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_TEXT_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCircleTextColor.setSummary(hexColor);

        mCircleAnimSpeed = (ListPreference) findPreference(PREF_STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED);
        mCircleAnimSpeed.setOnPreferenceChangeListener(this);
        mCircleAnimSpeed.setValue((Settings.System
                .getInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 3))
                + "");
        mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntry());

        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);

        mStatusBarAmPm = (ListPreference) prefSet.findPreference(STATUS_BAR_AM_PM);

        mStatusBarClock.setOnPreferenceChangeListener(this);

        if (DateFormat.is24HourFormat(getActivity())) {
            ((PreferenceCategory) prefSet.findPreference(STATUS_BAR_CLOCK_CATEGORY))
                    .removePreference(prefSet.findPreference(STATUS_BAR_AM_PM));
        } else {
            mStatusBarAmPm = (ListPreference) prefSet.findPreference(STATUS_BAR_AM_PM);
            int statusBarAmPm = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_AM_PM, 2);

            mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntry());
            mStatusBarAmPm.setOnPreferenceChangeListener(this);
        }

        mStatusBarBrightnessControl.setChecked(Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1);
        mStatusBarBrightnessControl.setOnPreferenceChangeListener(this);

        try {
            if (Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
            // Do nothing here
        }

        int statusBarBattery = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int signalStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mStatusBarNotifCount = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1);
        mStatusBarNotifCount.setOnPreferenceChangeListener(this);

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        mBatteryBar.setOnPreferenceChangeListener(this);
        mBatteryBar.setValue((Settings.System
                .getInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR, 0))
                + "");

        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarStyle.setOnPreferenceChangeListener(this);
        mBatteryBarStyle.setValue((Settings.System.getInt(getActivity()
                .getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0))
                + "");

        mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
        mBatteryBarColor.setOnPreferenceChangeListener(this);

        mBatteryBarChargingAnimation = (CheckBoxPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

        mBatteryBarThickness = (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
        mBatteryBarThickness.setOnPreferenceChangeListener(this);
        mBatteryBarThickness.setValue((Settings.System.getInt(getActivity()
                .getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1))
                + "");

        mStatusBarAutoHide = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_AUTO_HIDE);
        mStatusBarAutoHide.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.AUTO_HIDE_STATUSBAR, 0) == 1));

        mFullScreenStatusBar = (CheckBoxPreference)findPreference(PREF_FULLSCREEN_STATUSBAR);
        mFullScreenStatusBar.setChecked(Settings.System.getBoolean(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.FULLSCREEN_STATUSBAR, true)); 

        mFullScreenStatusBarTimeout = (ListPreference) findPreference(FREF_FULLSCREEN_STATUSBAR_TIMEOUT);
        mFullScreenStatusBarTimeout.setOnPreferenceChangeListener(this);
        mFullScreenStatusBarTimeout.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.FULLSCREEN_STATUSBAR_TIMEOUT, 10000) + ""); 

	mCircleColorReset = (Preference) findPreference(PREF_CIRCLE_COLOR_RESET);
        if (Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_RESET, 0) == 1) {
            circleColorReset();
        }	

        PreferenceCategory generalCategory =
                (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_GENERAL);

        if (Utils.isWifiOnly(getActivity())) {
            generalCategory.removePreference(mStatusBarCmSignal);
        }

	mClockStyle = (PreferenceScreen) prefSet.findPreference("clock_style_pref");
        if (mClockStyle != null) {
            updateClockStyleDescription();
        }

        if (Utils.isTablet(getActivity())) {
            generalCategory.removePreference(mStatusBarBrightnessControl);
        }
        
	updateBatteryIconOptions();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;
        ContentResolver resolver = getActivity().getContentResolver();
        if (mStatusBarAmPm != null && preference == mStatusBarAmPm) {
            int statusBarAmPm = Integer.valueOf((String) newValue);
            int index = mStatusBarAmPm.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_AM_PM, statusBarAmPm);
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarBattery) {
            int statusBarBattery = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_BATTERY, statusBarBattery);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
	    updateBatteryIconOptions();
            return true;
        } else if (preference == mCircleColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, intHex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_RESET, 0);
            return true;
        } else if (preference == mCircleTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_TEXT_COLOR, intHex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_RESET, 0);
            return true;
        } else if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarClock) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_CLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarBrightnessControl) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                                   Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarNotifCount) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_NOTIF_COUNT, value ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarChargingAnimation) {
            
            Settings.System.putInt(getActivity().getContentResolver(),
                                   Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE,
                                   ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mStatusBarAutoHide) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                                   Settings.System.AUTO_HIDE_STATUSBAR, value ? 1 : 0);
            return true;
        } else if (preference == mFullScreenStatusBar) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                                   Settings.System.FULLSCREEN_STATUSBAR, value ? 1 : 0);
            return true; 
        } else if (preference == mStatusBarClock) {
            int clockStyle = Integer.parseInt((String) newValue);
            int index = mStatusBarClock.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, clockStyle);
            mStatusBarClock.setSummary(mStatusBarClock.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);
            return true;
	} else if (preference == mCircleAnimSpeed) {
            int val = Integer.parseInt((String) newValue);
            int index = mCircleAnimSpeed.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, val);
            mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBar) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR, val);

        } else if (preference == mBatteryBarStyle) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, val);

        } else if (preference == mBatteryBarThickness) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, val);
	} else if (preference == mCircleColorReset) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_RESET, 1);
            circleColorReset();
            return true;
        } else if (preference == mFullScreenStatusBarTimeout) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FULLSCREEN_STATUSBAR_TIMEOUT, val);
            return true; 
        }
        return false;
    }

    private void circleColorReset() {
        int defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_blue_dark);
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, defaultColor);
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_CIRCLE_BATTERY_TEXT_COLOR, defaultColor);
        String hexColor = String.format("#%08x", (0xffffffff & defaultColor));
        mCircleColor.setSummary(hexColor);
        mCircleTextColor.setSummary(hexColor);
    }
	
    private void updateClockStyleDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
               Settings.System.STATUS_BAR_CLOCK, 0) == 1) {
            mClockStyle.setSummary(getString(R.string.clock_enabled));
        } else {
            mClockStyle.setSummary(getString(R.string.clock_disabled));
         }	

    }

     @Override
    public void onResume() {
        super.onResume();
        updateClockStyleDescription();
    }

     private void updateBatteryIconOptions() {
        int batteryIconStat = Settings.System.getInt(getActivity().getContentResolver(),
               Settings.System.STATUS_BAR_BATTERY, 0);

        if (batteryIconStat == 0 || batteryIconStat == 1 || batteryIconStat == 5) {
            mCircleColor.setEnabled(false);
            mCircleTextColor.setEnabled(false);
            mCircleAnimSpeed.setEnabled(false);
            mCircleColorReset.setEnabled(false);
        } else if (batteryIconStat == 2) {
            mCircleColor.setEnabled(true);
            mCircleTextColor.setEnabled(false);
            mCircleAnimSpeed.setEnabled(true);
            mCircleColorReset.setEnabled(true);
        } else {
            mCircleColor.setEnabled(true);
            mCircleTextColor.setEnabled(true);
            mCircleAnimSpeed.setEnabled(true);
            mCircleColorReset.setEnabled(true);
        }
    }	
}
