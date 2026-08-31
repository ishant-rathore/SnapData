package com.example.snapdata

import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.processing.OcrEngine
import org.junit.Assert.*
import org.junit.Test

class OcrEngineTest {

    @Test
    fun testParseInvoiceText_CleanScan() {
        val invoiceText = """
            TAX INVOICE
            Invoice #: INV-9812
            Date: 2026-08-15
            Due Date: 2026-09-15
            Vendor: Industrial Dynamics Corp
            Client: Global Rail Systems
            
            Item | Qty | Price | Total
            Steel Bearings 50mm | 100 | $15.00 | $1500.00
            Hydraulic Seals | 20 | $40.00 | $800.00
            Control Valves 2-inch | 5 | $120.00 | $600.00
            
            Subtotal: $2900.00
            Tax: $232.00
            Grand Total: $3132.00
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(invoiceText)

        assertEquals(DocumentType.INVOICE, result.detectedDocType)
        assertTrue(result.fields.isNotEmpty())
        assertTrue(result.fields.any { it.key.contains("Invoice", ignoreCase = true) })
        assertTrue(result.fields.any { it.key.contains("Grand Total", ignoreCase = true) })
        assertTrue(result.tables.isNotEmpty())
        assertEquals(4, result.tables[0].headers.size)
        assertEquals(3, result.tables[0].rows.size)
        assertTrue(result.overallConfidence >= 0.80f)
        assertEquals(ConfidenceSource.HEURISTIC, result.confidenceSource)
    }

    @Test
    fun testParseReceipt_MultiItemAndTotals() {
        val receiptText = """
            FreshMart Grocery Store #104
            Cashier: Maria S.
            Date: 10/12/2026
            
            Organic Whole Milk  1 x $4.99  $4.99
            Sourdough Bread     2 x $3.50  $7.00
            Avocado Bag         1 x $5.49  $5.49
            
            Subtotal: $17.48
            Tax: $1.40
            Total Amount: $18.88
            Change Due: $1.12
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(receiptText)

        assertEquals(DocumentType.RECEIPT, result.detectedDocType)
        assertTrue(result.fields.any { it.key.contains("Cashier", ignoreCase = true) || it.value.contains("Maria") })
        assertTrue(result.fields.any { it.key.contains("Subtotal", ignoreCase = true) })
        assertTrue(result.fields.any { it.key.contains("Total", ignoreCase = true) })
        assertTrue(result.tables.isNotEmpty())
        assertTrue(result.tables[0].rows.size >= 2)
    }

    @Test
    fun testParseBankStatement_WithLedgerTable() {
        val statementText = """
            STATEMENT OF ACCOUNT
            Bank: Standard Trust Bank
            Account Holder: Alex Mercer
            Account Number: 9876543210
            Statement Period: 01-Aug-2026 to 31-Aug-2026
            
            Date | Description | Ref | Debit | Credit | Balance
            02-Aug-2026 | Payroll Direct Dep | REF001 | - | $4500.00 | $7200.00
            05-Aug-2026 | Office Rent | REF002 | $1200.00 | - | $6000.00
            14-Aug-2026 | Cloud Services | REF003 | $350.00 | - | $5650.00
            
            Account Balance: $5650.00
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(statementText)

        assertEquals(DocumentType.BANK_STATEMENT, result.detectedDocType)
        assertTrue(result.fields.any { it.key.contains("Account", ignoreCase = true) })
        assertTrue(result.tables.isNotEmpty())
        assertEquals(6, result.tables[0].headers.size)
        assertEquals(3, result.tables[0].rows.size)
    }

    @Test
    fun testParseApplicationForm_WithCheckboxes() {
        val formText = """
            APPLICATION FORM - MEMBERSHIP REGISTRATION
            Full Name: Jane Doe
            Date of Birth: 1992-04-15
            Email Address: jane.doe@example.com
            Phone Number: +1-555-0199
            Status: [X] Active Member   [ ] Pending Review
            Applicant Signature: Jane Doe
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(formText)

        assertEquals(DocumentType.FORM, result.detectedDocType)
        assertTrue(result.fields.any { it.key.contains("Full Name", ignoreCase = true) })
        assertTrue(result.fields.any { it.key.contains("Date of Birth", ignoreCase = true) })
        assertTrue(result.fields.any { it.key.contains("Status", ignoreCase = true) || it.value.contains("[X]") })
    }

