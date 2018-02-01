package codesquad.service;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("questionService")
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    public Question create(User loginUser, Question question) {
        if (loginUser == null) throw new UnAuthorizedException();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return Optional.ofNullable(questionRepository.findOne(id)).orElseThrow(()->new IllegalArgumentException("존재하지 않는 질문입니다."));
    }

    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = findById(id);
        original.update(loginUser, updatedQuestion);
        return questionRepository.save(original);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }
}