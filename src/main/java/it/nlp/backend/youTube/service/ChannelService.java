package it.nlp.backend.youTube.service;

import it.nlp.backend.youTube.dto.ChannelInput;
import it.nlp.backend.youTube.dto.ChannelOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChannelService {
    Page<ChannelOutput> fetchChannels(Pageable pageable);
    List<ChannelOutput> addChannels(List<ChannelInput> channelInputList);
    void removeChannel(String channelId);
}
