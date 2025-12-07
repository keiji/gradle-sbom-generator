# License information Generator for Gradle

Create a License information from a Gradle dependencies.

This software analyzes a dependencies graph, obtains the dependency's POM (Project Object Model) files recursively, and extracts and exports each software's information.

## Usages

You can use this tool as a Gradle Plugin or a CLI tool.

### Gradle Plugin

1. Apply the plugin to your `build.gradle.kts`.

```kotlin
plugins {
    id("dev.keiji.license.maven-license-generator") version "0.0.4"
}
```

2. Configure the extension.

```kotlin
mavenLicenseGenerator {
    // Determine which configurations to verify.
    targets {
        create("main") {
            configurations = listOf("runtimeClasspath")
        }
    }

    // Temporary working directory
    workingDir = layout.buildDirectory.dir("tmp/maven-license-generator").get().asFile

    // Repositories to search for POM files
    repositoryUrls = listOf(
        "https://repo1.maven.org/maven2",
        "https://dl.google.com/android/maven2",
        "https://maven.repository.redhat.com/ga"
    )

    // Output settings
    outputSettings {
        create("complete") {
            path = layout.buildDirectory.file("licenses.json").get().asFile
            override = true
            prettyPrintEnabled = true
        }
        create("incomplete") {
            path = layout.buildDirectory.file("licenses-incomplete.json").get().asFile
            override = false
            prettyPrintEnabled = true
        }
    }

    // Other settings
    removeConflictingVersions = true
    ignoreScopes = listOf("test", "runtime")
    includeDependencies = true
    includeSettings = false
}
```

3. Run the task.

```bash
./gradlew generateMavenLicense
```

### CLI

1. Build the project and export the shadow jar.

```bash
./gradlew shadowJar
```

2. Generate dependencies file using Gradle.

```bash
./gradlew dependencies --configuration runtimeClasspath > ./dependencies.txt
```

<details>
<summary>Example: dependencies.txt</summary>

```
> Task :dependencies

------------------------------------------------------------
Root project 'gradle-dependencies'
------------------------------------------------------------

runtimeClasspath - Runtime classpath of compilation 'main' (target  (jvm)).
+--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.8.10
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-common:1.8.10
|    |    \--- org.jetbrains:annotations:13.0
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10
|         \--- org.jetbrains.kotlin:kotlin-stdlib:1.8.10 (*)
+--- org.jetbrains.kotlinx:kotlinx-cli:0.3.5
|    \--- org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.5
|         +--- org.jetbrains.kotlin:kotlin-stdlib-common:1.6.0 -> 1.8.10
|         \--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0 -> 1.8.10 (*)
+--- org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0
|    \--- org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.5.0
|         +--- org.jetbrains.kotlin:kotlin-stdlib:1.8.10 (*)
|         +--- org.jetbrains.kotlinx:kotlinx-serialization-bom:1.5.0
|         |    +--- org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0 (c)
|         |    +--- org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.5.0 (c)
|         |    +--- org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0 (c)
|         |    \--- org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.5.0 (c)
|         +--- org.jetbrains.kotlin:kotlin-stdlib-common:1.8.10
|         \--- org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0
|              \--- org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.5.0
|                   +--- org.jetbrains.kotlin:kotlin-stdlib:1.8.10 (*)
|                   +--- org.jetbrains.kotlinx:kotlinx-serialization-bom:1.5.0 (*)
|                   \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.8.10
\--- com.squareup.okhttp3:okhttp:4.10.0
     +--- com.squareup.okio:okio:3.0.0
     |    \--- com.squareup.okio:okio-jvm:3.0.0
     |         +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31 -> 1.8.10 (*)
     |         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.5.31 -> 1.8.10
     \--- org.jetbrains.kotlin:kotlin-stdlib:1.6.20 -> 1.8.10 (*)

(c) - dependency constraint
(*) - dependencies omitted (listed previously)

A web-based, searchable dependency report is available by adding the --scan option.

BUILD SUCCESSFUL in 662ms
1 actionable task: 1 executed
```

</details>

3. Create a configuration file (e.g. `settings.json`).

<details>
<summary>Example: settings.json</summary>

