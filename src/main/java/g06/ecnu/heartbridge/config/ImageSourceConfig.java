package g06.ecnu.heartbridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Config
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/17
 */
@Component
@ConfigurationProperties(prefix = "image-upload")
public class ImageSourceConfig {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