    @Test
    fun testParseMarkSheet_Transcript() {
        val marksheetText = """
            TRANSCRIPT OF RECORDS - SEMESTER IV
            Student Name: David Kim
            Registration Number: REG-2024-889
            Program: Bachelor of Science in Computer Engineering
            
            Course Code | Course Title | Credits | Grade | Grade Points
            CS401 | Distributed Systems | 4.0 | A | 16.0
            CS402 | Database Architecture | 4.0 | A- | 14.8
            CS403 | Mobile Computing | 3.0 | A+ | 12.0
            
            Total Credits: 11.0
            CGPA: 3.89
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(marksheetText)

        assertEquals(DocumentType.MARK_SHEET, result.detectedDocType)
        assertTrue(result.fields.any { it.key.contains("Student Name", ignoreCase = true) })
        assertTrue(result.fields.any { it.key.contains("GPA", ignoreCase = true) || it.key.contains("CGPA", ignoreCase = true) })
        assertTrue(result.tables.isNotEmpty())
        assertEquals(5, result.tables[0].headers.size)
        assertEquals(3, result.tables[0].rows.size)
    }

    @Test
    fun testParseDataTable_MultiSpaceAlignment() {
        val tableText = """
            Quarterly Regional Sales Data Matrix
            Region       Q1_Sales    Q2_Sales    Q3_Sales    Growth_Rate
            North_America  $450,000    $520,000    $610,000    +17.3%
            Europe         $380,000    $410,000    $445,000    +8.5%
            Asia_Pacific   $510,000    $590,000    $720,000    +22.0%
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(tableText)

        assertTrue(result.tables.isNotEmpty())
        assertEquals(5, result.tables[0].headers.size)
        assertEquals(3, result.tables[0].rows.size)
    }

    @Test
    fun testCameraPhoto_WithUnevenSpacingAndNoise() {
        val noisyCameraText = """
            TAX   INVOICE  
            Invoice No. :   INV-40912  
            Date := 2026/11/02
            Client Name  -  Acme Logistics
            
            Item Description | Qty | Amount
            Shipping Pallet A | 10 | $500.00
            Bubble Wrap Rolls | 5 | $75.00
            
            Total : $575.00
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(noisyCameraText)

        assertEquals(DocumentType.INVOICE, result.detectedDocType)
        assertTrue(result.fields.any { it.key.contains("Invoice", ignoreCase = true) })
        assertTrue(result.fields.any { it.key.contains("Total", ignoreCase = true) })
        assertTrue(result.tables.isNotEmpty())
    }

    @Test
    fun testEmptyInput_ReturnsSafeResult() {
        val emptyResult = OcrEngine.parseTextToStructuredData("")
        assertEquals(0, emptyResult.lineCount)
        assertEquals(0, emptyResult.fields.size)
        assertEquals(0, emptyResult.tables.size)
        assertEquals(0.0f, emptyResult.overallConfidence, 0.001f)
        assertTrue(emptyResult.summary.contains("No legible text", ignoreCase = true))

        val whitespaceResult = OcrEngine.parseTextToStructuredData("   \n\n\t  \n  ")
        assertEquals(0, whitespaceResult.lineCount)
        assertEquals(0.0f, whitespaceResult.overallConfidence, 0.001f)
    }

    @Test
    fun testMalformedGarbageInput_GeneratesQualityWarningsAndLowConfidence() {
        val garbageText = """
            ^~~!!###@@@%%%
            &&&&****^^^^~~~~
            ;;;;;:::::>>>>>
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(garbageText)

        assertTrue(result.qualityWarnings.isNotEmpty())
        assertTrue(result.overallConfidence < 0.60f)
        assertEquals(ConfidenceSource.HEURISTIC, result.confidenceSource)
    }

    @Test
    fun testConfidence_HonestNotFabricated() {
        // High quality text
        val highQuality = """
            Invoice #: INV-100
            Vendor: Alpha Corp
            Date: 2026-01-01
            Total: $500.00
        """.trimIndent()
        val highResult = OcrEngine.parseTextToStructuredData(highQuality)

        // Low quality noisy text
        val lowQuality = "A ^ ~ #"
        val lowResult = OcrEngine.parseTextToStructuredData(lowQuality)

        assertTrue("High quality text should have significantly higher confidence than noisy text", highResult.overallConfidence > lowResult.overallConfidence)
        assertEquals(ConfidenceSource.HEURISTIC, highResult.confidenceSource)
    }
}
