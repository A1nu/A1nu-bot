package ee.a1nu.application.web.controller;

import ee.a1nu.application.commandConstants.CommandName;
import ee.a1nu.application.commandConstants.CurrencyCommandNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class DocumentationController {

    @GetMapping(value="/documentation")
    public ModelAndView documentation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("CommandNames",CurrencyCommandNames.getCommandNames());
        modelAndView.setViewName("view/documentation");
        return modelAndView;
    }

}
