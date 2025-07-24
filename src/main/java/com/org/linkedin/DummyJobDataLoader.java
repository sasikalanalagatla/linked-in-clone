/*
package com.org.linkedin;

import com.org.linkedin.enums.JobType;
import com.org.linkedin.enums.WorkPlaceType;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.Random;

@Configuration
public class DummyJobDataLoader {

    @Bean
    public CommandLineRunner loadJobs(JobRepository jobRepository, UserRepository userRepository) {
        return args -> {
            Optional<User> user = userRepository.findByEmail("sasikalanalagatla@gmail.com"); // assuming Sasikala exists

            if (user == null) {
                System.out.println("User 'sasikala@example.com' not found. Skipping job creation.");
                return;
            }

            Random random = new Random();
            if (userRepository.count() <10) {

                for (int i = 1; i <= 1000; i++) {
                    Job job = new Job();
                    job.setJobTitle("Software Engineer " + i);
                    job.setCompany("Tech Company " + (i % 50));
                    job.setJobDescription("This is a dummy description for job post " + i);
                    job.setJobLocation("City " + (i % 10));
                    job.setJobTypes(i % 2 == 0 ? JobType.FULL_TIME : JobType.PART_TIME);
                    job.setJobWorkPlaceTypes(i % 3 == 0 ? WorkPlaceType.HYBRID : (i % 3 == 1 ? WorkPlaceType.REMOTE : WorkPlaceType.ONSITE));
                    job.setRecruiterEmail("recruiter" + i + "@example.com");
                    job.setApplicationsCount((long) random.nextInt(100));
                    job.setExperienceLevel(i % 3 == 0 ? "Entry Level" : (i % 3 == 1 ? "Mid Level" : "Senior Level"));
                    job.setJobPostEdited(false);
                    job.setCompanyId((long) (i % 100));
               job.setUser(user.get());

                    jobRepository.save(job);
                }

                System.out.println("✅ 1000 dummy job posts created for user Sasikala.");
           }
        };
    }
}
*/
