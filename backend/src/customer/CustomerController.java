package customer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import enums.Role;
import exceptions.ErrorResponse;
import user.User;
import utils.DateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CustomerController implements HttpHandler {
    private final String customerPath;
    private final CustomerService customerService;
    private final Gson gson;

    public CustomerController(String customerPath, CustomerService customerService) {
        this.customerPath = customerPath;
        this.customerService = customerService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        final Optional<String[]> queryParam = Optional.of(path.split("/"));

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        switch (requestMethod) {
            case "GET" -> {
                // GET /customer
                if (path.matches(customerPath)) {
                    try {
                        response = gson.toJson(customerService.findAll());
                        statusCode = 200;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // GET /customer/{id}
                } else if (path.matches(customerPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        response = gson.toJson(customerService.findById(id));
                        statusCode = 200;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // GET /customer/{id}/birthday
                } else if (path.matches(customerPath + "/[0-9]+" + "/birthday")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var customer = customerService.findById(id);

                        response = gson.toJson(Map.of("isBirthday", DateUtils.isBirthday(customer.getBirthDate())));
                        statusCode = 200;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "POST" -> {
                // POST /customer
                if (path.matches(customerPath)) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        var customer = gson.fromJson(requestBody, Customer.class);
                        customerService.save(
                                new Customer(
                                        customer.getName(),
                                        customer.getPhoneNumber(),
                                        customer.getBirthDate(),
                                        customer.getCpf(),
                                        customer.getAddress(),
                                        new User(customer.getUser().getUsername(), customer.getUser().getPassword(), Role.CUSTOMER),
                                        customer.getCreatedBy(),
                                        customer.getAllergies() == null ? new HashSet<>() : customer.getAllergies()
                                )
                        );

                        statusCode = 201;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "PUT" -> {
                // PUT /customer/{id}
                if (path.matches(customerPath + "/[0-9]+")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var customer = customerService.findById(id);

                        var updatedCustomer = gson.fromJson(requestBody, Customer.class);
                        updatedCustomer = new Customer(
                                id,
                                updatedCustomer.getName(),
                                updatedCustomer.getPhoneNumber(),
                                updatedCustomer.getBirthDate(),
                                updatedCustomer.getCpf(),
                                updatedCustomer.getAddress(),
                                updatedCustomer.getUser(),
                                updatedCustomer.getUpdatedBy(),
                                updatedCustomer.getAllergies()
                        );

                        customer.setName(updatedCustomer.getName());
                        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
                        customer.setBirthDate(updatedCustomer.getBirthDate());
                        customer.setCpf(updatedCustomer.getCpf());
                        customer.setAddress(updatedCustomer.getAddress());
                        customer.setUser(updatedCustomer.getUser());
                        customer.setUpdatedAt(updatedCustomer.getUpdatedAt());
                        customer.setUpdatedBy(updatedCustomer.getUpdatedBy());
                        customer.setAllergies(updatedCustomer.getAllergies());

                        customerService.update(customer);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "DELETE" -> {
                // DELETE /customer/{id}
                if (path.matches(customerPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var customer = customerService.findById(id);

                        customerService.delete(customer);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            default -> {
                response = gson.toJson(new ErrorResponse("Invalid request method"));
                statusCode = 405;
            }
        }

        if (response.getBytes().length > 0) {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } else {
            exchange.sendResponseHeaders(statusCode, -1);
        }

        exchange.close();
    }
}
