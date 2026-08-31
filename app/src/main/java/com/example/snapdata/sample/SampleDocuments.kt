package com.example.snapdata.sample

import android.graphics.*
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable

data class SampleDocument(
    val id: String,
    val title: String,
    val type: DocumentType,
    val description: String,
    val rawText: String,
    val fields: List<ExtractedField>,
    val tables: List<ExtractedTable>,
    val summary: String,
    val confidence: Float = 0.96f
) {
    fun createRenderedBitmap(): Bitmap {
        val width = 800
        val height = 1100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Paper background
        val bgPaint = Paint().apply { color = Color.parseColor("#FAF8F5") }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Subtle document border
        val borderPaint = Paint().apply {
            color = Color.parseColor("#E2DCD5")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRect(24f, 24f, width - 24f, height - 24f, borderPaint)

        // Top accent header
        val headerPaint = Paint().apply {
            color = when (type) {
                DocumentType.INVOICE -> Color.parseColor("#0052CC")
                DocumentType.RECEIPT -> Color.parseColor("#00875A")
                DocumentType.BANK_STATEMENT -> Color.parseColor("#172B4D")
                DocumentType.CERTIFICATE -> Color.parseColor("#FF8B00")
                DocumentType.MARK_SHEET -> Color.parseColor("#6554C0")
                else -> Color.parseColor("#344563")
            }
        }
        canvas.drawRect(24f, 24f, width - 24f, 110f, headerPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 28f
            isFakeBoldText = true
        }
        canvas.drawText(title, 48f, 75f, textPaint)

        // Draw body lines
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#253858")
            textSize = 17f
            typeface = Typeface.MONOSPACE
        }

        var y = 160f
        rawText.lines().take(36).forEach { line ->
            if (line.startsWith("---") || line.contains("|")) {
                bodyPaint.isFakeBoldText = true
                bodyPaint.color = Color.parseColor("#091E42")
            } else {
                bodyPaint.isFakeBoldText = false
                bodyPaint.color = Color.parseColor("#344563")
            }
            canvas.drawText(line.take(52), 48f, y, bodyPaint)
            y += 24f
        }

        // Stamp
        val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#3300875A")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawCircle(width - 120f, height - 120f, 60f, stampPaint)
        val stampTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00875A")
            textSize = 14f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("VERIFIED", width - 120f, height - 125f, stampTextPaint)
        canvas.drawText("SNAPDATA", width - 120f, height - 105f, stampTextPaint)

        return bitmap
    }
}

object SampleDocumentRepository {

