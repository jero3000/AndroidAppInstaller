package com.jero3000.appinstaller.repository.credential.json

import com.jero3000.appinstaller.model.Credential

fun CredentialDto.toCredential() = Credential(user, pass)

fun Credential.toCredentialDto() = CredentialDto(user, password)
