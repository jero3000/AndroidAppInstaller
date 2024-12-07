package com.jero3000.appinstaller.repository.domain

import com.jero3000.appinstaller.domain.DiscoverDevicesUseCase
import com.jero3000.appinstaller.platform.device.Device
import com.jero3000.appinstaller.platform.device.DeviceManager
import dev.mokkery.answering.returnsSuccess
import dev.mokkery.answering.sequentially
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class DiscoverDevicesUseCaseTest {
    @Test
    fun `flow returns the device list obtained from DeviceManager scan result`() = runTest {
        val device = mock<Device>{
            everySuspend { getManufacturer() } returnsSuccess  "man"
            everySuspend { getModel() } returnsSuccess  "model"
            everySuspend { getSerial() } returnsSuccess  "serial"
        }
        val deviceManager = mock<DeviceManager>{
            everySuspend { scan() } returnsSuccess listOf(device)
        }
        val discoverFlow = DiscoverDevicesUseCase(deviceManager)
        val devices = discoverFlow().first()
        assertEquals(devices.size, 1)
        assertEquals(devices.first().manufacturer, "man")
        assertEquals(devices.first().serial, "serial")
    }

    @Test
    fun `when new devices are added, then the flow returns an updated list`() = runTest{
        val device1 = mock<Device>{
            everySuspend { getManufacturer() } returnsSuccess  "man1"
            everySuspend { getModel() } returnsSuccess  "model1"
            everySuspend { getSerial() } returnsSuccess  "serial1"
        }
        val device2 = mock<Device>{
            everySuspend { getManufacturer() } returnsSuccess  "man2"
            everySuspend { getModel() } returnsSuccess  "model2"
            everySuspend { getSerial() } returnsSuccess  "serial2"
        }
        val deviceManager = mock<DeviceManager>{
            everySuspend { scan() } sequentially {
                returnsSuccess(listOf(device1))
                returnsSuccess(listOf(device1, device2))
            }
        }
        val discoverFlow = DiscoverDevicesUseCase(deviceManager)
        val flow = discoverFlow()
        val devices1 = flow.first()
        assertEquals(devices1.size, 1)
        assertEquals(devices1.first().manufacturer, "man1")
        val devices2 = flow.first()
        assertEquals(devices2.size, 2)
        assertEquals(devices2.last().manufacturer, "man2")

    }
}