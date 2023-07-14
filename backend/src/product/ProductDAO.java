package product;

import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ProductDAO implements Crud<Product> {
    private final List<Product> productList;

    public ProductDAO() {
        this.productList = new ArrayList<>();
    }

    @Override
    public Optional<Product> findById(int id) {
        return productList.stream()
                .filter(customer -> customer.getId() == id)
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return this.productList;
    }

    @Override
    public void save(Product entity) {
        this.productList.add(entity);
    }

    @Override
    public void update(Product entity) {
        var product = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Produto não encontrado"));
        this.productList.set(productList.indexOf(product), entity);
    }

    @Override
    public void delete(Product entity) {
        this.productList.remove(entity);
    }
}