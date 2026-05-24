package com.group19.util;

import com.group19.TestRunner;

/**
 * Unit tests for {@link FileUploadUtil}.
 *
 * @author Group19
 * @since 1.0
 */
public class FileUploadUtilTest extends TestRunner {

    // ---- isAllowedCvFile() ----

    public void testIsAllowedCvFileAcceptsPdf() {
        assertTrue(FileUploadUtil.isAllowedCvFile("resume.pdf"));
    }

    public void testIsAllowedCvFileAcceptsDoc() {
        assertTrue(FileUploadUtil.isAllowedCvFile("resume.doc"));
    }

    public void testIsAllowedCvFileAcceptsDocx() {
        assertTrue(FileUploadUtil.isAllowedCvFile("cv.docx"));
    }

    public void testIsAllowedCvFileCaseInsensitive() {
        assertTrue(FileUploadUtil.isAllowedCvFile("CV.DOC"));
        assertTrue(FileUploadUtil.isAllowedCvFile("Resume.PDF"));
        assertTrue(FileUploadUtil.isAllowedCvFile("CV.DocX"));
    }

    public void testIsAllowedCvFileRejectsTxt() {
        assertFalse(FileUploadUtil.isAllowedCvFile("resume.txt"));
    }

    public void testIsAllowedCvFileRejectsNull() {
        assertFalse(FileUploadUtil.isAllowedCvFile(null));
    }

    public void testIsAllowedCvFileRejectsEmptyString() {
        assertFalse(FileUploadUtil.isAllowedCvFile(""));
    }

    public void testIsAllowedCvFileRejectsBlankString() {
        assertFalse(FileUploadUtil.isAllowedCvFile("   "));
    }

    public void testIsAllowedCvFileRejectsNoExtension() {
        assertFalse(FileUploadUtil.isAllowedCvFile("resume"));
    }

    public void testIsAllowedCvFileRejectsUnsupportedImageExtension() {
        assertFalse(FileUploadUtil.isAllowedCvFile("resume.png"));
        assertFalse(FileUploadUtil.isAllowedCvFile("resume.jpg"));
    }

    public void testIsAllowedCvFileRejectsExecutable() {
        assertFalse(FileUploadUtil.isAllowedCvFile("malicious.exe"));
    }

    // ---- isAllowedImageFile() ----

    public void testIsAllowedImageFileAcceptsPng() {
        assertTrue(FileUploadUtil.isAllowedImageFile("avatar.png"));
    }

    public void testIsAllowedImageFileAcceptsJpg() {
        assertTrue(FileUploadUtil.isAllowedImageFile("photo.JPG"));
    }

    public void testIsAllowedImageFileAcceptsJpeg() {
        assertTrue(FileUploadUtil.isAllowedImageFile("photo.jpeg"));
    }

    public void testIsAllowedImageFileAcceptsGif() {
        assertTrue(FileUploadUtil.isAllowedImageFile("animated.gif"));
    }

    public void testIsAllowedImageFileAcceptsWebp() {
        assertTrue(FileUploadUtil.isAllowedImageFile("image.webp"));
    }

    public void testIsAllowedImageFileRejectsBmp() {
        assertFalse(FileUploadUtil.isAllowedImageFile("photo.bmp"));
    }

    public void testIsAllowedImageFileRejectsTiff() {
        assertFalse(FileUploadUtil.isAllowedImageFile("photo.tiff"));
    }

    public void testIsAllowedImageFileRejectsNull() {
        assertFalse(FileUploadUtil.isAllowedImageFile(null));
    }

    public void testIsAllowedImageFileRejectsEmptyString() {
        assertFalse(FileUploadUtil.isAllowedImageFile(""));
    }

    public void testIsAllowedImageFileRejectsPdf() {
        assertFalse(FileUploadUtil.isAllowedImageFile("file.pdf"));
    }

    // ---- buildStoredCvFileName() ----

    public void testBuildStoredCvFileNameCorrectPattern() {
        String result = FileUploadUtil.buildStoredCvFileName("231221618", "resume.pdf");
        assertEquals("cv_231221618.pdf", result);
    }

    public void testBuildStoredCvFileNameWithDocExtension() {
        String result = FileUploadUtil.buildStoredCvFileName("12345", "cv.doc");
        assertEquals("cv_12345.doc", result);
    }

    public void testBuildStoredCvFileNameDefaultsToPdfWhenNoExtension() {
        String result = FileUploadUtil.buildStoredCvFileName("12345", "resume");
        assertEquals("cv_12345.pdf", result);
    }

    public void testBuildStoredCvFileNameSanitizesStudentId() {
        // safeToken 将非字母数字的字符替换为 _
        String result = FileUploadUtil.buildStoredCvFileName("231 abc/def", "resume.pdf");
        assertEquals("cv_231_abc_def.pdf", result);
    }

    public void testBuildStoredCvFileNameWithSpecialCharsInStudentId() {
        String result = FileUploadUtil.buildStoredCvFileName("test@student!", "cv.pdf");
        assertEquals("cv_test_student_.pdf", result);
    }

    // ---- buildStoredAvatarFileName() ----

    public void testBuildStoredAvatarFileNameCorrectPattern() {
        String result = FileUploadUtil.buildStoredAvatarFileName("user123", "avatar.png");
        assertEquals("avatar_user123.png", result);
    }

    public void testBuildStoredAvatarFileNameWithJpgExtension() {
        String result = FileUploadUtil.buildStoredAvatarFileName("admin", "photo.jpg");
        assertEquals("avatar_admin.jpg", result);
    }

    public void testBuildStoredAvatarFileNameDefaultsToPngWhenNoExtension() {
        String result = FileUploadUtil.buildStoredAvatarFileName("user", "photo");
        assertEquals("avatar_user.png", result);
    }

    public void testBuildStoredAvatarFileNameSanitizesUserId() {
        String result = FileUploadUtil.buildStoredAvatarFileName("user name@site", "avatar.png");
        assertEquals("avatar_user_name_site.png", result);
    }

    // ---- extractFileNameFromPath() ----

    public void testExtractFileNameFromFullWindowsPath() {
        String result = FileUploadUtil.extractFileNameFromPath("C:\\uploads\\cv\\resume.pdf");
        assertEquals("resume.pdf", result);
    }

    public void testExtractFileNameFromFullUnixPath() {
        String result = FileUploadUtil.extractFileNameFromPath("/var/uploads/resume.pdf");
        assertEquals("resume.pdf", result);
    }

    public void testExtractFileNameReturnsSameIfNoPathSeparators() {
        String result = FileUploadUtil.extractFileNameFromPath("resume.pdf");
        assertEquals("resume.pdf", result);
    }

    public void testExtractFileNameFromPathReturnsNullForNull() {
        assertNull(FileUploadUtil.extractFileNameFromPath(null));
    }

    public void testExtractFileNameFromPathReturnsNullForEmptyString() {
        assertNull(FileUploadUtil.extractFileNameFromPath(""));
    }

    public void testExtractFileNameFromPathReturnsNullForBlankString() {
        assertNull(FileUploadUtil.extractFileNameFromPath("   "));
    }

    public void testExtractFileNameFromPathWithMixedSeparators() {
        // 提取路径中最后一个分隔符之后的文件名
        String result = FileUploadUtil.extractFileNameFromPath("uploads/data/resume.pdf");
        assertEquals("resume.pdf", result);
    }

    public static void main(String[] args) {
        new FileUploadUtilTest().runTestsAndExit();
    }
}
