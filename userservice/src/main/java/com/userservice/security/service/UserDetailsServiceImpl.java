package com.userservice.security.service;


import com.userservice.entity.RolePermissions;
import com.userservice.entity.User;
import com.userservice.repository.RolePermissionsRepository;
import com.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RolePermissionsRepository rolePermissionsRepository;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PERMISSIONS_CACHE_KEY = "user:permissions:";

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    public UserDetailsServiceImpl(UserRepository userRepository, RolePermissionsRepository rolePermissionsRepository) {
        this.userRepository = userRepository;
        this.rolePermissionsRepository = rolePermissionsRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        String cacheKey = PERMISSIONS_CACHE_KEY + email;
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        Set<String> cachedPermissions = new HashSet<>();

        if (cachedData instanceof Set<?>) {
            cachedPermissions = (Set<String>) cachedData;
        } else if (cachedData instanceof List<?>) {
            cachedPermissions = new HashSet<>((List<String>) cachedData);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        logger.info("User ID: {}, Email: {}, Roles size: {}", user.getId(), user.getEmail(),
                user.getRoles().size());



        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .peek(role -> logger.info("Processing role: {} (ID: {})", role.getRoleName(), role.getId()))
                .flatMap(role -> {
                    List<RolePermissions> perms = rolePermissionsRepository.findByUserRoles(role);
                    logger.info("Permissions for role {}: {}", role.getRoleName(),
                            perms.stream().map(p -> p.getAvailableServices().getModuleName() + ":" +
                                    p.getServicePermissions().getPermissionName()).collect(Collectors.toList()));
                    return perms.stream();
                })
                .map(rp -> {
                    String authority = rp.getAvailableServices().getModuleName() + ":" +
                            rp.getServicePermissions().getPermissionName();
                    logger.info("Authority created: {}", authority);
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toSet());
        logger.info("Final authorities for user {}: {}", email, authorities);


        Set<String> permissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        redisTemplate.opsForValue().set(cacheKey, permissions, 10, TimeUnit.MINUTES);
        logger.info("📦 Cached permissions for user {}: {}", email, permissions);

        return UserDetailsImpl.build(user, authorities);
    }
}
