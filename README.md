# local-configuration-app
### NOTE! This library is thought to be Open Sourced. Add no internal iZettle dependencies to it.
Used to configure the iZettle Android app. 

## TL;DR;
* Install LocalConfig: [Play store](https://play.google.com/store/apps/details?id=com.izettle.localconfig.application) or [releases on github](https://github.com/iZettle/local-configuration-app/releases)
* Run your 'main' application
* Modify values in LocalConfig
* Restart 'main' application

## HOW TO INSTALL THE APPLICATION
You will need to fetch a .apk of the application. That can be achieved in two ways. [Play store](https://play.google.com/store/apps/details?id=com.izettle.localconfig.application) or from [releases on github](https://github.com/iZettle/local-configuration-app/releases). The app on play store is currently restricted to izettles google apps organization so you will need to be logged in on an email that belongs to that organization to install using that method.

## USE THE APPLICATION
Upon initial install the LocalConfig will contain no data. The data is provided from your 'main' application. The 'main' application provides data in the LocalConfig application by using one of the libs:

```
// project wide build.gradle
allprojects {
    repositories {
        ....
        maven { url 'https://raw.githubusercontent.com/erikeelde/mahrepo/master' }
    }
}

// app build.gradle
dependencies {
    ....
    releaseCompile 'com.izettle.localconfiguration:localconfiguration-no-op:1.0'
    debugCompile 'com.izettle.localconfiguration:localconfiguration:1.0'
}

// submodule build.gradle
dependencies {
    ....
    provided 'com.izettle.localconfiguration:localconfiguration:1.0'
}
```
Your application will then automatially add configurations as you ask for them. So start the app and click around and LocalConfig will be populated with data. You can then switch back to LocalConfig to modify the values provided by the 'main' application. 



## USE THE CODE LIBRARY
You would typically do:
```
if (new LocalConfiguration(context).getBoolean("FeatureUnderDevelopment", false)) {
    // This code is not ready for release yet
    ....
} else {
    // This code will be run in release
    ....
}
```
Where the first argument - "FeatureUnderDevelopment" - is what you should look for to toggle your development feature in the LocalConfig application. And the second parameter - false - is the default value which will be returned unless you specify otherwise.

## USE THE SERVICE LIBRARY
Service in your manifest - export it if you want to communicate with it externally:
```
<service
    android:name=".ConfigurationService"
    android:exported="true" />
```

Service should extend LocalConfigurationService:
```
public class ConfigurationService extends LocalConfigurationService {
}
```

Modify configurations using adb shell: 

```
am startservice -n com.izettle.localconfig.sampleapplication.debug/com.izettle.localconfig.sampleapplication.ConfigurationService --ei FeatureUnderDevelopment 1234
am startservice -n com.izettle.localconfig.sampleapplication.debug/com.izettle.localconfig.sampleapplication.ConfigurationService --ez FeatureUnderDevelopment true|false
am startservice -n com.izettle.localconfig.sampleapplication.debug/com.izettle.localconfig.sampleapplication.ConfigurationService --es FeatureUnderDevelopment "my string"
```

Using intent:
```
Intent intent = new Intent(context, ConfigurationService.class);
intent.putExtra("FeatureUnderDevelopment", true);
startService(intent);
```

## SAMPLE APPLICATION
Simple showcase of how to use the LocalConfiguration application

## VISION
The vision is to make a public release of the application to the play store to benefit other developers. To facilitate less friction for usage we want to open source the libraries so we can provide them using jcenter. Along with the open sourcing of the libraries we want to open source the code to the application.

The idea is to write small libraries around LocalConfig to facilitate our usecases. The two that are written to date are just a start. If you have a usecase that is not covered by those libraries file it as an issue and we'll look over how we can support your usecase.


# Release
This can be simplified a great deal. 

Signing keystore + passwords are stored securely.

## Upload proguard mapping to firebase 
Service account will be stored securely.

Build app + upload proguard to firebase using: 
```
./gradlew -PFirebaseServiceAccountFilePath=/path/to/localconfig-895b7-firebase-crashreporting-v5jjd-fb0fc91c63.json :app:firebaseUploadReleaseProguardMapping
```

## Sign apk
Manually sign the app in the build folder
```
apksigner sign --ks /path/to/localconfig.jks --out app-release-signed.apk app-release-unsigned.apk
```

## Upload to play store
