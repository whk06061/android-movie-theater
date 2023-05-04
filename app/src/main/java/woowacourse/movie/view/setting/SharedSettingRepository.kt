package woowacourse.movie.view.setting

import android.content.Context
import android.content.SharedPreferences
import woowacourse.movie.domain.setting.SettingRepository

class SharedSettingRepository(private val context: Context) : SettingRepository {
    override fun getValue(key: String, default: Boolean): Boolean {
        val sharedPreference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreference.getBoolean(key, default)
    }

    override fun setValue(key: String, value: Boolean) {
        val sharedPreference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putBoolean(key, value).apply()
    }
}
