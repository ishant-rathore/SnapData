package com.example.snapdata

import com.example.snapdata.model.DocumentType
import com.example.snapdata.processing.MultiPageDocumentMerger
import com.example.snapdata.processing.OcrEngine
import com.example.snapdata.processing.PdfDocumentRenderer
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

class PdfProcessingTest {

    @Test
    fun testOnePagePdfExtraction() {
        val page1Text = """
            COMMERCIAL INVOICE
            Invoice No: INV-1001
            Date: 10/08/2026
            Customer: Sharma Electronics Pvt Ltd
            
            Description | Qty | Unit Price | Amount
            Industrial Valve | 5 | ₹2,000.00 | ₹10,000.00
            Gasket Kit | 10 | ₹150.00 | ₹1,500.00
            
            Subtotal: ₹11,500.00
            Tax: ₹1,035.00
            Total Due: ₹12,535.00
        """.trimIndent()

        val page1Ocr = OcrEngine.parseTextToStructuredData(page1Text)
        val pageData = listOf(
            MultiPageDocumentMerger.PageOcrData(
                pageIndex = 1,
                rawText = page1Text,
                ocrResult = page1Ocr
            )
        )

        val merged = MultiPageDocumentMerger.combineMultiPageResults(pageData)

        assertEquals(DocumentType.INVOICE, merged.detectedDocType)
        assertTrue(merged.rawText.contains("--- PAGE 1 OF 1 ---"))
        assertTrue(merged.fields.any { it.key.contains("Invoice No", ignoreCase = true) })
        assertTrue(merged.fields.any { it.key.contains("Total Due", ignoreCase = true) })
        assertEquals(1, merged.tables.size)
        assertEquals(2, merged.tables[0].rows.size)
        assertTrue(merged.summary.contains("1 page"))
    }

    @Test
    fun testTwoPagePdfExtractionAndFieldDeduplication() {
        val page1Text = """
            STATEMENT OF ACCOUNT
            Account Number: ACC-998877
            Statement Date: 01/08/2026
            Account Holder: Bharat Tech Ventures Pvt Ltd
            
            Date | Transaction Details | Debit | Credit | Balance
            01/08/2026 | Opening Balance | - | - | ₹1,50,000.00
            03/08/2026 | NEFT Transfer Recv | - | ₹50,000.00 | ₹2,00,000.00
        """.trimIndent()

        val page2Text = """
            STATEMENT OF ACCOUNT
            Account Number: ACC-998877
            Statement Date: 01/08/2026
            
            Date | Transaction Details | Debit | Credit | Balance
            10/08/2026 | Cloud Services Bill | ₹12,000.00 | - | ₹1,88,000.00
            15/08/2026 | Hardware Supplier | ₹35,000.00 | - | ₹1,53,000.00
            
            Total Debits: ₹47,000.00
            Total Credits: ₹50,000.00
            Closing Balance: ₹1,53,000.00
        """.trimIndent()

        val p1Ocr = OcrEngine.parseTextToStructuredData(page1Text)
        val p2Ocr = OcrEngine.parseTextToStructuredData(page2Text)

        val pages = listOf(
            MultiPageDocumentMerger.PageOcrData(pageIndex = 1, rawText = page1Text, ocrResult = p1Ocr),
            MultiPageDocumentMerger.PageOcrData(pageIndex = 2, rawText = page2Text, ocrResult = p2Ocr)
        )

        val merged = MultiPageDocumentMerger.combineMultiPageResults(pages)

        assertEquals(DocumentType.BANK_STATEMENT, merged.detectedDocType)
        // Check page order preservation
        val idxPage1 = merged.rawText.indexOf("--- PAGE 1 OF 2 ---")
        val idxPage2 = merged.rawText.indexOf("--- PAGE 2 OF 2 ---")
        assertTrue("Page 1 header should precede Page 2 header", idxPage1 < idxPage2)

        // Check deduplication of Account Number
        val accNumberFields = merged.fields.filter { it.key.contains("Account Number", ignoreCase = true) }
        assertEquals("Account Number should be deduplicated into 1 primary field", 1, accNumberFields.size)

        // Check cross-page table stitching
        assertEquals("Compatible multi-page transaction tables should be stitched into 1 table", 1, merged.tables.size)
        assertEquals("Stitched table should contain all 4 rows from both pages", 4, merged.tables[0].rows.size)
        assertTrue(merged.summary.contains("2 pages"))
    }

