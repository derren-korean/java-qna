package codesquad.service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    private User loginUser;

    @Before
    public void setup() throws UnAuthenticationException {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
        loginUser = userService.login(user.getUserId(), user.getPassword());
    }

    @Test
    public void anybody_can_access_to_question() {
        Iterable<Question> all = questionRepository.findAll();
        when(questionService.findAll()).thenReturn(all);
        assertThat(Optional.of(questionRepository.findAll()).isPresent(), is(true));
    }

    /**
     * service와 controller에서는 @LoginUser와 같이 다르기 때문에
     * loginUser에 null을 넣음
     */
    @Test (expected = UnAuthorizedException.class)
    public void ask_a_question_without_login() {
        Question question = new Question("first", "test");
        when(questionRepository.save(question)).thenReturn(question);
        Question savedQuestion = questionService.create(null, question);
        assertThat(question, is(equalTo(savedQuestion)));
    }

    @Test
    public void create_question_success() {
        Question question = new Question("first", "test");
        when(questionRepository.save(question)).thenReturn(question);
        Question savedQuestion = questionService.create(loginUser, question);
        assertThat(question, is(equalTo(savedQuestion)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_question_fail_nonExists_question() {
        Question noExistsQuestion = new Question("noExists", "cuz never created");
        questionService.update(loginUser, noExistsQuestion.getId(), noExistsQuestion);
    }

    /**
     * Mock으로 테스트를 save를 하다보니, 생성되는 Question의 id값이 0으로되어,
     * update를 테스트 할 수 없습니다.
     * 어떻게 진행해야 할까요?
     * 트랜잭션을 만들거나 활성 트랜잭션을 반환 하는 방식은 제 역량에서 무리가 있네요...
     */
    @Test
    public void update_question_success_changed_same_writer() {
        String updateContent = "update text";
        Question question = new Question("first", "test");
        when(questionRepository.save(question)).thenReturn(question);
        Question createdQuestion = questionService.create(loginUser, question);
        assertThat(question, is(equalTo(createdQuestion)));

        Question changedContent = new Question("first", updateContent);
        when(questionRepository.save(changedContent)).thenReturn(changedContent);
        Question updatedQuestion = questionService.update(loginUser, createdQuestion.getId(), changedContent);
        assertThat(changedContent, is(equalTo(updatedQuestion)));
//        assertThat(createdQuestion.getId(), is(updatedQuestion.getId()));
//        assertThat(createdQuestion.isOwner(loginUser), is(updatedQuestion.isOwner(loginUser)));
    }
}
