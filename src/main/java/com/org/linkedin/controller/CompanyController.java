package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Company;
import com.org.linkedin.service.impl.CompanyServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class CompanyController {

    private final CompanyServiceImpl companyService;

    public CompanyController(CompanyServiceImpl companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/company/add")
    public String showCompanyForm(Model model, Principal principal) {
        return companyService.addCompany(model, principal);
    }

    @PostMapping("/company/create")
    public String createCompany(@ModelAttribute("company") Company company, Principal principal, Model model) {
        return companyService.createCompany(company, principal, model);
    }

    @GetMapping("/company/{id}")
    public String getCompanyById(@PathVariable("id") Long id, Model model, Principal principal) {
        return companyService.showCompany(id, model, principal);
    }

    @GetMapping("/company/edit/{id}")
    public String showEditCompanyForm(@PathVariable("id") Long id, Model model, Principal principal) {
        return companyService.editCompany(id, model, principal);
    }

    @PostMapping("/company/update/{id}")
    public String updateCompany(@PathVariable("id") Long id, @ModelAttribute("company") Company company,
                                Principal principal, Model model) {
        return companyService.updateCompany(id, company, principal, model);
    }

    @PostMapping("/company/delete/{id}")
    public String deleteCompany(@PathVariable("id") Long id, Principal principal, Model model) {
        return companyService.deleteCompany(id, principal, model);
    }
}