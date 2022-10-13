package it.winter2223.bachelor.ak.backend.comments.service.impl;

import it.winter2223.bachelor.ak.backend.comments.service.CommentsService;
import org.springframework.stereotype.Service;

@Service
class CommentsServiceImpl implements CommentsService {

    @Override
    public String testMethod() {
        return "Test service";
    }
}