    val samples: List<SampleDocument> = listOf(
        SampleDocument(
            id = "sample_invoice_01",
            title = "Apex Tech Cloud Services Invoice",
            type = DocumentType.INVOICE,
            description = "Commercial SaaS & hosting monthly invoice with itemized line items and tax calculation.",
            rawText = """
                APEX CLOUD SOLUTIONS INC.
                100 Innovation Boulevard, Suite 400, San Jose, CA
                Tax ID: US-94827103-X
                
                TAX INVOICE
                Invoice Number: INV-2026-8842
                Invoice Date: August 28, 2026
                Due Date: September 15, 2026
                Payment Terms: Net 30
                
                BILL TO:
                Horizon Financial Technologies
                88 Wall Street, 12th Floor, New York, NY
                Contact: billing@horizonfintech.com
                
                Item Description | Qty | Unit Price | Total
                Enterprise Kubernetes Cluster Tier 3 | 1 | $1,250.00 | $1,250.00
                Dedicated SSD Storage (5TB NVMe) | 5 | $90.00 | $450.00
                Global CDN Bandwidth Package (50TB) | 1 | $320.00 | $320.00
                24/7 Dedicated SRE Support SLA | 1 | $500.00 | $500.00
                
                Subtotal: $2,520.00
                State Sales Tax (8.5%): $214.20
                TOTAL DUE: $2,734.20
                Payment Status: PENDING
                Bank Transfer: Chase Bank AC# 98234-1102
            """.trimIndent(),
            fields = listOf(
                ExtractedField(key = "Invoice Number", value = "INV-2026-8842", confidence = 0.99f, category = "Identifier"),
                ExtractedField(key = "Invoice Date", value = "August 28, 2026", confidence = 0.98f, category = "Temporal"),
                ExtractedField(key = "Due Date", value = "September 15, 2026", confidence = 0.97f, category = "Temporal"),
                ExtractedField(key = "Vendor Name", value = "Apex Cloud Solutions Inc.", confidence = 0.99f, category = "Party / Entity"),
                ExtractedField(key = "Client Name", value = "Horizon Financial Technologies", confidence = 0.98f, category = "Party / Entity"),
                ExtractedField(key = "Subtotal", value = "$2,520.00", confidence = 0.96f, category = "Financial"),
                ExtractedField(key = "Tax Rate", value = "8.5%", confidence = 0.94f, category = "Financial"),
                ExtractedField(key = "Tax Amount", value = "$214.20", confidence = 0.95f, category = "Financial"),
                ExtractedField(key = "Total Due", value = "$2,734.20", confidence = 0.99f, category = "Financial"),
                ExtractedField(key = "Payment Status", value = "PENDING", confidence = 0.95f, category = "Status")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "Line Items & Services",
                    headers = mutableListOf("Item Description", "Qty", "Unit Price", "Total"),
                    rows = mutableListOf(
                        mutableListOf("Enterprise Kubernetes Cluster Tier 3", "1", "$1,250.00", "$1,250.00"),
                        mutableListOf("Dedicated SSD Storage (5TB NVMe)", "5", "$90.00", "$450.00"),
                        mutableListOf("Global CDN Bandwidth Package (50TB)", "1", "$320.00", "$320.00"),
                        mutableListOf("24/7 Dedicated SRE Support SLA", "1", "$500.00", "$500.00")
                    ),
                    confidence = 0.96f
                )
            ),
            summary = "Invoice INV-2026-8842 issued by Apex Cloud Solutions Inc. to Horizon Financial Technologies for $2,734.20 due on Sep 15, 2026."
        ),
        SampleDocument(
            id = "sample_receipt_02",
            title = "Blue Bottle Artisan Cafe Receipt",
            type = DocumentType.RECEIPT,
            description = "Point of sale thermal store receipt with items, tips, payment method and auth code.",
            rawText = """
                BLUE BOTTLE COFFEE ROASTERS
                315 Linden St, San Francisco, CA 94102
                Tel: (415) 555-0199
                
                RECEIPT #77291
                Date: 2026-08-30 09:14 AM
                Server: Alex M.   •   Station: POS-02
                
                Item | Qty | Price
                Single Origin Pour Over (Bella Donovan) | 1 | $6.50
                Oat Milk Gibraltar Cappuccino | 2 | $11.00
                Avocado Tartine with Microgreens | 1 | $12.50
                Almond Croissant | 1 | $4.75
                
                Subtotal: $34.75
                SF City Mandate (4%): $1.39
                Sales Tax (8.625%): $3.00
                Tip (18%): $6.26
                TOTAL AMOUNT: $45.40
                
                Payment: Apple Pay (Mastercard ending in 4092)
                Approval Code: AUTH-881920
                THANK YOU FOR YOUR VISIT!
            """.trimIndent(),
            fields = listOf(
                ExtractedField(key = "Merchant", value = "Blue Bottle Coffee Roasters", confidence = 0.99f, category = "Party / Entity"),
                ExtractedField(key = "Receipt Number", value = "#77291", confidence = 0.97f, category = "Identifier"),
                ExtractedField(key = "Date & Time", value = "2026-08-30 09:14 AM", confidence = 0.98f, category = "Temporal"),
                ExtractedField(key = "Subtotal", value = "$34.75", confidence = 0.96f, category = "Financial"),
                ExtractedField(key = "Tip", value = "$6.26", confidence = 0.93f, category = "Financial"),
                ExtractedField(key = "Total Amount", value = "$45.40", confidence = 0.99f, category = "Financial"),
                ExtractedField(key = "Payment Method", value = "Apple Pay (Mastercard *4092)", confidence = 0.95f, category = "Financial")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "Purchased Items",
                    headers = mutableListOf("Item", "Qty", "Price"),
                    rows = mutableListOf(
                        mutableListOf("Single Origin Pour Over", "1", "$6.50"),
                        mutableListOf("Oat Milk Gibraltar Cappuccino", "2", "$11.00"),
                        mutableListOf("Avocado Tartine with Microgreens", "1", "$12.50"),
                        mutableListOf("Almond Croissant", "1", "$4.75")
                    ),
                    confidence = 0.95f
                )
            ),
            summary = "Store receipt #77291 from Blue Bottle Coffee Roasters totalling $45.40 paid via Apple Pay on Aug 30, 2026."
        ),
        SampleDocument(
            id = "sample_marksheet_03",
            title = "University Semester Academic Marksheet",
            type = DocumentType.MARK_SHEET,
            description = "Higher education academic transcript with semester grades, credits, SGPA, and classification.",
            rawText = """
                PACIFIC INSTITUTE OF TECHNOLOGY
                OFFICIAL TRANSCRIPT OF ACADEMIC RECORD
                
                Student Name: Ishant Rathore
                Enrollment ID: PIT-2024-CS-0412
                Program: Bachelor of Technology (Computer Science & AI)
                Semester: VI (Spring 2026)
                Examination Roll No: 8840192
                
                Course Code | Course Title | Credits | Grade | Grade Points
                CS601 | Advanced Machine Learning & Vision | 4.0 | A+ | 10.0
                CS602 | Distributed Cloud Architecture | 4.0 | A | 9.0
                CS603 | Database Engineering & Scalability | 3.0 | A+ | 10.0
                CS604 | Mobile Systems Design (Android Compose) | 3.0 | O | 10.0
                CS605 | Neural Networks & LLM Foundations | 4.0 | A | 9.0
                CS606 | Capstone Project Stage I | 2.0 | A+ | 10.0
                
                Total Credits Registered: 20.0
                Total Credits Earned: 20.0
                Semester Grade Point Average (SGPA): 9.60 / 10.00
                Cumulative Grade Point Average (CGPA): 9.45 / 10.00
                Result: PASSED WITH FIRST CLASS DISTINCTION
                Date of Issue: July 20, 2026
            """.trimIndent(),
            fields = listOf(
                ExtractedField(key = "Student Name", value = "Ishant Rathore", confidence = 0.99f, category = "Party / Entity"),
                ExtractedField(key = "Enrollment ID", value = "PIT-2024-CS-0412", confidence = 0.98f, category = "Identifier"),
                ExtractedField(key = "Program", value = "B.Tech Computer Science & AI", confidence = 0.97f, category = "General"),
                ExtractedField(key = "Semester", value = "Semester VI (Spring 2026)", confidence = 0.96f, category = "Temporal"),
                ExtractedField(key = "Total Credits", value = "20.0", confidence = 0.95f, category = "Academic"),
                ExtractedField(key = "SGPA", value = "9.60 / 10.00", confidence = 0.99f, category = "Academic"),
                ExtractedField(key = "CGPA", value = "9.45 / 10.00", confidence = 0.98f, category = "Academic"),
                ExtractedField(key = "Final Result", value = "First Class Distinction", confidence = 0.97f, category = "Academic")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "Course Grade Ledger",
                    headers = mutableListOf("Course Code", "Course Title", "Credits", "Grade", "Points"),
                    rows = mutableListOf(
                        mutableListOf("CS601", "Advanced Machine Learning & Vision", "4.0", "A+", "10.0"),
                        mutableListOf("CS602", "Distributed Cloud Architecture", "4.0", "A", "9.0"),
                        mutableListOf("CS603", "Database Engineering & Scalability", "3.0", "A+", "10.0"),
                        mutableListOf("CS604", "Mobile Systems Design (Compose)", "3.0", "O", "10.0"),
                        mutableListOf("CS605", "Neural Networks & LLM Foundations", "4.0", "A", "9.0"),
                        mutableListOf("CS606", "Capstone Project Stage I", "2.0", "A+", "10.0")
                    ),
                    confidence = 0.97f
                )
            ),
            summary = "Official university transcript for Ishant Rathore (Enrollment: PIT-2024-CS-0412) with SGPA 9.60 and First Class Distinction."
        ),
        SampleDocument(
            id = "sample_statement_04",
            title = "Metro National Bank Monthly Statement",
            type = DocumentType.BANK_STATEMENT,
            description = "Checking account statement showing opening/closing balances and transaction ledger.",
            rawText = """
                METRO NATIONAL BANK
                Account Statement - Premier Business Checking
                
                Account Holder: Quantum Labs LLC
                Account Number: *******-4491
                Statement Period: August 01, 2026 - August 31, 2026
                Branch: 500 Financial Center, Seattle, WA
                
                Opening Balance: $14,250.00
                Total Deposits (Credits): $18,400.00
                Total Withdrawals (Debits): -$9,620.50
                Closing Balance: $23,029.50
                
                Date | Description | Type | Amount | Balance
                08/03/2026 | Wire Transfer from Client Acme | Deposit | +$8,500.00 | $22,750.00
                08/07/2026 | AWS Cloud Hosting Infrastructure | Debit | -$1,240.50 | $21,509.50
                08/15/2026 | Stripe Payout Merchant Settlement | Deposit | +$9,900.00 | $31,409.50
                08/22/2026 | Commercial Office Lease Payment | Debit | -$5,500.00 | $25,909.50
                08/29/2026 | Payroll Direct Deposit Batch | Debit | -$2,880.00 | $23,029.50
            """.trimIndent(),
            fields = listOf(
                ExtractedField(key = "Bank Name", value = "Metro National Bank", confidence = 0.99f, category = "Party / Entity"),
                ExtractedField(key = "Account Holder", value = "Quantum Labs LLC", confidence = 0.98f, category = "Party / Entity"),
                ExtractedField(key = "Account Number", value = "*******-4491", confidence = 0.97f, category = "Identifier"),
                ExtractedField(key = "Period", value = "Aug 01, 2026 - Aug 31, 2026", confidence = 0.96f, category = "Temporal"),
                ExtractedField(key = "Opening Balance", value = "$14,250.00", confidence = 0.98f, category = "Financial"),
                ExtractedField(key = "Total Deposits", value = "$18,400.00", confidence = 0.97f, category = "Financial"),
                ExtractedField(key = "Total Withdrawals", value = "-$9,620.50", confidence = 0.97f, category = "Financial"),
                ExtractedField(key = "Closing Balance", value = "$23,029.50", confidence = 0.99f, category = "Financial")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "Account Transaction History",
                    headers = mutableListOf("Date", "Description", "Type", "Amount", "Balance"),
                    rows = mutableListOf(
                        mutableListOf("08/03/2026", "Wire Transfer Client Acme", "Deposit", "+$8,500.00", "$22,750.00"),
                        mutableListOf("08/07/2026", "AWS Infrastructure", "Debit", "-$1,240.50", "$21,509.50"),
                        mutableListOf("08/15/2026", "Stripe Merchant Settlement", "Deposit", "+$9,900.00", "$31,409.50"),
                        mutableListOf("08/22/2026", "Commercial Office Lease", "Debit", "-$5,500.00", "$25,909.50"),
                        mutableListOf("08/29/2026", "Payroll Direct Deposit", "Debit", "-$2,880.00", "$23,029.50")
                    ),
                    confidence = 0.96f
                )
            ),
            summary = "Metro National Bank statement for Quantum Labs LLC (*4491) closing with $23,029.50 balance for August 2026."
        )
    )
}
