package club.tempvs.stash.client;

import club.tempvs.stash.dto.SourceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(url = "${app.library.url}", value = "library", decode404 = true)
public interface SourceClient {

    @GetMapping("/source/{id}")
    SourceDto get(@PathVariable Long id);
}
