plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    useLibrary("org.apache.http.legacy")
    namespace = "com.example.snargemobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.snargemobile"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        packaging {
            resources {
                // Exclude duplicate META-INF files that cause the build to fail
                excludes += "META-INF/DEPENDENCIES"
                excludes += "META-INF/LICENSE"
                excludes += "META-INF/LICENSE.txt"
                excludes += "META-INF/license.txt"
                excludes += "META-INF/NOTICE"
                excludes += "META-INF/NOTICE.txt"
                excludes += "META-INF/notice.txt"

            }  }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}
configurations.all {
    exclude (group= "javax.activation", module= "javax.activation-api")
}


dependencies {

    implementation(libs.activity)
    implementation(libs.javamail)
    implementation(libs.activation)
    implementation(libs.activity)
    implementation(libs.bcrypt)
    implementation(libs.volley)
    implementation(libs.room.common)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation ("com.stripe:stripe-android:20.15.3")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation(libs.sdk.twilio)  {
    exclude(group = "org.apache.httpcomponents", module = "httpclient")
}
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}
