package org.example.project.appinstaller.repository.credential.json

import kotlinx.serialization.Serializable

@Serializable
data class CredentialDto(val user: String, val pass: String)