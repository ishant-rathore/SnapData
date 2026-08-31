package com.example.snapdata.export

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.snapdata.data.DocumentEntity
import com.example.snapdata.error.AppError
import com.example.snapdata.logging.AppLogger
import com.example.snapdata.model.ExportFormat
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Production-hardened Export Manager for SnapData.
 * Supports Excel (.xlsx), CSV (.csv), JSON (.json), and PDF (.pdf).
 *
 * Guarantees:
 * - RFC 4180 CSV standard compliance with UTF-8 BOM for Unicode/Hindi compatibility.
 * - Valid OpenXML SpreadsheetML standard compliance with multi-sheet & multi-table support,
 *   Base-26 column coordinate calculations, and XML 1.0 control character sanitization.
 * - Multi-page dynamic PDF pagination with header/footer page numbering and table splitting.
 * - Complete structured JSON payload with ISO-8601 UTC timestamps.
 * - Secure filename sanitization protecting against path traversal and forbidden filesystem characters.
 * - Android FileProvider content URI sharing with safe permission grant flags.
 */
object ExportManager {

    data class ExportResult(
        val file: File,
        val format: ExportFormat,
        val mimeType: String,
        val sizeBytes: Long,
        val success: Boolean = true,
        val errorMessage: String? = null
    )

    /**
     * Sanitizes arbitrary document titles into a safe filesystem filename.
     * Preserves valid Unicode (including Hindi, Arabic, Chinese, accented Latin),
     * replaces illegal filesystem characters (\ / : * ? " < > | and control chars) with underscores,
     * and trims to a safe length.
     */
    fun sanitizeFilename(title: String, fallback: String = "Document"): String {
        val clean = title
            .replace(Regex("[\\\\/:*?\"<>|\\x00-\\x1F]"), "_")
            .replace(Regex("\\s+"), "_")
            .replace(Regex("_+"), "_")
            .trim('_', '.', ' ')

        return if (clean.isBlank()) fallback else clean.take(40)
    }

    /**
     * Generates a unique, collision-resistant, filesystem-safe filename.
     */
    fun generateExportFilename(title: String, format: ExportFormat): String {
        val safeTitle = sanitizeFilename(title)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "SnapData_${safeTitle}_$timestamp.${format.extension}"
    }

    /**
     * Exports a [DocumentEntity] to the target [ExportFormat] inside the application's secure cache directory.
     * Throws descriptive exceptions on IO or formatting failures.
     */
    fun exportDocument(context: Context, doc: DocumentEntity, format: ExportFormat): ExportResult {
        AppLogger.i(AppLogger.LogDomain.EXPORT, "Starting export for document #${doc.id} ('${doc.title}') as ${format.displayName}")
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists() && !exportDir.mkdirs()) {
            val err = "Failed to create export directory in application cache: ${exportDir.absolutePath}"
            AppLogger.e(AppLogger.LogDomain.EXPORT, err)
            throw java.io.IOException(err)
        }

        val fileName = generateExportFilename(doc.title, format)
        val targetFile = File(exportDir, fileName)

        // Ensure clean target file
        if (targetFile.exists()) {
            targetFile.delete()
        }

