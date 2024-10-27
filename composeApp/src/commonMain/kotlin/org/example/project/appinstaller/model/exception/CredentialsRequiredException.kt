package org.example.project.appinstaller.model.exception

class CredentialsRequiredException(val host: String): Exception("Credentials required for host: $host")