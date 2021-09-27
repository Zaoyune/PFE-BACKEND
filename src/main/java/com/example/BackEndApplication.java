package com.example;

import com.example.Entites.*;
import com.example.GenerateCSV.GeneCsv;
import com.example.Repositories.ArticleRepository;
import com.example.Repositories.RatingRepository;
import com.example.SecurityService.AccountService;
import com.example.Service.initData;
import com.example.Service.initDataImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class BackEndApplication implements CommandLineRunner {
    @Autowired
    initDataImp i;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    GeneCsv geneCsv;
    List<String[]> dataLines = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
    }



    @Override
    public void run(String... args) throws Exception {


        System.out.println(i.initCategories());
        System.out.println(i.initMarque());
        System.out.println(i.initArticles());
        System.out.println(i.InitUser());
        System.out.println(i.initCommands());
        System.out.println(i.initRating());
        GeneCsv geneCsv = new GeneCsv();
        List<Rating> ratings = ratingRepository.findAll();

        dataLines.add(new String[]{"id", "id_user", "id_article", "rating","Article"});
        ratings.forEach(rating -> {
            dataLines.add(new String[]
                    {rating.getId() + "", rating.getUserr().getIdU() + "", rating.getArticle().getId() + "", rating.getStare() + "",rating.getArticle().getArtdesignation()});
        });
        geneCsv.givenDataArray_whenConvertToCSV_thenOutputCreated(dataLines);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    CommandLineRunner start(AccountService accountService){
        return args -> {
            accountService.addNewRole(new Role(null,"USER","??????"));
            accountService.addNewRole(new Role(null,"ADMIN","??????"));

            accountService.addNewUser(new Userr(null, "admin@gmail.com", "admin", "1234",  "male", "Benharouga" , "Hassan", "164658464896", "uzgsezgzegr", new ArrayList<>(), null, null));
            accountService.addNewUser(new Userr(null, "user1@gmail.com", "user5", "1234",  "male", "Bendahmane" , "Aymane", "164658464896", "ergzrgrez", new ArrayList<>(), null, null));

            accountService.addRoleToUser("user5","USER");
            accountService.addRoleToUser("admin","ADMIN");
            accountService.addRoleToUser("admin","USER");
        };
    }
}
