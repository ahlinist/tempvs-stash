package club.tempvs.stash.dto;

import club.tempvs.stash.domain.Image;
import lombok.Data;

@Data
public class ImageDto {

    private String objectId;
    private String imageInfo;
    private String content;
    private String fileName;

    public Image toImage() {
        return new Image(objectId, imageInfo, fileName);
    }
}
