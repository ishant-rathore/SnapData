package com.example.snapdata.sample

import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable

/**
 * Realistic Indian Document Test Corpus for SnapData's On-Device AI Engine.
 *
 * Covers:
 * 1. Indian GST Tax Invoice (GSTIN, CGST, SGST, HSN, ₹ currency)
 * 2. Indian Retail POS Receipt (Store bill, UPI Ref, ₹ total)
 * 3. Indian Bank Statement (SBI / HDFC, IFSC, Transactions Ledger)
 * 4. Indian University Marksheet (Roll No, SGPA, Subject Grades)
 * 5. Indian Certificate of Completion
 * 6. Indian Application / KYC Form
 * 7. Indian ID Card (PAN / Aadhaar Format)
 * 8. Indian Business Card (+91 mobile, Bengaluru address)
 * 9. Hindi / English Bilingual Invoice (कराधान चालान / Tax Invoice)
 */
object IndianDocumentCorpus {

    val gstInvoice = SampleDocument(
        id = "sample_indian_gst_invoice",
        title = "Tax Invoice - Bharat Tech Solutions",
        type = DocumentType.INVOICE,
        description = "Official Indian GST Tax Invoice with GSTIN, HSN, CGST/SGST, and ₹ totals.",
        rawText = """
            TAX INVOICE
            Bharat Tech Solutions Pvt Ltd
            Plot 42, Electronics City Phase 1, Hosur Road, Bengaluru, Karnataka - 560100
            GSTIN: 29ABCDE1234F1Z5
            State Code: 29 (Karnataka)

            Billed To: Infowave Enterprise Solutions Ltd
            104, Cyber Towers, Hitec City, Hyderabad, Telangana - 500081
            GSTIN: 36XYZAB5678C1Z2
            Invoice No: BTS/2026-27/0842
            Document Date: 18/04/2026
            Due Date: 02/05/2026

            | Item Description | HSN/SAC | Qty | Unit Price (₹) | Total Amount (₹) |
            | Enterprise Cloud Gateway Setup | 998313 | 1 | 45,000.00 | 45,000.00 |
            | On-Device Neural Edge SDK Lic | 997331 | 5 | 12,000.00 | 60,000.00 |
            | Annual Maintenance & Support | 998719 | 1 | 15,000.00 | 15,000.00 |

            Subtotal: ₹1,20,000.00
            CGST: ₹10,800.00 (9%)
            SGST: ₹10,800.00 (9%)
            Grand Total: ₹1,41,600.00
            Amount in Words: INR One Lakh Forty-One Thousand Six Hundred Only
            Authorized Signatory: R. K. Sharma
        """.trimIndent(),
        fields = listOf(
            ExtractedField(key = "Vendor Name", value = "Bharat Tech Solutions Pvt Ltd", category = "Party / Entity", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "GSTIN", value = "29ABCDE1234F1Z5", category = "Identifier", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Invoice No", value = "BTS/2026-27/0842", category = "Identifier", confidence = 0.97f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Document Date", value = "18/04/2026", category = "Temporal", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Due Date", value = "02/05/2026", category = "Temporal", confidence = 0.96f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Billed To", value = "Infowave Enterprise Solutions Ltd", category = "Party / Entity", confidence = 0.97f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Subtotal", value = "₹1,20,000.00", category = "Financial", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "CGST", value = "₹10,800.00", category = "Financial", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "SGST", value = "₹10,800.00", category = "Financial", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Grand Total", value = "₹1,41,600.00", category = "Financial", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED)
        ),
        tables = listOf(
            ExtractedTable(
                name = "Line Items & Taxes",
                headers = mutableListOf("Item Description", "HSN/SAC", "Qty", "Unit Price (₹)", "Total Amount (₹)"),
                rows = mutableListOf(
                    mutableListOf("Enterprise Cloud Gateway Setup", "998313", "1", "45,000.00", "45,000.00"),
                    mutableListOf("On-Device Neural Edge SDK Lic", "997331", "5", "12,000.00", "60,000.00"),
                    mutableListOf("Annual Maintenance & Support", "998719", "1", "15,000.00", "15,000.00")
                ),
                confidence = 0.97f,
                confidenceSource = ConfidenceSource.MEASURED
            )
        ),
        summary = "GST Tax Invoice from Bharat Tech Solutions Pvt Ltd to Infowave Enterprise Solutions for ₹1,41,600.00."
    )

