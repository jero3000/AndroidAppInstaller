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
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.commons.net)
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
        mainClass = "org.example.project.appinstaller.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project.appinstaller"
            packageVersion = "1.0.0"
        }
    }
}
