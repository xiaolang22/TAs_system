package com.group19.service;

import com.group19.dto.CVExtractResult;
import com.group19.model.TA;
import com.group19.util.CVParserUtil;
import jakarta.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CVService {
    public CVExtractResult extractFromText(String cvText) {
        return CVParserUtil.parse(cvText);
    }

    public CVExtractResult extractFromUpload(Part cvPart, String originalFileName, String savedFilePath)
            throws IOException {
        String extracted = readReadableText(cvPart, originalFileName, savedFilePath);
        return CVParserUtil.parse(extracted);
    }

    public TA mergeExtractedData(TA base, CVExtractResult extracted, String cvFileName) {
        TA target = (base == null) ? new TA() : base;
        if (notBlank(extracted.getProgramme())) {
            target.setProgramme(extracted.getProgramme());
        }
        if (notBlank(extracted.getSkills())) {
            target.setSkills(extracted.getSkills());
        }
        if (notBlank(extracted.getExperience())) {
            target.setExperience(extracted.getExperience());
        }
        if (notBlank(cvFileName)) {
            target.setCvFileName(cvFileName);
        }
        return target;
    }

    private String readReadableText(Part cvPart, String originalFileName, String savedFilePath) throws IOException {
        String lower = originalFileName == null ? "" : originalFileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".docx")) {
            String docxText = extractDocxText(savedFilePath);
            if (notBlank(docxText)) {
                return docxText;
            }
        }
        if (lower.endsWith(".txt")) {
            try (InputStream in = cvPart.getInputStream()) {
                return new String(readAll(in), StandardCharsets.UTF_8);
            }
        }

        // Fallback extraction for PDF/DOC: keep readable text blocks only.
        try (InputStream in = cvPart.getInputStream()) {
            byte[] bytes = readAll(in);
            String raw = new String(bytes, StandardCharsets.ISO_8859_1);
            return raw.replaceAll("[^\\p{L}\\p{N}\\p{Punct}\\s]", " ").replaceAll(" +", " ");
        }
    }

    private String extractDocxText(String savedFilePath) {
        try (ZipFile zip = new ZipFile(savedFilePath, StandardCharsets.UTF_8)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            StringBuilder builder = new StringBuilder();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.getName().startsWith("word/") || !entry.getName().endsWith(".xml")) {
                    continue;
                }
                try (InputStream in = zip.getInputStream(entry)) {
                    String xml = new String(readAll(in), StandardCharsets.UTF_8);
                    builder.append(xml.replaceAll("<[^>]+>", " ")).append('\n');
                }
            }
            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return bos.toByteArray();
    }

    private static boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
