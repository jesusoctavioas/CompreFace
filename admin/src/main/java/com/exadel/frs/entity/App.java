/*
 * Copyright (c) 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.exadel.frs.entity;

import static com.exadel.frs.enums.AppRole.OWNER;
import com.exadel.frs.enums.AppRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"guid"})
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_id_seq")
    @SequenceGenerator(name = "app_id_seq", sequenceName = "app_id_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String guid;
    private String apiKey;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAppRole> userAppRoles = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppModel> appModelAccess = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Model> models = new ArrayList<>();

    public Optional<UserAppRole> getOwner() {
        return userAppRoles
                .stream()
                .filter(userAppRole -> OWNER.equals(userAppRole.getRole()))
                .findFirst();
    }

    public Optional<UserAppRole> getUserAppRole(Long userId) {
        return userAppRoles
                .stream()
                .filter(userAppRole -> userAppRole.getId().getUserId().equals(userId))
                .findFirst();
    }

    public void addUserAppRole(User user, AppRole role) {
        UserAppRole userAppRole = new UserAppRole(user, this, role);
        userAppRoles.add(userAppRole);
        user.getUserAppRoles().add(userAppRole);
    }

    public void deleteUserAppRole(final String userGuid) {
        val optional = userAppRoles.stream()
                                   .filter(userApp -> userApp.getUser().getGuid().equals(userGuid))
                                   .findFirst();

        if (optional.isPresent()) {
            val userAppRole = optional.get();
            userAppRole.getUser().getUserAppRoles().remove(userAppRole);
            userAppRoles.remove(userAppRole);
        }
    }
}