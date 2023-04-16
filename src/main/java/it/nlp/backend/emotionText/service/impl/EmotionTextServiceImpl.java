package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.authentication.repository.UserRepository;
import it.nlp.backend.emotionText.dto.EmotionTextOutput;
import it.nlp.backend.emotionText.model.EmotionText;
import it.nlp.backend.emotionText.repository.EmotionTextRepository;
import it.nlp.backend.emotionText.service.EmotionTextService;
import it.nlp.backend.emotionText.service.InternetEmotionTextService;
import it.nlp.backend.exception.messages.SecurityExceptionMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static it.nlp.backend.exception.messages.TextExceptionMessages.COMMENTS_NUMBER_OUT_OF_RANGE;

@Service
class EmotionTextServiceImpl implements EmotionTextService {

    private final EmotionTextRepository textRepository;
    private final UserRepository userRepository;
    private final EmotionTextRepository emotionTextRepository;
    private final InternetEmotionTextService internetEmotionTextService;
    private final EmotionTextMapper emotionTextMapper;

    EmotionTextServiceImpl(EmotionTextRepository textRepository,
                           UserRepository userRepository,
                           EmotionTextRepository emotionTextRepository,
                           InternetEmotionTextService internetEmotionTextService) {
        this.textRepository = textRepository;
        this.userRepository = userRepository;
        this.emotionTextRepository = emotionTextRepository;
        this.internetEmotionTextService = internetEmotionTextService;
        this.emotionTextMapper = new EmotionTextMapper();
    }

    @Override
    public List<EmotionTextOutput> fetchYTCommentsFromPopularVideos() {
        return internetEmotionTextService.fetchYTCommentsFromPopularVideos().stream()
                .map(text -> emotionTextMapper.mapToEmotionTextOutput(textRepository.save(text)))
                .toList();
    }

    @Override
    public List<EmotionTextOutput> fetchYTCommentsFromVideosOfSavedChannels() {
        return internetEmotionTextService.fetchYTCommentsFromVideosOfSavedChannels().stream()
                .map(text -> emotionTextMapper.mapToEmotionTextOutput(textRepository.save(text)))
                .toList();
    }

    @Override
    public Page<EmotionTextOutput> fetchEmotionTexts(Pageable pageable) {
        return textRepository.findAll(pageable)
                .map(emotionTextMapper::mapToEmotionTextOutput);
    }

    @Override
    public List<EmotionTextOutput> fetchEmotionTextsToBeAssigned(String userIdString, String textsNumberString) {
        int textsNumber = parseCommentsNumber(textsNumberString);
        var userId = UUID.fromString(userIdString);

        validateUserId(userId);

        List<UUID> userAssignedTextsIds = getIdsOfEmotionTextsAssignedByUser(userId);

        List<EmotionText> notAssignedEmotionTexts = getEmotionTextsNotAssignedByUser(userAssignedTextsIds);

        Collections.sort(notAssignedEmotionTexts);

        List<EmotionText> emotionTextsSublist = getEmotionTextsSublist(notAssignedEmotionTexts, textsNumber);

        return emotionTextsSublist.stream().map(emotionTextMapper::mapToEmotionTextOutput).toList();
    }

    private int parseCommentsNumber(String commentsNumber) {
        int number;
        number = Integer.parseInt(commentsNumber);
        if (number < 1 || number > 100) {
            throw new IllegalArgumentException(COMMENTS_NUMBER_OUT_OF_RANGE.getMessage());
        }
        return number;
    }

    private void validateUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(SecurityExceptionMessages.NO_USER_WITH_PASSED_ID.getMessage() + userId);
        }
    }

    private List<UUID> getIdsOfEmotionTextsAssignedByUser(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow();
        return user.getAssignedEmotionTextIds();
    }

    private List<EmotionText> getEmotionTextsNotAssignedByUser(List<UUID> userAssignedTextIds) {
        return emotionTextRepository.findAll().stream()
                .filter(emotionText -> !userAssignedTextIds.contains(emotionText.getEmotionTextId()))
                .collect(Collectors.toList());
    }

    private List<EmotionText> getEmotionTextsSublist(List<EmotionText> notAssignedEmotionTexts, int commentsNumber) {
        List<EmotionText> commentsSublist = new ArrayList<>();
        int elementsNumber = Math.min(notAssignedEmotionTexts.size(), commentsNumber);
        if (elementsNumber > 0) {
            commentsSublist = notAssignedEmotionTexts.subList(0, elementsNumber);
        }
        return commentsSublist;
    }
}
