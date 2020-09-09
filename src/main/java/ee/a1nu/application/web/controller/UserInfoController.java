package ee.a1nu.application.web.controller;

import discord4j.common.util.Snowflake;
import ee.a1nu.application.bot.Bot;
import ee.a1nu.application.database.entity.Guild;
import ee.a1nu.application.database.entity.Member;
import ee.a1nu.application.database.entity.Pocket;
import ee.a1nu.application.database.entity.Transaction;
import ee.a1nu.application.database.service.MemberService;
import ee.a1nu.application.database.service.PocketService;
import ee.a1nu.application.database.service.TransactionService;
import ee.a1nu.application.utils.PermissionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.List;

@Controller
public class UserInfoController {
    private final Bot bot;
    private final PocketService pocketService;
    private final MemberService memberService;
    private final TransactionService transactionService;

    @Autowired
    public UserInfoController(
            Bot bot,
            PocketService pocketService,
            MemberService memberService,
            TransactionService transactionService
    ) {
        this.bot = bot;
        this.pocketService = pocketService;
        this.memberService = memberService;
        this.transactionService = transactionService;
    }


    @GetMapping(value="/dashboard/user-info")
    public ModelAndView getUserInfo(@RequestParam("guildId") Long guildId) {
        ModelAndView modelAndView = new ModelAndView();

        if (!PermissionsUtils.checkUserExistsOnGuild(bot, guildId)) {
            return new ModelAndView("redirect:/permission-denied");
        }
        DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberService.getOrCreateMember(Snowflake.of(Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")))));
        Pocket pocket = pocketService.getOrCreatePocket(member.getId(), guildId);
        modelAndView.addObject("balance", pocket.getCurrencyAmount());
        modelAndView.addObject("guildName", Objects.requireNonNull(bot.getGuild(Snowflake.of(guildId))).getName());
        modelAndView.addObject("guildId", Objects.requireNonNull(guildId));
        modelAndView.setViewName("view/user-info/user-info");
        return modelAndView;
    }

    @GetMapping(value = "/user-transactions")
    public ModelAndView getUserTransactions(
            @RequestParam("guildId") Long guildId,
            @RequestParam(value = "userId", required = false) Long userId
    ) {
        ModelAndView modelAndView = new ModelAndView();
        Long memberId;

        if (userId != null) {
            if (PermissionsUtils.checkAdministrativeAllowance(bot, guildId)) {
                return new ModelAndView("redirect:/permission-denied");
            }
            memberId = userId;
        } else {
            DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            memberId = Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")));
        }

        Pocket pocket = pocketService.getPocket(memberId, guildId);
        List<Transaction> transactionList = transactionService.getAllTransactions(pocket);
        transactionList.sort((t1, t2) -> {
            if (t1.getDate().isEqual(t2.getDate())) return 0;
            if (t1.getDate().isBefore(t2.getDate())) return -1;
            else return 1;
        });
        modelAndView.addObject("transactions", transactionList);
        modelAndView.addObject("formatter", DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"));
        modelAndView.setViewName("view/user-info/transactions");

        return modelAndView;
    }
}
