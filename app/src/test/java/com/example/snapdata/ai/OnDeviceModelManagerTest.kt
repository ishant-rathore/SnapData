package com.example.snapdata.ai

import android.content.Context
import android.content.SharedPreferences
import com.example.snapdata.ai.model.ModelDownloadProgress
import com.example.snapdata.ai.model.ModelMetadata
import com.example.snapdata.ai.model.ModelStatus
import com.example.snapdata.ai.model.OnDeviceModelManager
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class OnDeviceModelManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var mockContext: Context
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private val fakePrefsData = mutableMapOf<String, Any>()

    private lateinit var testFilesDir: File
    private lateinit var testMetadata: ModelMetadata

    @Before
    fun setUp() {
        testFilesDir = tempFolder.newFolder("filesDir")
        testMetadata = ModelMetadata(
            modelId = "test-doc-ai-v1",
            localFileName = "test_model_v1.bin",
            expectedSizeBytes = 2048L,
            requiredStorageBytes = 1000L,
            downloadUrl = "mock://local"
        )

        mockContext = mockk(relaxed = true)
        mockPrefs = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        every { mockContext.filesDir } returns testFilesDir
        every { mockContext.getSharedPreferences(any(), any()) } returns mockPrefs

        every { mockPrefs.getString(any(), any()) } answers {
            val key = firstArg<String>()
            val def = secondArg<String?>()
            fakePrefsData[key] as? String ?: def
        }
        every { mockPrefs.getLong(any(), any()) } answers {
            val key = firstArg<String>()
            val def = secondArg<Long>()
            fakePrefsData[key] as? Long ?: def
        }

        every { mockPrefs.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } answers {
            fakePrefsData[firstArg<String>()] = secondArg<String>()
            mockEditor
        }
        every { mockEditor.putLong(any(), any()) } answers {
            fakePrefsData[firstArg<String>()] = secondArg<Long>()
            mockEditor
        }
        every { mockEditor.remove(any()) } answers {
            fakePrefsData.remove(firstArg<String>())
            mockEditor
        }
        every { mockEditor.apply() } just Runs
    }

    @After
    fun tearDown() {
        unmockkAll()
        fakePrefsData.clear()
    }

    @Test
    fun testModelStatusInitialNotInstalledWhenFileMissing() {
        val manager = OnDeviceModelManager(mockContext, testMetadata)
        assertEquals(ModelStatus.NOT_INSTALLED, manager.status.value)
        assertFalse(manager.modelFile.exists())
    }

    @Test
    fun testDownloadModelAndVerifySuccess() = runTest {
        val manager = OnDeviceModelManager(mockContext, testMetadata)
        val result = manager.downloadModel()

        assertTrue(result.isSuccess)
        val file = result.getOrThrow()
        assertTrue(file.exists())
        assertTrue(file.length() > 0)
        assertEquals(ModelStatus.READY, manager.status.value)
        assertTrue(manager.lastVerifiedTimestamp.value > 0L)
    }

    @Test
    fun testCorruptedModelFileFailsVerification() = runTest {
        val manager = OnDeviceModelManager(mockContext, testMetadata)
        val corruptFile = File(manager.modelsDirectory, "corrupt.bin")
        corruptFile.writeText("INVALID_HEADER_GARBAGE_DATA")

        val isValid = manager.verifyModelFile(corruptFile)
        assertFalse(isValid)
    }

    @Test
    fun testDeleteModelRemovesFileAndResetsStatus() = runTest {
        val manager = OnDeviceModelManager(mockContext, testMetadata)
        val downloadRes = manager.downloadModel()
        assertTrue(downloadRes.isSuccess)
        assertTrue(manager.modelFile.exists())

        val deleted = manager.deleteModel()
        assertTrue(deleted)
        assertFalse(manager.modelFile.exists())
        assertEquals(ModelStatus.NOT_INSTALLED, manager.status.value)
        assertEquals(0L, manager.lastVerifiedTimestamp.value)
    }

    @Test
    fun testSha256ChecksumCalculation() {
        val manager = OnDeviceModelManager(mockContext, testMetadata)
        val sampleFile = tempFolder.newFile("sample.txt")
        sampleFile.writeText("SnapData Offline AI Test Payload")

        val checksum = manager.calculateSha256(sampleFile)
        assertNotNull(checksum)
        assertTrue(checksum.isNotBlank())
        assertEquals(64, checksum.length)
    }
}