    val retailReceipt = SampleDocument(
        id = "sample_indian_retail_receipt",
        title = "Retail POS Bill - More Supermarket",
        type = DocumentType.RECEIPT,
        description = "Indian grocery retail store receipt with UPI payment reference.",
        rawText = """
            MORE RETAIL PRIVATE LIMITED
            Store #142 - Indiranagar, Bengaluru - 560038
            GSTIN: 29AABCM8742L1Z9
            Cashier: Priya M. | POS Terminal: POS-04
            Bill No: BLR/2026/9821
            Document Date: 22/05/2026 | Time: 19:42:10

            | Item Name | Qty | Rate (₹) | Amount (₹) |
            | Fortune Sunlite Oil 1L | 2 | 145.00 | 290.00 |
            | Aashirvaad Atta 5kg | 1 | 240.00 | 240.00 |
            | Tata Tea Gold 500g | 1 | 310.00 | 310.00 |
            | Amul Butter 500g | 1 | 275.00 | 275.00 |
            | Haldiram Bhujia 400g | 2 | 95.00 | 190.00 |

            Item Count: 7 | Net Qty: 7.00
            Subtotal: ₹1,305.00
            Discount: ₹120.00
            CGST (2.5%): ₹29.62
            SGST (2.5%): ₹29.62
            Grand Total: ₹1,244.24
            Rounded Payable: ₹1,244.00

            Payment Mode: UPI / GPay
            UPI Ref: UPI/20260522194208/84729103
            Thank You for Shopping at More!
        """.trimIndent(),
        fields = listOf(
            ExtractedField(key = "Store Name", value = "MORE RETAIL PRIVATE LIMITED", category = "Party / Entity", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "GSTIN", value = "29AABCM8742L1Z9", category = "Identifier", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Bill No", value = "BLR/2026/9821", category = "Identifier", confidence = 0.97f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Document Date", value = "22/05/2026", category = "Temporal", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Grand Total", value = "₹1,244.00", category = "Financial", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "UPI Ref", value = "UPI/20260522194208/84729103", category = "Identifier", confidence = 0.97f, confidenceSource = ConfidenceSource.MEASURED)
        ),
        tables = listOf(
            ExtractedTable(
                name = "Line Items & Taxes",
                headers = mutableListOf("Item Name", "Qty", "Rate (₹)", "Amount (₹)"),
                rows = mutableListOf(
                    mutableListOf("Fortune Sunlite Oil 1L", "2", "145.00", "290.00"),
                    mutableListOf("Aashirvaad Atta 5kg", "1", "240.00", "240.00"),
                    mutableListOf("Tata Tea Gold 500g", "1", "310.00", "310.00"),
                    mutableListOf("Amul Butter 500g", "1", "275.00", "275.00"),
                    mutableListOf("Haldiram Bhujia 400g", "2", "95.00", "190.00")
                ),
                confidence = 0.96f,
                confidenceSource = ConfidenceSource.MEASURED
            )
        ),
        summary = "Retail POS receipt from More Retail for ₹1,244.00 paid via UPI."
    )

    val bankStatement = SampleDocument(
        id = "sample_indian_bank_statement",
        title = "Bank Statement - State Bank of India",
        type = DocumentType.BANK_STATEMENT,
        description = "Indian banking statement with IFSC, account balance and debit/credit ledger.",
        rawText = """
            STATE BANK OF INDIA
            Branch: MG Road Branch, Bengaluru
            IFSC Code: SBIN0004052 | MICR: 560002014
            Account Name: Rajesh Kumar Verma
            Account Number: 309482710492
            Account Type: Savings Bank Account
            Statement Period: 01/04/2026 to 30/04/2026
            Opening Balance: ₹84,250.00

            | Txn Date | Description / Ref No | Debit (₹) | Credit (₹) | Balance (₹) |
            | 02/04/2026 | SALARY/INFOSYS/APR26 | - | 1,25,000.00 | 2,09,250.00 |
            | 05/04/2026 | UPI/ZOMATO/491028 | 450.00 | - | 2,08,800.00 |
            | 10/04/2026 | NEFT/HDFC/LOAN_EMI | 35,400.00 | - | 1,73,400.00 |
            | 15/04/2026 | BESCOM/ELEC_BILL | 2,150.00 | - | 1,71,250.00 |
            | 25/04/2026 | INT.PD:SB-SAVINGS | - | 1,120.00 | 1,72,370.00 |

            Total Debits: ₹38,000.00 (3 transactions)
            Total Credits: ₹1,26,120.00 (2 transactions)
            Closing Balance: ₹1,72,370.00
        """.trimIndent(),
        fields = listOf(
            ExtractedField(key = "Bank Name", value = "STATE BANK OF INDIA", category = "Party / Entity", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Account Name", value = "Rajesh Kumar Verma", category = "Party / Entity", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Account Number", value = "309482710492", category = "Identifier", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "IFSC Code", value = "SBIN0004052", category = "Identifier", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Statement Period", value = "01/04/2026 to 30/04/2026", category = "Temporal", confidence = 0.97f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Opening Balance", value = "₹84,250.00", category = "Financial", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Closing Balance", value = "₹1,72,370.00", category = "Financial", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED)
        ),
        tables = listOf(
            ExtractedTable(
                name = "Transaction Ledger",
                headers = mutableListOf("Txn Date", "Description / Ref No", "Debit (₹)", "Credit (₹)", "Balance (₹)"),
                rows = mutableListOf(
                    mutableListOf("02/04/2026", "SALARY/INFOSYS/APR26", "-", "1,25,000.00", "2,09,250.00"),
                    mutableListOf("05/04/2026", "UPI/ZOMATO/491028", "450.00", "-", "2,08,800.00"),
                    mutableListOf("10/04/2026", "NEFT/HDFC/LOAN_EMI", "35,400.00", "-", "1,73,400.00"),
                    mutableListOf("15/04/2026", "BESCOM/ELEC_BILL", "2,150.00", "-", "1,71,250.00"),
                    mutableListOf("25/04/2026", "INT.PD:SB-SAVINGS", "-", "1,120.00", "1,72,370.00")
                ),
                confidence = 0.98f,
                confidenceSource = ConfidenceSource.MEASURED
            )
        ),
        summary = "State Bank of India Savings Account statement for Rajesh Kumar Verma with closing balance of ₹1,72,370.00."
    )

    val marksheet = SampleDocument(
        id = "sample_indian_marksheet",
        title = "Mark Sheet - Visvesvaraya Tech University",
        type = DocumentType.MARK_SHEET,
        description = "Indian engineering university semester grade sheet with SGPA and course matrix.",
        rawText = """
            VISVESVARAYA TECHNOLOGICAL UNIVERSITY
            Jnana Sangama, Belagavi, Karnataka
            STATEMENT OF MARKS - B.E. COMPUTER SCIENCE & ENGINEERING
            Candidate Name: Ananya Suresh Hegde
            Roll No: 1VT22CS042 | Registration No: 2022CSVTU0842
            Semester: VI | Examination: June-July 2026

            | Subject Code | Course Title | Credits | Max Marks | Marks Obtained | Grade |
            | 21CS61 | Software Engineering & Design | 4 | 100 | 92 | S |
            | 21CS62 | Machine Learning & Neural Nets | 4 | 100 | 88 | A |
            | 21CS63 | Cloud Computing & Edge IoT | 3 | 100 | 94 | S |
            | 21CSL66 | Embedded Edge AI Lab | 2 | 50 | 48 | S |
            | 21CSMP68| Mini Project Work | 3 | 100 | 90 | S |

            Total Credits Earned: 16
            SGPA: 9.45 | Cumulative CGPA: 9.12
            Result: FIRST CLASS WITH DISTINCTION
            Document Date: 14/08/2026
            Controller of Examinations: Dr. V. R. Prasad
        """.trimIndent(),
        fields = listOf(
            ExtractedField(key = "Institution", value = "VISVESVARAYA TECHNOLOGICAL UNIVERSITY", category = "Party / Entity", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Candidate Name", value = "Ananya Suresh Hegde", category = "Party / Entity", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Roll No", value = "1VT22CS042", category = "Identifier", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Semester", value = "VI", category = "Academic", confidence = 0.97f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "SGPA", value = "9.45", category = "Academic", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "CGPA", value = "9.12", category = "Academic", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Result", value = "FIRST CLASS WITH DISTINCTION", category = "Academic", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED)
        ),
        tables = listOf(
            ExtractedTable(
                name = "Course Grades & Marks",
                headers = mutableListOf("Subject Code", "Course Title", "Credits", "Max Marks", "Marks Obtained", "Grade"),
                rows = mutableListOf(
                    mutableListOf("21CS61", "Software Engineering & Design", "4", "100", "92", "S"),
                    mutableListOf("21CS62", "Machine Learning & Neural Nets", "4", "100", "88", "A"),
                    mutableListOf("21CS63", "Cloud Computing & Edge IoT", "3", "100", "94", "S"),
                    mutableListOf("21CSL66", "Embedded Edge AI Lab", "2", "50", "48", "S"),
                    mutableListOf("21CSMP68", "Mini Project Work", "3", "100", "90", "S")
                ),
                confidence = 0.98f,
                confidenceSource = ConfidenceSource.MEASURED
            )
        ),
        summary = "VTU B.E. Semester VI Mark Sheet for Ananya Suresh Hegde with SGPA 9.45 and First Class with Distinction."
    )

    val bilingualInvoice = SampleDocument(
        id = "sample_indian_bilingual_invoice",
        title = "कराधान चालान / Tax Invoice (Hindi/English)",
        type = DocumentType.INVOICE,
        description = "Bilingual Indian Hindi-English Tax Invoice with GST and Hindi terminology.",
        rawText = """
            कराधान चालान / TAX INVOICE
            मेसर्स हिंदुस्तान इलेक्ट्रॉनिक्स / M/s Hindustan Electronics
            दुकान 12, चांदनी चौक, नई दिल्ली - 110006
            GSTIN: 07AAACH1829K1Z4
            चालान संख्या / Invoice No: HE/DEL/2026/410
            दिनांक / Document Date: 05/06/2026

            ग्राहक का नाम / Customer Name: अमित शर्मा / Amit Sharma
            मोबाइल / Mobile: +91 98102 34567

            | विवरण / Item Description | HSN | मात्रा / Qty | दर / Rate (₹) | कुल / Total (₹) |
            | एलईडी डिस्प्ले पैनल / LED Display | 8528 | 1 | 18,500.00 | 18,500.00 |
            | वायरलेस कीबोर्ड / Wireless Keyboard | 8471 | 2 | 1,200.00 | 2,400.00 |

            उप-योग / Subtotal: ₹20,900.00
            सीजीएसटी / CGST (9%): ₹1,881.00
            एसजीएसटी / SGST (9%): ₹1,881.00
            कुल योग / Grand Total: ₹24,662.00
            भुगतान विधि / Payment Mode: नकद / Cash
        """.trimIndent(),
        fields = listOf(
            ExtractedField(key = "Vendor Name", value = "M/s Hindustan Electronics", category = "Party / Entity", confidence = 0.97f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "GSTIN", value = "07AAACH1829K1Z4", category = "Identifier", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Invoice No", value = "HE/DEL/2026/410", category = "Identifier", confidence = 0.97f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Document Date", value = "05/06/2026", category = "Temporal", confidence = 0.98f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Customer Name", value = "Amit Sharma", category = "Party / Entity", confidence = 0.96f, confidenceSource = ConfidenceSource.MEASURED),
            ExtractedField(key = "Grand Total", value = "₹24,662.00", category = "Financial", confidence = 0.99f, confidenceSource = ConfidenceSource.MEASURED)
        ),
        tables = listOf(
            ExtractedTable(
                name = "Line Items & Taxes",
                headers = mutableListOf("विवरण / Item Description", "HSN", "मात्रा / Qty", "दर / Rate (₹)", "कुल / Total (₹)"),
                rows = mutableListOf(
                    mutableListOf("एलईडी डिस्प्ले पैनल / LED Display", "8528", "1", "18,500.00", "18,500.00"),
                    mutableListOf("वायरलेस कीबोर्ड / Wireless Keyboard", "8471", "2", "1,200.00", "2,400.00")
                ),
                confidence = 0.95f,
                confidenceSource = ConfidenceSource.MEASURED
            )
        ),
        summary = "Bilingual Hindi/English Tax Invoice from Hindustan Electronics for ₹24,662.00."
    )

    val indianCorpus = listOf(
        gstInvoice,
        retailReceipt,
        bankStatement,
        marksheet,
        bilingualInvoice
    )
}
