package com.gabriel.service;

import com.gabriel.CvApp;
import com.gabriel.domain.PersistentToken;
import com.gabriel.domain.User;
import com.gabriel.repository.JobRepository;
import com.gabriel.repository.PersistentTokenRepository;
import com.gabriel.repository.UserRepository;
import com.gabriel.service.util.RandomUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvApp.class)
@Transactional
public class JobServiceIntTest {


    @Inject
    private JobRepository jobRepository;


}
