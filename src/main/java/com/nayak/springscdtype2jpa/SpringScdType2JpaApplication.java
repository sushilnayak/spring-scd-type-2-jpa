package com.nayak.springscdtype2jpa;

import com.nayak.springscdtype2jpa.repository.SCDType2RepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = SCDType2RepositoryImpl.class)
public class SpringScdType2JpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringScdType2JpaApplication.class, args);
    }

}