    @Test
    fun testMultiPagePdfWithTableStitching() {
        val page1Text = """
            PURCHASE ORDER
            PO Number: PO-5544
            Vendor: Apex Industrial Supplies Pvt Ltd
            
            Item # | Description | Quantity | Unit Cost | Total
            001 | Titanium Rods 10mm | 50 | ₹300.00 | ₹15,000.00
            002 | Carbon Steel Plates | 20 | ₹1,000.00 | ₹20,000.00
        """.trimIndent()

        val page2Text = """
            PURCHASE ORDER
            PO Number: PO-5544
            
            Item # | Description | Quantity | Unit Cost | Total
            003 | Stainless Bolts M8 | 500 | ₹5.00 | ₹2,500.00
            004 | Brass Fittings | 100 | ₹80.00 | ₹8,000.00
        """.trimIndent()

        val page3Text = """
            PURCHASE ORDER
            PO Number: PO-5544
            
            Item # | Description | Quantity | Unit Cost | Total
            005 | High Temp Lubricant | 10 | ₹450.00 | ₹4,500.00
            
            Subtotal: ₹50,000.00
            Shipping: ₹1,500.00
            Grand Total: ₹51,500.00
        """.trimIndent()

        val pages = listOf(
            MultiPageDocumentMerger.PageOcrData(1, page1Text, OcrEngine.parseTextToStructuredData(page1Text)),
            MultiPageDocumentMerger.PageOcrData(2, page2Text, OcrEngine.parseTextToStructuredData(page2Text)),
            MultiPageDocumentMerger.PageOcrData(3, page3Text, OcrEngine.parseTextToStructuredData(page3Text))
        )

        val merged = MultiPageDocumentMerger.combineMultiPageResults(pages)

        assertTrue(merged.rawText.contains("--- PAGE 1 OF 3 ---"))
        assertTrue(merged.rawText.contains("--- PAGE 2 OF 3 ---"))
        assertTrue(merged.rawText.contains("--- PAGE 3 OF 3 ---"))

        assertEquals("Should stitch all 3 pages into 1 consolidated table", 1, merged.tables.size)
        assertEquals("Total table rows should equal 5 across 3 pages", 5, merged.tables[0].rows.size)
        assertTrue(merged.fields.any { it.key.contains("Grand Total", ignoreCase = true) })
    }

    @Test
    fun testLargePdfMultiPageProcessing() {
        val pageCount = 15
        val pages = mutableListOf<MultiPageDocumentMerger.PageOcrData>()

        for (i in 1..pageCount) {
            val pText = """
                OPERATIONAL LOG REPORT
                Facility ID: FAC-01
                Log Sequence: LOG-PAGE-$i
                Timestamp: 2026-08-30 10:00:0$i
                
                Sensor ID | Metric | Reading | Status
                SENS-${i}A | Temperature | 72.5 C | NORMAL
                SENS-${i}B | Pressure | 101.3 kPa | OPTIMAL
            """.trimIndent()

            pages.add(
                MultiPageDocumentMerger.PageOcrData(
                    pageIndex = i,
                    rawText = pText,
                    ocrResult = OcrEngine.parseTextToStructuredData(pText)
                )
            )
        }

        val merged = MultiPageDocumentMerger.combineMultiPageResults(pages)

        assertEquals("Preserves all $pageCount page demarcations", pageCount, merged.rawText.split("--- PAGE ").size - 1)
        assertEquals("Stitches identical log tables across all 15 pages", 1, merged.tables.size)
        assertEquals("Should have 30 total rows (2 per page * 15 pages)", 30, merged.tables[0].rows.size)
        assertTrue(merged.summary.contains("$pageCount pages"))
    }

    @Test
    fun testEmptyDocumentReturnsSafeEmptyResult() {
        val merged = MultiPageDocumentMerger.combineMultiPageResults(emptyList())
        assertEquals(0, merged.fields.size)
        assertEquals(0, merged.tables.size)
        assertEquals("", merged.rawText)
        assertEquals(0.0f, merged.overallConfidence, 0.01f)
    }

    @Test
    fun testPdfErrorHierarchyMessages() {
        val invalidError = PdfDocumentRenderer.PdfError.InvalidPdf("Malformed PDF stream")
        val emptyError = PdfDocumentRenderer.PdfError.EmptyPdf("0 pages found")
        val encryptedError = PdfDocumentRenderer.PdfError.EncryptedPdf("AES-256 password protection active")
        val unsupportedError = PdfDocumentRenderer.PdfError.UnsupportedPdf("Vector JBIG2 stream unsupported")
        val renderError = PdfDocumentRenderer.PdfError.RenderingFailure(pageIndex = 2, detail = "Failed page 3")
        val ocrError = PdfDocumentRenderer.PdfError.OcrFailure(pageIndex = 1, detail = "Low contrast token failure")

        assertTrue(invalidError.userMessage.isNotBlank())
        assertTrue(emptyError.userMessage.isNotBlank())
        assertTrue(encryptedError.userMessage.contains("password", ignoreCase = true) || encryptedError.userMessage.contains("encrypt", ignoreCase = true))
        assertTrue(unsupportedError.userMessage.isNotBlank())
        assertTrue(renderError.userMessage.contains("page 3", ignoreCase = true))
        assertTrue(ocrError.userMessage.isNotBlank())
    }
}
