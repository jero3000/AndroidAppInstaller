package com.jero3000.appinstaller.platform.uri

interface UriParser {
    fun getProtocol(uri: String): String
    fun getHost(uri: String): String
    fun getPath(uri: String): String
    fun getFilename(uri: String): String
}