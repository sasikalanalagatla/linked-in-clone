package com.org.linkedin.controller;

import com.org.linkedin.model.Certification;
import com.org.linkedin.model.User;
import com.org.linkedin.service.CertificationService;
import com.org.linkedin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/certifications")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/new/{userId}")
    public String newCertification(@PathVariable Long userId, Model model) {
        model.addAttribute("certification", new Certification());
        model.addAttribute("userId", userId);
        return "certification-form";
    }

    @PostMapping
    public String saveCertification(@ModelAttribute Certification certification, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        certification.setUser(user);
        certificationService.saveCertification(certification);
        return "redirect:/profile/" + userId;
    }

    @GetMapping("/edit/{id}")
    public String editCertification(@PathVariable Long id, Model model) {
        Certification certification = certificationService.getCertificationById(id);
        model.addAttribute("certification", certification);
        model.addAttribute("userId", certification.getUser().getUserId());
        return "certification-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCertification(@PathVariable Long id, @ModelAttribute Certification certification, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        certification.setUser(user);
        certification.setCertificationId(id); // Ensure the ID is set for update
        certificationService.saveCertification(certification);
        return "redirect:/profile/" + userId;
    }

    @PostMapping("/delete/{id}")
    public String deleteCertification(@PathVariable Long id) {
        Certification certification = certificationService.getCertificationById(id);
        Long userId = certification.getUser().getUserId();
        certificationService.deleteCertification(id);
        return "redirect:/profile/" + userId;
    }
}