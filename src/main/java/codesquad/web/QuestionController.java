package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnADeleteService;
import codesquad.service.QuestionService;
import com.google.common.collect.Iterables;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Resource(name = "questionService")
    private QuestionService questionService;

    @Resource(name = "qnaDeleteService")
    private QnADeleteService qnADeleteService;

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", questionService.findById(id));
        return "/qna/show";
    }

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping("/")
    public String create(
            @LoginUser User loginUser,
            @RequestParam("title") String title,
            @RequestParam("contents") String contents,
            Model model) {
        model.addAttribute("question",
                questionService.create(loginUser, new Question(title, contents)));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String update(
            @PathVariable long id,
            HttpSession session,
            Model model) {
        User loginUser = HttpSessionUtils.getUserFromSession(session);
        if (!questionService.findById(id).isOwner(loginUser)) throw new UnAuthorizedException();
        model.addAttribute("question", questionService.findById(id));
        return "/qna/updateForm";
    }

    @PostMapping("/{id}/update")
    public String update(
            @LoginUser User loginUser,
            @PathVariable long id,
            @RequestParam("title") String title,
            @RequestParam("contents") String contents,
            Model model) {
        model.addAttribute("question",
                questionService.update(loginUser, id, new Question(title, contents)));
        return "/qna/show";
    }

    @DeleteMapping("/{id}")
    public String delete(
            @LoginUser User loginUser,
            @PathVariable long id,
            Model model) throws CannotDeleteException {
        qnADeleteService.deleteQuestion(loginUser, id);
        model.addAttribute("questions", Arrays.asList(Iterables.toArray(questionService.findAll(), Question.class)));
        return "home";
    }
}