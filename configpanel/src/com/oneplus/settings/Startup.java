/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017 The LineageOS Project
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

package com.oneplus.settings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.evervolv.internal.util.FileUtils;
import com.oneplus.settings.utils.Constants;
import com.oneplus.settings.utils.DozeUtils;

public class Startup extends BroadcastReceiver {

    private static final String TAG = Startup.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Disable button settings if needed
        if (isSliderModeAvailable(context)) {
            // Restore nodes to saved preference values
            for (String pref : Constants.sButtonPrefKeys) {
                String node, value;
                if (Constants.sStringNodePreferenceMap.containsKey(pref)) {
                    node = Constants.sStringNodePreferenceMap.get(pref);
                    value = Constants.getPreferenceString(context, pref);
                } else {
                    node = Constants.sBooleanNodePreferenceMap.get(pref);
                    value = Constants.isPreferenceEnabled(context, pref) ?
                           "1" : "0";
                }
                if (!FileUtils.writeLine(node, value)) {
                    Log.w(TAG, "Write to node " + node +
                        " failed while restoring saved preference values");
                }
            }
        }
        DozeUtils.checkService(context);
    }

    private boolean isSliderModeAvailable(Context context) {
        if (!FileUtils.fileExists(Constants.NOTIF_SLIDER_TOP_NODE) ||
                !FileUtils.fileExists(Constants.NOTIF_SLIDER_MIDDLE_NODE) ||
                !FileUtils.fileExists(Constants.NOTIF_SLIDER_BOTTOM_NODE)) {
            context.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(context, ButtonSettingsActivity.class.getName()),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            return false;
        }
        return true;
    }
}
