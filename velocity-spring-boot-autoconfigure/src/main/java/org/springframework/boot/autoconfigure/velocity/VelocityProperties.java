/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.velocity;

import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * {@link ConfigurationProperties} for configuring Velocity.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 * @deprecated as of 1.4 following the deprecation of Velocity support in Spring Framework
 * 4.3
 */
@Deprecated
@ConfigurationProperties(prefix = "spring.velocity")
public class VelocityProperties extends AbstractTemplateViewResolverProperties {

    public static final String DEFAULT_RESOURCE_LOADER_PATH = "classpath:/templates/";

    public static final String DEFAULT_PREFIX = "";

    public static final String DEFAULT_SUFFIX = ".vm";

    /**
     * Name of the DateTool helper object to expose in the Velocity context of the view.
     */
    private String dateToolAttribute;

    /**
     * Name of the NumberTool helper object to expose in the Velocity context of the view.
     */
    private String numberToolAttribute;

    /**
     * Additional velocity properties.
     */
    private Map<String, String> properties = new HashMap<String, String>();

    /**
     * Template path.
     */
    private String resourceLoaderPath = DEFAULT_RESOURCE_LOADER_PATH;

    /**
     * Velocity Toolbox config location, for example "/WEB-INF/toolbox.xml". Automatically
     * loads a Velocity Tools toolbox definition file and expose all defined tools in the
     * specified scopes.
     */
    private String toolboxConfigLocation;

    /**
     * Prefer file system access for template loading. File system access enables hot
     * detection of template changes.
     */
    private boolean preferFileSystemAccess = true;

    public VelocityProperties() {
        super(DEFAULT_PREFIX, DEFAULT_SUFFIX);
    }

    public String getDateToolAttribute() {
        return this.dateToolAttribute;
    }

    public void setDateToolAttribute(String dateToolAttribute) {
        this.dateToolAttribute = dateToolAttribute;
    }

    public String getNumberToolAttribute() {
        return this.numberToolAttribute;
    }

    public void setNumberToolAttribute(String numberToolAttribute) {
        this.numberToolAttribute = numberToolAttribute;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getResourceLoaderPath() {
        return this.resourceLoaderPath;
    }

    public void setResourceLoaderPath(String resourceLoaderPath) {
        this.resourceLoaderPath = resourceLoaderPath;
    }

    public String getToolboxConfigLocation() {
        return this.toolboxConfigLocation;
    }

    public void setToolboxConfigLocation(String toolboxConfigLocation) {
        this.toolboxConfigLocation = toolboxConfigLocation;
    }

    public boolean isPreferFileSystemAccess() {
        return this.preferFileSystemAccess;
    }

    public void setPreferFileSystemAccess(boolean preferFileSystemAccess) {
        this.preferFileSystemAccess = preferFileSystemAccess;
    }

    public void applyToViewResolver(Object viewResolver) {
        invokeSuperApplyToViewResolverMethod(viewResolver);
        VelocityViewResolver resolver = (VelocityViewResolver) viewResolver;
        resolver.setToolboxConfigLocation(getToolboxConfigLocation());
        resolver.setDateToolAttribute(getDateToolAttribute());
        resolver.setNumberToolAttribute(getNumberToolAttribute());
    }

    protected void invokeSuperApplyToViewResolverMethod(Object viewResolver) {
        Method method = findMethod(AbstractTemplateViewResolverProperties.class, "applyToMvcViewResolver", Object.class);

        if (method != null) { // Since Spring Framework 5
            try {
                method.invoke(this, viewResolver);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            doApplyToViewResolver(viewResolver); // Compatible with Spring Framework < 5
        }

    }

    /**
     * Apply the given properties to a {@link AbstractTemplateViewResolver}. Use Object in
     * signature to avoid runtime dependency on MVC, which means that the template engine
     * can be used in a non-web application.
     *
     * @param viewResolver the resolver to apply the properties to.
     */
    protected void doApplyToViewResolver(Object viewResolver) {
        Assert.isInstanceOf(AbstractTemplateViewResolver.class, viewResolver,
                "ViewResolver is not an instance of AbstractTemplateViewResolver :"
                        + viewResolver);
        AbstractTemplateViewResolver resolver = (AbstractTemplateViewResolver) viewResolver;
        resolver.setPrefix(getPrefix());
        resolver.setSuffix(getSuffix());
        resolver.setCache(isCache());
        if (getContentType() != null) {
            resolver.setContentType(getContentType().toString());
        }
        resolver.setViewNames(getViewNames());
        resolver.setExposeRequestAttributes(isExposeRequestAttributes());
        resolver.setAllowRequestOverride(isAllowRequestOverride());
        resolver.setAllowSessionOverride(isAllowSessionOverride());
        resolver.setExposeSessionAttributes(isExposeSessionAttributes());
        resolver.setExposeSpringMacroHelpers(isExposeSpringMacroHelpers());
        resolver.setRequestContextAttribute(getRequestContextAttribute());
        // The resolver usually acts as a fallback resolver (e.g. like a
        // InternalResourceViewResolver) so it needs to have low precedence
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5);
    }

}
