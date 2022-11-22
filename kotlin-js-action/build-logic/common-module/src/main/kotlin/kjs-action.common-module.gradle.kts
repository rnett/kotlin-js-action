import org.gradle.api.tasks.testing.AbstractTestTask

plugins {
    id("kjs-action.docs-leaf")
    id("kjs-action.publishing")
}

tasks.withType(AbstractTestTask::class.java).configureEach {
    testLogging {
        showExceptions = true   // It is true by default. Set it just for explicitness.
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

repositories {
    mavenCentral()
}