package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.authentication.repository.UserRepository;
import it.nlp.backend.emotionText.dto.EmotionTextOutput;
import it.nlp.backend.emotionText.model.EmotionText;
import it.nlp.backend.emotionText.repository.EmotionTextRepository;
import it.nlp.backend.emotionText.service.EmotionTextService;
import it.nlp.backend.emotionText.service.InternetEmotionTextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static it.nlp.backend.exception.messages.SecurityExceptionMessages.NO_USER_WITH_PASSED_EMAIL;
import static it.nlp.backend.exception.messages.TextExceptionMessages.TEXTS_NUMBER_OUT_OF_RANGE;

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
    public List<EmotionTextOutput> fetchEmotionTextsToBeAssigned(String userEmail, String textsNumberString) {
        int textsNumber = parseTextsNumber(textsNumberString);

        List<UUID> userAssignedTextsIds = getIdsOfEmotionTextsAssignedByUser(userEmail);

        List<EmotionText> notAssignedEmotionTexts = getEmotionTextsNotAssignedByUser(userAssignedTextsIds);

        Collections.sort(notAssignedEmotionTexts);

        List<EmotionText> emotionTextsSublist = getEmotionTextsSublist(notAssignedEmotionTexts, textsNumber);

        return emotionTextsSublist.stream().map(emotionTextMapper::mapToEmotionTextOutput).toList();
    }

    private int parseTextsNumber(String textsNumber) {
        int number;
        number = Integer.parseInt(textsNumber);
        if (number < 1 || number > 100) {
            throw new IllegalArgumentException(TEXTS_NUMBER_OUT_OF_RANGE.getMessage());
        }
        return number;
    }

    private List<UUID> getIdsOfEmotionTextsAssignedByUser(String userEmail) {
        var user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new IllegalArgumentException(NO_USER_WITH_PASSED_EMAIL.getMessage() + userEmail));
        return user.getAssignedEmotionTextIds();
    }

    private List<EmotionText> getEmotionTextsNotAssignedByUser(List<UUID> userAssignedTextIds) {
        return emotionTextRepository.findAll().stream()
                .filter(emotionText -> !userAssignedTextIds.contains(emotionText.getEmotionTextId()))
                .collect(Collectors.toList());
    }

    private List<EmotionText> getEmotionTextsSublist(List<EmotionText> notAssignedEmotionTexts, int textsNumber) {
        List<EmotionText> textsSublist = new ArrayList<>();
        int elementsNumber = Math.min(notAssignedEmotionTexts.size(), textsNumber);
        if (elementsNumber > 0) {
            textsSublist = notAssignedEmotionTexts.subList(0, elementsNumber);
        }
        return textsSublist;
    }
}
