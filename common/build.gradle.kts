plugins {
    alias(libs.plugins.media.android.library)
}

android {
    namespace = "com.loong.android.media.common"
}

dependencies {

    implementation(libs.androidx.startup)
    api(libs.timber)

}