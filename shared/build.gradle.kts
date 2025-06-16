import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelightGradlePlugin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokoMultiplatformResources)
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
            implementation(libs.koin.android)
            implementation(libs.androidx.navigation)
            implementation(libs.ktor.client.okhttp)
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
    buildTypes {
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard.pro")
            isMinifyEnabled = false
            isJniDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
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

sqldelight {
    databases {
        create("RustHubDatabase") {
            dialect(libs.sqldelight.dialect.sqlite)
            packageName.set("pl.cuyer.rusthub.database")
        }
        linkSqlite.set(true)
    }
}