plugins{
    kotlin("js") version "1.5.10" apply false
    kotlin("jvm") version "1.5.10" apply false
    id("com.vanniktech.maven.publish") version "0.15.1" apply false
    id("org.jetbrains.dokka") version "1.4.32" apply false
}

allprojects{

    group = "com.github.rnett.ktjs-github-action"
    version = "1.2.0-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
    }
}