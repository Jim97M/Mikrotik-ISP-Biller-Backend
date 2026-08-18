package com.userservice.repository;


import java.util.List;
import java.util.Optional;

import com.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Corrected findByEmail method
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT s FROM User s WHERE s.email= :email")
    Optional<User> findOtpByEmail(@Param("email") String email);

    @Query("SELECT s FROM User s WHERE s.email= :email")
    User findByMail(@Param("email") String email);

    @Query("SELECT s FROM User s WHERE s.email= :email")
    List<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT s FROM User s WHERE s = :currentUser")
    User findAnotherUser(User currentUser);

    @Query("SELECT r FROM User u JOIN u.roles r WHERE u.id = :userId")
    Roles findRolesByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.userType = :userType")
    List<User> getUsersByUserType(@Param("userType") UserType userType);
//
//    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleKey = :roleKey")
//    List<User> getUsersByRoleName(@Param("roleKey") String roleKey);

    @Query("SELECT u.phoneNumber FROM User u WHERE u.phoneNumber =:phoneNumber")
    String getUserPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT s.email FROM User s WHERE s.email= :email")
    String getUserEmail(@Param("email") String email);

    List<User> findByTerritoryId(Long territoryId);

}

