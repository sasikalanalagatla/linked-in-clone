package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Skill;
import com.org.linkedin.model.User;
import com.org.linkedin.service.SkillService;
import com.org.linkedin.service.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;
    private final UserServiceImpl userService;

    public SkillController(SkillService skillService, UserServiceImpl userService) {
        this.skillService = skillService;
        this.userService = userService;
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Long id, Model model) {
        Skill skill = (id != null) ? skillService.getSkillById(id) : new Skill();
        model.addAttribute("skill", skill);
        return "skill-form";
    }

    @PostMapping("/save")
    public String saveSkill(@ModelAttribute Skill skill , Principal principal) {
        if (skill == null) {
            throw new CustomException("INVALID_SKILL", "Skill data cannot be null");
        }
        String email = principal.getName();
        User user = userService.findByEmail(email);
        Long userId = user.getUserId();
        skillService.saveSkillForUser(userId, skill);
        return "redirect:/profile/" + userId;
    }

    @GetMapping("/delete/{id}")
    public String deleteSkill(@PathVariable Long id ,Principal principal) {
        if (id == null) {
            throw new CustomException("INVALID_SKILL_ID", "Skill ID cannot be null");
        }
        String email = principal.getName();
        User user = userService.findByEmail(email);
        Long userId = user.getUserId();
        skillService.deleteUserSkill(userId, id);
        return "redirect:/profile/" + userId;
    }
}