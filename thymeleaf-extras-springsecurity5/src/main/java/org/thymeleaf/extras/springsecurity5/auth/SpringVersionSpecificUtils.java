/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.extras.springsecurity5.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.extras.springsecurity5.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.1
 *
 */
final class SpringVersionSpecificUtils {


    private static final Logger LOG = LoggerFactory.getLogger(SpringVersionSpecificUtils.class);

    private static final String PACKAGE_NAME = SpringVersionSpecificUtils.class.getPackage().getName();
    // Spring Security 5 requires at least Spring 5
    private static final String SPRING5_DELEGATE_CLASS = PACKAGE_NAME + ".Spring5VersionSpecificUtility";


    private static final ISpringVersionSpecificUtility spring5Delegate;



    static {

        if (SpringVersionUtils.isSpring50AtLeast()) {

            LOG.trace("[THYMELEAF][TESTING] Spring 5.0+ found on classpath. Initializing auth utility for Spring 5");

            try {
                final Class<?> implClass = ClassLoaderUtils.loadClass(SPRING5_DELEGATE_CLASS);
                spring5Delegate = (ISpringVersionSpecificUtility) implClass.newInstance();
            } catch (final Exception e) {
                throw new ExceptionInInitializerError(
                        new ConfigurationException(
                            "Environment has been detected to be at least Spring 5, but thymeleaf could not initialize a " +
                            "delegate of class \"" + SPRING5_DELEGATE_CLASS + "\"", e));
            }

        } else {

            throw new ExceptionInInitializerError(
                    new ConfigurationException(
                        "The auth infrastructure could not create utility for the specific version of Spring being" +
                        "used. Currently only Spring 5.x is supported."));

        }

    }




    static EvaluationContext wrapEvaluationContext(
            final EvaluationContext evaluationContext, final IExpressionObjects expresionObjects) {

        if (spring5Delegate != null) {
            return spring5Delegate.wrapEvaluationContext(evaluationContext, expresionObjects);
        }

        throw new ConfigurationException(
                "The authorization infrastructure could not create initializer for the specific version of Spring being" +
                "used. Currently only Spring 5.x is supported.");

    }




    private SpringVersionSpecificUtils() {
        super();
    }


    
}