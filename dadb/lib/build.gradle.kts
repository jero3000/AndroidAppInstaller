plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

kotlin{
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    } // Match Kotlin to Java 17
}

sourceSets {
    main {
        kotlin {
            srcDirs("dadb/dadb/src/main/kotlin")
        }
    }
}

dependencies {
    api("com.squareup.okio:okio:2.10.0")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

