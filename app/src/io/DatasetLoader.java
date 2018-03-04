package io;
import model.Dataset;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;
import java.io.FileNotFoundException;

public interface DatasetLoader<T> {

    Dataset<T> load() throws FileNotFoundException;

    default boolean hasExtension(File file, String extension){
        if (Objects.requireNonNull(file).isDirectory()){
            return false;
        }
        String ext = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf('.') + 1);
        return ext.equalsIgnoreCase(Objects.requireNonNull(extension));
    }

    default List<File> getWithSubfolders(File file, String extension){
        List<File> result = new ArrayList<>();

        if (hasExtension(file, extension)){
            result.add(file);
        }
        else if (file.isDirectory()){
            File[] files = file.listFiles();

            for (File f : Objects.requireNonNull(files)){
                result.addAll(getWithSubfolders(f, extension));
            }
        }
        return result;
    }
}
