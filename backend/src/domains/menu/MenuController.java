package domains.menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import domains.product.ProductService;
import domains.user.UserService;
import exceptions.UnauthorizedRequestException;
import utils.APIUtils;
import utils.Constants;
import utils.CustomResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuController implements HttpHandler {
    private final String menuPath;
    private final MenuService menuService;
    private final ProductService productService;
    private final UserService userService;
    private final Gson gson;

    public MenuController(String menuPath, MenuService menuService, ProductService productService, UserService userService) {
        this.menuPath = menuPath;
        this.menuService = menuService;
        this.productService = productService;
        this.userService = userService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> getHandler(exchange);
            case "POST" -> postHandler(exchange);
            case "PUT" -> putHandler(exchange);
            case "DELETE" -> deleteHandler(exchange);
            case "PATCH" -> patchHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_METHOD)));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));

        // GET /menu (all menus)
        if (path.matches(menuPath)) {
            try {
                response = gson.toJson(menuService.findAll());
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /menu/{id} (menu by id)
        else if (path.matches(menuPath + "/[0-9]+")) {
            try {
                int id = Integer.parseInt(splittedPath.get()[2]);
                response = gson.toJson(menuService.findById(id));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
            response = gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_ENDPOINT));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));
        String requestBody = new String(exchange.getRequestBody().readAllBytes());

        // POST /menu (create menu)
        if (path.matches(menuPath)) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null));

                var menu = gson.fromJson(requestBody, Menu.class);
                var products = menu.getProducts().stream()
                        .map(product -> productService.findById(product.getId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                menu.setProducts(products);
                menu.setCreatedBy(user.getId());

                response = gson.toJson(menuService.save(menu));
                statusCode = HttpURLConnection.HTTP_CREATED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
            response = gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_ENDPOINT));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }

    private void putHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));
        String requestBody = new String(exchange.getRequestBody().readAllBytes());

        // PUT /menu/{id} (update menu)
        if (path.matches(menuPath + "/[0-9]+")) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var menu = gson.fromJson(requestBody, Menu.class);
                menu.setId(id);
                menu.setUpdatedBy(user.getId());

                menuService.update(menu);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
            response = gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_ENDPOINT));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }

    private void deleteHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // DELETE /menu/{id}
        if (path.matches(menuPath + "/[0-9]+")) {
            try {
                userService.authorizeUserByRole(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);


                menuService.delete(id);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse("Invalid id"));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
            response = gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_ENDPOINT));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }

    private void patchHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // PATCH /menu/{id}/add-product/{productId} (add product to menu)
        if (path.matches(menuPath + "/[0-9]+/add-product/[0-9]+")) {
            try {
                userService.authorizeUserByRole(tokenFromHeaders.orElse(null));

                int menuId = Integer.parseInt(splittedPath.get()[2]);
                int productId = Integer.parseInt(splittedPath.get()[4]);

                var menu = menuService.findById(menuId);
                var product = productService.findById(productId);

                menuService.addProduct(menu, product);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // PATCH /menu/remove-product/{productId} (remove product from menu)
        else if (path.matches(menuPath + "/remove-product/[0-9]+")) {
            try {
                userService.authorizeUserByRole(tokenFromHeaders.orElse(null));

                int productId = Integer.parseInt(splittedPath.get()[3]);
                var product = productService.findById(productId);

                menuService.removeProduct(product);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
            response = gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_ENDPOINT));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }
}
