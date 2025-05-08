plugins {
    alias(libs.plugins.media.android.library)
}

android {
    namespace = "com.loong.android.media.local"
}

dependencies {

    implementation(projects.common)
    implementation(projects.player)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core.ktx)
}