package com.example.snapdata

import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.processing.AiProcessingError
import com.example.snapdata.processing.GeminiAiService
import com.example.snapdata.processing.OcrEngine
import org.junit.Assert.*
import org.junit.Test
import java.net.ConnectException

class GeminiAiServiceTest {

    @Test
    fun testParseGeminiValidStructuredJson() {
        val validAiJson = """
            {
              "documentType": "INVOICE",
              "summary": "Commercial invoice for industrial hardware parts totaling $2,484.00.",
              "fields": [
                {
                  "key": "Invoice Number",
                  "value": "INV-2026-9812",
                  "confidence": 0.99,
                  "category": "Identifier"
                },
                {
                  "key": "Total Amount",
                  "value": "$2,484.00",
                  "confidence": 0.98,
                  "category": "Financial"
                },
                {
                  "key": "Vendor Name",
                  "value": "Apex Industrial Supply",
                  "confidence": 0.97,
                  "category": "Party / Entity"
                }
              ],
              "tables": [
                {
                  "name": "Line Items",
                  "headers": ["Item Description", "Qty", "Unit Price", "Total"],
                  "rows": [
                    ["50mm Steel Bearings", "100", "$15.00", "$1,500.00"],
                    ["Hydraulic Seals", "20", "$40.00", "$800.00"]
                  ],
                  "confidence": 0.96
                }
              ],
              "rawText": "TAX INVOICE\nInvoice #: INV-2026-9812\nTotal: $2,484.00"
            }
        """.trimIndent()

        val result = GeminiAiService.parseGeminiStructuredJson(validAiJson, fallbackText = "Fallback")
        assertNotNull(result)
        result!!

        assertEquals(DocumentType.INVOICE, result.detectedDocType)
        assertEquals("Commercial invoice for industrial hardware parts totaling $2,484.00.", result.summary)
        assertEquals(3, result.fields.size)
        assertEquals("INV-2026-9812", result.fields.first { it.key == "Invoice Number" }.value)
        assertEquals(1, result.tables.size)
        assertEquals(4, result.tables[0].headers.size)
        assertEquals(2, result.tables[0].rows.size)
        assertTrue(result.overallConfidence > 0.95f)
        assertEquals(ConfidenceSource.MEASURED, result.confidenceSource)
    }

    @Test
    fun testParseGeminiMarkdownWrappedJson() {
        val markdownWrappedJson = """
            ```json
            {
              "documentType": "RECEIPT",
              "summary": "Supermarket point of sale receipt.",
              "fields": [
                {
                  "key": "Store",
                  "value": "QuickMart #102",
                  "confidence": 0.95,
                  "category": "Party / Entity"
                }
              ],
              "tables": [],
              "rawText": "QuickMart #102"
            }
            ```
        """.trimIndent()

        val result = GeminiAiService.parseGeminiStructuredJson(markdownWrappedJson, fallbackText = "")
        assertNotNull(result)
        assertEquals(DocumentType.RECEIPT, result!!.detectedDocType)
        assertEquals(1, result.fields.size)
    }

    @Test
    fun testParseGeminiSurroundingNarrativeText() {
        val aiOutputWithChatter = """
            Here is the extracted document data in JSON format:
            ```json
            {
              "documentType": "CERTIFICATE",
              "summary": "Certificate of Training Completion",
              "fields": [
                {
                  "key": "Recipient",
                  "value": "Dr. Sarah Connor",
                  "confidence": 0.96
                }
              ]
            }
            ```
            Hope this helps with your processing!
        """.trimIndent()

        val result = GeminiAiService.parseGeminiStructuredJson(aiOutputWithChatter, fallbackText = "")
        assertNotNull(result)
        assertEquals(DocumentType.CERTIFICATE, result!!.detectedDocType)
        assertEquals("Certificate of Training Completion", result.summary)
        assertEquals(1, result.fields.size)
        assertEquals("Dr. Sarah Connor", result.fields[0].value)
    }

    @Test
    fun testParseGeminiUnexpectedAndMalformedEnum() {
        // Unknown custom string
        val jsonWithCustomEnum = """
            {
              "documentType": "water_utility_bill_scan",
              "fields": [{"key": "Total", "value": "$45.00"}]
            }
        """.trimIndent()

        val result = GeminiAiService.parseGeminiStructuredJson(jsonWithCustomEnum, fallbackText = "")
        assertNotNull(result)
        // Should sanitize "bill" -> INVOICE
        assertEquals(DocumentType.INVOICE, result!!.detectedDocType)

        // Completely unrecognized enum
        val jsonWithGibberishEnum = """
            {
              "documentType": "XYZ_COMPLETELY_UNKNOWN_SCHEMA_123",
              "fields": [{"key": "Field", "value": "123"}]
            }
        """.trimIndent()

        val result2 = GeminiAiService.parseGeminiStructuredJson(jsonWithGibberishEnum, fallbackText = "")
        assertNotNull(result2)
        assertEquals(DocumentType.GENERAL_DOCUMENT, result2!!.detectedDocType)
    }

