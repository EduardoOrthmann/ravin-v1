package order;

import bill.BillService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import customer.CustomerService;
import employee.EmployeeService;
import enums.OrderStatus;
import enums.Role;
import exceptions.ErrorResponse;
import exceptions.UnauthorizedRequestException;
import user.UserService;
import utils.APIUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class OrderController implements HttpHandler {
    private final String orderPath;
    private final OrderService orderService;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final BillService billService;
    private final Gson gson;

    public OrderController(String orderPath, OrderService orderService, UserService userService, EmployeeService employeeService, CustomerService customerService, BillService billService) {
        this.orderPath = orderPath;
        this.orderService = orderService;
        this.userService = userService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.billService = billService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "PATCH" -> patchHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, gson.toJson(new ErrorResponse("Invalid request method")));
        }
    }

    private void patchHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // PATCH /order/{id}/update-status/{status}
        if (path.matches(orderPath + "/[0-9]+/update-status/[a-zA-Z]+")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                var orderId = Integer.parseInt(splittedPath.get()[2]);
                var orderStatus = OrderStatus.valueOf(splittedPath.get()[4]);

                var user = userService.findByToken(headerToken);
                var order = orderService.findById(orderId);

                if (order.getEmployeeId() == null) {
                    throw new UnauthorizedRequestException("O pedido ainda não foi aceito por nenhum funcionário");
                }

                var assignedEmployeeUserId = employeeService.findById(order.getEmployeeId()).getUser().getId();
                if (assignedEmployeeUserId != user.getId()) {
                    throw new UnauthorizedRequestException();
                }

                orderService.updateStatus(order, orderStatus, user.getId());

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // PATCH /order/{id}/take-order
        else if (path.matches(orderPath + "/[0-9]+/take-order")) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.EMPLOYEE, Role.MANAGER, Role.ADMIN));

                var orderId = Integer.parseInt(splittedPath.get()[2]);
                var order = orderService.findById(orderId);
                var employee = employeeService.findByUserId(user.getId());

                orderService.takeOrder(order, employee);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // // PATCH /order/{id}/cancel-order
        else if (path.matches(orderPath + "/[0-9]+/cancel-order")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                var id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var order = orderService.findById(id);
                var associatedBill = billService.findByOrderId(order.getId());
                var associatedCustomer = customerService.findById(associatedBill.getCustomerId());

                if (associatedCustomer.getUser().getId() != user.getId()) {
                    throw new UnauthorizedRequestException();
                }

                orderService.cancelOrder(order, user.getId());

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        } else {
            response = gson.toJson(new ErrorResponse("Invalid endpoint"));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }
}
