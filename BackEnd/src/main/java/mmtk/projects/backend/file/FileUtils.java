package mmtk.projects.backend.file;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Author : Name
 * Created At : 03/11/2024, Nov ,13, 17
 * Project Name : BookSocialNetwork
 **/
@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String fileUrl){
        if(StringUtils.isBlank(fileUrl)){
            return null;
        }
        try{
            Path filePath = new File(fileUrl).toPath();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.warn("No File Found in the Path {}", fileUrl);
        }
        return null;
    }
}
