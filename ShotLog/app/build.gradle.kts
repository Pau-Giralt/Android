plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
}

android {
    namespace = "com.example.shotlog"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.shotlog"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

// Documentació del codi amb Dokka (l'eina de JetBrains per a Kotlin, equivalent a Javadoc).
// Es genera amb:  gradlew :app:dokkaGenerate
//   - docs/html     -> documentació en format web de Dokka
//   - docs/javadoc  -> la mateixa documentació amb l'aspecte de Javadoc
dokka {
    moduleName.set("ShotLog")
    dokkaPublications.named("html") {
        outputDirectory.set(rootProject.layout.projectDirectory.dir("docs/html"))
    }
    dokkaPublications.named("javadoc") {
        outputDirectory.set(rootProject.layout.projectDirectory.dir("docs/javadoc"))
    }
    dokkaSourceSets.configureEach {
        includes.from("Module.md")
    }
}
