package ee.a1nu.application.web.controller;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import ee.a1nu.application.bot.Bot;
import ee.a1nu.application.bot.core.data.CommandCategory;
import ee.a1nu.application.database.constants.TransactionType;
import ee.a1nu.application.database.entity.CurrencyCommand;
import ee.a1nu.application.database.entity.Guild;
import ee.a1nu.application.database.entity.Pocket;
import ee.a1nu.application.database.service.PocketService;
import ee.a1nu.application.database.service.TransactionService;
import ee.a1nu.application.utils.PermissionsUtils;
import ee.a1nu.application.web.dto.ErrorDTO;
import ee.a1nu.application.web.dto.MemberPocketDTO;
import ee.a1nu.application.database.service.CurrencyCommandService;
import ee.a1nu.application.database.service.GuildEntityService;
import ee.a1nu.application.web.mapper.MemberPocketMapper;
import ee.a1nu.application.web.vm.TransactionVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ConfigurationController {

    private Bot bot;
    private GuildEntityService guildEntityService;
    private CurrencyCommandService currencyCommandService;
    private PocketService pocketService;
    private TransactionService transactionService;

    @Autowired
    public ConfigurationController(
            Bot bot,
            GuildEntityService guildEntityService,
            CurrencyCommandService currencyCommandService,
            PocketService pocketService,
            TransactionService transactionService
    ) {
        this.bot = bot;
        this.guildEntityService = guildEntityService;
        this.currencyCommandService = currencyCommandService;
        this.pocketService = pocketService;
        this.transactionService = transactionService;
    }

    @GetMapping(value="/configuration")
    public ModelAndView guildConfigurationList(
            @RequestParam("guildId") Long guildId,
            HttpServletRequest request
    ) {
        DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (PermissionsUtils.checkAdministrativeAllowance(bot, Long.parseLong(Objects.requireNonNull(principal.getAttribute("id"))), guildId)) {
            return new ModelAndView("redirect:/permission-denied");
        }

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        Snowflake snowflake = Snowflake.of(guildId);
        ModelAndView modelAndView = new ModelAndView();
        guildEntityService.createIfNotExists(snowflake);
        discord4j.core.object.entity.Guild guild = bot.getClient().getGuildById(snowflake).block();

        Map<String, CommandCategory> commandCategoryMap = CommandCategory.getMap();

        modelAndView.addObject("csrf", csrf);
        modelAndView.addObject("guildName", Objects.requireNonNull(guild).getName());
        modelAndView.addObject("guildId", guildId);
        modelAndView.addObject("commands", commandCategoryMap);
        modelAndView.setViewName("view/configuration/configuration");
        return modelAndView;
    }

    @GetMapping(value="/configuration/edit")
    public ModelAndView editConfiguration(@RequestParam("guildId") Long guildId, @RequestParam("type") CommandCategory commandCategory) {
        DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (PermissionsUtils.checkAdministrativeAllowance(bot, Long.parseLong(Objects.requireNonNull(principal.getAttribute("id"))), guildId)) {
            return new ModelAndView("redirect:/permission-denied");
        }

        ModelAndView modelAndView = new ModelAndView();
        discord4j.core.object.entity.Guild guild = bot.getClient().getGuildById(Snowflake.of(guildId)).block();
        modelAndView.addObject("guildName", Objects.requireNonNull(guild).getName());
        modelAndView.addObject("guildId", guildId);
        modelAndView.addObject("currentConfiguration", commandCategory.getName());
        modelAndView.addObject("commandCategory", commandCategory);
        switch (commandCategory) {
            case CURRENCY: {
                CurrencyCommand currencyCommand = currencyCommandService.getOrCreateCurrencyCommandForGuild(Snowflake.of(guildId));
                modelAndView.addObject("currencyCommand", currencyCommand);
                modelAndView.setViewName("view/configuration/currency");
                break;
            }
        }

        return modelAndView;
    }
    @PostMapping(value="/configuration/currency")
    public ModelAndView saveConfiguration(
            @RequestParam("guildId") Long guildId,
            @RequestParam("type") CommandCategory commandCategory,
            CurrencyCommand currencyCommandUpdate) {
        DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (PermissionsUtils.checkAdministrativeAllowance(bot, Long.parseLong(Objects.requireNonNull(principal.getAttribute("id"))), guildId)) {
            return new ModelAndView("redirect:/permission-denied");
        }
        ModelAndView modelAndView = new ModelAndView();
        discord4j.core.object.entity.Guild guild = bot.getClient().getGuildById(Snowflake.of(guildId)).block();
        modelAndView.addObject("guildName", Objects.requireNonNull(guild).getName());
        modelAndView.addObject("guildId", guildId);
        modelAndView.addObject("currentConfiguration", commandCategory.getName());
        modelAndView.addObject("commandCategory", commandCategory);

        if (!currencyCommandUpdate.getId().equals(currencyCommandService.getCurrencyCommandForGuild(Snowflake.of(guildId)).getId())) {
            modelAndView.addObject("errorMessage", true);
            CurrencyCommand currencyCommand = currencyCommandService.getOrCreateCurrencyCommandForGuild(Snowflake.of(guildId));
            modelAndView.addObject("currencyCommand", currencyCommand);
        } else {
            modelAndView.addObject("currencyCommand", currencyCommandService.update(currencyCommandUpdate));
            modelAndView.addObject("successMessage", true);
        }
        modelAndView.setViewName("view/configuration/currency");
        return modelAndView;
    }

    @GetMapping(value = "/api/configuration/members-pockets")
    public Object getGuildMembersPockets (
            @RequestParam Long guildId
    ) {
        DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (PermissionsUtils.checkAdministrativeAllowance(bot, Long.parseLong(Objects.requireNonNull(principal.getAttribute("id"))), guildId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorDTO.builder().errorCode(403).errorMessage("permissions").build());
        }

        CurrencyCommand currencyCommand = currencyCommandService.getCurrencyCommandForGuild(Snowflake.of(guildId));
        if (!currencyCommand.isEnabled()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDTO.builder().errorCode(403).errorMessage("configDisabled").build());
        }

        List<MemberPocketDTO> memberPocketDTOList = new ArrayList<>();
        bot.getGuildMembers(Snowflake.of(guildId))
            .stream()
            .filter(member -> !member.isBot())
            .forEach(member -> {
                Pocket memberPocket = pocketService.getOrCreatePocket(member.getId().asLong(), guildId);
                MemberPocketDTO memberPocketDTO = MemberPocketMapper.map(memberPocket, member);

                if (memberPocket.getLastTransaction() != null) {
                    memberPocketDTO.setLastTransaction(memberPocket.getLastTransaction());

                }
                memberPocketDTOList.add(memberPocketDTO);
            });

        return ResponseEntity.ok().body(memberPocketDTOList);
    }

    @PostMapping(value = "/api/configuration/save-pocket")
    @ResponseBody
    public Object updatePocket(@RequestBody TransactionVM transactionVM) {
        DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (PermissionsUtils.checkAdministrativeAllowance(bot, Long.parseLong(Objects.requireNonNull(principal.getAttribute("id"))), transactionVM.getGuildId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorDTO.builder().errorCode(403).errorMessage("permissions").build());
        }

        Pocket pocket = pocketService.getPocket(transactionVM.getMemberId(), transactionVM.getGuildId());

        if (!pocket.getId().equals(transactionVM.getPocketId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDTO.builder().errorCode(403).errorMessage("wrong id's").build());
        }

        if (!pocketService.validateOperation(pocket, transactionVM.getDiff())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDTO.builder().errorCode(403).errorMessage("balance cannot fall below zero").build());
        }

        if (transactionVM.getDescription() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDTO.builder().errorCode(403).errorMessage("description cannot be empty").build());
        }

        Pocket updatedPocket = pocketService.updatePocket(pocket, transactionVM.getDiff(), transactionVM.isFrozen(), transactionVM.getDescription(), TransactionType.MANUAL);

        Member guildMember = bot.getGuildMember(updatedPocket.getGuild().getSnowflake(), updatedPocket.getMember().getSnowflake());

        return ResponseEntity.ok().body(MemberPocketMapper.map(updatedPocket, guildMember));
    }
}
