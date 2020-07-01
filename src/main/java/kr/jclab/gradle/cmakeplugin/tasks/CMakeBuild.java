package kr.jclab.gradle.cmakeplugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;

public class CMakeBuild extends DefaultTask {
    private final DirectoryProperty variantDirectory = getProject().getObjects().directoryProperty();
    private final ConfigurableFileCollection makeFiles = getProject().files();
    private final DirectoryProperty outputDirectory = getProject().getObjects().directoryProperty();
    private final RegularFileProperty binary = getProject().getObjects().fileProperty();
    private final ListProperty<String> arguments = getProject().getObjects().listProperty(String.class).empty();

    @TaskAction
    public void executeMake() {
        String cmakeExecutable = System.getenv().getOrDefault("CMAKE_EXECUTABLE", "cmake");

        getProject().exec(execSpec -> {
            execSpec.setWorkingDir(getVariantDirectory());
            execSpec.commandLine(
                    cmakeExecutable,
                    "--build",
                    ".");
        });
    }

    public void generatedBy(final TaskProvider<? extends Task> task) {
        variantDirectory.set(task.flatMap(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).getVariantDirectory();
            } else if (it instanceof ConfigureTask) {
                return ((ConfigureTask) it).getMakeDirectory();
            } else {
                throw new IllegalArgumentException("Make task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));
        outputDirectory.set(task.flatMap(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).getVariantDirectory();
            } else if (it instanceof ConfigureTask) {
                return ((ConfigureTask) it).getPrefixDirectory();
            } else {
                throw new IllegalArgumentException("Make task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));
        dependsOn(task);
        makeFiles.setFrom(task.map(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).getCmakeFiles();
            } else if (it instanceof ConfigureTask) {
                return ((ConfigureTask) it).getOutputs().getFiles();
            } else {
                throw new IllegalArgumentException("Make task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));
    }

    public void binary(Provider<String> path) {
        binary.set(outputDirectory.file(path));
    }

    @Internal
    public final DirectoryProperty getVariantDirectory() {
        return variantDirectory;
    }

    @InputFiles
    public final ConfigurableFileCollection getMakeFiles() {
        return makeFiles;
    }

    @OutputDirectory
    public final DirectoryProperty getOutputDirectory() {
        return outputDirectory;
    }

    @OutputFile
    public final RegularFileProperty getBinary() {
        return binary;
    }

    @Internal
    public final ListProperty<String> getArguments() {
        return arguments;
    }
}
