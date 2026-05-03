package com.example.canteen.menu;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuService {

    private final MenuRepository repository;

    public MenuService(MenuRepository repository) {
        this.repository = repository;
    }

    public MenuItem add(MenuItem item) {
        return repository.save(item);
    }

    public List<MenuItem> getAll() {
        return repository.findAll();
    }

}
