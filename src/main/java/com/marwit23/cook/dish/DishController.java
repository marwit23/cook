package com.marwit23.cook.dish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private DishService dishService;

    @Autowired
    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<Dish> findAll() {
        return dishService.findAll();
    }

    @GetMapping("/{dishId}")
    public Dish findById(@PathVariable Long dishId) {
        Dish theDish = dishService.findById(dishId);

        if(theDish == null){
            throw new RuntimeException("Dish id not found -" +dishId);
        }
        return theDish;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMiN')")
    public Dish addDish(@RequestBody Dish theDish) {
        theDish.setDishId(0);
        dishService.save(theDish);
        return theDish;
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Dish updateDish(@RequestBody Dish theDish) {
        dishService.save(theDish);
        return theDish;
    }

    @DeleteMapping("/{dishId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteById(@PathVariable Long dishId) {
        Dish tempDish = dishService.findById(dishId);
        if(tempDish == null){
            throw new RuntimeException("Dish id not found -" + dishId);
        }
        dishService.deleteById(dishId);
        return "Deleted dish id - " + dishId;
    }
}
