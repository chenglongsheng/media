plugins {
    alias(libs.plugins.media.android.library)
    alias(libs.plugins.ksp)
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
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}