    @Test
    fun testParseGeminiMissingAndNullFields() {
        val jsonWithNullAndTypedValues = """
            {
              "documentType": "FORM",
              "summary": null,
              "fields": [
                {
                  "key": "Age",
                  "value": 34,
                  "confidence": 0.92
                },
                {
                  "key": "Is Verified",
                  "value": true,
                  "confidence": 0.90
                },
                {
                  "key": "Hobbies",
                  "value": ["Reading", "Hiking"],
                  "confidence": 0.88
                },
                {
                  "key": null,
                  "value": "Should be skipped"
                },
                {
                  "key": "",
                  "value": "Blank key should be skipped"
                }
              ]
            }
        """.trimIndent()

        val result = GeminiAiService.parseGeminiStructuredJson(jsonWithNullAndTypedValues, fallbackText = "Form Text")
        assertNotNull(result)
        assertEquals(DocumentType.FORM, result!!.detectedDocType)
        // Summary should default gracefully
        assertFalse(result.summary.isBlank())
        // 3 valid fields (Age -> "34", Is Verified -> "true", Hobbies -> "Reading, Hiking")
        assertEquals(3, result.fields.size)
        assertEquals("34", result.fields.first { it.key == "Age" }.value)
        assertEquals("true", result.fields.first { it.key == "Is Verified" }.value)
        assertEquals("Reading, Hiking", result.fields.first { it.key == "Hobbies" }.value)
    }

    @Test
    fun testParseGeminiInvalidConfidenceValues() {
        val jsonWithOddConfidence = """
            {
              "documentType": "RECEIPT",
              "fields": [
                {
                  "key": "Item",
                  "value": "Coffee",
                  "confidence": 1.5
                },
                {
                  "key": "Price",
                  "value": "$4.00",
                  "confidence": -0.5
                },
                {
                  "key": "Tax",
                  "value": "$0.40",
                  "confidence": "98%"
                }
              ]
            }
        """.trimIndent()

        val result = GeminiAiService.parseGeminiStructuredJson(jsonWithOddConfidence, fallbackText = "")
        assertNotNull(result)
        assertEquals(3, result!!.fields.size)
        // > 1.0 clamped to 1.0
        assertEquals(1.0f, result.fields[0].confidence, 0.001f)
        // Negative clamped to 0.0
        assertEquals(0.0f, result.fields[1].confidence, 0.001f)
        // "98%" parsed as 0.98
        assertEquals(0.98f, result.fields[2].confidence, 0.001f)
        assertTrue(result.overallConfidence in 0.0f..1.0f)
    }

    @Test
    fun testParseGeminiMalformedAndRaggedTables() {
        val jsonWithRaggedTable = """
            {
              "documentType": "BANK_STATEMENT",
              "tables": [
                {
                  "name": "Transactions",
                  "headers": ["Date", "Description", "Amount", "Balance"],
                  "rows": [
                    ["2026-08-01", "Deposit", "$1,000.00"],
                    ["2026-08-02", "Payment", "$200.00", "$800.00", "EXTRA_COL_DATA"],
                    ["2026-08-03", "Transfer", "$50.00", "$750.00"]
                  ],
                  "confidence": "92%"
                },
                {
                  "name": "No Headers Table",
                  "headers": [],
                  "rows": [
                    ["A", "B", "C"],
                    ["D", "E", "F"]
                  ]
                }
              ]
            }
        """.trimIndent()

        val result = GeminiAiService.parseGeminiStructuredJson(jsonWithRaggedTable, fallbackText = "")
        assertNotNull(result)
        assertEquals(2, result!!.tables.size)

        // First table: 4 columns
        val table1 = result.tables[0]
        assertEquals(4, table1.headers.size)
        assertEquals(3, table1.rows.size)
        // Row 0 was missing 4th column -> padded to 4
        assertEquals(4, table1.rows[0].size)
        assertEquals("-", table1.rows[0][3])
        // Row 1 had 5 columns -> normalized/trimmed to 4
        assertEquals(4, table1.rows[1].size)
        assertEquals(0.92f, table1.confidence, 0.001f)

        // Second table: synthesized headers "Col 1", "Col 2", "Col 3"
        val table2 = result.tables[1]
        assertEquals(3, table2.headers.size)
        assertEquals("Col 1", table2.headers[0])
        assertEquals(2, table2.rows.size)
    }

    @Test
    fun testParseGeminiMalformedGarbageJson() {
        val garbage = "Not json { [ <xml> ???"
        val result = GeminiAiService.parseGeminiStructuredJson(garbage, fallbackText = "fallback")
        assertNull(result)
    }

