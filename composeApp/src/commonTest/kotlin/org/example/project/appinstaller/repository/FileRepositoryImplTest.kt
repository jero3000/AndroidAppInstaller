package org.example.project.appinstaller.repository

import dev.mokkery.mock
import org.example.project.appinstaller.platform.filesystem.FileUtils
import org.example.project.appinstaller.platform.filesystem.PlatformFileSystem
import org.example.project.appinstaller.platform.uri.UriParser
import org.example.project.appinstaller.repository.credential.CredentialRepository
import org.example.project.appinstaller.repository.file.FileRepositoryImpl
import org.example.project.appinstaller.repository.file.datasource.FileDataSource
import org.junit.Test
import dev.mokkery.MockMode.autofill
import dev.mokkery.MockMode.autoUnit
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsArgAt
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import dev.zwander.kotlin.file.IPlatformFile
import kotlinx.coroutines.test.runTest
import org.example.project.appinstaller.model.Credential
import org.example.project.appinstaller.model.exception.CredentialsRequiredException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class FileRepositoryImplTest {

    @Test
    fun `Getting a file checks if the the file exists in the correct path for the temporal directory`(){
        val fileDataSource = mock<FileDataSource>()
        val fileSystem = mock<PlatformFileSystem>(autofill){
            every { getTempDirectory() } returns "/tmp"
            every { getFileSeparator() } returns "/"
            every { getUri(any()) } returnsArgAt 0
        }
        val uriParser = mock<UriParser>(autofill){
            every { getFilename(any()) } returns "file.apk"
        }
        val credentialsRepo = mock<CredentialRepository>()
        val fileMock = mock<IPlatformFile>{
            every { getExists() } returns true
        }
        val fileUtils = mock<FileUtils> {
            every { getFileFromPath(any(), any()) } returns fileMock
        }
        val repository = FileRepositoryImpl(fileDataSource, fileSystem, uriParser, credentialsRepo, fileUtils)
        val file = repository.getFile("http://test.com/file.apk")
        assertNotNull(file)
        assertEquals(file, fileMock)
        verify{ fileUtils.getFileFromPath(eq("/tmp/AndroidAppInstaller/file.apk"), any()) }
    }

    @Test
    fun `Getting a file checks returns null if the file does not exist`(){
        val fileDataSource = mock<FileDataSource>()
        val fileSystem = mock<PlatformFileSystem>(autofill){
            every { getTempDirectory() } returns "/tmp"
            every { getFileSeparator() } returns "/"
            every { getUri(any()) } returnsArgAt 0
        }
        val uriParser = mock<UriParser>(autofill){
            every { getFilename(any()) } returns "file.apk"
        }
        val credentialsRepo = mock<CredentialRepository>()
        val fileMock = mock<IPlatformFile>{
            every { getExists() } returns false
        }
        val fileUtils = mock<FileUtils> {
            every { getFileFromPath(any(), any()) } returns fileMock
        }
        val repository = FileRepositoryImpl(fileDataSource, fileSystem, uriParser, credentialsRepo, fileUtils)
        val file = repository.getFile("http://test.com/file.apk")
        assertNull(file)
        verify{ fileUtils.getFileFromPath(eq("/tmp/AndroidAppInstaller/file.apk"), any()) }
    }

    @Test
    fun `Fetching a file ensures that the temporal directory is already created`() = runTest {
        val fileMock = mock<IPlatformFile>()
        val fileDataSource = mock<FileDataSource>{
            everySuspend { getFile(any(), any(), any()) } returns Result.success(fileMock)
        }
        val fileSystem = mock<PlatformFileSystem>(autofill){
            every { getTempDirectory() } returns "/tmp"
            every { getFileSeparator() } returns "/"
            every { getUri(any()) } returnsArgAt 0
        }
        val uriParser = mock<UriParser>(autofill){
            every { getFilename(any()) } returns "file.apk"
        }
        val credentialsRepo = mock<CredentialRepository>{
            everySuspend { getCredential(any()) } returns null
        }
        val dirMock = mock<IPlatformFile>(autoUnit){
            every { getExists() } returns false
            every { mkdir() } returns true
        }
        val fileUtils = mock<FileUtils> {
            every { getFileFromPath(any(), any()) } returns dirMock
        }
        val repository = FileRepositoryImpl(fileDataSource, fileSystem, uriParser, credentialsRepo, fileUtils)
        repository.fetchFile("http://test.com/file.apk")
        verify { fileUtils.getFileFromPath(eq("/tmp/AndroidAppInstaller"), eq(true)) }
        verify { dirMock.getExists() }
        verify { dirMock.mkdir() }
    }

    @Test
    fun `Fetching a file checks for available credentials for the host name and fetches from data source using those credentials`() = runTest{
        val fileMock = mock<IPlatformFile>()
        val fileDataSource = mock<FileDataSource>{
            everySuspend { getFile(any(), any(), any()) } returns Result.success(fileMock)
        }
        val fileSystem = mock<PlatformFileSystem>(autofill){
            every { getTempDirectory() } returns "/tmp"
            every { getFileSeparator() } returns "/"
            every { getUri(any()) } returnsArgAt 0
        }
        val uriParser = mock<UriParser>(autofill){
            every { getFilename(any()) } returns "file.apk"
        }
        val credential = Credential("user", "pass")
        val credentialsRepo = mock<CredentialRepository>() {
            everySuspend { getCredential(any()) } returns credential
        }
        val dirMock = mock<IPlatformFile>(autoUnit){
            every { getExists() } returns false
            every { mkdir() } returns true
        }
        val fileUtils = mock<FileUtils> {
            every { getFileFromPath(any(), any()) } returns dirMock
        }
        val repository = FileRepositoryImpl(fileDataSource, fileSystem, uriParser, credentialsRepo, fileUtils)
        val result = repository.fetchFile("http://test.com/file.apk")
        verifySuspend { credentialsRepo.getCredential(eq("test.com")) }
        verifySuspend { fileDataSource.getFile(eq("http://test.com/file.apk"), eq("/tmp/AndroidAppInstaller"), eq(credential)) }
        assertTrue(result.isSuccess)
    }

    @Test
    fun `Fetching a file clears the credentials if the fetching from data source failed due to a wrong login`() = runTest {
        val fileDataSource = mock<FileDataSource>{
            everySuspend { getFile(any(), any(), any()) } returns Result.failure(CredentialsRequiredException("test.com"))
        }
        val fileSystem = mock<PlatformFileSystem>(autofill){
            every { getTempDirectory() } returns "/tmp"
            every { getFileSeparator() } returns "/"
            every { getUri(any()) } returnsArgAt 0
        }
        val uriParser = mock<UriParser>(autofill){
            every { getFilename(any()) } returns "file.apk"
        }
        val credential = Credential("user", "pass")
        val credentialsRepo = mock<CredentialRepository>(autoUnit){
            everySuspend { getCredential(any()) } returns credential
        }
        val dirMock = mock<IPlatformFile>(autoUnit){
            every { getExists() } returns false
            every { mkdir() } returns true
        }
        val fileUtils = mock<FileUtils> {
            every { getFileFromPath(any(), any()) } returns dirMock
        }
        val repository = FileRepositoryImpl(fileDataSource, fileSystem, uriParser, credentialsRepo, fileUtils)
        val file = repository.fetchFile("http://test.com/file.apk")
        verifySuspend { credentialsRepo.getCredential(eq("test.com")) }
        verifySuspend { fileDataSource.getFile(eq("http://test.com/file.apk"), eq("/tmp/AndroidAppInstaller"), eq(credential)) }
        assertFalse(file.isSuccess)
        verifySuspend { credentialsRepo.deleteCredential(eq("test.com")) }
    }
}