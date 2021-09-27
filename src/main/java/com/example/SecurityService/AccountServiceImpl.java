package com.example.SecurityService;

import com.example.Entites.Role;
import com.example.Entites.Userr;
import com.example.Repositories.RoleRepository;
import com.example.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private UserRepository appUserRepository;
    private RoleRepository appRoleRepository;

    private PasswordEncoder passwordEncoder;

    public AccountServiceImpl(UserRepository appUserRepository, RoleRepository appRoleRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Userr addNewUser(Userr appUser) {
        String pw=appUser.getUserPassword();
        appUser.setUserPassword(passwordEncoder.encode(pw));
        return appUserRepository.save(appUser);
    }

    @Override
    public Role addNewRole(Role appRole) {
        return appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        Userr appUser = appUserRepository.findByUserLogin(username);
        Role appRole = appRoleRepository.findByRole(roleName);
        appUser.getAppRoles().add(appRole);
    }

    @Override
    public Userr loadUserByUsername(String username) {
        return appUserRepository.findByUserLogin(username);
    }

    @Override
    public List<Userr> listUsers() {
        return appUserRepository.findAll();
    }
}
