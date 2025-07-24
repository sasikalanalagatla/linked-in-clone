package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
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

    private final CertificationService certificationService;

    private final UserService userService;

    public CertificationController(CertificationService certificationService, UserService userService) {
        this.certificationService = certificationService;
        this.userService = userService;
    }

    @GetMapping("/new/{userId}")
    public String newCertification(@PathVariable Long userId, Model model) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }

        userService.getUserById(userId);
        model.addAttribute("certification", new Certification());
        model.addAttribute("userId", userId);

        return "certification-form";
    }

    @PostMapping
    public String saveCertification(@ModelAttribute Certification certification, @RequestParam Long userId, Model model) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (certification == null) {
            throw new CustomException("INVALID_CERTIFICATION", "Certification data cannot be null");
        }

        try {
            User user = userService.getUserById(userId);
            certification.setUser(user);
            certificationService.saveCertification(certification);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            model.addAttribute("certification", certification);
            model.addAttribute("userId", userId);
            model.addAttribute("error", "Error saving certification: " + e.getMessage());
            return "certification-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editCertification(@PathVariable Long id, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_CERTIFICATION_ID", "Certification ID cannot be null");
        }

        Certification certification = certificationService.getCertificationById(id);
        model.addAttribute("certification", certification);
        model.addAttribute("userId", certification.getUser().getUserId());

        return "certification-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCertification(@PathVariable Long id, @ModelAttribute Certification certification, @RequestParam Long userId, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_CERTIFICATION_ID", "Certification ID cannot be null");
        }
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (certification == null) {
            throw new CustomException("INVALID_CERTIFICATION", "Certification data cannot be null");
        }

        try {
            User user = userService.getUserById(userId);
            certification.setUser(user);
            certification.setCertificationId(id);
            certificationService.saveCertification(certification);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            model.addAttribute("certification", certification);
            model.addAttribute("userId", userId);
            model.addAttribute("error", "Error updating certification: " + e.getMessage());
            return "certification-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCertification(@PathVariable Long id, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_CERTIFICATION_ID", "Certification ID cannot be null");
        }

        try {
            Certification certification = certificationService.getCertificationById(id);
            Long userId = certification.getUser().getUserId();
            certificationService.deleteCertification(id);

            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            model.addAttribute("error", "Error deleting certification: " + e.getMessage());

            return "error";
        }
    }
}