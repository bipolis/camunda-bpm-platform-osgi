/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.camunda.bpm.extension.osgi.engine;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.classloading.BundleDelegatingClassLoader;
import org.camunda.bpm.extension.osgi.classloading.ClassLoaderWrapper;
import org.osgi.framework.Bundle;

/**
 * @author Guillaume Nodet
 */
public class ProcessEngineFactory {

    protected ProcessEngineConfiguration processEngineConfiguration;
    protected Bundle bundle;

    protected ProcessEngine processEngine;

    public void init() {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();

        try {
            ClassLoader cl = new BundleDelegatingClassLoader(bundle);

            Thread.currentThread().setContextClassLoader(new ClassLoaderWrapper(
                    cl,
                    ProcessEngineFactory.class.getClassLoader(),
                    ProcessEngineConfiguration.class.getClassLoader(),
                    previous
            ));

            processEngineConfiguration.setClassLoader(cl);

            processEngine = processEngineConfiguration.buildProcessEngine();

        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    public void destroy() {
        if (processEngine != null) {
            processEngine.close();
        }
    }

    public ProcessEngine getObject() {
        return processEngine;
    }

    public ProcessEngineConfiguration getProcessEngineConfiguration() {
        return processEngineConfiguration;
    }

    public void setProcessEngineConfiguration(ProcessEngineConfiguration processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
    }


    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
