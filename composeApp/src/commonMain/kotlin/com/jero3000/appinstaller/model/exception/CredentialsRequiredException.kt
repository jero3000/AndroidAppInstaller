package com.jero3000.appinstaller.model.exception

class CredentialsRequiredException(val host: String): Exception("Credentials required for host: $host")