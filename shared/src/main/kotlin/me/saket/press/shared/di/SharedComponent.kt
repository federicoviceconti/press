package me.saket.press.shared.di

import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.preference.PreferenceManager
import com.russhwolf.settings.AndroidSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import me.saket.press.PressDatabase
import me.saket.press.shared.sync.git.DeviceInfo
import me.saket.press.shared.sync.git.File

actual object SharedComponent : BaseSharedComponent() {

  fun initialize(appContext: Application) {
    setupGraph(PlatformDependencies(
        sqlDriver = { sqliteDriver(appContext) },
        settings = { settings(appContext) },
        deviceInfo = { deviceInfo(appContext) }
    ))
  }

  private fun sqliteDriver(appContext: Application) =
    AndroidSqliteDriver(PressDatabase.Schema, appContext, "press.db")

  private fun settings(appContext: Application) =
    AndroidSettings(PreferenceManager.getDefaultSharedPreferences(appContext))

  private fun deviceInfo(context: Context): DeviceInfo {
    return DeviceInfo(
        appStorage = File(context.filesDir.path),
        deviceName = {
          // https://stackoverflow.com/a/45696806/2511884
          val bluetoothName = Settings.Secure.getString(context.contentResolver, "bluetooth_name")
          bluetoothName ?: android.os.Build.MODEL
        }
    )
  }
}
