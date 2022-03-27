plugins {
    `java-library`
    `maven-publish`
}

group = "net.gigaclub"
version = "1.18.2.1.0.0"

val myArtifactId: String = rootProject.name
val myArtifactGroup: String = project.group.toString()
val myArtifactVersion: String = project.version.toString()

val myGithubUsername = "GigaClub"
val myGithubHttpUrl = "https://github.com/${myGithubUsername}/${myArtifactId}"
val myGithubIssueTrackerUrl = "https://github.com/${myGithubUsername}/${myArtifactId}/issues"
val myLicense = "MIT"
val myLicenseUrl = "https://opensource.org/licenses/MIT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/gigaclub/baseapi")
        metadataSources {
            mavenPom()
            artifact()
        }
        credentials {
            username = System.getenv("GITHUB_PACKAGES_USERID")
            password = System.getenv("GITHUB_PACKAGES_IMPORT_TOKEN")
        }
    }
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "extendedclip"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.jetbrains:annotations:23.0.0")
    annotationProcessor("org.jetbrains:annotations:23.0.0")
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
    from("LICENCE.md") {
        into("META-INF")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${myGithubUsername}/${myArtifactId}")
            credentials {
                username = System.getenv("GITHUB_PACKAGES_USERID")
                password = System.getenv("GITHUB_PACKAGES_IMPORT_TOKEN")
            }
        }
    }
}

publishing {
    publications {
        register("gprRelease", MavenPublication::class) {
            groupId = myArtifactGroup
            artifactId = myArtifactId
            version = myArtifactVersion

            from(components["java"])

            artifact(sourcesJar)

            pom {
                packaging = "jar"
                name.set(myArtifactId)
                url.set(myGithubHttpUrl)
                scm {
                    url.set(myGithubHttpUrl)
                }
                issueManagement {
                    url.set(myGithubIssueTrackerUrl)
                }
                licenses {
                    license {
                        name.set(myLicense)
                        url.set(myLicenseUrl)
                    }
                }
                developers {
                    developer {
                        id.set(myGithubUsername)
                    }
                }
            }
        }
    }
}