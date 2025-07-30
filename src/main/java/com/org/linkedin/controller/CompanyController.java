package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Company;
import com.org.linkedin.service.CompanyService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/company/add")
    public String showCompanyForm(Model model, Principal principal) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to create a company");
            }
            model.addAttribute("company", new Company());
            return "add-company";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/company/create")
    public String createCompany(@ModelAttribute("company") Company company, Principal principal, Model model) {
        try {
            Company savedCompany = companyService.createCompany(company, principal);
            return "redirect:/company/" + savedCompany.getId();
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("company", company);
            return "company-form";
        }
    }

    @GetMapping("/company/{id}")
    public String getCompanyById(@PathVariable("id") Long id, Model model, Principal principal) {
        try {
            model.addAllAttributes(companyService.getCompanyDetails(id, principal));
            return "company";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/company/edit/{id}")
    public String showEditCompanyForm(@PathVariable("id") Long id, Model model, Principal principal) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to edit a company");
            }
            Company company = companyService.getCompanyById(id);
            model.addAttribute("company", company);
            return "add-company";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/company/update/{id}")
    public String updateCompany(@PathVariable("id") Long id, @ModelAttribute("company") Company company,
                                Principal principal, Model model) {
        try {
            companyService.updateCompany(company, principal);
            return "redirect:/company/" + id;
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("company", company);
            return "company-form";
        }
    }

    @PostMapping("/company/delete/{id}")
    public String deleteCompany(@PathVariable("id") Long id, Principal principal, Model model) {
        try {
            companyService.deleteCompany(id, principal);
            return "redirect:/";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }
}