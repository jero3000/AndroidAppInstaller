package org.example.project.appinstaller.domain

import org.example.project.appinstaller.model.AppPackage
import org.example.project.appinstaller.model.BuildVariant
import kotlin.test.Test
import kotlin.test.assertEquals

class ResolvePackageUrlUseCaseTest{

    @Test
    fun `resolve url where the location ends with a slash`(){
        val resolver = ResolvePackageUrlUseCase()
        val buildVariant = BuildVariant("name", "https://www.example.com/", emptyMap(), emptyList())
        val appPackage = AppPackage("name", "packageName", "releaseR{major}.{minor}.{micro}_{build}.apk")
        val placeholders = mapOf(
            ResolvePackageUrlUseCase.MAJOR_PLACEHOLDER to "1",
            ResolvePackageUrlUseCase.MINOR_PLACEHOLDER to "2",
            ResolvePackageUrlUseCase.MICRO_PLACEHOLDER to "3",
            ResolvePackageUrlUseCase.BUILD_PLACEHOLDER to "4",
        )
        val url = resolver(buildVariant, appPackage, placeholders)
        assertEquals("https://www.example.com/releaseR1.2.3_4.apk", url)
    }

    @Test
    fun `resolve url where the location ends without a slash`(){
        val resolver = ResolvePackageUrlUseCase()
        val buildVariant = BuildVariant("name", "https://www.example.com", emptyMap(), emptyList())
        val appPackage = AppPackage("name", "packageName", "releaseR{major}.{minor}.{micro}_{build}.apk")
        val placeholders = mapOf(
            ResolvePackageUrlUseCase.MAJOR_PLACEHOLDER to "1",
            ResolvePackageUrlUseCase.MINOR_PLACEHOLDER to "2",
            ResolvePackageUrlUseCase.MICRO_PLACEHOLDER to "3",
            ResolvePackageUrlUseCase.BUILD_PLACEHOLDER to "4",
        )
        val url = resolver(buildVariant, appPackage, placeholders)
        assertEquals("https://www.example.com/releaseR1.2.3_4.apk", url)
    }
}