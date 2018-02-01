package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.security.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.String.format;

@Service("qnaDeleteService")
public class QnADeleteService {
    private static final Logger log = LoggerFactory.getLogger(QnADeleteService.class);
    private static final String DIFFERENT_OWNER = "자신이 작성한 %s에 대해서만 수정/삭제가 가능합니다.";
    private static final String ALREADY_DELETED = "이미 삭제된 %s입니다.";

    @Resource(name = "questionService")
    private QuestionService questionService;

    @Resource(name = "answerService")
    private AnswerService answerService;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question original = questionService.findById(questionId);
        if (isDeleted(original)) throw new CannotDeleteException(format(ALREADY_DELETED, "질문"));
        if (!original.isOwner(loginUser)) throw new UnAuthorizedException(format(DIFFERENT_OWNER,"질문"));

        original.delete();
        Question updatedQuestion = questionService.update(loginUser, questionId, original);
        deleteHistoryService.save(new DeleteHistory(ContentType.QUESTION, updatedQuestion.getId(), loginUser, LocalDateTime.now()));
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) throws CannotDeleteException {
        Question question = questionService.findById(questionId);
        if (isDeleted(question)) throw new CannotDeleteException(format(ALREADY_DELETED, "질문"));
        Answer answer = answerService.create(loginUser, question, contents);
        question.addAnswer(answer);
        return answer;
    }

    private boolean isDeleted(Question question) {
        return Optional.ofNullable(deleteHistoryService.findByContentIdAndContentType(question.getId(), ContentType.QUESTION)).isPresent() || question.isDeleted();
    }

    @Transactional
    public void deleteAnswer(User loginUser, long answerId) throws CannotDeleteException {
        Answer answer = answerService.findById(answerId);
        if (isDeleted(answer)) throw new CannotDeleteException(format(ALREADY_DELETED, "답변"));
        if (!answer.isOwner(loginUser)) throw new UnAuthorizedException(format(DIFFERENT_OWNER,"답변"));

        answer.delete();
        Answer updatedAnswer = answerService.update(loginUser, answer.getId(), answer);
        deleteHistoryService.save(new DeleteHistory(ContentType.ANSWER, updatedAnswer.getId(), loginUser, LocalDateTime.now()));
    }

    private boolean isDeleted(Answer answer) {
        return Optional.ofNullable(deleteHistoryService.findByContentIdAndContentType(answer.getId(), ContentType.ANSWER)).isPresent() || answer.isDeleted();
    }
}