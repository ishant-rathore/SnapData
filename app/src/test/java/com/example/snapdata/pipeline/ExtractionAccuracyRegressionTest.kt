package com.example.snapdata.pipeline

import com.example.snapdata.ai.engine.OnDeviceNeuralDocumentAnalyzer
import com.example.snapdata.model.DocumentType
import com.example.snapdata.processing.OcrEngine
import org.junit.Assert.*
import org.junit.Test

/**
 * Regression Test Suite for Extraction Accuracy, Document Classification,
 * and Disambiguation between Non-Monetary Metrics (e.g. Quantity) and Financial Totals.
 */
class ExtractionAccuracyRegressionTest {

    @Test
    fun testReportedScenario_InvoiceWithTotalQtyAndGrandTotal() {
        // Document text representing the exact reported scenario
        val rawOcrText = """
            TAX INVOICE
            Invoice No: INV-2026-84910
            Invoice Date: 31/08/2026
            Billed To: Horizon Enterprises Pvt Ltd
            GSTIN: 27AAAAA0000A1Z5
            HSN/SAC: 998313
            
            Description | Qty | Rate | Amount
            Cloud Infrastructure Hosting | 10.0 | 6000.00 | 60000.00
            Enterprise Cybersecurity Suite | 10.0 | 6000.00 | 60000.00
            
            Total Qty: 20.0
            Subtotal: ₹1,20,000.00
            CGST (9%): ₹10,800.00
            SGST (9%): ₹10,800.00
            Grand Total: ₹1,41,600.00
        """.trimIndent()

        // 1. Test OnDeviceNeuralDocumentAnalyzer classification & extraction
        val analyzer = OnDeviceNeuralDocumentAnalyzer()
        val neuralResult = analyzer.analyzeRawOcrText(rawOcrText)

        // Document MUST be classified as INVOICE (never MARK_SHEET)
        assertEquals("Document must be classified as INVOICE", DocumentType.INVOICE, neuralResult.documentType)

        // Verify Total Amount is not polluted with Total Qty (20.0)
        val grandTotalField = neuralResult.fields.find { it.key.equals("Grand Total", ignoreCase = true) || it.key.equals("Total Amount", ignoreCase = true) }
        assertNotNull("Grand Total field should exist", grandTotalField)
        assertEquals("Grand Total must be ₹1,41,600.00", "₹1,41,600.00", grandTotalField?.value)

        // Verify Total Quantity is extracted as its own field or separated
        val qtyField = neuralResult.fields.find { it.key.contains("Quantity", ignoreCase = true) || it.key.contains("Qty", ignoreCase = true) }
        if (qtyField != null) {
            assertEquals("Total Quantity should be 20.0", "20.0", qtyField.value)
            assertNotEquals("Total Quantity must not equal Grand Total", grandTotalField?.value, qtyField.value)
        }

        // Verify Subtotal
        val subtotalField = neuralResult.fields.find { it.key.contains("Subtotal", ignoreCase = true) }
        assertNotNull("Subtotal field should exist", subtotalField)
        assertEquals("Subtotal must be ₹1,20,000.00", "₹1,20,000.00", subtotalField?.value)

        // 2. Test OcrEngine classification & extraction
        val ocrResult = OcrEngine.parseTextToStructuredData(rawOcrText)
        assertEquals("OcrEngine must classify as INVOICE", DocumentType.INVOICE, ocrResult.detectedDocType)

        val ocrGrandTotal = ocrResult.fields.find { it.key.contains("Grand Total", ignoreCase = true) }
        assertNotNull("OcrEngine Grand Total field should exist", ocrGrandTotal)
        assertEquals("OcrEngine Grand Total must be ₹1,41,600.00", "₹1,41,600.00", ocrGrandTotal?.value)
    }

    @Test
    fun testAcademicMarkSheet_ClassificationNotTriggeredByGenericNumbers() {
        val academicText = """
            MAHARASHTRA STATE BOARD OF TECHNICAL EDUCATION
            STATEMENT OF MARKS
            Enrollment No: 202698712
            Seat No: M410982
            Candidate Name: Rohit Sharma
            Semester: VI
            
            Course Code | Course Title | Max Marks | Marks Obtained
            CS601 | Mobile Computing | 100 | 88
            CS602 | Cloud Architecture | 100 | 92
            CS603 | Machine Learning | 100 | 85
            
            Total Marks: 265 / 300
            Percentage: 88.33%
            SGPA: 9.20
            Result: FIRST CLASS WITH DISTINCTION
        """.trimIndent()

        val analyzer = OnDeviceNeuralDocumentAnalyzer()
        val result = analyzer.analyzeRawOcrText(academicText)

        assertEquals("Academic transcript must be classified as MARK_SHEET", DocumentType.MARK_SHEET, result.documentType)
        assertTrue("Must extract SGPA or Percentage", result.fields.any { it.key.contains("SGPA", true) || it.key.contains("Percentage", true) || it.key.contains("Total Marks", true) })
    }

    @Test
    fun testBankStatement_ClassificationAndBalance() {
        val bankText = """
            HDFC BANK LIMITED
            STATEMENT OF ACCOUNT
            Branch: Bandra Kurla Complex, Mumbai
            Account Holder: Rajesh Kumar
            Account Number: 50100239481928
            IFSC Code: HDFC0000240
            
            Date | Narration | Chq/Ref No | Withdrawal | Deposit | Closing Balance
            01/08/2026 | Opening Balance | - | - | - | 1,50,000.00
            15/08/2026 | NEFT-SALARY | 98124 | - | 75,000.00 | 2,25,000.00
            20/08/2026 | UPI-RENT | 44102 | 30,000.00 | - | 1,95,000.00
            
            Closing Balance: ₹1,95,000.00
        """.trimIndent()

        val analyzer = OnDeviceNeuralDocumentAnalyzer()
        val result = analyzer.analyzeRawOcrText(bankText)

        assertEquals("Bank statement must be classified as BANK_STATEMENT", DocumentType.BANK_STATEMENT, result.documentType)
        assertTrue("Must extract IFSC Code", result.fields.any { it.key.contains("IFSC", true) && it.value.contains("HDFC0000240") })
        assertTrue("Must extract Account Number", result.fields.any { it.key.contains("Account", true) })
    }
}
