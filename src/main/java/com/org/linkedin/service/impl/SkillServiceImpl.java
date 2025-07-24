package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Skill;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.SkillRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.SkillService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillServiceImpl(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveSkillForUser(Long userId, Skill skill) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (skill == null) {
            throw new CustomException("INVALID_SKILL", "Skill data cannot be null");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "User with ID " + userId + " not found");
        }

        Skill savedSkill;
        if (skill.getSkillId() != null) {
            savedSkill = skillRepository.findById(skill.getSkillId())
                    .orElseThrow(() -> new CustomException("SKILL_NOT_FOUND", "Skill with ID " + skill.getSkillId() + " not found"));
            savedSkill.setSkillName(skill.getSkillName());
        } else {
            savedSkill = new Skill();
            savedSkill.setSkillName(skill.getSkillName());
        }

        savedSkill = skillRepository.save(savedSkill);

        if (!user.get().getSkills().contains(savedSkill)) {
            user.get().getSkills().add(savedSkill);
            userRepository.save(user.get());
        }

    }

    @Override
    public void deleteUserSkill(Long userId, Long skillId) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (skillId == null) {
            throw new CustomException("INVALID_SKILL_ID", "Skill ID cannot be null");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "User with ID " + userId + " not found");
        }

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new CustomException("SKILL_NOT_FOUND", "Skill with ID " + skillId + " not found"));

        user.get().getSkills().remove(skill);
        userRepository.save(user.get());
    }

    @Override
    public Skill getSkillById(Long id) {
        if (id == null) {
            throw new CustomException("INVALID_SKILL_ID", "Skill ID cannot be null");
        }

        return skillRepository.findById(id)
                .orElseThrow(() -> new CustomException("SKILL_NOT_FOUND", "Skill with ID " + id + " not found"));
    }
}