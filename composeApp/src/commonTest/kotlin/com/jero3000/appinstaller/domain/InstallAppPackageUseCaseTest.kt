package com.jero3000.appinstaller.domain

import com.jero3000.appinstaller.model.AppPackage
import com.jero3000.appinstaller.platform.device.Device
import com.jero3000.appinstaller.platform.device.DeviceManager
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertTrue

class InstallAppPackageUseCaseTest {

    @Test
    fun `If no device is found then a failed result is returned`() = runTest{
        val deviceManager = mock<DeviceManager>(MockMode.autofill)
        val installPackage = InstallAppPackageUseCase(deviceManager)
        val pkg = AppPackage("", "", "", null)
        val result = installPackage("serial", pkg, Device.InstallMode.DOWNGRADE.key)
        assertTrue(result.isFailure)
    }

    @Test
    fun `If the package was not downloaded, then a failed result is returned`() = runTest{
        val deviceManager = mock<DeviceManager>(MockMode.autofill){
            everySuspend { getDevice(any()) } returns mock()
        }
        val installPackage = InstallAppPackageUseCase(deviceManager)
        val pkg = AppPackage("", "", "", null)
        val result = installPackage("serial", pkg, Device.InstallMode.DOWNGRADE.key)
        assertTrue(result.isFailure)
    }


    @Test
    fun `Install function from device is called with the correct parameters`() = runTest{
        val device = mock<Device>(MockMode.autofill)
        val deviceManager = mock<DeviceManager>(MockMode.autofill){
            everySuspend { getDevice(any()) } returns device
        }
        val installPackage = InstallAppPackageUseCase(deviceManager)
        val pkg = AppPackage("", "", "", mock{
            every { getExists() } returns true
        })
        installPackage("serial", pkg, Device.InstallMode.DOWNGRADE.key)
        verifySuspend { device.install(pkg, Device.InstallMode.DOWNGRADE) }
    }
}