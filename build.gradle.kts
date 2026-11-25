plugins {
    java
    application
}

group = "ro.ubb.map"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.7")
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))
        }
        resources {
            setSrcDirs(listOf("src"))
            include("*.properties")
        }
    }
}

application {
    mainClass.set("Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}