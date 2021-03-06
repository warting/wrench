package com.example.wrench.wrenchprefs

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.example.wrench.MyEnum
import com.example.wrench.R
import com.izettle.wrench.preferences.WrenchPreferences
import javax.inject.Inject

class WrenchPreferencesFragmentViewModel @Inject constructor(val resources: Resources, private val wrenchPreferences: WrenchPreferences) : ViewModel() {

    fun getStringConfiguration(): String? {
        return wrenchPreferences.getString(resources.getString(R.string.string_configuration), "string1")
    }

    fun getUrlConfiguration(): String? {
        return wrenchPreferences.getString(resources.getString(R.string.url_configuration), "http://www.example.com/path?param=value")
    }

    fun getBooleanConfiguration(): Boolean? {
        return wrenchPreferences.getBoolean(resources.getString(R.string.boolean_configuration), true)
    }

    fun getIntConfiguration(): Int? {
        return wrenchPreferences.getInt(resources.getString(R.string.int_configuration), 1)
    }

    fun getEnumConfiguration(): MyEnum? {
        return wrenchPreferences.getEnum(resources.getString(R.string.enum_configuration), MyEnum::class.java, MyEnum.SECOND)
    }

    fun getServiceStringConfiguration(): String? {
        return wrenchPreferences.getString(resources.getString(R.string.service_configuration), null)
    }
}