```json
{
  "target_file_path": "./dependencies.txt",
  "working_dir": "./tmp",
  "local_repository_dirs": [
  ],
  "repository_urls": [
    "https://repo1.maven.org/maven2",
    "https://dl.google.com/android/maven2",
    "https://maven.repository.redhat.com/ga"
  ],
  "remove_conflicting_versions": true,
  "ignore_scopes": [
    "test",
    "runtime"
  ],
  "include_dependencies": true,
  "include_settings": false,
  "output": {
    "complete": {
      "path": "./licenses.json",
      "override": true,
      "is_pretty_print_enabled": true
    },
    "incomplete": {
      "path": "./licenses-incomplete.json",
      "override": false,
      "is_pretty_print_enabled": true
    }
  }
}
```
</details>

4. Run the jar.

```bash
java -jar build/libs/gradle-license-generator-0.0.4-all.jar ./settings.json
```

## How to Publish

1. Set the environment variables.

```bash
export CENTRAL_PORTAL_USERNAME=<username>
export CENTRAL_PORTAL_PASSWORD=<password>
```

2. Run the task.

```bash
./gradlew publishAllPublicationsToCentralPortal
```

## Output Example

<details>
<summary>Example: licenses.json (PrettyPrint enabled)</summary>

```
{
    "settings": null,
    "pom_list": [
        {
            "group_id": "com.squareup.okhttp3",
            "artifact_id": "okhttp",
            "version": "4.10.0",
            "name": "okhttp",
            "url": "https://square.github.io/okhttp/",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "Square, Inc.",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "com.squareup.okio",
                    "artifact_id": "okio-jvm",
                    "version": "3.0.0",
                    "scope": "compile"
                },
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib",
                    "version": "1.6.20",
                    "scope": "compile"
                }
            ],
            "depth": 1
        },
        {
            "group_id": "com.squareup.okio",
            "artifact_id": "okio",
            "version": "3.0.0",
            "name": "okio",
            "url": "https://github.com/square/okio/",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "Square, Inc.",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-common",
                    "version": "1.5.31",
                    "scope": "runtime"
                }
            ],
            "depth": 1
        },
        {
            "group_id": "org.jetbrains.kotlin",
            "artifact_id": "kotlin-stdlib",
            "version": "1.8.10",
            "name": "Kotlin Stdlib",
            "url": "https://kotlinlang.org/",
            "licenses": [
                {
                    "name": "The Apache License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "Kotlin Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-common",
                    "version": "1.8.10",
                    "scope": "compile"
                },
                {
                    "group_id": "org.jetbrains",
                    "artifact_id": "annotations",
                    "version": "13.0",
                    "scope": "compile"
                }
            ],
            "depth": 1
        },
        {
            "group_id": "org.jetbrains.kotlinx",
            "artifact_id": "kotlinx-cli",
            "version": "0.3.5",
            "name": "kotlinx-cli",
            "url": "https://github.com/Kotlin/kotlinx-cli",
            "licenses": [
                {
                    "name": "The Apache License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "JetBrains Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
            ],
            "depth": 1
        },
        {
            "group_id": "com.squareup.okio",
            "artifact_id": "okio-jvm",
            "version": "3.0.0",
            "name": "okio",
            "url": "https://github.com/square/okio/",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "Square, Inc.",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-jdk8",
                    "version": "1.5.31",
                    "scope": "compile"
                },
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-common",
                    "version": "1.5.31",
                    "scope": "compile"
                }
            ],
            "depth": 2
        },
        {
            "group_id": "org.jetbrains.kotlin",
            "artifact_id": "kotlin-stdlib-common",
            "version": "1.8.10",
            "name": "Kotlin Stdlib Common",
            "url": "https://kotlinlang.org/",
            "licenses": [
                {
                    "name": "The Apache License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "Kotlin Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
            ],
            "depth": 2
        },
        {
            "group_id": "org.jetbrains.kotlin",
            "artifact_id": "kotlin-stdlib-jdk7",
            "version": "1.8.10",
            "name": "Kotlin Stdlib Jdk7",
            "url": "https://kotlinlang.org/",
            "licenses": [
                {
                    "name": "The Apache License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "Kotlin Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib",
                    "version": "1.8.10",
                    "scope": "compile"
                }
            ],
            "depth": 2
        },
        {
            "group_id": "org.jetbrains.kotlin",
            "artifact_id": "kotlin-stdlib-jdk8",
            "version": "1.8.10",
            "name": "Kotlin Stdlib Jdk8",
            "url": "https://kotlinlang.org/",
            "licenses": [
                {
                    "name": "The Apache License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "Kotlin Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib",
                    "version": "1.8.10",
                    "scope": "compile"
                },
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-jdk7",
                    "version": "1.8.10",
                    "scope": "compile"
                }
            ],
            "depth": 2
        },
        {
            "group_id": "org.jetbrains.kotlinx",
            "artifact_id": "kotlinx-cli-jvm",
            "version": "0.3.5",
            "name": "kotlinx-cli",
            "url": "https://github.com/Kotlin/kotlinx-cli",
            "licenses": [
                {
                    "name": "The Apache License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "JetBrains Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-common",
                    "version": "1.6.0",
                    "scope": "runtime"
                },
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-jdk8",
                    "version": "1.6.0",
                    "scope": "runtime"
                }
            ],
            "depth": 2
        },
        {
            "group_id": "org.jetbrains.kotlinx",
            "artifact_id": "kotlinx-serialization-bom",
            "version": "1.5.0",
            "name": "kotlinx-serialization-bom",
            "url": "https://github.com/Kotlin/kotlinx.serialization",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "https://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "JetBrains Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
            ],
            "depth": 2
        },
        {
            "group_id": "org.jetbrains.kotlinx",
            "artifact_id": "kotlinx-serialization-core",
            "version": "1.5.0",
            "name": "kotlinx-serialization-core",
            "url": "https://github.com/Kotlin/kotlinx.serialization",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "https://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "JetBrains Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlinx",
                    "artifact_id": "kotlinx-serialization-core-jvm",
                    "version": "1.5.0",
                    "scope": "compile"
                }
            ],
            "depth": 2
        },
        {
            "group_id": "org.jetbrains.kotlinx",
            "artifact_id": "kotlinx-serialization-core-jvm",
            "version": "1.5.0",
            "name": "kotlinx-serialization-core",
            "url": "https://github.com/Kotlin/kotlinx.serialization",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "https://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "JetBrains Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib",
                    "version": "1.8.10",
                    "scope": "compile"
                },
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-common",
                    "version": "1.8.10",
                    "scope": "compile"
                }
            ],
            "depth": 2
        },
        {
            "group_id": "org.jetbrains",
            "artifact_id": "annotations",
            "version": "13.0",
            "name": "IntelliJ IDEA Annotations",
            "url": "http://www.jetbrains.org",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "JetBrains Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
            ],
            "depth": 3
        },
        {
            "group_id": "org.jetbrains.kotlinx",
            "artifact_id": "kotlinx-serialization-json",
            "version": "1.5.0",
            "name": "kotlinx-serialization-json",
            "url": "https://github.com/Kotlin/kotlinx.serialization",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "https://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "JetBrains Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlinx",
                    "artifact_id": "kotlinx-serialization-json-jvm",
                    "version": "1.5.0",
                    "scope": "compile"
                }
            ],
            "depth": 3
        },
        {
            "group_id": "org.jetbrains.kotlinx",
            "artifact_id": "kotlinx-serialization-json-jvm",
            "version": "1.5.0",
            "name": "kotlinx-serialization-json",
            "url": "https://github.com/Kotlin/kotlinx.serialization",
            "licenses": [
                {
                    "name": "The Apache Software License, Version 2.0",
                    "url": "https://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            ],
            "developers": [
                {
                    "name": "JetBrains Team",
                    "url": null
                }
            ],
            "organization": null,
            "dependencies": [
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib",
                    "version": "1.8.10",
                    "scope": "compile"
                },
                {
                    "group_id": "org.jetbrains.kotlin",
                    "artifact_id": "kotlin-stdlib-common",
                    "version": "1.8.10",
                    "scope": "compile"
                },
                {
                    "group_id": "org.jetbrains.kotlinx",
                    "artifact_id": "kotlinx-serialization-core-jvm",
                    "version": "1.5.0",
                    "scope": "compile"
                }
            ],
            "depth": 3
        }
    ]
}
```

</details>

`*-incomplete.json` is contained entries when applicable at least one of condition below.

* The POM file could not be obtained from the specified repositories
* Developers and organization information is not exist in the POM file
* Licenses information is not declared in the POM file

we need research to fill in the missing information.

### License

```
Copyright 2023-2025 ARIYAMA Keiji

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
