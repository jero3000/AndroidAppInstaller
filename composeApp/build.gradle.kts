import dev.mokkery.gradle.mokkery
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinxDatetime)
            implementation(libs.kotlinxSerializationJson)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.kotlinx.io)
            implementation(libs.kmpFile)
            implementation(compose.materialIconsExtended)
            implementation(libs.multiplatform.settings)
            implementation(libs.ktor.core)
            implementation(libs.cryptography.core)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.lifecycle.kmp)
            implementation(libs.filekit.core)
            implementation(libs.filekit.compose)
            implementation(libs.kmpFile.filekit)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.commons.net)
            implementation(libs.multiplatform.settings)
            implementation(libs.cryptography.provider.jdk)
            implementation(libs.appdirs)
            implementation(libs.dadb)
            implementation(libs.jadb)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(mokkery("coroutines"))
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.koin.test)
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.jero3000.appinstaller.MainKt"
        val appVersion = "1.0.0"
        jvmArgs += listOf("-DversionName=$appVersion")
        nativeDistributions {
            modules("java.instrument", "java.management", "java.prefs", "jdk.security.auth", "jdk.unsupported")
            linux {
                iconFile.set(project.file("src/desktopMain/resources/installer_icon.png"))
            }
            macOS{
                iconFile.set(project.file("src/desktopMain/resources/installer_icon.icns"))
            }
            windows{
                iconFile.set(project.file("src/desktopMain/resources/installer_icon.ico"))
            }
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.jero3000.appinstaller"
            packageVersion = appVersion
            description = "Android application installer"
            copyright = "© 2024 Jerónimo Muñoz. MIT license"
            licenseFile.set(project.file("../LICENSE"))
        }
    }
}
