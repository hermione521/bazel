load("//tools/build_rules:genproto.bzl", "java_proto_library")

java_proto_library(
    name = "build_data_proto",
    src = "build_data.proto",
)

java_binary(
    name = "benchmark",
    srcs = glob(["*.java"]),
    deps = [
        "//src/java_tools/benchmark/java/com/google/devtools/build/benchmark/codegenerator:codegenerator_lib",
        ":build_data_proto",
        "//src/main/java/com/google/devtools/build/lib:shell",
        "//src/main/java/com/google/devtools/common/options",
        "//third_party:guava",
    ],
    main_class = "com.google.devtools.build.benchmark.Main",
)