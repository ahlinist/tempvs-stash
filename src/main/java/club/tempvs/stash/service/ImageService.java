package club.tempvs.stash.service;

import club.tempvs.stash.dto.ImageDto;

import java.util.List;

public interface ImageService {

    void delete(List<String> objectIds);

    ImageDto store(ImageDto imageDto);
}
