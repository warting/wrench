// https://handstandsam.com/2018/02/11/kotlin-buildsrc-for-better-gradle-dependency-management/

object Versions {
    const val targetSdk = 28
    const val compileSdk = 28
    const val minSdk = 14

    const val appVersionCode = 11
    const val appVersionName = "1.0.10"

    const val dagger = "2.16"

    const val wrench = "0.3"

    const val arch_core = "1.1.1"
    const val junit = "4.12"
    const val retrofit = "2.3.0"
    const val okhttp_logging_interceptor = "3.9.0"
    const val mockwebserver = "3.8.1"
    const val apache_commons = "2.5"
    const val mockito = "2.21.0"
    const val mockito_all = "1.10.19"
    const val dexmaker = "2.2.0"
    const val glide = "4.7.1"
    const val timber = "4.7.0"
    const val android_gradle_plugin = "3.3.0-alpha07"
    const val rxjava2 = "2.1.3"
    const val rx_android = "2.0.1"
    const val hamcrest = "1.3"
    const val kotlin = "1.2.61"
    const val work = "1.0.0-alpha01"
    const val navigation = "1.0.0-alpha05"
    const val kotlin_coroutines = "0.24.0"

    const val room = "2.0.0-rc01"
    const val lifecycle = "2.0.0-rc01"
    const val paging = "2.0.0-rc01"
    const val atsl_runner = "1.1.0-alpha4"
    const val atsl_rules = "1.1.0-alpha4"
    const val espresso = "3.1.0-alpha4"
    const val constraint_layout = "1.1.0"
    const val support = "1.0.0-rc01"
}

object AndroidTestingSupportLibrary {
    const val runner = "androidx.test:runner:${Versions.atsl_runner}"
    const val rules = "androidx.test:rules:${Versions.atsl_rules}"
}

object Lifecycle {
    const val core = "androidx.lifecycle:lifecycle-livedata-core:${Versions.lifecycle}"
    const val runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
    const val extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val java8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    const val compiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
}

object Espresso {
    const val core = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val contrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
    const val intents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
}

object Room {
    const val runtime = "androidx.room:room-runtime:${Versions.room}"
    const val compiler = "androidx.room:room-compiler:${Versions.room}"
    const val rxjava2 = "androidx.room:room-rxjava2:${Versions.room}"
    const val testing = "androidx.room:room-testing:${Versions.room}"
}

object Mockito {
    const val core = "org.mockito:mockito-core:${Versions.mockito}"
    const val all = "org.mockito:mockito-all:${Versions.mockito_all}"
}

object Kotlin {
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val test = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val allopen = "org.jetbrains.kotlin:kotlin-allopen:${Versions.kotlin}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlin_coroutines}"
}

object Wrench {
    const val core = "com.izettle.wrench:wrench-core:${Versions.wrench}"
    const val prefs = "com.izettle.wrench:wrench-prefs:${Versions.wrench}"
    const val prefs_no_op = "com.izettle.wrench:wrench-prefs-no-op:${Versions.wrench}"
}

object Navigation {

    const val fragment = "android.arch.navigation:navigation-fragment:${Versions.navigation}"
    const val ui = "android.arch.navigation:navigation-ui:${Versions.navigation}"

    const val fragmentKotlin = "android.arch.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val uiKotlin = "android.arch.navigation:navigation-ui-ktx:${Versions.navigation}"

    const val testing = "android.arch.navigation:navigation-testing:${Versions.navigation}"

    const val safeArgsPlugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
}

object Oss {
    const val plugin = "com.google.gms:oss-licenses:0.9.2"
    const val runtime = "com.google.android.gms:play-services-oss-licenses:16.0.0"
}

object Libs {
    val wrench = Wrench
    val lifecycle = Lifecycle
    val support = Support
    val dagger = Dagger
    val mockito = Mockito
    val androidTestingSupportLibrary = AndroidTestingSupportLibrary
    val espresso = Espresso
    val room = Room
    val kotlin = Kotlin
    val navigation = Navigation
    val oss = Oss

    const val paging = "androidx.paging:paging-runtime:${Versions.paging}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"

    const val constraint_layout = "androidx.constraintlayout:constraintlayout:${Versions.constraint_layout}"

    const val junit = "junit:junit:${Versions.junit}"

}

object Dagger {
    const val runtime = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
}

object Support {
    const val annotations = "androidx.annotation:annotation:${Versions.support}"
    const val app_compat = "androidx.appcompat:appcompat:${Versions.support}"
    const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.support}"
    const val cardview = "androidx.cardview:cardview:${Versions.support}"
    const val design = "com.google.android.material:material:${Versions.support}"
    const val v4 = "androidx.legacy:legacy-support-v4:${Versions.support}"
    const val core_utils = "androidx.legacy:legacy-support-core-utils:${Versions.support}"
}
