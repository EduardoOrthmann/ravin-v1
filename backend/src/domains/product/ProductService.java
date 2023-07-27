package domains.product;

import utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product findById(int id) {
        return productDAO.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.PRODUCT_NOT_FOUND));
    }

    public List<Product> findAll() {
        return productDAO.findAll();
    }

    public Product save(Product entity) {
        return productDAO.save(entity);
    }

    public void update(Product entity) {
        productDAO.update(entity);
    }

    public void delete(Product entity) {
        productDAO.delete(entity);
    }
}