        try {
            when (format) {
                ExportFormat.EXCEL -> generateExcelXlsx(doc, targetFile)
                ExportFormat.CSV -> generateCsv(doc, targetFile)
                ExportFormat.JSON -> generateJson(doc, targetFile)
                ExportFormat.PDF -> generatePdf(doc, targetFile)
            }
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.EXPORT, "Export to ${format.displayName} failed: ${e.localizedMessage}", e)
            if (targetFile.exists()) {
                targetFile.delete()
            }
            throw java.io.IOException("Export to ${format.displayName} failed: ${e.message}", e)
        }

        val length = targetFile.length()
        if (length == 0L) {
            AppLogger.e(AppLogger.LogDomain.EXPORT, "Export generated an empty 0-byte file: ${targetFile.name}")
            throw java.io.IOException("Export produced an empty 0-byte file.")
        }

        AppLogger.i(AppLogger.LogDomain.EXPORT, "Successfully generated export ${targetFile.name} ($length bytes)")

        return ExportResult(
            file = targetFile,
            format = format,
            mimeType = format.mimeType,
            sizeBytes = length,
            success = true
        )
    }

    /**
     * Shares the exported file using Android's standard [Intent.ACTION_SEND] and [FileProvider].
     */
    fun shareExportedFile(context: Context, result: ExportResult) {
        if (!result.file.exists() || result.file.length() == 0L) {
            AppLogger.e(AppLogger.LogDomain.SHARING, "Export file missing or empty: ${result.file.absolutePath}")
            throw java.io.FileNotFoundException("Export file does not exist or is empty: ${result.file.absolutePath}")
        }

        val authority = "${context.packageName}.fileprovider"
        val uri = try {
            FileProvider.getUriForFile(context, authority, result.file)
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.SHARING, "FileProvider URI generation failed ($authority): ${e.localizedMessage}", e)
            throw SecurityException("Failed to generate content URI via FileProvider ($authority): ${e.message}", e)
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = result.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "SnapData Export: ${result.file.name}")
            putExtra(Intent.EXTRA_TEXT, "Extracted data exported from SnapData (${result.format.displayName}).")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share Document via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            AppLogger.i(AppLogger.LogDomain.SHARING, "Launching share chooser for ${result.file.name}")
            context.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            AppLogger.w(AppLogger.LogDomain.SHARING, "No compatible application found to share ${result.format.displayName} files", e)
            throw ActivityNotFoundException("No application found on device to share ${result.format.displayName} files.")
        }
    }

    // =========================================================================
    // 1. JSON EXPORT
    // =========================================================================

    fun generateJsonString(doc: DocumentEntity): String {
        val json = Json {
            prettyPrint = true
            encodeDefaults = true
            isLenient = true
        }

        val rootObj = buildJsonObject {
            put("snapDataVersion", "2.0")
            put(
                "exportedAt",
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date())
            )
            put("documentId", doc.id)
            put("title", doc.title)
            put("documentType", doc.docType)
            put("pageCount", doc.pageCount)
            put("overallConfidence", doc.overallConfidence)
            put("summary", doc.summary)
            put("createdAt", doc.createdAt)
            put("updatedAt", doc.updatedAt)

            putJsonArray("fields") {
                doc.getFieldsList().forEach { field ->
                    addJsonObject {
                        put("id", field.id)
                        put("key", field.key)
                        put("value", field.value)
                        put("category", field.category)
                        put("confidence", field.confidence)
                        put("isUserEdited", field.isUserEdited)
                        put("confidenceSource", field.confidenceSource.name)
                    }
                }
            }

            putJsonArray("tables") {
                doc.getTablesList().forEachIndexed { index, table ->
                    addJsonObject {
                        put("id", table.id)
                        put("name", table.name.ifBlank { "Table ${index + 1}" })
                        put("confidence", table.confidence)
                        put("confidenceSource", table.confidenceSource.name)
                        putJsonArray("headers") {
                            table.headers.forEach { add(it) }
                        }
                        putJsonArray("rows") {
                            table.rows.forEach { row ->
                                val rowArray = buildJsonArray {
                                    row.forEach { add(it) }
                                }
                                add(rowArray)
                            }
                        }
                    }
                }
            }

            put("rawOcrText", doc.rawOcrText)
        }

        return json.encodeToString(rootObj)
    }

    private fun generateJson(doc: DocumentEntity, file: File) {
        OutputStreamWriter(FileOutputStream(file), Charsets.UTF_8).use { writer ->
            writer.write(generateJsonString(doc))
        }
    }

    // =========================================================================
    // 2. CSV EXPORT (RFC 4180 with UTF-8 BOM)
    // =========================================================================

    /**
     * Escapes a single CSV cell according to RFC 4180.
     * Prevents CSV Formula Injection (DDE) by prefixing dangerous formula symbols (=, +, -, @, \t, \r) with '.
     */
    fun escapeCsv(raw: String?): String {
        if (raw == null) return "\"\""
        var cell = raw
        // Neutralize formula injection
        if (cell.isNotEmpty() && (cell.startsWith("=") || cell.startsWith("+") || cell.startsWith("-") || cell.startsWith("@") || cell.startsWith("\t") || cell.startsWith("\r"))) {
            cell = "'$cell"
        }
        val escaped = cell.replace("\"", "\"\"")
        return "\"$escaped\""
    }

    fun generateCsvString(doc: DocumentEntity): String {
        val sb = StringBuilder()
        sb.appendLine("${escapeCsv("Document Title")},${escapeCsv(doc.title)}")
        sb.appendLine("${escapeCsv("Document Type")},${escapeCsv(doc.docType)}")
        sb.appendLine("${escapeCsv("Extraction Confidence")},${escapeCsv("${(doc.overallConfidence * 100).toInt()}%")}")
        sb.appendLine("${escapeCsv("Page Count")},${escapeCsv(doc.pageCount.toString())}")
        if (doc.summary.isNotBlank()) {
            sb.appendLine("${escapeCsv("Summary")},${escapeCsv(doc.summary)}")
        }
        sb.appendLine()

        val fields = doc.getFieldsList()
        if (fields.isNotEmpty()) {
            sb.appendLine("--- KEY-VALUE FIELDS ---")
            sb.appendLine("${escapeCsv("Field Key")},${escapeCsv("Field Value")},${escapeCsv("Category")},${escapeCsv("Confidence")},${escapeCsv("Edited")}")
            fields.forEach { f ->
                sb.appendLine("${escapeCsv(f.key)},${escapeCsv(f.value)},${escapeCsv(f.category)},${escapeCsv("${(f.confidence * 100).toInt()}%")},${escapeCsv(f.isUserEdited.toString())}")
            }
        }

        val tables = doc.getTablesList()
        tables.forEachIndexed { index, table ->
            sb.appendLine()
            val tblName = table.name.ifBlank { "Table ${index + 1}" }
            sb.appendLine("--- TABLE ${index + 1}: $tblName ---")
            if (table.headers.isNotEmpty()) {
                sb.appendLine(table.headers.joinToString(",") { escapeCsv(it) })
            }
            table.rows.forEach { row ->
                sb.appendLine(row.joinToString(",") { escapeCsv(it) })
            }
        }

        return sb.toString()
    }

    private fun generateCsv(doc: DocumentEntity, file: File) {
        FileOutputStream(file).use { fos ->
            // Write UTF-8 Byte Order Mark (BOM: EF BB BF) so Excel on Windows opens Unicode/Hindi flawlessly
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            OutputStreamWriter(fos, Charsets.UTF_8).use { writer ->
                writer.write(generateCsvString(doc))
            }
        }
    }

    // =========================================================================
    // 3. MARKDOWN EXPORT
    // =========================================================================

    fun generateMarkdownString(doc: DocumentEntity): String {
        val sb = StringBuilder()
        sb.appendLine("# ${doc.title.ifBlank { "Untitled Document" }}")
        sb.appendLine("**Type**: ${doc.docType} | **Confidence**: ${(doc.overallConfidence * 100).toInt()}% | **Pages**: ${doc.pageCount}")
        sb.appendLine()
        if (doc.summary.isNotBlank()) {
            sb.appendLine("## Summary")
            sb.appendLine(doc.summary)
            sb.appendLine()
        }
        val fields = doc.getFieldsList()
        if (fields.isNotEmpty()) {
            sb.appendLine("## Key Fields")
            fields.forEach { f ->
                sb.appendLine("- **${f.key}**: ${f.value} `(${f.category})`")
            }
            sb.appendLine()
        }
        val tables = doc.getTablesList()
        tables.forEachIndexed { index, table ->
            val tblName = table.name.ifBlank { "Table ${index + 1}" }
            sb.appendLine("## $tblName")
            if (table.headers.isNotEmpty()) {
                sb.appendLine("| " + table.headers.joinToString(" | ") + " |")
                sb.appendLine("| " + table.headers.joinToString(" | ") { "---" } + " |")
            }
            table.rows.forEach { row ->
                sb.appendLine("| " + row.joinToString(" | ") + " |")
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    // =========================================================================
    // 4. PURE KOTLIN OPENXML EXCEL (.xlsx) GENERATOR
    // =========================================================================

    /**
     * Converts a 0-indexed column integer into a bijective Base-26 column coordinate (0 -> A, 25 -> Z, 26 -> AA).
     */
    fun getColumnLetter(colIndex: Int): String {
        var index = colIndex
        val sb = StringBuilder()
        while (index >= 0) {
            sb.append(('A'.code + (index % 26)).toChar())
            index = (index / 26) - 1
        }
        return sb.reverse().toString()
    }

    /**
     * Sanitizes string for XML 1.0 specifications and escapes entity characters.
     * Strips control characters (code < 0x20 except 0x09, 0x0A, 0x0D).
     */
    fun escapeXml(str: String?): String {
        if (str == null) return ""
        val clean = str.filter { ch ->
            val code = ch.code
            code == 0x09 || code == 0x0A || code == 0x0D ||
                    (code in 0x20..0xD7FF) ||
                    (code in 0xE000..0xFFFD)
        }
        return clean
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    /**
     * Generates a valid XML inlineStr cell element with preserve-space semantics.
     */
    private fun makeCellXml(colLetter: String, rowNum: Int, value: String): String {
        val escaped = escapeXml(value)
        return "<c r=\"$colLetter$rowNum\" t=\"inlineStr\"><is><t xml:space=\"preserve\">$escaped</t></is></c>"
    }

    /**
     * Sanitizes sheet names for Excel compatibility (max 31 chars, no forbidden chars []:*?/\).
     */
    private fun sanitizeSheetName(rawName: String, fallback: String): String {
        val clean = rawName
            .replace(Regex("[\\[\\]:*?/\\\\]"), "_")
            .trim()
            .take(31)
        return if (clean.isBlank()) fallback else clean
    }

    fun generateExcelXlsx(doc: DocumentEntity, file: File) {
        val fields = doc.getFieldsList()
        val tables = doc.getTablesList()

        // Sheet 1: Overview & Key-Value Fields
        val sheet1Xml = buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
            append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n")
            append("<sheetData>\n")
            append("<row r=\"1\">${makeCellXml("A", 1, "SnapData Digitized Document Export")}</row>\n")
            append("<row r=\"2\">${makeCellXml("A", 2, "Title:")}${makeCellXml("B", 2, doc.title)}</row>\n")
            append("<row r=\"3\">${makeCellXml("A", 3, "Document Type:")}${makeCellXml("B", 3, doc.docType)}</row>\n")
            append("<row r=\"4\">${makeCellXml("A", 4, "Overall Confidence:")}${makeCellXml("B", 4, "${(doc.overallConfidence * 100).toInt()}%")}</row>\n")
            append("<row r=\"5\">${makeCellXml("A", 5, "Pages:")}${makeCellXml("B", 5, doc.pageCount.toString())}</row>\n")
            if (doc.summary.isNotBlank()) {
                append("<row r=\"6\">${makeCellXml("A", 6, "Summary:")}${makeCellXml("B", 6, doc.summary)}</row>\n")
            }
            append("<row r=\"7\"/>\n")
            append("<row r=\"8\">${makeCellXml("A", 8, "Field Key")}${makeCellXml("B", 8, "Extracted Value")}${makeCellXml("C", 8, "Category")}${makeCellXml("D", 8, "Confidence")}${makeCellXml("E", 8, "Edited")}</row>\n")

            var r = 9
            fields.forEach { f ->
                append("<row r=\"$r\">")
                append(makeCellXml("A", r, f.key))
                append(makeCellXml("B", r, f.value))
                append(makeCellXml("C", r, f.category))
                append(makeCellXml("D", r, "${(f.confidence * 100).toInt()}%"))
                append(makeCellXml("E", r, f.isUserEdited.toString()))
                append("</row>\n")
                r++
            }

            // If only 1 sheet mode is needed or summary tables
            if (tables.isNotEmpty()) {
                r += 2
                append("<row r=\"$r\">${makeCellXml("A", r, "Detected Tabular Matrices (${tables.size} table(s))")}</row>\n")
                r++
                tables.forEachIndexed { tIdx, tbl ->
                    append("<row r=\"$r\">${makeCellXml("A", r, "Table ${tIdx + 1}: ${tbl.name.ifBlank { "Table ${tIdx + 1}" }} (${tbl.rows.size} rows x ${tbl.headers.size} cols)")}</row>\n")
                    r++
                }
            }

            append("</sheetData>\n</worksheet>")
        }

        // Additional sheets for each table
        val tableSheets = tables.mapIndexed { index, tbl ->
            val sheetNumber = index + 2
            val sheetXml = buildString {
                append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
                append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n")
                append("<sheetData>\n")

                var r = 1
                val tblTitle = tbl.name.ifBlank { "Table ${index + 1}" }
                append("<row r=\"$r\">${makeCellXml("A", r, tblTitle)}</row>\n")
                r++

                // Headers
                if (tbl.headers.isNotEmpty()) {
                    append("<row r=\"$r\">")
                    tbl.headers.forEachIndexed { hIdx, header ->
                        val col = getColumnLetter(hIdx)
                        append(makeCellXml(col, r, header))
                    }
                    append("</row>\n")
                    r++
                }

                // Rows
                tbl.rows.forEach { row ->
                    append("<row r=\"$r\">")
                    row.forEachIndexed { cIdx, cell ->
                        val col = getColumnLetter(cIdx)
                        append(makeCellXml(col, r, cell))
                    }
                    append("</row>\n")
                    r++
                }

                append("</sheetData>\n</worksheet>")
            }
            Pair(sheetNumber, sheetXml)
        }

        val contentTypesXml = buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
            append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n")
            append("    <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n")
            append("    <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n")
            append("    <Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n")
            append("    <Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n")
            tableSheets.forEach { (sheetNum, _) ->
                append("    <Override PartName=\"/xl/worksheets/sheet$sheetNum.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n")
            }
            append("</Types>")
        }

        val relsXml = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
            </Relationships>
        """.trimIndent()

        val workbookXml = buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
            append("<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n")
            append("    <sheets>\n")
            append("        <sheet name=\"Overview\" sheetId=\"1\" r:id=\"rId1\"/>\n")
            tableSheets.forEachIndexed { idx, _ ->
                val sheetNum = idx + 2
                val tbl = tables[idx]
                val safeName = sanitizeSheetName(tbl.name, "Table $sheetNum")
                append("        <sheet name=\"${escapeXml(safeName)}\" sheetId=\"$sheetNum\" r:id=\"rId$sheetNum\"/>\n")
            }
            append("    </sheets>\n")
            append("</workbook>")
        }

        val workbookRelsXml = buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
            append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n")
            append("    <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>\n")
            tableSheets.forEach { (sheetNum, _) ->
                append("    <Relationship Id=\"rId$sheetNum\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet$sheetNum.xml\"/>\n")
            }
            append("</Relationships>")
        }

        FileOutputStream(file).use { fos ->
            ZipOutputStream(fos).use { zos ->
                addZipEntry(zos, "[Content_Types].xml", contentTypesXml)
                addZipEntry(zos, "_rels/.rels", relsXml)
                addZipEntry(zos, "xl/workbook.xml", workbookXml)
                addZipEntry(zos, "xl/_rels/workbook.xml.rels", workbookRelsXml)
                addZipEntry(zos, "xl/worksheets/sheet1.xml", sheet1Xml)
                tableSheets.forEach { (sheetNum, xml) ->
                    addZipEntry(zos, "xl/worksheets/sheet$sheetNum.xml", xml)
                }
            }
        }
    }

    private fun addZipEntry(zos: ZipOutputStream, name: String, content: String) {
        val entry = ZipEntry(name)
        zos.putNextEntry(entry)
        zos.write(content.toByteArray(Charsets.UTF_8))
        zos.closeEntry()
    }

    // =========================================================================
    // 5. MULTI-PAGE PDF DOCUMENT EXPORT
    // =========================================================================

    private fun generatePdf(doc: DocumentEntity, file: File) {
        val pdfDocument = PdfDocument()
        var currentPageNumber = 1
        val pageWidth = 595
        val pageHeight = 842
        val leftMargin = 36f
        val rightMargin = 559f
        val usableWidth = rightMargin - leftMargin // 523f
        val bottomMargin = 780f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Helper to start a page
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var y = 0f

        fun drawHeaderBanner() {
            paint.color = Color.parseColor("#0066FF")
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, paint)

            paint.color = Color.WHITE
            paint.textSize = 20f
            paint.isFakeBoldText = true
            canvas.drawText("SnapData Digitized Document", leftMargin, 42f, paint)

            paint.textSize = 11f
            paint.isFakeBoldText = false
            paint.color = Color.parseColor("#D0E2FF")
            canvas.drawText("AI-Powered Structured Data Extraction Report", leftMargin, 66f, paint)
            y = 115f
        }

        fun drawPageHeader() {
            paint.color = Color.parseColor("#0066FF")
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 32f, paint)

            paint.color = Color.WHITE
            paint.textSize = 10f
            paint.isFakeBoldText = true
            canvas.drawText("SnapData Digitized Document • ${doc.title.take(30)}", leftMargin, 20f, paint)
            y = 52f
        }

        fun drawPageFooter(pageNum: Int) {
            paint.color = Color.parseColor("#94A3B8")
            paint.textSize = 8f
            paint.isFakeBoldText = false
            canvas.drawLine(leftMargin, 805f, rightMargin, 805f, paint)
            canvas.drawText("Generated by SnapData Mobile Document Extraction Engine • Offline-First Guaranteed", leftMargin, 822f, paint)
            val pageStr = "Page $pageNum"
            val pageStrWidth = paint.measureText(pageStr)
            canvas.drawText(pageStr, rightMargin - pageStrWidth, 822f, paint)
        }

        fun advancePage() {
            drawPageFooter(currentPageNumber)
            pdfDocument.finishPage(page)
            currentPageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            drawPageHeader()
        }

        try {
            // First page setup
            drawHeaderBanner()

            // Document Metadata Box
            paint.color = Color.parseColor("#F1F5F9")
            canvas.drawRoundRect(leftMargin, y, rightMargin, y + 72f, 8f, 8f, paint)

            paint.color = Color.parseColor("#0F172A")
            paint.textSize = 13f
            paint.isFakeBoldText = true
            canvas.drawText("Document: ${doc.title.ifBlank { "Untitled Document" }.take(50)}", leftMargin + 14f, y + 24f, paint)

            paint.textSize = 9.5f
            paint.isFakeBoldText = false
            paint.color = Color.parseColor("#475569")
            canvas.drawText(
                "Type: ${doc.docType}   •   Confidence: ${(doc.overallConfidence * 100).toInt()}%   •   Pages: ${doc.pageCount}",
                leftMargin + 14f,
                y + 44f,
                paint
            )
            canvas.drawText(
                "Created: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(doc.createdAt))}",
                leftMargin + 14f,
                y + 60f,
                paint
            )

            y += 88f

            // Summary Section
            if (doc.summary.isNotBlank()) {
                if (y + 40f > bottomMargin) advancePage()

                paint.color = Color.parseColor("#0F172A")
                paint.textSize = 11f
                paint.isFakeBoldText = true
                canvas.drawText("Executive Summary", leftMargin, y, paint)
                y += 16f

                paint.color = Color.parseColor("#334155")
                paint.textSize = 9f
                paint.isFakeBoldText = false
                val summaryLines = wrapText(doc.summary, usableWidth, paint)
                summaryLines.forEach { line ->
                    if (y + 14f > bottomMargin) advancePage()
                    canvas.drawText(line, leftMargin, y, paint)
                    y += 13f
                }
                y += 10f
            }

            // Key-Value Fields Section
            val fields = doc.getFieldsList()
            if (fields.isNotEmpty()) {
                if (y + 45f > bottomMargin) advancePage()

                paint.color = Color.parseColor("#0F172A")
                paint.textSize = 11f
                paint.isFakeBoldText = true
                canvas.drawText("Extracted Key-Value Fields (${fields.size})", leftMargin, y, paint)
                y += 16f

                // Table Header
                paint.color = Color.parseColor("#E2E8F0")
                canvas.drawRect(leftMargin, y - 11f, rightMargin, y + 5f, paint)
                paint.color = Color.parseColor("#475569")
                paint.textSize = 8.5f
                paint.isFakeBoldText = true
                canvas.drawText("ATTRIBUTE", leftMargin + 6f, y, paint)
                canvas.drawText("VALUE", leftMargin + 170f, y, paint)
                canvas.drawText("CATEGORY", leftMargin + 380f, y, paint)
                canvas.drawText("CONFIDENCE", leftMargin + 460f, y, paint)
                y += 14f

                fields.forEachIndexed { idx, f ->
                    if (y + 16f > bottomMargin) {
                        advancePage()
                        // Re-draw sub-header on new page
                        paint.color = Color.parseColor("#E2E8F0")
                        canvas.drawRect(leftMargin, y - 11f, rightMargin, y + 5f, paint)
                        paint.color = Color.parseColor("#475569")
                        paint.textSize = 8.5f
                        paint.isFakeBoldText = true
                        canvas.drawText("ATTRIBUTE (cont.)", leftMargin + 6f, y, paint)
                        canvas.drawText("VALUE", leftMargin + 170f, y, paint)
                        canvas.drawText("CATEGORY", leftMargin + 380f, y, paint)
                        canvas.drawText("CONFIDENCE", leftMargin + 460f, y, paint)
                        y += 14f
                    }

                    // Background zebra row
                    if (idx % 2 == 1) {
                        paint.color = Color.parseColor("#F8FAFC")
                        canvas.drawRect(leftMargin, y - 10f, rightMargin, y + 4f, paint)
                    }

                    paint.color = Color.parseColor("#1E293B")
                    paint.textSize = 8.5f
                    paint.isFakeBoldText = true
                    canvas.drawText(f.key.take(24), leftMargin + 6f, y, paint)

                    paint.isFakeBoldText = false
                    paint.color = Color.parseColor("#0F172A")
                    canvas.drawText(f.value.take(36), leftMargin + 170f, y, paint)

                    paint.color = Color.parseColor("#64748B")
                    canvas.drawText(f.category.take(15), leftMargin + 380f, y, paint)

                    paint.color = Color.parseColor("#059669")
                    canvas.drawText("${(f.confidence * 100).toInt()}%", leftMargin + 465f, y, paint)

                    // Divider line
                    paint.color = Color.parseColor("#E2E8F0")
                    canvas.drawLine(leftMargin, y + 4f, rightMargin, y + 4f, paint)
                    y += 15f
                }
                y += 12f
            }

            // Tabular Matrices Section
            val tables = doc.getTablesList()
            tables.forEachIndexed { tIdx, tbl ->
                if (y + 50f > bottomMargin) advancePage()

                val tblTitle = tbl.name.ifBlank { "Table ${tIdx + 1}" }
                paint.color = Color.parseColor("#0F172A")
                paint.textSize = 11f
                paint.isFakeBoldText = true
                canvas.drawText("Tabular Matrix: $tblTitle", leftMargin, y, paint)
                y += 16f

                val colCount = maxOf(1, tbl.headers.size)
                val colWidth = usableWidth / colCount

                // Column Headers
                paint.color = Color.parseColor("#0066FF")
                canvas.drawRect(leftMargin, y - 11f, rightMargin, y + 5f, paint)
                paint.color = Color.WHITE
                paint.textSize = 8f
                paint.isFakeBoldText = true

                tbl.headers.forEachIndexed { i, h ->
                    canvas.drawText(h.take(20), leftMargin + 4f + (i * colWidth), y, paint)
                }
                y += 14f

                // Data Rows
                paint.isFakeBoldText = false
                tbl.rows.forEachIndexed { rIdx, row ->
                    if (y + 15f > bottomMargin) {
                        advancePage()
                        // Re-draw table header
                        paint.color = Color.parseColor("#0066FF")
                        canvas.drawRect(leftMargin, y - 11f, rightMargin, y + 5f, paint)
                        paint.color = Color.WHITE
                        paint.textSize = 8f
                        paint.isFakeBoldText = true
                        tbl.headers.forEachIndexed { i, h ->
                            canvas.drawText(h.take(20), leftMargin + 4f + (i * colWidth), y, paint)
                        }
                        y += 14f
                        paint.isFakeBoldText = false
                    }

                    paint.color = if (rIdx % 2 == 0) Color.WHITE else Color.parseColor("#F8FAFC")
                    canvas.drawRect(leftMargin, y - 10f, rightMargin, y + 4f, paint)

                    paint.color = Color.parseColor("#1E293B")
                    paint.textSize = 8f
                    row.forEachIndexed { i, cell ->
                        canvas.drawText(cell.take(22), leftMargin + 4f + (i * colWidth), y, paint)
                    }

                    paint.color = Color.parseColor("#E2E8F0")
                    canvas.drawLine(leftMargin, y + 4f, rightMargin, y + 4f, paint)
                    y += 15f
                }
                y += 14f
            }

            drawPageFooter(currentPageNumber)
            pdfDocument.finishPage(page)

            FileOutputStream(file).use { fos ->
                pdfDocument.writeTo(fos)
            }
        } finally {
            pdfDocument.close()
        }
    }

    private fun wrapText(text: String, maxWidth: Float, paint: Paint): List<String> {
        if (text.isBlank()) return emptyList()
        val words = text.split(Regex("\\s+"))
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }
}
