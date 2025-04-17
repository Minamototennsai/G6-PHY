package g06.ecnu.heartbridge.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/api")
public class ImageController {

    // 从 application.yml 配置文件中注入外部存储路径
    @Value("${image-upload.path}")
    private String rawUploadDir;

    // 获取图片
    @GetMapping("/image/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) throws IOException {
        String uploadDir = rawUploadDir.substring(rawUploadDir.lastIndexOf("///") + 3);
        Path imagePath = Paths.get(uploadDir + imageName);
        File file = imagePath.toFile();
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);

        HttpHeaders headers = new HttpHeaders();
        if (getExtension(file).equals(".jpg") || getExtension(file).equals(".jpeg")) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        } else if (getExtension(file).equals(".gif")) {
            headers.setContentType(MediaType.IMAGE_GIF);
        } else if (getExtension(file).equals(".png")) {
            headers.setContentType(MediaType.IMAGE_PNG);
        }
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    // 上传图片
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String uploadDir = rawUploadDir.substring(rawUploadDir.lastIndexOf("///") + 3);
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String uuidFileName = UUID.randomUUID() + getExtension(file);

            Path targetLocation = Paths.get(uploadDir).resolve(uuidFileName);

            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation);

            String fileUrl = "/api/image/" + uuidFileName;
            return ResponseEntity.ok("{\"data\": "+ "\"" + fileUrl + "\"" + "}");
        } catch (IOException ex) {
            return ResponseEntity.status(500).body("Could not store file");
        }
    }

    private String getExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
    }

    private String getExtension(File file) {
        String originalFileName = file.getName();
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }
}
