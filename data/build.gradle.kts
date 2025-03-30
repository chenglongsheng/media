plugins {
    alias(libs.plugins.media.android.library)
}

android {
    namespace = "com.loong.android.media.data"
}

dependencies {

    implementation(projects.common)
    implementation(libs.kotlinx.coroutines)
}