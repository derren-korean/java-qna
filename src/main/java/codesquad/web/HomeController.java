package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QuestionService;
import com.google.common.collect.Iterables;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.Arrays;

@Controller
public class HomeController {

    @Resource(name = "questionService")
    private QuestionService questionService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("questions", Arrays.asList(Iterables.toArray(questionService.findAll(), Question.class)));
        return "home";
    }
}
