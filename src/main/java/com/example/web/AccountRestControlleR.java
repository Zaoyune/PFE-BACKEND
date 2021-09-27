package com.example.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Entites.Article;
import com.example.Entites.Role;
import com.example.Entites.Userr;
import com.example.JWTUtil;
import com.example.Repositories.ArticleRepository;
import com.example.Repositories.UserRepository;
import com.example.SecurityService.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
public class AccountRestControlleR {
    private AccountService accountService;
    //private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository appUserRepository;
    @Autowired
    private ArticleRepository rarticleRepository;

    public AccountRestControlleR(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping(path = "/users")
    //@PostAuthorize("hasAuthority('USER')")
    @PreAuthorize("hasAuthority('USER')")
    public List<Userr> appUsers(){
        return accountService.listUsers();
    }

    /********************************************/
    @GetMapping(path = "/userss")
    @PreAuthorize("hasAuthority('USER')")
    public List<Userr> appUserss(){
        return appUserRepository.findAll();
    }
    @PostMapping(path="/AddUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Userr AddUser(@RequestBody Userr a){
        return appUserRepository.save(a);
    }
    /********************************************/

    /*******************TEST*************************/
    @GetMapping(path = "/lesarticles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Article> lesarticles(){
        return rarticleRepository.findAll();
    }
    @PostMapping(path = "/AjouterArticles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Article save(@RequestBody Article a){
        return rarticleRepository.save(a);
    }

    /************************************************/

    @PostMapping(path = "/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    //@PostAuthorize("hasAuthority('ADMIN')")
    public Userr saveUser(@RequestBody Userr appUser){
        return accountService.addNewUser(appUser);
    }
    @PostMapping(path = "/roles")
    //@PostAuthorize("hasAuthority('ADMIN')")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Role saveRole(@RequestBody Role appRole){
        return accountService.addNewRole(appRole);
    }
    @PostMapping(path = "/AddRoleToUser")
    public void AddRoleToUser(@RequestBody RoleUserForm roleUserForm){
         accountService.addRoleToUser(roleUserForm.getUserName(),roleUserForm.getRoleName());
    }
    @GetMapping(path="/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)throws Exception{{
        String authToken=request.getHeader(JWTUtil.AUTH_HEADER);

        //on cherche si ce refresh-token existe deja puis on check si son access-token n'est pas dans la black list après on crée un nouveau access-token
        if(authToken!=null && authToken.startsWith(JWTUtil.PREFIX))
            try {
                String jwtRefreshToken = authToken.substring(7);
                Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();//créer cet algorithm ou bien ce token
                DecodedJWT decodedJWT = jwtVerifier.verify(jwtRefreshToken);
                String username=decodedJWT.getSubject();
                Userr appUser=accountService.loadUserByUsername(username);
                String NewJwtAccessToken= JWT.create()
                        .withSubject(appUser.getUserLogin())
                        .withExpiresAt(new Date(System.currentTimeMillis()+JWTUtil.EXPIRE_ACCESS_TOKEN))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",appUser.getAppRoles().stream().map(r->r.getRole()).collect(Collectors.toList()))
                        .sign(algorithm);


                Map<String,String> idToken=new HashMap<>();
                idToken.put("access-token",NewJwtAccessToken);
                idToken.put("refresh-token",jwtRefreshToken);
                //response.setHeader("Authorization",jwtAccessToken);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(),idToken);
                //envoyer l'objet sous format json au coeur de la reponse https
            }catch(Exception e) {
                throw e;
            }
        else{
            throw new RuntimeException("Refresh-Token required!!!!!");
        }
    }
    }
    @GetMapping(path = "/profile")
    public Userr profile(Principal principal){
        return accountService.loadUserByUsername(principal.getName());
    }

   /* @PostMapping(value = {"/authenticate","/login"})
    public Object loginUser(@RequestParam String username, @RequestParam String password)
    {
        Authentication authentication=authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(username, password));

        return authentication;
    }*/
}

@Data
class RoleUserForm{
    private String userName;
    private String roleName;
}
