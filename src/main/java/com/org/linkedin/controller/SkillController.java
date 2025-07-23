package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Skill;
import com.org.linkedin.service.SkillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Long id, Model model) {
        Skill skill = (id != null) ? skillService.getSkillById(id) : new Skill();
        model.addAttribute("skill", skill);
        return "skill-form";
    }

    @PostMapping("/save")
    public String saveSkill(@ModelAttribute Skill skill) {
        if (skill == null) {
            throw new CustomException("INVALID_SKILL", "Skill data cannot be null");
        }
        Long userId = 1L; // Hardcoded for development
        skillService.saveSkillForUser(userId, skill);
        return "redirect:/profile/" + userId;
    }

    @GetMapping("/delete/{id}")
    public String deleteSkill(@PathVariable Long id) {
        if (id == null) {
            throw new CustomException("INVALID_SKILL_ID", "Skill ID cannot be null");
        }
        Long userId = 1L; // Hardcoded for development
        skillService.deleteUserSkill(userId, id);
        return "redirect:/profile/" + userId;
    }
}