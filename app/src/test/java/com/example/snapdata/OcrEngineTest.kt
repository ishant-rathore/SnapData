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
            Date: 15/08/2026
            Due Date: 15/09/2026
            Vendor: Industrial Dynamics India Pvt Ltd
            Client: Bharat Rail Systems
            
            Item | Qty | Price | Total
            Steel Bearings 50mm | 100 | ₹150.00 | ₹15000.00
            Hydraulic Seals | 20 | ₹400.00 | ₹8000.00
            Control Valves 2-inch | 5 | ₹1200.00 | ₹6000.00
            
            Subtotal: ₹29000.00
            Tax: ₹2320.00
            Grand Total: ₹31320.00
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
            Rajasthan Mart Superstore #104
            Cashier: Priya M.
            Date: 10/12/2026
            
            Organic Cow Milk    1 x ₹65.00  ₹65.00
            Whole Wheat Bread   2 x ₹45.00  ₹90.00
            Tata Tea Gold       1 x ₹310.00 ₹310.00
            
            Subtotal: ₹465.00
            Tax: ₹23.25
            Total Amount: ₹488.25
            Change Due: ₹11.75
        """.trimIndent()

        val result = OcrEngine.parseTextToStructuredData(receiptText)

        assertEquals(DocumentType.RECEIPT, result.detectedDocType)
        assertTrue(result.fields.any { it.key.contains("Cashier", ignoreCase = true) || it.value.contains("Priya") })
        assertTrue(result.fields.any { it.key.contains("Subtotal", ignoreCase = true) })
        assertTrue(result.fields.any { it.key.contains("Total", ignoreCase = true) })
        assertTrue(result.tables.isNotEmpty())
        assertTrue(result.tables[0].rows.size >= 2)
    }

    @Test
    fun testParseBankStatement_WithLedgerTable() {
        val statementText = """
            STATEMENT OF ACCOUNT
            Bank: State Bank of India
            Account Holder: Rajesh Kumar Verma
            Account Number: 9876543210
            Statement Period: 01-Aug-2026 to 31-Aug-2026
            
            Date | Description | Ref | Debit | Credit | Balance
            02-Aug-2026 | Salary Direct Dep | REF001 | - | ₹45000.00 | ₹72000.00
            05-Aug-2026 | Office Rent | REF002 | ₹12000.00 | - | ₹60000.00
            14-Aug-2026 | Cloud Services | REF003 | ₹3500.00 | - | ₹56500.00
            
            Account Balance: ₹56500.00
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
            Full Name: Aarav Sharma
            Date of Birth: 1992-04-15
            Email Address: aarav.sharma@example.in
            Phone Number: +91 98765 43210
            Status: [X] Active Member   [ ] Pending Review
            Applicant Signature: Aarav Sharma
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
            Student Name: Ananya Sharma
            Registration Number: REG-2024-889
            Program: Bachelor of Technology in Computer Science & AI
            
            Course Code | Course Title | Credits | Grade | Grade Points
            CS401 | Distributed Systems | 4.0 | A | 16.0
            CS402 | Database Architecture | 4.0 | A- | 14.8
            CS403 | Mobile Computing | 3.0 | A+ | 12.0
            
            Total Credits: 11.0
            CGPA: 9.45
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
            North_Zone     ₹4,50,000   ₹5,20,000   ₹6,10,000   +17.3%
            West_Zone      ₹3,80,000   ₹4,10,000   ₹4,45,000   +8.5%
            South_Zone     ₹5,10,000   ₹5,90,000   ₹7,20,000   +22.0%
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
            Client Name  -  Apex Logistics India
            
            Item Description | Qty | Amount
            Shipping Pallet A | 10 | ₹5,000.00
            Bubble Wrap Rolls | 5 | ₹750.00
            
            Total : ₹5,750.00
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
            Vendor: Alpha Corp India
            Date: 2026-01-01
            Total: ₹5,000.00
        """.trimIndent()
        val highResult = OcrEngine.parseTextToStructuredData(highQuality)

        // Low quality noisy text
        val lowQuality = "A ^ ~ #"
        val lowResult = OcrEngine.parseTextToStructuredData(lowQuality)

        assertTrue("High quality text should have significantly higher confidence than noisy text", highResult.overallConfidence > lowResult.overallConfidence)
        assertEquals(ConfidenceSource.HEURISTIC, highResult.confidenceSource)
    }
}
