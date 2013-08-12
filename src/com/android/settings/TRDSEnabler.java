/*
 * Copyright (C) 2013 SlimRoms
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

package com.android.settings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.android.settings.util.Helpers; 

import java.util.List;

public class TRDSEnabler implements CompoundButton.OnCheckedChangeListener {
    private final Context mContext;
    private Switch mSwitch;
    private boolean mStateMachineEvent;
    private PowerManager pm;

    // list off apps which we restart just to be sure due that AOSP
    // does not every time reload all resources on onConfigurationChanged
    // or because some apps are just not programmed well on that part.
    private String mTRDSApps[] = new String[] {
        "com.android.contacts",
        "com.android.calendar",
        "com.android.email",
        "com.android.vending",
        "com.android.mms",
        "com.google.android.talk",
        "com.google.android.gm",
        "com.google.android.googlequicksearchbox",
        "com.google.android.youtube",
        "com.google.android.apps.genie.geniewidget",
        "com.google.android.apps.plus",
        "com.google.android.apps.maps"
    };

    public TRDSEnabler(Context context, Switch switch_) {
        mContext = context;
        mSwitch = switch_;
    }

    public void resume() {
        mSwitch.setOnCheckedChangeListener(this);
        setSwitchState();
    }

    public void pause() {
        mSwitch.setOnCheckedChangeListener(null);
    }

    public void setSwitch(Switch switch_) {
        if (mSwitch == switch_) return;
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch = switch_;
        mSwitch.setOnCheckedChangeListener(this);
        setSwitchState();
    }
}
