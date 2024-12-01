package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.AppPackage
import org.example.project.appinstaller.model.AppVersion
import org.example.project.appinstaller.model.BuildVariant
import kotlin.test.Test
import kotlin.test.assertEquals

class ResolvePackageUrlUseCaseTest{

    @Test
    fun `resolve url where the location ends with a slash`(){
        val resolver = ResolvePackageUrlUseCase()
        val buildVariant = BuildVariant("name", "https://www.example.com/", emptyMap(), emptyList())
        val appPackage = AppPackage("name", "packageName", "releaseR{major}.{minor}.{micro}_{build}.apk")
        val appVersion = AppVersion(
            "1",
            "2",
            "3",
            "4",
        )
        val url = resolver(buildVariant, appPackage, appVersion, "manufacturer")
        assertEquals("https://www.example.com/releaseR1.2.3_4.apk", url)
    }

    @Test
    fun `resolve url where the location ends without a slash`(){
        val resolver = ResolvePackageUrlUseCase()
        val buildVariant = BuildVariant("name", "https://www.example.com", emptyMap(), emptyList())
        val appPackage = AppPackage("name", "packageName", "releaseR{major}.{minor}.{micro}_{build}.apk")
        val appVersion = AppVersion(
            "1",
            "2",
            "3",
            "4",
        )
        val url = resolver(buildVariant, appPackage, appVersion, "manufacturer")
        assertEquals("https://www.example.com/releaseR1.2.3_4.apk", url)
    }

    @Test
    fun `resolve url where the location includes manufacturer with mapping available`(){
        val resolver = ResolvePackageUrlUseCase()
        val buildVariant = BuildVariant("name", "https://www.example.com", mapOf(
            "manufacturer" to "man"
        ), emptyList())
        val appPackage = AppPackage("name", "packageName", "releaseR{major}.{minor}.{micro}_{build}_{device}.apk")
        val appVersion = AppVersion(
            "1",
            "2",
            "3",
            "4",
        )
        val url = resolver(buildVariant, appPackage, appVersion, "manufacturer")
        assertEquals("https://www.example.com/releaseR1.2.3_4_man.apk", url)
    }

}