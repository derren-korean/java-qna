package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service("answerService")
public class AnswerService {
    private static final Logger log = LoggerFactory.getLogger(AnswerService.class);

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    public Answer findById(long id) {
        return Optional.ofNullable(answerRepository.findOne(id)).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다."));
    }

    public Answer create(User loginUser, Question question, String contents) {
        Answer answer = new Answer(loginUser, question, contents);
        log.debug("answer : {}", answer);
        return answerRepository.save(answer);
    }

    public Answer update(User loginUser, long id, Answer updatedAnswer) {
        Answer original = findById(id);
        original.update(loginUser, updatedAnswer);
        return answerRepository.save(original);
    }
}
