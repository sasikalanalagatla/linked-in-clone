package com.org.linkedin.service;

import com.org.linkedin.model.Skill;

public interface SkillService {
    Skill saveSkillForUser(Long userId, Skill skill);
    void deleteUserSkill(Long userId, Long skillId);
    Skill getSkillById(Long id);
}
