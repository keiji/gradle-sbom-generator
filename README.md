SBOM(Software Bill of Materials) for Gradle *not plugin
----

Create an SBOM (Software Bill of Materials) from a Gradle dependencies file.

This software analyzes a dependencies file created by Gradle, obtains the dependency's POM (Project Object Model) files recursively, and extracts and exports each software's information.

```
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
```

### Usage

1. Save dependency information to a file using Gradle.

```
./gradlew dependencies --configuration runtimeClasspath > ./dependencies.txt
```

<details>
<summary>dependencies.txt</summary>

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

2. Build and export jar(shadow-jar).

```
./gradlew shadowJar
```

3. Run `build/libs/gradle-dependencies-0.0.2-all.jar`

```
java -jar build/libs/gradle-dependencies-0.0.2-all.jar ./settings.json
```

4. SBOM `sbom.json` and `sbom-incomplete.json` will be created.

<details>
<summary>sbom.json</summary>

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

* Empty developer and organization
* Empty license

### License

```
Copyright 2023 ARIYAMA Keiji

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
