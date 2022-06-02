package com.gsu.semantic.service;

import com.gsu.knowledgebase.util.Constants;
import com.gsu.semantic.model.Role;
import com.gsu.semantic.model.User;
import com.gsu.semantic.repository.SemanticGraphDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by cnytync on 30/11/14.
 */
@Service("sgUserService")
public class SgUserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(SgUserService.class);

    private SemanticGraphDao semanticGraphDao;

    public SgUserService(){

    }

    public SgUserService(SemanticGraphDao semanticGraphDao) {
        this.semanticGraphDao = semanticGraphDao;
    }

    public UserDetails loadUserByUsername(String login) throws AuthenticationException {
        logger.info("UserDetails Database Service : " + login);

        // check user exists in database
        User user = semanticGraphDao.findUserByUsername(login);
        if (user == null) {
            logger.warn("User({}) does not exist in system", login);
            throw new UsernameNotFoundException("There is no user with this username.");
        }

        boolean containsLoginRole = checkLoginRole(user);

        if (!containsLoginRole) {
            throw new UsernameNotFoundException("Access denied.");
        }

        if ((user.getStatus() == null || user.getStatus() == 0)) {
            throw new UsernameNotFoundException("User is not confirmed");
        }

        //boolean enabled = user.getStatus() == AccountStatus.ACTIVE;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        if (user.getLoginTryCount() != null && user.getLoginTryCount() >= 3) {
            accountNonLocked = false;
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true, accountNonExpired,
                credentialsNonExpired, accountNonLocked, this.getAuthorities(user.getRoleId()));
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roleList) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Role role : roleList) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Long roleId) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        authorities.add(new SimpleGrantedAuthority(Constants.ROLE_NAME(roleId.intValue())));

        return authorities;
    }

    private boolean checkLoginRole(User user) {
        if (user.getRoleId() == 0) {
            return false;
        }

        if (user.getRoleId() == Constants.ROLE_ADMIN
                || user.getRoleId() == Constants.ROLE_MODERATOR
                || user.getRoleId() == Constants.ROLE_USER) {
            return true;
        } else {
            return false;
        }
    }
}
