package com.example.snapdata

import androidx.lifecycle.SavedStateHandle
import com.example.snapdata.processing.ImagePreprocessor
import com.example.snapdata.processing.PdfDocumentRenderer
import org.junit.Assert.*
import org.junit.Test

class AcquisitionHardeningTest {

    @Test
    fun testSampleCalculationForUltraHighResolutionImage() {
        // 48MP Camera Photo (8000 x 6000)
        val sampleSize48MP = ImagePreprocessor.calculateInSampleSize(8000, 6000, 2560)
        assertTrue("48MP image should be downsampled by power-of-2 factor >= 2", sampleSize48MP >= 2)
        assertEquals(4, sampleSize48MP)

        // 108MP Camera Photo (12000 x 9000)
        val sampleSize108MP = ImagePreprocessor.calculateInSampleSize(12000, 9000, 2560)
        assertTrue("108MP image should have sampleSize >= 4", sampleSize108MP >= 4)
        assertEquals(8, sampleSize108MP)

        // Standard Document Scan (1920 x 1080)
        val sampleSize1080p = ImagePreprocessor.calculateInSampleSize(1920, 1080, 2560)
        assertEquals("Standard 1080p document scan within max dimension should not be downsampled", 1, sampleSize1080p)

        // Boundary test (2560 x 1440)
        val sampleSizeBoundary = ImagePreprocessor.calculateInSampleSize(2560, 1440, 2560)
        assertEquals(1, sampleSizeBoundary)

        // Above boundary (5120 x 2880)
        val sampleSizeAboveBoundary = ImagePreprocessor.calculateInSampleSize(5120, 2880, 2560)
        assertEquals(2, sampleSizeAboveBoundary)
    }

    @Test
    fun testImageErrorHierarchyMessages() {
        val inaccessible = ImagePreprocessor.ImageError.InaccessibleUri("Content provider revoked uri")
        val emptyFile = ImagePreprocessor.ImageError.EmptyFile("0-byte payload")
        val corrupted = ImagePreprocessor.ImageError.CorruptedFile("PNG header invalid")
        val security = ImagePreprocessor.ImageError.SecurityError("Permission Denial: reading com.android.providers.media")
        val oom = ImagePreprocessor.ImageError.OutOfMemory("Heap capacity exceeded")

        assertTrue(inaccessible.userMessage.contains("access or read", ignoreCase = true))
        assertTrue(emptyFile.userMessage.contains("no data", ignoreCase = true) || emptyFile.userMessage.contains("0 bytes", ignoreCase = true))
        assertTrue(corrupted.userMessage.contains("corrupted", ignoreCase = true) || corrupted.userMessage.contains("unsupported", ignoreCase = true))
        assertTrue(security.userMessage.contains("denied", ignoreCase = true) || security.userMessage.contains("permission", ignoreCase = true))
        assertTrue(oom.userMessage.contains("memory", ignoreCase = true))
    }

    @Test
    fun testPdfErrorHierarchyMessages() {
        val fileAccess = PdfDocumentRenderer.PdfError.FileAccessError("Storage permission revoked")
        val encrypted = PdfDocumentRenderer.PdfError.EncryptedPdf("AES password protection active")
        val invalid = PdfDocumentRenderer.PdfError.InvalidPdf("Missing %PDF header")
        val empty = PdfDocumentRenderer.PdfError.EmptyPdf("0 pages found")
        val memory = PdfDocumentRenderer.PdfError.MemoryError("Rendering memory exhausted")

        assertTrue(fileAccess.userMessage.contains("access", ignoreCase = true))
        assertTrue(encrypted.userMessage.contains("password", ignoreCase = true) || encrypted.userMessage.contains("encrypted", ignoreCase = true))
        assertTrue(invalid.userMessage.contains("corrupted", ignoreCase = true) || invalid.userMessage.contains("valid", ignoreCase = true))
        assertTrue(empty.userMessage.contains("0 pages", ignoreCase = true) || empty.userMessage.contains("empty", ignoreCase = true))
        assertTrue(memory.userMessage.contains("memory", ignoreCase = true))
    }

    @Test
    fun testSavedStateHandleKeyStorage() {
        val handle = SavedStateHandle()
        handle["snapdata_pending_camera_uri"] = "content://com.example.snapdata.fileprovider/camera/snap_123.jpg"
        handle["snapdata_pending_camera_path"] = "/cache/camera/snap_123.jpg"
        handle["snapdata_current_screen"] = "ACQUISITION"

        assertEquals("content://com.example.snapdata.fileprovider/camera/snap_123.jpg", handle.get<String>("snapdata_pending_camera_uri"))
        assertEquals("/cache/camera/snap_123.jpg", handle.get<String>("snapdata_pending_camera_path"))
        assertEquals("ACQUISITION", handle.get<String>("snapdata_current_screen"))

        handle.remove<String>("snapdata_pending_camera_uri")
        assertNull(handle.get<String>("snapdata_pending_camera_uri"))
    }
}
