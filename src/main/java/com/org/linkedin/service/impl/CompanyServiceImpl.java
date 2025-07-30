package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Company;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ApplyJobRepository;
import com.org.linkedin.repository.CompanyRepository;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.CompanyService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplyJobRepository applyJobRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository,
                              UserRepository userRepository,
                              JobRepository jobRepository,
                              ApplyJobRepository applyJobRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applyJobRepository = applyJobRepository;
    }

    @Override
    public Company createCompany(Company company, Principal principal) throws CustomException {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in to create a company");
        }
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found"));
        company.setUser(user);
        return companyRepository.save(company);
    }

    @Override
    public Company getCompanyById(Long id) throws CustomException {
        return companyRepository.findById(id)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Company not found"));
    }

    @Override
    public Map<String, Object> getCompanyDetails(Long id, Principal principal) throws CustomException {
        Company company = getCompanyById(id);
        List<Job> jobs = jobRepository.findByCompanyId(id);

        for (Job job : jobs) {
            Long applicationsCount = applyJobRepository.countByJobId(job.getId());
            job.setApplicationsCount(applicationsCount);
        }

        Map<String, Object> modelAttributes = new HashMap<>();
        modelAttributes.put("company", company);
        modelAttributes.put("jobs", jobs);

        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found"));
            modelAttributes.put("loggedInUser", user);

            Set<Long> appliedJobIds = applyJobRepository.findAppliedJobIdsByUserUserId(user.getUserId());
            modelAttributes.put("appliedJobIds", appliedJobIds);
        }

        return modelAttributes;
    }

    @Override
    public Company updateCompany(Company company, Principal principal) throws CustomException {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in to edit a company");
        }
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found"));
        Company existingCompany = companyRepository.findById(company.getId())
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
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found"));
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Company not found"));

        if (!company.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("FORBIDDEN", "User not authorized to delete this company");
        }

        companyRepository.deleteById(id);
    }
}