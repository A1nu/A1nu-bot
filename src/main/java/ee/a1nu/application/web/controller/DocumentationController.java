package ee.a1nu.application.web.controller;

import ee.a1nu.application.commandConstants.CommandNamesService;
import ee.a1nu.application.commandConstants.CurrencyCommandNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class DocumentationController {

    private final CommandNamesService commandNamesService;

    @Autowired
    public DocumentationController(CommandNamesService commandNamesService) {
        this.commandNamesService = commandNamesService;
    }

    @GetMapping(value="/documentation")
    public ModelAndView documentation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("CommandNames",CurrencyCommandNames.getCommandNames());
        modelAndView.addObject("commandCategories", commandNamesService.getAllCategoriesWithCommand());
        modelAndView.setViewName("view/documentation");
        return modelAndView;
    }

}
