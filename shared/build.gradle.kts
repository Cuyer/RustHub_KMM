import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelightGradlePlugin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokoMultiplatformResources)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        homepage = "https://github.com/Cuyer"
        summary = "Application shared module"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = false
            export(libs.moko.resources)
        }
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.sql.delight.android.driver)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.compose)
            implementation(libs.androidx.navigation)
            implementation(libs.androidx.work.runtime)
            implementation(libs.ktor.client.okhttp)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.messaging)
            implementation(libs.google.auth)
            implementation(libs.androidx.credentials)
            implementation(libs.google.identity)
            implementation(libs.kotlin.coroutines.play.services)
        }
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.auth)
            implementation(libs.sql.delight.runtime)
            implementation(libs.sql.delight.coroutines.extensions)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.kotlin.datetime)
            implementation(libs.napier)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime)
            implementation(libs.paging)
            implementation(libs.sqldelight.extensions.paging3)
            api(libs.moko.resources)
            implementation(libs.moko.permissions)
            implementation(libs.moko.permissions.notifications)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.sql.delight.native.driver)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "pl.cuyer.rusthub"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    flavorDimensions += "mode"
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

    productFlavors {
        create("production") {
            dimension = "mode"
            buildConfigField("String", "BASE_URL", "\"https://api.rusthub.me/\"")
            buildConfigField("String", "PRIVACY_POLICY_URL", "\"https://rusthub.me/privacy\"")
            buildConfigField("String", "TERMS_URL", "\"https://rusthub.me/terms\"")
            signingConfig = signingConfigs.getByName("production")
        }
        create("development") {
            dimension = "mode"
            buildConfigField("String", "BASE_URL", "\"https://api.dev.rusthub.me/\"")
            buildConfigField("String", "PRIVACY_POLICY_URL", "\"http://localhost:5173/privacy\"")
            buildConfigField("String", "TERMS_URL", "\"http://localhost:5173/terms\"")
            signingConfig = signingConfigs.getByName("development")
        }
    }

    buildFeatures {
        buildConfig = true
    }


    
    buildTypes {
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard.pro")
            isMinifyEnabled = false
            isJniDebuggable = false
        }

        getByName("debug") {
            signingConfig = signingConfigs.getByName("development")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

multiplatformResources {
    resourcesPackage.set("pl.cuyer.rusthub")
    resourcesClassName.set("SharedRes")
}

buildkonfig {
    packageName = "pl.cuyer.rusthub"
    exposeObjectWithName = "SharedBuildConfig"
    defaultConfigs {
        buildConfigField(STRING, "VERSION_NAME", project.property("VERSION_NAME") as String)
    }
}

sqldelight {
    databases {
        create("RustHubDatabase") {
            dialect(libs.sqldelight.dialect.sqlite)
            packageName.set("pl.cuyer.rusthub.database")
        }
        linkSqlite.set(true)
    }
}
