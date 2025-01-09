package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.AppPackage
import com.jero3000.appinstaller.model.AppVersion
import com.jero3000.appinstaller.model.BuildVariant
import kotlin.test.Test
import kotlin.test.assertEquals

class ResolvePackageUrlUseCaseTest{
    @Test
    fun `concatenation test 1`() {
        val variant = BuildVariant("", "ftp://host", emptyMap(), emptyList())
        val pkg = AppPackage("", "", "path/file.txt", emptyMap(), null)
        val version = AppVersion("1", "2", "3", "1")
        val resolveUrl = ResolvePackageUrlUseCase()
        val result = resolveUrl(variant, pkg, version, "man")
        assertEquals("ftp://host/path/file.txt", result)
    }

    @Test
    fun `concatenation test 2`() {
        val variant = BuildVariant("", "ftp://host/", emptyMap(), emptyList())
        val pkg = AppPackage("", "", "path/file.txt", emptyMap(), null)
        val version = AppVersion("1", "2", "3", "1")
        val resolveUrl = ResolvePackageUrlUseCase()
        val result = resolveUrl(variant, pkg, version, "man")
        assertEquals("ftp://host/path/file.txt", result)
    }

    @Test
    fun `concatenation test 3`() {
        val variant = BuildVariant("", "ftp://host", emptyMap(), emptyList())
        val pkg = AppPackage("", "", "/path/file.txt", emptyMap(), null)
        val version = AppVersion("1", "2", "3", "1")
        val resolveUrl = ResolvePackageUrlUseCase()
        val result = resolveUrl(variant, pkg, version, "man")
        assertEquals("ftp://host/path/file.txt", result)
    }

    @Test
    fun `concatenation test 4`() {
        val variant = BuildVariant("", "ftp://host/", emptyMap(), emptyList())
        val pkg = AppPackage("", "", "/path/file.txt", emptyMap(), null)
        val version = AppVersion("1", "2", "3", "1")
        val resolveUrl = ResolvePackageUrlUseCase()
        val result = resolveUrl(variant, pkg, version, "man")
        assertEquals("ftp://host/path/file.txt", result)
    }

    @Test
    fun `ensure all placeholders are replaced`() {
        val variant = BuildVariant("", "ftp://host{major}_{minor}_{micro}_{build}_{device}", emptyMap(), emptyList())
        val pkg = AppPackage("", "", "/path/file{major}_{minor}_{micro}_{build}_{device}.txt", emptyMap(), null)
        val version = AppVersion("1", "2", "3", "1")
        val resolveUrl = ResolvePackageUrlUseCase()
        val result = resolveUrl(variant, pkg, version, "man")
        assertEquals("ftp://host1_2_3_1_man/path/file1_2_3_1_man.txt", result)
    }

    @Test
    fun `ensure all modifiers are processed`() {
        val variant = BuildVariant("", "ftp://host{device:uppercase}_{device:lowercase}_{device:camelcase}", emptyMap(), emptyList())
        val pkg = AppPackage("", "", "/path/file{major}_{minor}_{micro}_{build}_{device}.txt", emptyMap(), null)
        val version = AppVersion("1", "2", "3", "1")
        val resolveUrl = ResolvePackageUrlUseCase()
        val result = resolveUrl(variant, pkg, version, "man")
        assertEquals("ftp://hostMAN_man_Man/path/file1_2_3_1_man.txt", result)
    }

    @Test
    fun `ensure alternate paths are applied when required`() {
        val variant = BuildVariant("", "ftp://host{device:uppercase}_{device:lowercase}_{device:camelcase}", emptyMap(), emptyList())
        val pkg = AppPackage("", "", "/path/file{major}_{minor}_{micro}_{build}_{device}.txt",
            mapOf("man" to "/MaN/file{major}_{minor}_{micro}_{build}_{device}.txt"),
            null)
        val version = AppVersion("1", "2", "3", "1")
        val resolveUrl = ResolvePackageUrlUseCase()
        val result = resolveUrl(variant, pkg, version, "man")
        assertEquals("ftp://hostMAN_man_Man/MaN/file1_2_3_1_man.txt", result)
    }
}