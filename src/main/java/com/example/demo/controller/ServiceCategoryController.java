package com.example.demo.controller;

import com.example.demo.entity.ServiceCategory;
import com.example.demo.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final ServiceCategoryRepository repository;

    // Lấy tất cả danh mục (user + admin đều xem được)
    @GetMapping
    public List<ServiceCategory> getAll() {
        return repository.findAll();
    }

    // Thêm danh mục (admin)
    @PostMapping
    public ServiceCategory create(@RequestBody ServiceCategory category) {
        return repository.save(category);
    }

    // Sửa danh mục (admin)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceCategory> update(@PathVariable Long id,
                                                   @RequestBody ServiceCategory updated) {
        return repository.findById(id).map(cat -> {
            cat.setName(updated.getName());
            cat.setDescription(updated.getDescription());
            cat.setMinPrice(updated.getMinPrice());
            cat.setMaxPrice(updated.getMaxPrice());
            cat.setUnit(updated.getUnit());
            return ResponseEntity.ok(repository.save(cat));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Xóa danh mục (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok("Đã xóa!");
    }
}