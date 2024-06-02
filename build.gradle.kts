// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    // 因为本项目使用的kotlin编译器版本为1.9.10，因此需配置匹配的ksp版本
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}