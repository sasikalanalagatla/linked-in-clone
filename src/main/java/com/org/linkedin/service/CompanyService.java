package com.org.linkedin.service;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Company;

import java.security.Principal;
import java.util.List;

public interface CompanyService {
    Company createCompany(Company company, Principal principal) throws CustomException;
    Company getCompanyById(Long id) throws CustomException;
    List<Company> getAllCompanies();
    Company updateCompany(Company company, Principal principal) throws CustomException;
    void deleteCompany(Long id, Principal principal) throws CustomException;
}