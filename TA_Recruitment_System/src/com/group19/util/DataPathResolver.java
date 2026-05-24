package com.group19.util;

import jakarta.servlet.ServletContext;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Data file path resolution utility.
 * Resolves absolute paths for data files according to priority, using Servlet
 * context parameters:
 * 1) initialisation parameter path configured in web.xml;
 * 2) default web-relative path;
 * 3) the data directory under the application root;
 * 4) the data directory under the current working directory.
 *
 * @author Group19
 * @since 1.0
 */
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
