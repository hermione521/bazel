package(default_visibility = ["//src/java_tools/benchmark:__subpackages__"])

java_binary(
    name = "codegenerator",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/com/google/devtools/common/options",
        "//third_party/java/javapoet"
    ],
    main_class = "com.google.devtools.build.benchmark.codegenerator.Main",
)

java_library(
    name = "codegenerator_testlib",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/com/google/devtools/common/options",
        "//third_party/java/javapoet"
    ],
)
