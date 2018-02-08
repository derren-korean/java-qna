package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiQnAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() {
        QuestionDto questionDto = createQuestion().toQuestionDto();
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity(getApiPath(Question.class), questionDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(questionDto));
    }

    @Test
    public void 누구든지_질문을_읽을_수_있다() {
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity(getApiPath(Question.class), createQuestion().toQuestionDto(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto dbQuestion = template()
                .getForObject(location, QuestionDto.class);
        assertNotNull(dbQuestion);
    }

    @Test
    public void update() {
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity(getApiPath(Question.class), createQuestion().toQuestionDto(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto createdDto = basicAuthTemplate()
                .getForObject(location, QuestionDto.class);

        basicAuthTemplate()
                .put(location, new QuestionDto("updateTitle", "updatedContents"));

        QuestionDto dbQuestion = basicAuthTemplate()
                .getForObject(location, QuestionDto.class);

        assertThat(createdDto.getId(), is(dbQuestion.getId()));
        assertThat(createdDto, not(dbQuestion));
    }

    // todo : 질문입니다. 정황상으로 UnAuthorizedException 이지만 그렇게 되지 않습니다.
    // 어떻게 해야할까요? 아래 있는 문자를 커맨드+쉬프트+o 를하고 쳐보세요~
    // Question.java:83l
    @Test (expected = UnAuthorizedException.class)
    public void fail_different_user_update() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(getApiPath(Question.class), createQuestion().toQuestionDto(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        basicAuthTemplate(differentUser())
                .put(location, new QuestionDto("updateTitle", "updatedContents"));
    }

    private Question createQuestion() {
        return new Question("title", "contents");
    }
}
