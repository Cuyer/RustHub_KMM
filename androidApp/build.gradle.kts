plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.performance)
}

android {
    namespace = "pl.cuyer.rusthub.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "pl.cuyer.rusthub.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = project.property("VERSION_NAME") as String
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(17)
    }

    signingConfigs {
        create("development") {
            storeFile = rootProject.file("androidApp/keystore-dev.jks")
            storePassword = System.getenv("DEV_STORE_PASSWORD") ?: "2f032facZ@"
            keyAlias = System.getenv("DEV_SIGNING_KEY_ALIAS") ?: "androiddev"
            keyPassword = System.getenv("DEV_SIGNING_KEY_PASSWORD") ?: "2f032facZ@"
        }
        create("production") {
            storeFile = rootProject.file("androidApp/keystore-prod.jks")
            storePassword = System.getenv("PROD_STORE_PASSWORD")
            keyAlias = System.getenv("PROD_SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("PROD_SIGNING_KEY_PASSWORD")
        }
    }

    flavorDimensions += "mode"
    productFlavors {
        create("production") {
            dimension = "mode"
            signingConfig = signingConfigs.getByName("production")
        }
        create("development") {
            dimension = "mode"
            applicationIdSuffix = ".development"
            signingConfig = signingConfigs.getByName("development")
        }
    }



    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            signingConfig = signingConfigs.getByName("development")
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(project.dependencies.platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.kotlin.datetime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.viewmodel)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.androidx.compose.material3.windowsizeclass)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.compose.icons)
    implementation(libs.paging.compose)
    implementation(libs.moko.permissions.compose)
    implementation(libs.moko.permissions.notifications)
    implementation(libs.kotlin.serialization)
    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.performance)
    implementation(libs.google.auth)
    implementation(libs.play.review.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.compat)
    implementation(libs.google.play.app.update)
    implementation(libs.google.play.app.update.ktx)
    coreLibraryDesugaring(libs.desugar.jdk.libs.v215)
    debugImplementation(libs.compose.ui.tooling)
}
