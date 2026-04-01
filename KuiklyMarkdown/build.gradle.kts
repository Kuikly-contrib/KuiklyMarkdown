plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "2.0.21"
    id("com.android.library")
    id("maven-publish")
}

group = findProperty("groupId")?.toString() ?: "com.tencent.kuiklybase"
version = findProperty("mavenVersion")?.toString() ?: System.getenv("kuiklyBizVersion") ?: "1.0.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release")
    }

    js(IR) {
        browser()
    }

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            val coreVersion = findProperty("kuiklyCoreVersion")?.toString() ?: Version.getKuiklyVersion()
            dependencies {
                api("com.tencent.kuikly-open:core:${coreVersion}")
                api("com.tencent.kuikly-open:core-annotations:${coreVersion}")
                // coroutine 依赖
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core"){
                    version{ strictly("1.8.0-KBA-002") }
                }
                // Serialization 依赖
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1-KBA-003")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val nativeMain by creating {
            dependsOn(commonMain)
            iosMain.dependsOn(this)
        }
    }
}

android {
    namespace = "com.tencent.kuiklybase.markdown"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}

publishing {
    repositories {
        maven {
            val repoUrl = findProperty("mavenRepoUrl")?.toString()
                ?: findProperty("MAVEN_REPO_URL")?.toString()
            if (repoUrl != null) {
                url = uri(repoUrl)
            }
            credentials {
                username = findProperty("mavenUsername")?.toString()
                    ?: System.getenv("mavenUserName") ?: ""
                password = findProperty("mavenPassword")?.toString()
                    ?: System.getenv("mavenPassword") ?: ""
            }
        }
    }
}