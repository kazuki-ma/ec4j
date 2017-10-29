/**
 * Copyright (c) 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.ec4j.core;

import java.io.IOException;

import org.eclipse.ec4j.core.Resources.Resource;
import org.eclipse.ec4j.core.model.EditorConfig;
import org.eclipse.ec4j.core.model.Version;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.core.parser.EditorConfigModelHandler;
import org.eclipse.ec4j.core.parser.EditorConfigParser;

/**
 * Implements the capability of loading an {@link EditorConfig} object out of a {@link Resource}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class EditorConfigLoader {

    private static final EditorConfigLoader DEFAULT = new EditorConfigLoader(Version.CURRENT,
            OptionTypeRegistry.getDefault());

    public static EditorConfigLoader getDefault() {
        return DEFAULT;
    }

    public static EditorConfigLoader of(Version version) throws VersionException {
        return of(version, OptionTypeRegistry.getDefault());
    }

    public static EditorConfigLoader of(Version version, OptionTypeRegistry registry) throws VersionException {
        if (version.compareTo(Version.CURRENT) > 0) {
            throw new VersionException("Required version is greater than the current version.");
        }
        return new EditorConfigLoader(version, registry);
    }

    private final OptionTypeRegistry registry;
    private final Version version;

    EditorConfigLoader(Version version, OptionTypeRegistry registry) {
        super();
        this.version = version;
        this.registry = registry;
    }

    /**
     * @return the {@link OptionTypeRegistry} associated with this {@link EditorConfigLoader}
     */
    public OptionTypeRegistry getRegistry() {
        return registry;
    }

    /**
     * @return the version of the EditorConfig spec the current {@link EditorConfigLoader} is able to read
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Loads an {@link EditorConfig} object out of the given {@code configFile}.
     *
     * @param configFile
     *            the {@link Resource} to read the EditorConfig model from
     * @return a new {@link EditorConfig} instance
     * @throws EditorConfigException
     *             if anything goes wrong, incl IO problems that get wrapped as {@link EditorConfigException}s
     */
    public EditorConfig load(Resource configFile) throws EditorConfigException {
        try {
            EditorConfigModelHandler handler = new EditorConfigModelHandler(registry, version);
            EditorConfigParser parser = EditorConfigParser.builder().build();
            parser.parse(configFile, handler);
            EditorConfig result = handler.getEditorConfig();
            return result;
        } catch (IOException e) {
            throw new EditorConfigException("Could not load " + configFile.getPath(), e);
        }
    }
}