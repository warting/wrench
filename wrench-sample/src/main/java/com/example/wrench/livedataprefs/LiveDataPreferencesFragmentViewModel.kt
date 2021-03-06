package com.example.wrench.livedataprefs

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.example.wrench.MyEnum
import com.example.wrench.R
import com.izettle.wrench.livedata.WrenchLiveData
import javax.inject.Inject

class LiveDataPreferencesFragmentViewModel @Inject constructor(val application: Application) : ViewModel() {

    fun getStringConfiguration(): LiveData<String> {
        return WrenchLiveData.create(application, application.resources.getString(R.string.string_configuration), "string1")
    }

    fun getIntConfiguration(): LiveData<Int> {
        return WrenchLiveData.create(application, application.resources.getString(R.string.int_configuration), 1)
    }

    fun getBooleanConfiguration(): LiveData<Boolean> {
        return WrenchLiveData.create(application, application.resources.getString(R.string.boolean_configuration), true)
    }

    fun getUrlConfiguration(): LiveData<String> {
        return WrenchLiveData.create(application, application.resources.getString(R.string.url_configuration), "http://www.example.com/path?param=value")
    }

    fun getEnumConfiguration(): LiveData<MyEnum> {
        return WrenchLiveData.create(application, application.resources.getString(R.string.enum_configuration), MyEnum::class.java, MyEnum.FIRST)
    }

    fun getServiceStringConfiguration(): LiveData<String> {
        return WrenchLiveData.create(application, application.resources.getString(R.string.service_configuration), null)
    }
}