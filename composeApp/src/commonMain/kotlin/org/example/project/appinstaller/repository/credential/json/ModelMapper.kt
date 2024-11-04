package org.example.project.appinstaller.repository.credential.json

import org.example.project.appinstaller.model.Credential

fun CredentialDto.toCredential() = Credential(user, pass)

fun Credential.toCredentialDto() = CredentialDto(user, password)
