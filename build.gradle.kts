plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
    id("com.jfrog.artifactory")
//    id("com.diffplug.spotless")
}

version = "0.0.2"
group = "com.keygenqt.response"

//spotless {
//    kotlin {
//        target("**/*.kt")
//        licenseHeaderFile("$buildDir/../LICENSE")
//    }
//}

publishing {
    publications {
        register("aar", MavenPublication::class) {
            groupId = group.toString()
            artifactId = project.name
            artifact("$buildDir/outputs/aar/android-response-result-$version-debug.aar")
        }
    }
}

artifactory {
    setContextUrl("https://artifactory.keygenqt.com/artifactory")
    publish {
        repository {
            setRepoKey("open-source")
            setUsername(findProperty("arusername").toString())
            setPassword(findProperty("arpassword").toString())
        }
        defaults {
            publications("aar")
            setPublishArtifacts(true)
        }
    }
}

android {

    compileSdk = 30

    defaultConfig {
        minSdk = 23
        targetSdk = 31
        setProperty("archivesBaseName", "android-response-result-$version")
    }
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("androidx.paging:paging-compose:1.0.0-alpha12")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
}