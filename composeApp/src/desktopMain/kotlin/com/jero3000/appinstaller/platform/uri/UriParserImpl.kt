package com.jero3000.appinstaller.platform.uri

import java.net.URI
import kotlin.io.path.Path
import kotlin.io.path.name

actual class UriParserImpl: UriParser {
    override fun getProtocol(uri: String) = URI(uri).scheme!!

    override fun getHost(uri: String) = URI(uri).host!!

    override fun getPath(uri: String) = URI(uri).path!!

    override fun getFilename(uri: String) = Path(URI(uri).path).name
}