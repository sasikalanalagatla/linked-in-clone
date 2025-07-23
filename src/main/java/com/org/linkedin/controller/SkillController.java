package com.org.linkedin.controller;

import com.org.linkedin.model.Skill;
import com.org.linkedin.service.SkillService;
import jakarta.servlet.http.HttpSession;
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
        Long userId = 1l;
        skillService.saveSkillForUser(userId, skill);
        return "redirect:/profile/"+1;
    }

    @GetMapping("/delete/{id}")
    public String deleteSkill(@PathVariable Long id) {
        Long userId = 1l;
        skillService.deleteUserSkill(userId, id);
        return "redirect:/profile/"+1;
    }
}
