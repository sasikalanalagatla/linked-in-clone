package com.org.linkedin.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CongirutaionClass {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dvll6zsqw",
                "api_key", "585267761728879",
                "api_secret", "uT0BhvkpxsoPjDe4SujuJmw44oY"));
    }
}
