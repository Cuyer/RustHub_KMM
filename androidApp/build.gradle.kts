plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.performance)
    alias(libs.plugins.ossLicenses)
}

composeCompiler {
/*    reportsDestination = layout.buildDirectory.dir("composeReports")
    metricsDestination = layout.buildDirectory.dir("composeMetrics")
    includeSourceInformation.set(true)*/
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

android {
    namespace = "pl.cuyer.rusthub.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "pl.cuyer.rusthub.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 106
        versionName = project.property("VERSION_NAME") as String
        buildConfigField("String", "SERVER_DETAILS_ADMOB_NATIVE_AD_ID", "\"ca-app-pub-4286204280518303/9767963470\"")
        buildConfigField("String", "SERVERS_ADMOB_NATIVE_AD_ID", "\"ca-app-pub-4286204280518303/4096035325\"")
        buildConfigField("String", "ITEMS_ADMOB_NATIVE_AD_ID", "\"ca-app-pub-4286204280518303/1469871989\"")
        buildConfigField("String", "MONUMENTS_ADMOB_NATIVE_AD_ID", "\"ca-app-pub-4286204280518303/8411157612\"")
    }

    androidResources {
        generateLocaleConfig = true
        localeFilters.addAll(listOf("pl", "en", "de", "fr", "ru", "es", "uk", "pt"))
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

    bundle {
        language {
            enableSplit = false
        }
    }

    signingConfigs {
        create("development") {
            storeFile = rootProject.file("androidApp/keystore-dev.jks")
            storePassword = System.getenv("DEV_STORE_PASSWORD")
            keyAlias = System.getenv("DEV_SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("DEV_SIGNING_KEY_PASSWORD")
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(project.dependencies.platform(libs.compose.bom))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.kotlin.datetime)
    implementation(libs.androidx.activity.compose)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.viewmodel)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.material3.adaptive.navigation3) {
        isChanging = true
    }
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
    implementation(libs.play.billing)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.compat)
    implementation(libs.google.play.app.update)
    implementation(libs.google.play.app.update.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.ads)
    implementation(libs.play.services.oss.licenses)
    coreLibraryDesugaring(libs.desugar.jdk.libs.v215)
    debugImplementation(libs.compose.ui.tooling)
}

/*tasks.register("printComposeMetrics") {
    group = "compose"
    description = "Prints Compose compiler metrics and reports"
    dependsOn("assemble")
    doLast {
        val metricsDir = layout.buildDirectory.dir("composeMetrics").get().asFile
        val reportsDir = layout.buildDirectory.dir("composeReports").get().asFile
        println("Compose metrics location: $metricsDir")
        metricsDir.walk().filter { it.isFile }.forEach { println(it.readText()) }
        println("Compose reports location: $reportsDir")
        reportsDir.walk().filter { it.isFile }.forEach { println(it.readText()) }
    }
}*/
