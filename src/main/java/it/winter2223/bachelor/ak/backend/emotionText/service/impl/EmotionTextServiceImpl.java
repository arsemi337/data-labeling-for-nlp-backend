package it.winter2223.bachelor.ak.backend.emotionText.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import it.winter2223.bachelor.ak.backend.authentication.repository.UserRepository;
import it.winter2223.bachelor.ak.backend.comment.exception.CommentException;
import it.winter2223.bachelor.ak.backend.emotionText.dto.EmotionTextOutput;
import it.winter2223.bachelor.ak.backend.emotionText.model.EmotionText;
import it.winter2223.bachelor.ak.backend.emotionText.repository.EmotionTextRepository;
import it.winter2223.bachelor.ak.backend.emotionText.service.EmotionTextService;
import it.winter2223.bachelor.ak.backend.emotionText.service.InternetCommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.NO_USER_WITH_PASSED_ID;
import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.COMMENTS_NUMBER_IS_NOT_INTEGER;
import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.COMMENTS_NUMBER_OUT_OF_RANGE;

@Service
class EmotionTextServiceImpl implements EmotionTextService {

    private final EmotionTextRepository textRepository;
    private final UserRepository userRepository;
    private final EmotionTextRepository emotionTextRepository;
    private final InternetCommentService internetCommentService;
    private final EmotionTextMapper emotionTextMapper;

    EmotionTextServiceImpl(EmotionTextRepository textRepository,
                           UserRepository userRepository,
                           EmotionTextRepository emotionTextRepository,
                           InternetCommentService internetCommentService) {
        this.textRepository = textRepository;
        this.userRepository = userRepository;
        this.emotionTextRepository = emotionTextRepository;
        this.internetCommentService = internetCommentService;
        this.emotionTextMapper = new EmotionTextMapper();
    }

    @Override
    public List<EmotionTextOutput> fetchYTComments() {
        List<EmotionText> emotionTexts;

        emotionTexts = internetCommentService.fetchYTComments();

        return emotionTexts.stream()
                .map(t -> emotionTextMapper.mapToEmotionTextOutput(textRepository.save(t)))
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
        try {
            number = Integer.parseInt(commentsNumber);
        } catch (NumberFormatException e) {
            throw new CommentException(COMMENTS_NUMBER_IS_NOT_INTEGER.getMessage(), e);
        }
        if (number < 1 || number > 100) {
            throw new CommentException(COMMENTS_NUMBER_OUT_OF_RANGE.getMessage());
        }
        return number;
    }

    private void validateUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new FirebaseAuthenticationException(NO_USER_WITH_PASSED_ID.getMessage());
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
