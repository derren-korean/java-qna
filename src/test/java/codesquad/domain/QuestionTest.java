package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class QuestionTest {

    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private Question question;

    @Before
    public void setup() {
        question = new Question("title", "contents");
    }

    @Test
    public void writeBy_for_create() {
        assertNull(question.getWriter());

        question.writeBy(JAVAJIGI);
        assertThat(question.isOwner(JAVAJIGI), is(true));
        assertThat(question.isOwner(SANJIGI), is(false));
    }

    @Test (expected = UnAuthorizedException.class)
    public void updated_by_not_owner() {
        question.writeBy(JAVAJIGI);
        question.update(SANJIGI, question);
    }

    @Test
    public void updated_by_owner() {

        String originalTitle = "title";
        String originalContents = "contents";
        question.writeBy(JAVAJIGI);

        String updateTitle = "updated title";
        String updateContents = "updated contents";
        Question updatedQuestion = new Question(updateTitle, updateContents);
        question.update(JAVAJIGI, updatedQuestion);

        assertThat(question.getTitle(), is(updateTitle));
        assertThat(question.getTitle(), not(originalTitle));

        assertThat(question.getContents(), is(updateContents));
        assertThat(question.getContents(), not(originalContents));
    }
}
