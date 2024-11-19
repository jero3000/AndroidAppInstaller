package org.example.project.appinstaller.platform.device

interface DeviceManager {
    /**
     * Scans for new devices
     */
    suspend fun scan(): Result<List<Device>>

    /**
     * Get a device that was previously discovered
     */
    suspend fun getDevice(serial: String): Device?
}