/**
 * hub-imageinspector-ws
 *
 * Copyright (c) 2019 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.blackduck.imageinspectorws.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Level;

@RestController
public class ImageInspectorController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String BASE_LOGGER_NAME = "com.synopsys";

    // Endpoints
    private static final String GET_BDIO_PATH = "/getbdio";
    private static final String GET_SERVICE_VERSION = "/getversion";
    // Mandatory query param
    static final String TARFILE_PATH_QUERY_PARAM = "tarfile";
    // Optional query params
    static final String BLACKDUCK_PROJECT_NAME_QUERY_PARAM = "blackduckprojectname";
    static final String BLACKDUCK_PROJECT_VERSION_QUERY_PARAM = "blackduckprojectversion";
    static final String CODELOCATION_PREFIX_QUERY_PARAM = "codelocationprefix";
    static final String ORGANIZE_COMPONENTS_BY_LAYER_QUERY_PARAM = "organizecomponentsbylayer";
    static final String INCLUDE_REMOVED_COMPONENTS_QUERY_PARAM = "includeremovedcomponents";
    static final String CLEANUP_WORKING_DIR_QUERY_PARAM = "cleanup";
    static final String CONTAINER_FILESYSTEM_PATH_PARAM = "resultingcontainerfspath";
    static final String CONTAINER_FILESYSTEM_EXCLUDED_PATHS_PARAM = "resultingcontainerfsexcludedpaths";
    static final String LOGGING_LEVEL_PARAM = "logginglevel";
    static final String IMAGE_REPO_PARAM = "imagerepo";
    static final String IMAGE_TAG_PARAM = "imagetag";
    static final String PLATFORM_TOP_LAYER_ID_PARAM = "platformtoplayerid";

    @Autowired
    private ImageInspectorHandler imageInspectorHandler;

    @RequestMapping(path = GET_BDIO_PATH, method = RequestMethod.GET)
    public ResponseEntity<String> getBdio(final HttpServletRequest request, @RequestParam(value = TARFILE_PATH_QUERY_PARAM) final String tarFilePath,
        @RequestParam(value = BLACKDUCK_PROJECT_NAME_QUERY_PARAM, defaultValue = "") final String blackDuckProjectName,
        @RequestParam(value = BLACKDUCK_PROJECT_VERSION_QUERY_PARAM, defaultValue = "") final String blackDuckProjectVersion,
        @RequestParam(value = CODELOCATION_PREFIX_QUERY_PARAM, defaultValue = "") final String codeLocationPrefix,
        @RequestParam(value = ORGANIZE_COMPONENTS_BY_LAYER_QUERY_PARAM, required = false, defaultValue = "false") final boolean organizeComponentsByLayer,
        @RequestParam(value = INCLUDE_REMOVED_COMPONENTS_QUERY_PARAM, required = false, defaultValue = "false") final boolean includeRemovedComponents,
        @RequestParam(value = CLEANUP_WORKING_DIR_QUERY_PARAM, required = false, defaultValue = "true") final boolean cleanupWorkingDir,
        @RequestParam(value = CONTAINER_FILESYSTEM_PATH_PARAM, required = false, defaultValue = "") final String containerFileSystemPath,
        @RequestParam(value = CONTAINER_FILESYSTEM_EXCLUDED_PATHS_PARAM, required = false, defaultValue = "") final String containerFileSystemExcludedPathListString,
        @RequestParam(value = LOGGING_LEVEL_PARAM, required = false, defaultValue = "INFO") final String loggingLevel,
        @RequestParam(value = IMAGE_REPO_PARAM, required = false, defaultValue = "") final String givenImageRepo,
        @RequestParam(value = IMAGE_TAG_PARAM, required = false, defaultValue = "") final String givenImageTag,
        @RequestParam(value = PLATFORM_TOP_LAYER_ID_PARAM, required = false, defaultValue = "") final String platformTopLayerId) {
        logger.info(String.format("Endpoint %s called; tarFilePath: %s; containerFileSystemPath=%s, loggingLevel=%s, platformTopLayerId=%s", GET_BDIO_PATH, tarFilePath, containerFileSystemPath, loggingLevel, platformTopLayerId));
        setLoggingLevel(loggingLevel);
        return imageInspectorHandler.getBdio(request.getScheme(), request.getServerName(), request.getServerPort(), request.getRequestURI(), tarFilePath, blackDuckProjectName, blackDuckProjectVersion, codeLocationPrefix, givenImageRepo,
            givenImageTag,
            organizeComponentsByLayer, includeRemovedComponents, cleanupWorkingDir,
            containerFileSystemPath, containerFileSystemExcludedPathListString,
            loggingLevel, platformTopLayerId);
    }

    @RequestMapping(path = GET_SERVICE_VERSION, method = RequestMethod.GET)
    public ResponseEntity<String> getServiceVersion(final HttpServletRequest request) {
        logger.info(String.format("Endpoint %s called", GET_SERVICE_VERSION));
        return imageInspectorHandler.getServiceVersion();
    }

    private void setLoggingLevel(final String newLoggingLevel) {
        logger.info(String.format("Setting logging level to %s", newLoggingLevel));
        try {
            final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(BASE_LOGGER_NAME);
            root.setLevel(Level.toLevel(newLoggingLevel));
            if (logger.isDebugEnabled()) {
                logger.info("DEBUG logging is enabled");
            } else {
                logger.info("DEBUG logging is not enabled");
            }
        } catch (final Exception e) {
            logger.error(String.format("Error setting logging level to %s: %s", newLoggingLevel, e.getMessage()));
        }
    }
}
