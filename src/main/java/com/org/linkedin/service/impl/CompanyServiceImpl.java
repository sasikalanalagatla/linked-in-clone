package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Company;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.CompanyRepository;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.ApplyJobRepository;
import com.org.linkedin.service.CompanyService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final JobRepository jobRepository;
    private final ApplyJobRepository applyJobRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository,
                              UserService userService,
                              JobRepository jobRepository,
                              ApplyJobRepository applyJobRepository) {
        this.companyRepository = companyRepository;
        this.userService = userService;
        this.jobRepository = jobRepository;
        this.applyJobRepository = applyJobRepository;
    }

    @Override
    public Company createCompany(Company company, Principal principal) throws CustomException {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in to create a company");
        }
        if (company == null) {
            throw new CustomException("INVALID_COMPANY", "Company cannot be null");
        }
        User user = userService.findByEmail(principal.getName());
        company.setUser(user);
        return companyRepository.save(company);
    }

    @Override
    public Company getCompanyById(Long id) throws CustomException {
        if (id == null) {
            throw new CustomException("INVALID_ID", "Company ID cannot be null");
        }
        return companyRepository.findById(id)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Company not found"));
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Company updateCompany(Long id, Company company, Principal principal) throws CustomException {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in to edit a company");
        }
        if (id == null) {
            throw new CustomException("INVALID_ID", "Company ID cannot be null");
        }
        if (company == null) {
            throw new CustomException("INVALID_COMPANY", "Company cannot be null");
        }
        User user = userService.findByEmail(principal.getName());
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Company not found"));

        if (!existingCompany.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("FORBIDDEN", "User not authorized to edit this company");
        }

        existingCompany.setName(company.getName());
        existingCompany.setDescription(company.getDescription());
        existingCompany.setLocation(company.getLocation());
        existingCompany.setWebsite(company.getWebsite());
        existingCompany.setLogoUrl(company.getLogoUrl());
        return companyRepository.save(existingCompany);
    }

    @Override
    public void deleteCompany(Long id, Principal principal) throws CustomException {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in to delete a company");
        }
        if (id == null) {
            throw new CustomException("INVALID_ID", "Company ID cannot be null");
        }
        User user = userService.findByEmail(principal.getName());
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Company not found"));

        if (!company.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("FORBIDDEN", "User not authorized to delete this company");
        }

        companyRepository.deleteById(id);
    }
}