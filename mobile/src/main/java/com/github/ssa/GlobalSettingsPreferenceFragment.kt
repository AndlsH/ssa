/*******************************************************************************
 *                                                                             *
 *  Copyright (C) 2017 by Max Lv <max.c.lv@gmail.com>                          *
 *  Copyright (C) 2017 by Mygod Studio <contact-shadowsocks-android@mygod.be>  *
 *                                                                             *
 *  This program is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by       *
 *  the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                        *
 *                                                                             *
 *  This program is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 *  GNU General Public License for more details.                               *
 *                                                                             *
 *  You should have received a copy of the GNU General Public License          *
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                             *
 *******************************************************************************/

package com.github.ssa

import android.os.Build
import android.os.Bundle
<<<<<<< HEAD:mobile/src/main/java/com/github/ssa/GlobalSettingsPreferenceFragment.kt
import android.support.design.widget.Snackbar
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.Preference
import com.github.ssa.App.Companion.app
import com.github.ssa.bg.BaseService
import com.github.ssa.preference.DataStore
import com.github.ssa.utils.DirectBoot
import com.github.ssa.utils.Key
import com.github.ssa.utils.TcpFastOpen
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers
=======
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.github.ssa.bg.BaseService
import com.github.ssa.preference.DataStore
import com.github.ssa.utils.DirectBoot
import com.github.ssa.utils.Key
import com.github.ssa.utils.TcpFastOpen
import com.github.ssa.utils.remove
import com.takisoft.preferencex.PreferenceFragmentCompat
>>>>>>> upstream/master:mobile/src/main/java/com/github/shadowsocks/GlobalSettingsPreferenceFragment.kt

class GlobalSettingsPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = DataStore.publicStore
        DataStore.initGlobal()
        addPreferencesFromResource(R.xml.pref_global)
        val boot = findPreference(Key.isAutoConnect) as SwitchPreference
        boot.setOnPreferenceChangeListener { _, value ->
            BootReceiver.enabled = value as Boolean
            true
        }
        boot.isChecked = BootReceiver.enabled
        if (Build.VERSION.SDK_INT >= 24) boot.setSummary(R.string.auto_connect_summary_v24)

        val canToggleLocked = findPreference(Key.directBootAware)
        if (Build.VERSION.SDK_INT >= 24) canToggleLocked.setOnPreferenceChangeListener { _, newValue ->
            if (Core.directBootSupported && newValue as Boolean) DirectBoot.update() else DirectBoot.clean()
            true
        } else canToggleLocked.remove()

        val tfo = findPreference(Key.tfo) as SwitchPreference
        tfo.isChecked = DataStore.tcpFastOpen
        tfo.setOnPreferenceChangeListener { _, value ->
            if (value as Boolean) {
                val result = TcpFastOpen.enabled(true)
                if (result != null && result != "Success.") (activity as MainActivity).snackbar(result).show()
                TcpFastOpen.sendEnabled
            } else true
        }
        if (!TcpFastOpen.supported) {
            tfo.isEnabled = false
            tfo.summary = getString(R.string.tcp_fastopen_summary_unsupported, System.getProperty("os.version"))
        }

        val serviceMode = findPreference(Key.serviceMode)
        val portProxy = findPreference(Key.portProxy)
        val portLocalDns = findPreference(Key.portLocalDns)
        val portTransproxy = findPreference(Key.portTransproxy)
        val onServiceModeChange = Preference.OnPreferenceChangeListener { _, newValue ->
            val (enabledLocalDns, enabledTransproxy) = when (newValue as String?) {
                Key.modeProxy -> Pair(false, false)
                Key.modeVpn -> Pair(true, false)
                Key.modeTransproxy -> Pair(true, true)
                else -> throw IllegalArgumentException("newValue: $newValue")
            }
            portLocalDns.isEnabled = enabledLocalDns
            portTransproxy.isEnabled = enabledTransproxy
            true
        }
        val listener: (Int) -> Unit = {
            if (it == BaseService.STOPPED) {
                tfo.isEnabled = true
                serviceMode.isEnabled = true
                portProxy.isEnabled = true
                onServiceModeChange.onPreferenceChange(null, DataStore.serviceMode)
            } else {
                tfo.isEnabled = false
                serviceMode.isEnabled = false
                portProxy.isEnabled = false
                portLocalDns.isEnabled = false
                portTransproxy.isEnabled = false
            }
        }
        listener((activity as MainActivity).state)
        MainActivity.stateListener = listener
        serviceMode.onPreferenceChangeListener = onServiceModeChange
    }

    override fun onDestroy() {
        MainActivity.stateListener = null
        super.onDestroy()
    }
}
