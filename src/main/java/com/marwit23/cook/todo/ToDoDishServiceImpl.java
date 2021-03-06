package com.marwit23.cook.todo;

import com.marwit23.cook._exception.EntityNotFoundException;
import com.marwit23.cook.dish.DishIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToDoDishServiceImpl implements ToDoDishService {

    private ToDoDishRepository toDoDishRepository;

    @Autowired
    public ToDoDishServiceImpl(ToDoDishRepository toDoDishRepository) {
        this.toDoDishRepository = toDoDishRepository;
    }

    @Override
    public List<ToDoDish> findAll() {
        return toDoDishRepository.findAll();
    }

    @Override
    public List<ToDoDish> findAllWithParams(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<ToDoDish> toDoDishPage = toDoDishRepository.findAll(pageable);
        return toDoDishPage.getContent();
    }

    @Override
    public ToDoDish findById(Long toDoDishId) {
        Optional<ToDoDish> result = toDoDishRepository.findById(toDoDishId);
        ToDoDish theToDoDish;
        if (result.isPresent()) theToDoDish = result.get();
        else throw new EntityNotFoundException("to-do dish", toDoDishId.toString());
        return theToDoDish;
    }

    // * quantity validation
    @Override
    public void save(ToDoDish toDoDish) {
        for (DishIngredient dishIngredient : toDoDish.getDish().getDishIngredients()) {
            if ((dishIngredient.getIngredient().getAvailableQuantity() -
                    (dishIngredient.getQuantityGrams() * toDoDish.getDishQuantity())
                    ) < 0) {
                throw new RuntimeException
                        ("Cannot create this order. Ingredient: " + dishIngredient.getIngredient().getIngredientName() + " doesn't have sufficient quantity.");
            } else toDoDishRepository.save(toDoDish);
        }
    }

    // * quantity validation
    @Override
    public void update(ToDoDish toDoDish, Long toDoDishId) {
        Optional<ToDoDish> result = toDoDishRepository.findById(toDoDishId);
        ToDoDish oldToDoDish = null;
        if(result.isPresent()) oldToDoDish = result.get();
        int difference = toDoDish.getDishQuantity() - oldToDoDish.getDishQuantity();
        for (DishIngredient dishIngredient : toDoDish.getDish().getDishIngredients()) {
            if ((dishIngredient.getIngredient().getAvailableQuantity() - difference * dishIngredient.getQuantityGrams() ) < 0) {
                throw new RuntimeException
                        ("Cannot create this order. Ingredient: " + dishIngredient.getIngredient().getIngredientName() + " doesn't have sufficient quantity.");
            } else toDoDishRepository.save(toDoDish);
        }
    }

    @Override
    public void deleteById(Long toDoDishId) {
        toDoDishRepository.deleteById(toDoDishId);
    }
}
