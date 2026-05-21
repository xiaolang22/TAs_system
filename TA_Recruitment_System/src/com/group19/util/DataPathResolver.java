package com.group19.util;

import jakarta.servlet.ServletContext;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class DataPathResolver {
    private DataPathResolver() {
    }

    public static Path resolve(ServletContext context, String paramName, String defaultWebPath, String fallbackFileName) {
        String configured = context.getInitParameter(paramName);
        String webRelativePath = configured == null || configured.isBlank() ? defaultWebPath : configured;
        String realPath = context.getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        String appRoot = context.getRealPath("/");
        if (appRoot != null && !appRoot.isBlank()) {
            return Paths.get(appRoot, "data", fallbackFileName);
        }
        return Paths.get(System.getProperty("user.dir"), "data", fallbackFileName);
    }
}
