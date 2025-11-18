plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "etec.com.tcc.palmslibras"
    compileSdk = 35

    defaultConfig {
        applicationId = "etec.com.tcc.palmslibras"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Dependências principais da UI
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0") // Use parênteses
    implementation("androidx.recyclerview:recyclerview:1.3.2") // Use parênteses

    // JBCrypt - Para hashing de senhas
    implementation("org.mindrot:jbcrypt:0.4") // Use parênteses

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-core:1.0.0")
    implementation(libs.datastore.preferences.rxjava3)

    // Dependências de Teste
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}