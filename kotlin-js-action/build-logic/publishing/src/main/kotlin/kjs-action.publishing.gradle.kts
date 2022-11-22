import com.rnett.action.MetadataExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)

    signAllPublications()

    pom {
        name.set(project.providers.provider { project.extensions.getByType<MetadataExtension>() }.flatMap { it.title })
        description.set(project.providers.provider { project.description })
        inceptionYear.set("2021")
        url.set("https://github.com/rnett/kotlin-js-action/")

        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        scm {
            url.set("https://github.com/rnett/kotlin-js-action.git")
            connection.set("scm:git:git://github.com/rnett/kotlin-js-action.git")
            developerConnection.set("scm:git:ssh://git@github.com/rnett/kotlin-js-action.git")
        }

        developers {
            developer {
                id.set("rnett")
                name.set("Ryan Nett")
                url.set("https://github.com/rnett/")
            }
        }
    }
}