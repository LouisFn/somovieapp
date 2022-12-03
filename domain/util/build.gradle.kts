plugins {
    id(Plugins.kotlin)
    kotlin(Plugins.kapt)
}

dependencies {
    test()

    implementation(project(":common"))
    implementation(project(":domain:model"))
    implementation(project(":domain:repository"))

    implementation(Libraries.Hilt.core)
    implementation(Libraries.Paging.common)
    implementation(Libraries.Coroutines.android)

    testImplementation(project(":test:testfixtures:kotlin"))
}
