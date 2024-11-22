package org.example.project.appinstaller.platform.intent

import java.awt.Desktop
import java.net.URI

class BrowserLauncherImpl : BrowserLauncher {
    override fun launchUrl(url: String) {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            desktop.browse(URI(url))
        }
    }
}
