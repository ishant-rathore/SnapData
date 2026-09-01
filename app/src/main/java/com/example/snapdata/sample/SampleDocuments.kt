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
            title = "Aarohan Digital Cloud Services Invoice",
            type = DocumentType.INVOICE,
            description = "Commercial SaaS & hosting monthly invoice with itemized line items and GST calculation.",
            rawText = """
                AAROHAN DIGITAL SOLUTIONS PVT. LTD.
                Office No. 402, Tech Plaza, Andheri East, Mumbai, Maharashtra 400069
                GSTIN: 27ABCDE1234F1Z5
                
                TAX INVOICE
                Invoice Number: INV-2026-1042
                Invoice Date: 28 August 2026
                Due Date: 15 September 2026
                Payment Terms: Net 30
                
                BILL TO:
                Shree Technologies Pvt. Ltd.
                Tech Park, Shivajinagar, Pune, Maharashtra 411001
                Contact: accounts@shreetech.in
                
                Item Description | Qty | Unit Price | Total
                Enterprise Cloud Cluster Tier 3 | 1 | ₹1,25,000.00 | ₹1,25,000.00
                Dedicated NVMe SSD Storage (5TB) | 5 | ₹9,000.00 | ₹45,000.00
                High-Speed CDN Bandwidth (50TB) | 1 | ₹32,000.00 | ₹32,000.00
                24/7 Dedicated SRE Support SLA | 1 | ₹50,000.00 | ₹50,000.00
                
                Subtotal: ₹2,52,000.00
                CGST (9%): ₹22,680.00
                SGST (9%): ₹22,680.00
                TOTAL DUE: ₹2,97,360.00
                Payment Status: PENDING
                Bank Transfer: HDFC Bank AC# 50100492819234 (IFSC: HDFC0000128)
            """.trimIndent(),
            fields = listOf(
                ExtractedField(key = "Invoice Number", value = "INV-2026-1042", confidence = 0.99f, category = "Identifier"),
                ExtractedField(key = "Invoice Date", value = "28 August 2026", confidence = 0.98f, category = "Temporal"),
                ExtractedField(key = "Due Date", value = "15 September 2026", confidence = 0.97f, category = "Temporal"),
                ExtractedField(key = "Vendor Name", value = "Aarohan Digital Solutions Pvt. Ltd.", confidence = 0.99f, category = "Party / Entity"),
                ExtractedField(key = "Client Name", value = "Shree Technologies Pvt. Ltd.", confidence = 0.98f, category = "Party / Entity"),
                ExtractedField(key = "GSTIN", value = "27ABCDE1234F1Z5", confidence = 0.98f, category = "Tax"),
                ExtractedField(key = "Subtotal", value = "₹2,52,000.00", confidence = 0.96f, category = "Financial"),
                ExtractedField(key = "CGST (9%)", value = "₹22,680.00", confidence = 0.95f, category = "Financial"),
                ExtractedField(key = "SGST (9%)", value = "₹22,680.00", confidence = 0.95f, category = "Financial"),
                ExtractedField(key = "Total Due", value = "₹2,97,360.00", confidence = 0.99f, category = "Financial"),
                ExtractedField(key = "Payment Status", value = "PENDING", confidence = 0.95f, category = "Status")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "Line Items & Services",
                    headers = mutableListOf("Item Description", "Qty", "Unit Price", "Total"),
                    rows = mutableListOf(
                        mutableListOf("Enterprise Cloud Cluster Tier 3", "1", "₹1,25,000.00", "₹1,25,000.00"),
                        mutableListOf("Dedicated NVMe SSD Storage (5TB)", "5", "₹9,000.00", "₹45,000.00"),
                        mutableListOf("High-Speed CDN Bandwidth (50TB)", "1", "₹32,000.00", "₹32,000.00"),
                        mutableListOf("24/7 Dedicated SRE Support SLA", "1", "₹50,000.00", "₹50,000.00")
                    ),
                    confidence = 0.96f
                )
            ),
            summary = "Invoice INV-2026-1042 issued by Aarohan Digital Solutions Pvt. Ltd. to Shree Technologies Pvt. Ltd. for ₹2,97,360.00 due on 15 Sep 2026."
        ),
        SampleDocument(
            id = "sample_receipt_02",
            title = "BrewBean Café POS Thermal Receipt",
            type = DocumentType.RECEIPT,
            description = "Point of sale thermal cafe receipt with items, GST breakdown, and UPI payment details.",
            rawText = """
                BREWBEAN CAFÉ
                Shop No. 4, Hill Road, Bandra West, Mumbai, Maharashtra 400050
                Tel: +91 98765 43210
                GSTIN: 27AABCU9603R1ZM
                
                TAX INVOICE / RECEIPT #77291
                Date: 30/08/2026 09:14 AM
                Server: Rohan M.   •   Station: POS-02
                
                Item | Qty | Price
                Special Masala Chai | 2 | ₹120.00
                Cold Coffee with Ice Cream | 2 | ₹340.00
                Paneer Tikka Grilled Sandwich | 1 | ₹180.00
                Butter Croissant | 1 | ₹110.00
                
                Subtotal: ₹750.00
                CGST (2.5%): ₹18.75
                SGST (2.5%): ₹18.75
                TOTAL AMOUNT: ₹787.50
                
                Payment Method: UPI (GPay / aarav@okhdfcbank)
                UPI Ref No: 62410881920
                THANK YOU FOR YOUR VISIT!
            """.trimIndent(),
            fields = listOf(
                ExtractedField(key = "Merchant", value = "BrewBean Café", confidence = 0.99f, category = "Party / Entity"),
                ExtractedField(key = "Receipt Number", value = "#77291", confidence = 0.97f, category = "Identifier"),
                ExtractedField(key = "Date & Time", value = "30/08/2026 09:14 AM", confidence = 0.98f, category = "Temporal"),
                ExtractedField(key = "GSTIN", value = "27AABCU9603R1ZM", confidence = 0.97f, category = "Tax"),
                ExtractedField(key = "Subtotal", value = "₹750.00", confidence = 0.96f, category = "Financial"),
                ExtractedField(key = "CGST", value = "₹18.75", confidence = 0.94f, category = "Financial"),
                ExtractedField(key = "SGST", value = "₹18.75", confidence = 0.94f, category = "Financial"),
                ExtractedField(key = "Total Amount", value = "₹787.50", confidence = 0.99f, category = "Financial"),
                ExtractedField(key = "Payment Method", value = "UPI (GPay / aarav@okhdfcbank)", confidence = 0.95f, category = "Financial")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "Purchased Items",
                    headers = mutableListOf("Item", "Qty", "Price"),
                    rows = mutableListOf(
                        mutableListOf("Special Masala Chai", "2", "₹120.00"),
                        mutableListOf("Cold Coffee with Ice Cream", "2", "₹340.00"),
                        mutableListOf("Paneer Tikka Grilled Sandwich", "1", "₹180.00"),
                        mutableListOf("Butter Croissant", "1", "₹110.00")
                    ),
                    confidence = 0.95f
                )
            ),
            summary = "Store receipt #77291 from BrewBean Café totalling ₹787.50 paid via UPI on 30 Aug 2026."
        ),
        SampleDocument(
            id = "sample_marksheet_03",
            title = "Pillai College of Engineering Academic Marksheet",
            type = DocumentType.MARK_SHEET,
            description = "Higher education academic transcript with semester grades, credits, SGPA, and classification.",
            rawText = """
                PILLAI COLLEGE OF ENGINEERING
                Dr. K. M. Vasudevan Pillai Campus, New Panvel, Navi Mumbai, Maharashtra 410206
                OFFICIAL TRANSCRIPT OF ACADEMIC RECORD
                
                Student Name: Aarav Sharma
                Enrollment / PRN: PCE-2024-CS-0412
                Program: Bachelor of Technology (Computer Science & AI)
                Semester: Semester VI (Even Semester 2026)
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
                Date of Issue: 20 July 2026
            """.trimIndent(),
            fields = listOf(
                ExtractedField(key = "Student Name", value = "Aarav Sharma", confidence = 0.99f, category = "Party / Entity"),
                ExtractedField(key = "Enrollment ID", value = "PCE-2024-CS-0412", confidence = 0.98f, category = "Identifier"),
                ExtractedField(key = "Program", value = "B.Tech Computer Science & AI", confidence = 0.97f, category = "General"),
                ExtractedField(key = "Semester", value = "Semester VI (Even Semester 2026)", confidence = 0.96f, category = "Temporal"),
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
            summary = "Official university transcript for Aarav Sharma (PRN: PCE-2024-CS-0412) with SGPA 9.60 and First Class Distinction."
        ),
        SampleDocument(
            id = "sample_statement_04",
            title = "State Bank of India Monthly Statement",
            type = DocumentType.BANK_STATEMENT,
            description = "Current account statement showing opening/closing balances and UPI/NEFT transaction ledger.",
            rawText = """
                STATE BANK OF INDIA
                Account Statement - Corporate Current Account
                
                Account Holder: Bharat Tech Ventures Pvt. Ltd.
                Account Number: XXXXXXXX4491
                IFSC: SBIN0001234
                Statement Period: 01 August 2026 - 31 August 2026
                Branch: Andheri East Branch, Mumbai, Maharashtra 400069
                
                Opening Balance: ₹1,42,500.00
                Total Deposits (Credits): ₹1,84,000.00
                Total Withdrawals (Debits): -₹96,205.00
                Closing Balance: ₹2,30,295.00
                
                Date | Description | Type | Amount | Balance
                03/08/2026 | NEFT Inward - Client Settlement | Deposit | +₹85,000.00 | ₹2,27,500.00
                07/08/2026 | Cloud Infrastructure Hosting | Debit | -₹12,405.00 | ₹2,15,095.00
                15/08/2026 | UPI Settlement - Razorpay Payout | Deposit | +₹99,000.00 | ₹3,14,095.00
                22/08/2026 | Commercial Office Lease Payment | Debit | -₹55,000.00 | ₹2,59,095.00
                29/08/2026 | IMPS Payroll Salary Transfer | Debit | -₹28,800.00 | ₹2,30,295.00
            """.trimIndent(),
            fields = listOf(
                ExtractedField(key = "Bank Name", value = "State Bank of India", confidence = 0.99f, category = "Party / Entity"),
                ExtractedField(key = "Account Holder", value = "Bharat Tech Ventures Pvt. Ltd.", confidence = 0.98f, category = "Party / Entity"),
                ExtractedField(key = "Account Number", value = "XXXXXXXX4491", confidence = 0.97f, category = "Identifier"),
                ExtractedField(key = "Period", value = "01 Aug 2026 - 31 Aug 2026", confidence = 0.96f, category = "Temporal"),
                ExtractedField(key = "Opening Balance", value = "₹1,42,500.00", confidence = 0.98f, category = "Financial"),
                ExtractedField(key = "Total Deposits", value = "₹1,84,000.00", confidence = 0.97f, category = "Financial"),
                ExtractedField(key = "Total Withdrawals", value = "-₹96,205.00", confidence = 0.97f, category = "Financial"),
                ExtractedField(key = "Closing Balance", value = "₹2,30,295.00", confidence = 0.99f, category = "Financial")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "Account Transaction History",
                    headers = mutableListOf("Date", "Description", "Type", "Amount", "Balance"),
                    rows = mutableListOf(
                        mutableListOf("03/08/2026", "NEFT Client Settlement", "Deposit", "+₹85,000.00", "₹2,27,500.00"),
                        mutableListOf("07/08/2026", "Cloud Infrastructure Hosting", "Debit", "-₹12,405.00", "₹2,15,095.00"),
                        mutableListOf("15/08/2026", "UPI Razorpay Payout", "Deposit", "+₹99,000.00", "₹3,14,095.00"),
                        mutableListOf("22/08/2026", "Commercial Office Lease", "Debit", "-₹55,000.00", "₹2,59,095.00"),
                        mutableListOf("29/08/2026", "IMPS Payroll Salary Transfer", "Debit", "-₹28,800.00", "₹2,30,295.00")
                    ),
                    confidence = 0.96f
                )
            ),
            summary = "State Bank of India statement for Bharat Tech Ventures Pvt. Ltd. (*4491) closing with ₹2,30,295.00 balance for August 2026."
        )
    )
}
