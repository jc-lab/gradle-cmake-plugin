package kr.jclab.gradle.cmakeplugin.plugins.cmake;

import kr.jclab.gradle.cmakeplugin.tasks.CMake;
import kr.jclab.gradle.cmakeplugin.tasks.CMakeBuild;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * A sample plugin that wraps a CMake build with Gradle to take care of dependency management.
 */
public class CMakeApplicationPlugin implements Plugin<Project> {
    public void apply(final Project project) {
        project.getPluginManager().apply("kr.jclab.gradle.cmakeplugin.wrapped-native-application");

        /*
         * Create some tasks to drive the CMake build
         */
        TaskContainer tasks = project.getTasks();

        TaskProvider<CMake> cmakeDebug = tasks.register("cmakeDebug", CMake.class, task -> {
            task.setBuildType("Debug");
            task.getIncludeDirs().from(project.getConfigurations().getByName("cppCompile"));
            task.getLinkFiles().from(project.getConfigurations().getByName("linkDebug"));
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("debug"));
            task.getProjectDirectory().set(project.getLayout().getProjectDirectory());
        });

        TaskProvider<CMake> cmakeRelease = tasks.register("cmakeRelease", CMake.class, task -> {
            task.setBuildType("RelWithDebInfo");
            task.getIncludeDirs().from(project.getConfigurations().getByName("cppCompile"));
            task.getLinkFiles().from(project.getConfigurations().getByName("linkRelease"));
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("release"));
            task.getProjectDirectory().set(project.getLayout().getProjectDirectory());
        });

        TaskProvider<CMakeBuild> assembleDebug = tasks.register("assembleDebug", CMakeBuild.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the debug binaries");
            task.generatedBy(cmakeDebug);
            task.binary(project.provider(() -> project.getName()));
        });

        TaskProvider<CMakeBuild> assembleRelease = tasks.register("assembleRelease", CMakeBuild.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the release binaries");
            task.generatedBy(cmakeRelease);
            task.binary(project.provider(() -> project.getName()));
        });

        tasks.named("assemble", task -> task.dependsOn(assembleDebug));
    }
}
