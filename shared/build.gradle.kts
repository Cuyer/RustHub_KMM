import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.sqlDelightGradlePlugin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokoMultiplatformResources)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.protobuf)
}

val rusthubEnvironment = providers.gradleProperty("RUSTHUB_ENVIRONMENT").orElse("production").get()
val isDevelopment = rusthubEnvironment.equals("development", ignoreCase = true)

kotlin {
    androidLibrary {
        namespace = "pl.cuyer.rusthub"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        localDependencySelection {
            selectBuildTypeFrom.set(listOf("debug", "release"))
            productFlavorDimension("mode") {
                selectFrom.set(listOf("development", "production"))
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
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
            implementation(libs.sqlcipher.android)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.security.crypto)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.compose)
            implementation(libs.androidx.navigation)
            implementation(libs.androidx.work.runtime)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.certificate.transparency)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.messaging)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.appcheck.playintegrity)
            implementation(libs.firebase.appcheck.debug)
            implementation(libs.firebase.config)
            implementation(libs.google.auth)
            implementation(libs.androidx.credentials)
            implementation(libs.google.identity)
            implementation(libs.kotlin.coroutines.play.services)
            implementation(libs.google.play.app.update)
            implementation(libs.google.play.app.update.ktx)
            implementation(libs.play.review.ktx)
            implementation(libs.play.billing)
            implementation(libs.androidx.appcompat)
            implementation(libs.play.services.ads)
            implementation(libs.user.messaging.platform)
            implementation(libs.play.integrity)
        }
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.compose.runtime)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.auth)
            implementation(libs.ktor.client.encoding)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.datastore)
            implementation(libs.kotlin.serialization.protobuf)
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

multiplatformResources {
    resourcesPackage.set("pl.cuyer.rusthub")
    resourcesClassName.set("SharedRes")
}

buildkonfig {
    packageName = "pl.cuyer.rusthub"
    exposeObjectWithName = "SharedBuildConfig"
    defaultConfigs {
        buildConfigField(STRING, "VERSION_NAME", project.property("VERSION_NAME") as String)
        buildConfigField(
            STRING,
            "BASE_URL",
            if (isDevelopment) "https://api.dev.rusthub.me/" else "https://api.rusthub.me/",
        )
        buildConfigField(
            STRING,
            "PRIVACY_POLICY_URL",
            if (isDevelopment) "http://localhost:5173/privacy" else "https://rusthub.me/privacy",
        )
        buildConfigField(
            STRING,
            "TERMS_URL",
            if (isDevelopment) "http://localhost:5173/terms" else "https://rusthub.me/terms",
        )
        buildConfigField(BOOLEAN, "USE_ENCRYPTED_DB", (!isDevelopment).toString())
        buildConfigField(BOOLEAN, "IS_DEBUG_BUILD", isDevelopment.toString())
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

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
}
