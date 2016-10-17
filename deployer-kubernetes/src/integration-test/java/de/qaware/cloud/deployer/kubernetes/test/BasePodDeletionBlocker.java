/*
 * Copyright 2016 QAware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.qaware.cloud.deployer.kubernetes.test;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;

import java.util.concurrent.TimeoutException;

/**
 * @author sjahreis
 */
public abstract class BasePodDeletionBlocker {

    // Timeout in seconds
    private static final int TIMEOUT = 300;

    private final KubernetesClient kubernetesClient;
    private final boolean[] block = {true};
    private final Watch watcher;
    private int timeoutCounter = 0;

    BasePodDeletionBlocker(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
        watcher = this.registerWatcher();
    }

    public void block() throws InterruptedException, TimeoutException {
        while (block[0]) {
            timeoutCounter++;
            if (timeoutCounter > TIMEOUT) {
                throw new TimeoutException("No pod deletion occurred in the last " + timeoutCounter + " seconds");
            }
            Thread.sleep(1000);
        }
        watcher.close();
    }

    public abstract boolean continueBlocking(Watcher.Action eventAction, Pod eventPod);

    boolean podEquals(Pod pod1, Pod pod2) {
        ObjectMeta metadata1 = pod1.getMetadata();
        ObjectMeta metadata2 = pod2.getMetadata();
        return metadata1.getName().equals(metadata2.getName()) && metadata1.getNamespace().equals(metadata2.getNamespace());
    }

    private Watch registerWatcher() {
        return kubernetesClient.pods().watch(new Watcher<Pod>() {
            @Override
            public void eventReceived(Action eventAction, Pod eventPod) {
                boolean continueBlocking = continueBlocking(eventAction, eventPod);
                if (!continueBlocking)
                    block[0] = false;
            }

            @Override
            public void onClose(KubernetesClientException e) {
            }
        });
    }
}
