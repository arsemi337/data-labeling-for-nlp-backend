package it.nlp.backend.youTube.service.impl;

import com.google.api.services.youtube.model.ChannelListResponse;
import io.micrometer.common.util.StringUtils;
import it.nlp.backend.emotionText.service.YouTubeService;
import it.nlp.backend.youTube.repository.ChannelRepository;
import it.nlp.backend.youTube.dto.ChannelInput;
import it.nlp.backend.youTube.dto.ChannelOutput;
import it.nlp.backend.youTube.service.ChannelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static it.nlp.backend.exception.messages.ChannelExceptionMessages.*;

@Service
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final YouTubeService youTubeService;
    private final ChannelMapper channelMapper;

    public ChannelServiceImpl(ChannelRepository channelRepository, YouTubeService youTubeService) {
        this.channelRepository = channelRepository;
        this.youTubeService = youTubeService;
        this.channelMapper = new ChannelMapper();
    }

    @Override
    public Page<ChannelOutput> fetchChannels(Pageable pageable) {
        return channelRepository.findAll(pageable)
                .map(channelMapper::mapToChannelOutput);
    }

    @Override
    public List<ChannelOutput> addChannels(List<ChannelInput> channelInputList) {
        channelInputList.forEach(this::validateChannelInput);

        List<String> channelIdList = channelInputList.stream()
                .map(ChannelInput::channelId)
                .toList();
        ChannelListResponse channelListResponse = youTubeService.getChannelInformation(channelIdList);

        return channelListResponse.getItems().stream()
                .map(channelMapper::mapToChannel)
                .map(channelRepository::save)
                .map(channelMapper::mapToChannelOutput)
                .toList();
    }

    @Override
    public void removeChannel(String channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException(NULL_CHANNEL_ID.getMessage());
        }
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException(NO_CHANNEL_WITH_ENTERED_ID.getMessage() + " (" + channelId + ")");
        }
        channelRepository.deleteById(channelId);
    }

    private void validateChannelInput(ChannelInput channelInput) {
        String channelId = channelInput.channelId();
        if (StringUtils.isBlank(channelId)) {
            throw new IllegalArgumentException(CONTAINS_NULL_CHANNEL_ID.getMessage());
        }
        if (channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException(CHANNEL_ALREADY_EXISTS.getMessage() + " (" + channelId + ")");
        }
    }
}
