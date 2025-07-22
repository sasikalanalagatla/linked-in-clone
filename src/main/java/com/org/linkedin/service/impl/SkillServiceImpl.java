package com.org.linkedin.service.impl;

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
    public Skill saveSkillForUser(Long userId, Skill skill) {
        Optional<User> user = userRepository.findById(userId);
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

        if (!user.get().getSkills().contains(savedSkill)) {
            user.get().getSkills().add(savedSkill);
            userRepository.save(user.get());
        }

        return savedSkill;
    }


    @Override
    public void deleteUserSkill(Long userId, Long skillId) {
        Optional<User> user = userRepository.findById(userId);
        Skill skill = skillRepository.findById(skillId).orElse(null);

        if (user != null && skill != null) {
            user.get().getSkills().remove(skill);
            userRepository.save(user.get());
        }
    }

    @Override
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElse(null);
    }
}
