package it.nlp.backend.youTube.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.nlp.backend.youTube.dto.ChannelInput;
import it.nlp.backend.youTube.dto.ChannelOutput;
import it.nlp.backend.youTube.service.ChannelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "YouTube channel")
@RequestMapping("/api/v1/youtube/channels")
@SecurityRequirement(name = "Bearer Authentication")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping
    ResponseEntity<Page<ChannelOutput>> fetchChannels(Pageable pageable) {
        return ResponseEntity.ok(channelService.fetchChannels(pageable));
    }

    @PostMapping
    ResponseEntity<List<ChannelOutput>> addChannels(@RequestBody List<ChannelInput> channelInputList) {
        return ResponseEntity.ok(channelService.addChannels(channelInputList));
    }

    @DeleteMapping("/{channelId}")
    void removeChannel(@PathVariable String channelId) {
        channelService.removeChannel(channelId);
    }
}
