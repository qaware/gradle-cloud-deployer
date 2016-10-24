<p align="acenter">
    <img align="center" src="https://github.com/qaware/gradle-cloud-deployer/blob/master/wiki/logo.png?raw=true" alt="gradle-cloud-deployer" href="https://github.com/qaware/gradle-cloud-deployer"/>
</p>

#Gradle-Cloud-Deployer
[![Build Status](https://travis-ci.org/qaware/gradle-cloud-deployer.svg?branch=master)](https://travis-ci.org/qaware/gradle-cloud-deployer)
[![codebeat badge](https://codebeat.co/badges/660364b2-bd46-4c5a-9f14-920ac85ecec1)](https://codebeat.co/projects/github-com-qaware-gradle-cloud-deployer)
[![codecov](https://codecov.io/gh/qaware/gradle-cloud-deployer/branch/master/graph/badge.svg)](https://codecov.io/gh/qaware/gradle-cloud-deployer)
[![Dependency Status](https://dependencyci.com/github/qaware/gradle-cloud-deployer/badge)](https://dependencyci.com/github/qaware/gradle-cloud-deployer)
[![Dependency Status](https://www.versioneye.com/user/projects/5809b705912815003afa4729/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5809b705912815003afa4729)
[![Apache License 2](http://img.shields.io/badge/license-ASF2-blue.svg)](https://github.com/qaware/gradle-cloud-deployer/blob/master/LICENSE)

The Gradle-Cloud-Deployer is a gradle plugin which deploys your applications directly into your cloud.

## Supported cloud orchestrators
The supported cloud orchestrators and concepts are ...

[![Marathon](https://github.com/qaware/gradle-cloud-deployer/blob/master/wiki/marathon.png?raw=true)](https://mesosphere.github.io/marathon/)
#### Supported Concepts
- App
- Group

[![Kubernetes](https://github.com/qaware/gradle-cloud-deployer/blob/master/wiki/kubernetes.png?raw=true)](http://kubernetes.io)
#### Supported Concepts
- Deployment
- Service
- ReplicationController
- Pod


## Quick start
This quick start demonstrates the usage of the Gradle-Cloud-Deployer plugin.

### 1. Apply the plugin
Build script snippet for use in **all Gradle versions**:
```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.de.qaware.cloud.deployer:deployer:0.9.1"
  }
}

apply plugin: "de.qaware.cloud.deployer"
```

Build script snippet for new, incubating, plugin mechanism introduced in **Gradle 2.1**:
```groovy
plugins {
  id "de.qaware.cloud.deployer" version "0.9.1"
}
```

To control if you applied the plugin correctly type:
 ```
 gradlew tasks
 ```

 You should see the `Deployment tasks` section in your task list:
 ```bash
 Deployment tasks
 ----------------
 delete - Deletes the specified environment (e.g. --environmentId=test).
 deleteAll - Deletes all environments.
 deploy - Deploys the specified environment (e.g. --environmentId=test).
 deployAll - Deploys all environments.
 ```

### 2. Configure the plugin
This build script snippet demonstrates how to configure the plugin to deploy to a Marathon and a Kubernetes cloud:

```groovy
deployer {
    marathon {
        id = "marathon-zwitscher"
        baseUrl = "http://your-address.com"
        strategy = "REPLACE"
        auth {
            token = defaultToken(file("token.txt"))
        }
        files = [file("marathon-zwitscher-config.json")]
    }
    kubernetes {
        id = "kubernetes-zwitscher"
        baseUrl = "https://your-address.com"
        strategy = "UPDATE"
        auth {
            username = "admin"
            password = "s3cr3t"
        }
        ssl {
            trustAll = true
        }
        files = [file("kubernetes-zwitscher-config.json")]
    }
}
```

You can define multiple environments. The example creates an `marathon-zwitscher` and `kubernetes-zwitscher` environment.
Thereby it's possible to mix different cloud orchestrators. Additionally the plugin allows authentication (e.g. via token
or username and password) and ssl connections (e.g. via a self-signed certificate).

Examples for a
[kubernetes config file](https://github.com/qaware/cloud-native-zwitscher/blob/master/zwitscher-config/k8s-zwitscher-config.yml)
and a
[marathon config file](https://github.com/qaware/cloud-native-zwitscher/blob/master/zwitscher-config/marathon-zwitscher-config.json)
can be found in QAware's
[Cloud Native Zwitscher Showcase](https://github.com/qaware/cloud-native-zwitscher).

See the [documentation](#documentation) for more details on configuration.

### 3. Execute a task
After configuring the plugin you can use the different tasks to

- deploy all environments
```bash
gradlew deployAll
```

- deploy a single environment
```bash
gradlew deploy --environmentId=test
```

- delete all environments
```bash
gradlew deleteAll
```

- delete a single environment
```bash
gradlew delete --environmentId=test
```

See the [documentation](#documentation) for more details on the tasks.

## Documentation
The [wiki](https://github.com/qaware/gradle-cloud-deployer/wiki) contains the documentation for plugin configuration and
usage.

## Development
For details on building and developing the Gradle-Cloud-Deployer plugin, please see the
[wiki](https://github.com/qaware/gradle-cloud-deployer/wiki).

## Contributing
Is there anything missing? Do you have ideas for new features or improvements?

All you have to do is to fork this repository, improve the code and issue a pull request.

## Maintainer
Simon Jahreiß (@sjahreis)

## Sponsor
[![QAware GmbH](https://github.com/qaware/gradle-cloud-deployer/blob/master/wiki/qaware.png?raw=true)](http://www.qaware.de)

## License
This software is provided under the Apache License, Version 2.0 license.
See the [`LICENSE`](https://github.com/qaware/gradle-cloud-deployer/blob/master/LICENSE) file for details.
