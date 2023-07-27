package utils;

public class Constants {
    // database
    public static final String DATABASE_NAME = "restaurant";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "postgres";
    public static final int PORT = 5432;
    public static final String DRIVER = "org.postgresql.Driver";
    public static final String URL = "jdbc:postgresql://localhost:" + PORT + "/" + DATABASE_NAME;

    // paths
    public static final String USER_PATH = "/user";
    public static final String CUSTOMER_PATH = "/customer";
    public static final String EMPLOYEE_PATH = "/employee";
    public static final String PRODUCT_PATH = "/product";
    public static final String MENU_PATH = "/menu";
    public static final String TABLE_PATH = "/table";
    public static final String RESERVED_TABLE_PATH = "/reserved-table";
    public static final String ORDER_PATH = "/order";
    public static final String BILL_PATH = "/bill";

    // messages
    public static final String USER_NOT_FOUND = "Usuário não encontrado";
    public static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado";
    public static final String EMPLOYEE_NOT_FOUND = "Funcionário não encontrado";
    public static final String PRODUCT_NOT_FOUND = "Produto não encontrado";
    public static final String MENU_NOT_FOUND = "Cardápio não encontrado";
    public static final String TABLE_NOT_FOUND = "Mesa não encontrada";
    public static final String RESERVED_TABLE_NOT_FOUND = "Mesa reservada não encontrada";
    public static final String ORDER_NOT_FOUND = "Pedido não encontrado";
    public static final String BILL_NOT_FOUND = "Comanda não encontrada";
    public static final String TOKEN_NOT_FOUND = "Token não encontrado";
    public static final String INVALID_REQUEST_METHOD = "Método de requisição inválido";
    public static final String INVALID_REQUEST_ENDPOINT = "Endpoint inválido";
    public static final String SUCCESS_MESSAGE = "Operação realizada com sucesso";
    public static final String USERNAME_ALREADY_EXISTS = "Username já cadastrado";

    public static final int MINIMUM_AGE = 18;
}