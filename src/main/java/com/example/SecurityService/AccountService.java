package com.example.SecurityService;



import com.example.Entites.Role;
import com.example.Entites.Userr;

import java.util.List;

public interface AccountService {
    Userr addNewUser(Userr appUser);
    Role addNewRole(Role appRole);
    void addRoleToUser(String userLogin,String role);
    Userr loadUserByUsername(String userLogin);
    List<Userr> listUsers();
}