    @Test
    fun testParseGeminiPartialAndTruncatedJson() {
        val truncated = """
            {
              "documentType": "INVOICE",
              "fields": [
                {
                  "key": "Invoice Number",
                  "val
        """.trimIndent()

        val result = GeminiAiService.parseGeminiStructuredJson(truncated, fallbackText = "fallback")
        assertNull(result)
    }

    @Test
    fun testParseGeminiFullResponsePayload() {
        val responsePayload = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "{\n  \"documentType\": \"BANK_STATEMENT\",\n  \"summary\": \"Monthly checking account statement.\",\n  \"fields\": [\n    {\n      \"key\": \"Account Number\",\n      \"value\": \"****5678\",\n      \"confidence\": 0.98,\n      \"category\": \"Identifier\"\n    }\n  ],\n  \"tables\": []\n}"
                      }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val result = GeminiAiService.parseGeminiResponsePayload(responsePayload, fallbackText = "")
        assertNotNull(result)
        assertEquals(DocumentType.BANK_STATEMENT, result!!.detectedDocType)
        assertEquals("Monthly checking account statement.", result.summary)
    }

    @Test
    fun testErrorClassificationApiKeyMissing() {
        val error = AiProcessingError.ApiKeyMissing
        assertEquals("GEMINI_API_KEY is not configured.", error.message)
        assertTrue(error.userFriendlyReason.contains("Secrets panel", ignoreCase = true))
    }

    @Test
    fun testErrorClassificationInvalidApiKey() {
        val error401 = GeminiAiService.classifyHttpError(401, "{\"error\": {\"message\": \"API key not valid\"}}")
        assertTrue(error401 is AiProcessingError.InvalidApiKey)

        val error403 = GeminiAiService.classifyHttpError(403, "{\"error\": {\"message\": \"Permission denied\"}}")
        assertTrue(error403 is AiProcessingError.InvalidApiKey)

        val error400InvalidKey = GeminiAiService.classifyHttpError(400, "{\"error\": {\"message\": \"API_KEY_INVALID\"}}")
        assertTrue(error400InvalidKey is AiProcessingError.InvalidApiKey)
    }

    @Test
    fun testErrorClassificationRateLimit429() {
        val error429 = GeminiAiService.classifyHttpError(429, "{\"error\": {\"code\": 429, \"message\": \"RESOURCE_EXHAUSTED\"}}")
        assertTrue(error429 is AiProcessingError.RateLimitExceeded)
        assertTrue(error429.userFriendlyReason.contains("quota exceeded", ignoreCase = true) || error429.userFriendlyReason.contains("rate limit", ignoreCase = true))
    }

    @Test
    fun testErrorClassificationNetworkUnavailable() {
        val unknownHost = AiProcessingError.NetworkUnavailable("generativelanguage.googleapis.com")
        assertTrue(unknownHost.message.contains("Network unavailable"))
        assertTrue(unknownHost.userFriendlyReason.contains("offline", ignoreCase = true))

        val connectException = ConnectException("Connection refused")
        val networkErr = AiProcessingError.NetworkUnavailable(connectException.message ?: "")
        assertEquals("Network unavailable: Connection refused", networkErr.message)
    }

    @Test
    fun testErrorClassificationOfflineMode() {
        val error = AiProcessingError.OfflineModeForced
        assertEquals("Offline mode requested.", error.message)
        assertTrue(error.userFriendlyReason.contains("100% locally on-device", ignoreCase = true))
    }

    @Test
    fun testErrorClassificationMalformedResponse() {
        val malformedJson = "This is not valid JSON at all!"
        val parsed = GeminiAiService.parseGeminiStructuredJson(malformedJson, fallbackText = "raw text")
        assertNull(parsed)

        val error = AiProcessingError.MalformedResponse(malformedJson)
        assertTrue(error.message.contains("Malformed AI response"))
    }

    @Test
    fun testErrorClassificationApiError() {
        val error500 = GeminiAiService.classifyHttpError(500, "Internal Server Error")
        assertTrue(error500 is AiProcessingError.ApiError)
        assertEquals(500, (error500 as AiProcessingError.ApiError).statusCode)
    }

    @Test
    fun testErrorClassificationTimeout() {
        val error = AiProcessingError.Timeout(30000)
        assertTrue(error.message.contains("timed out after 30000ms"))
        assertTrue(error.userFriendlyReason.contains("Recovered using fast on-device OCR", ignoreCase = true))
    }

    @Test
    fun testFallbackPreservesAllOcrDataAndAllowsEditing() {
        val sampleText = """
            INVOICE
            Invoice No: 12345
            Date: 2026-08-30
            Total: $500.00
        """.trimIndent()

        val parsedLocal = OcrEngine.parseTextToStructuredData(sampleText)
        assertEquals(DocumentType.INVOICE, parsedLocal.detectedDocType)
        assertTrue(parsedLocal.fields.isNotEmpty())
        assertTrue(parsedLocal.overallConfidence > 0.8f)
        assertEquals(ConfidenceSource.HEURISTIC, parsedLocal.confidenceSource)
    }
}
