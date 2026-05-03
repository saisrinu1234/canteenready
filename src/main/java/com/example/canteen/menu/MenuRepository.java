package com.example.canteen.menu;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<MenuItem, UUID> {

    boolean existsByName(String name);
}