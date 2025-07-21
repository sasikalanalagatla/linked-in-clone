package com.org.linkedin.service.impl;

import com.org.linkedin.model.Skill;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.SkillRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.SkillService;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillServiceImpl(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Skill saveSkillForUser(Long userId, Skill skill) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        Skill savedSkill;

        if (skill.getSkillId() != null) {
            savedSkill = skillRepository.findById(skill.getSkillId()).orElse(null);
            if (savedSkill != null) {
                savedSkill.setSkillName(skill.getSkillName());
            } else {
                savedSkill = new Skill();
                savedSkill.setSkillName(skill.getSkillName());
            }
        } else {
            savedSkill = new Skill();
            savedSkill.setSkillName(skill.getSkillName());
        }

        savedSkill = skillRepository.save(savedSkill);

        if (!user.getSkills().contains(savedSkill)) {
            user.getSkills().add(savedSkill);
            userRepository.save(user);
        }

        return savedSkill;
    }


    @Override
    public void deleteUserSkill(Long userId, Long skillId) {
        User user = userRepository.findById(userId).orElse(null);
        Skill skill = skillRepository.findById(skillId).orElse(null);

        if (user != null && skill != null) {
            user.getSkills().remove(skill);
            userRepository.save(user);
        }
    }

    @Override
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElse(null);
    }
}
