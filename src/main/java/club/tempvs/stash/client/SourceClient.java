package club.tempvs.stash.client;

import club.tempvs.stash.dto.SourceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "library", decode404 = true)
@RequestMapping("/api/source")
public interface SourceClient {

    @GetMapping("/{id}")
    SourceDto get(@PathVariable Long id);
}
