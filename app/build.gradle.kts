plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.chaquo.python")
    alias(libs.plugins.compose.compiler)
}

android {
    ndkVersion = "27.0.11718014"
    namespace = "com.example.smishingdetectionapp"
    compileSdk = 34


    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    defaultConfig {
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
        applicationId = "com.example.smishingdetectionapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Merge the vectorDrawables from both branches

        vectorDrawables {
            useSupportLibrary = true
        }

   }

        // Include the buildConfigFields from master
        buildConfigField("String", "EMAIL", "\"smsphishing8@gmail.com\"")
        buildConfigField("String", "EMAILPASSWORD", "\"xedr gaek jdsv ujxw\"")
        buildConfigField("String", "SERVERIP", "\"http:192.168.?.?:3000\"")

        buildConfigField("String", "EMAIL", "\"smsphishing8@gmail.com\"") // Gmail Email for emailing user the verification code
        buildConfigField("String", "EMAILPASSWORD", "\"xedr gaek jdsv ujxw\"") // Gmail Password
        buildConfigField("String", "SERVERIP", "\"http:192.168.?.?:3000\"") //Server IP address
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
//    ndk {
//        abiFilters("armeabi-v7a", "x86")
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8


    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"
    }

    packaging {
        resources {
            excludes += setOf(
                    "/META-INF/{AL2.0,LGPL2.1}",
                    "/META-INF/DEPENDENCIES",
                    "/META-INF/LICENSE",
                    "/META-INF/LICENSE.txt",
                    "/META-INF/NOTICE",
                    "/META-INF/NOTICE.txt",
                    "META-INF/INDEX.LIST"
            )
        }
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
        getByName("main").resources.srcDirs("src/main/resources")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(platform(libs.compose.bom))
    implementation(files("libs/activation.jar"))
    implementation(files("libs/mail.jar"))
    implementation(libs.preference)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation("androidx.core:core-ktx:1.6.0")
    implementation(libs.activity)

    // Merge testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    debugImplementation(libs.ui.tooling)
    implementation(libs.javax.mail.api)
    implementation(libs.gson)
    debugImplementation(libs.ui.test.manifest)

    // External libraries from feature/ocr-functionality
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.simplexml)

    // Google Cloud Vision API
    implementation(libs.google.cloud.vision.v11000)

    // Google Cloud Translate API
    implementation(libs.google.cloud.translate.v1950)

    // gRPC and Protobuf dependencies
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.auth)
    implementation(libs.grpc.core)

    // For email system
    implementation(files("libs/activation.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(files("libs/mail.jar"))

    // Additional dependencies from master
    implementation(files("libs/sqliteassethelper-2.0.1.jar"))
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.11.0")
    implementation("com.google.android.material:material:1.2.0-alpha02")
    implementation(files("libs/activation.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(files("libs/mail.jar"))
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("com.tbuonomo:dotsindicator:4.3")

}
