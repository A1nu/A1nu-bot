package ee.a1nu.application.web.controller;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import ee.a1nu.application.bot.Bot;
import ee.a1nu.application.bot.core.data.CommandCategory;
import ee.a1nu.application.database.constants.TransactionType;
import ee.a1nu.application.database.entity.CurrencyCommand;
import ee.a1nu.application.database.entity.Pocket;
import ee.a1nu.application.database.service.PocketService;
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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ConfigurationController {

    private final Bot bot;
    private final GuildEntityService guildEntityService;
    private final CurrencyCommandService currencyCommandService;
    private final PocketService pocketService;

    @Autowired
    public ConfigurationController(
            Bot bot,
            GuildEntityService guildEntityService,
            CurrencyCommandService currencyCommandService,
            PocketService pocketService
    ) {
        this.bot = bot;
        this.guildEntityService = guildEntityService;
        this.currencyCommandService = currencyCommandService;
        this.pocketService = pocketService;
    }

    @GetMapping(value="/configuration")
    public ModelAndView guildConfigurationList(
            @RequestParam("guildId") Long guildId,
            HttpServletRequest request
    ) {
        if (PermissionsUtils.checkAdministrativeAllowance(bot, guildId)) {
            return new ModelAndView("redirect:/permission-denied");
        }

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        Snowflake guildSnowflake = Snowflake.of(guildId);
        ModelAndView modelAndView = new ModelAndView();
        guildEntityService.createIfNotExists(guildSnowflake);

        Map<String, CommandCategory> commandCategoryMap = CommandCategory.getMap();

        modelAndView.addObject("csrf", csrf);
        modelAndView.addObject("guildName", Objects.requireNonNull(bot.getGuild(guildSnowflake)).getName());
        modelAndView.addObject("guildId", guildId);
        modelAndView.addObject("commands", commandCategoryMap);
        modelAndView.setViewName("view/configuration/configuration");
        return modelAndView;
    }

    @GetMapping(value="/configuration/edit")
    public ModelAndView editConfiguration(@RequestParam("guildId") Long guildId, @RequestParam("type") CommandCategory commandCategory) {
        if (PermissionsUtils.checkAdministrativeAllowance(bot, guildId)) {
            return new ModelAndView("redirect:/permission-denied");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("guildName", Objects.requireNonNull(bot.getGuild(Snowflake.of(guildId))).getName());
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
        if (PermissionsUtils.checkAdministrativeAllowance(bot, guildId)) {
            return new ModelAndView("redirect:/permission-denied");
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("guildName", Objects.requireNonNull(bot.getGuild(Snowflake.of(guildId))).getName());
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
        if (PermissionsUtils.checkAdministrativeAllowance(bot, guildId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorDTO.builder().errorCode(403).errorMessage("permissions").build());
        }

        CurrencyCommand currencyCommand = currencyCommandService.getCurrencyCommandForGuild(Snowflake.of(guildId));
        if (currencyCommand == null || !currencyCommand.isEnabled()) {
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
        if (PermissionsUtils.checkAdministrativeAllowance(bot, transactionVM.getGuildId())) {
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
