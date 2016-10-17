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

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;

import java.util.List;

public class MultiPodDeletionBlocker extends BasePodDeletionBlocker {

    private final List<Pod> pods;

    public MultiPodDeletionBlocker(KubernetesClient kubernetesClient, List<Pod> pods) {
        super(kubernetesClient);
        this.pods = pods;
    }

    @Override
    public boolean continueBlocking(Watcher.Action eventAction, Pod eventPod) {
        return !(pods.stream().anyMatch(pod -> podEquals(pod, eventPod) && eventAction.name().equals("DELETED")));
    }
}
