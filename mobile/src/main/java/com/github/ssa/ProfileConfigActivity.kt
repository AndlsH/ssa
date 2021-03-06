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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.ssa.plugin.PluginContract
import com.github.ssa.preference.DataStore

class ProfileConfigActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE_PLUGIN_HELP = 1
    }

    private val child by lazy { supportFragmentManager.findFragmentById(R.id.content) as ProfileConfigFragment }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_profile_config)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_navigation_close)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!super.onSupportNavigateUp()) finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_config_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?) = child.onOptionsItemSelected(item)

    override fun onBackPressed() {
        if (DataStore.dirty) AlertDialog.Builder(this)
                .setTitle(R.string.unsaved_changes_prompt)
                .setPositiveButton(R.string.yes) { _, _ -> child.saveAndExit() }
                .setNegativeButton(R.string.no) { _, _ -> finish() }
                .setNeutralButton(android.R.string.cancel, null)
                .create()
                .show() else super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CODE_PLUGIN_HELP) super.onActivityResult(requestCode, resultCode, data)
        else if (resultCode == Activity.RESULT_OK) AlertDialog.Builder(this)
                .setTitle("?")
                .setMessage(data?.getCharSequenceExtra(PluginContract.EXTRA_HELP_MESSAGE))
                .show()
    }
}
