plugins {
    alias(libs.plugins.media.android.library)
}

android {
    namespace = "com.loong.android.media.player"
}

dependencies {

    api(libs.androidx.media3.seesion)
    api(libs.androidx.media3.exoplayer)
    api(libs.androidx.media3.common)
    implementation(libs.androidx.core.ktx)
    implementation(projects.common)
    implementation(libs.androidx.lifecycle.livedata.core.ktx)
}