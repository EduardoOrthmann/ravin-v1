package customer;

import templates.AddressForm;
import templates.LocalDateField;
import employee.Employee;
import employee.EmployeeService;
import enums.Allergy;
import utils.ObjectUtils;

import javax.swing.*;

public class CustomerView {
    private final CustomerService customerService;
    private final EmployeeService employeeService;

    public CustomerView(CustomerService customerService, EmployeeService employeeService) {
        this.customerService = customerService;
        this.employeeService = employeeService;
    }

    public void view() {
        String[] customerOptions = {"findById", "findAll", "save", "update", "delete", "addAllergy", "deleteAllergy"};

        String selectedCustomerOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do Cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                customerOptions,
                null
        );

        switch (selectedCustomerOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(customerService.findById(id));
            }

            case "findAll" -> System.out.println(customerService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um funcionário antes de criar um cliente");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira um nome:");
                var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:");
                var birthDate = LocalDateField.showInputLocalDateDialog("Insira a data do nascimento:");
                var cpf = JOptionPane.showInputDialog("Insira o cpf:");
                var address = AddressForm.showInputLocalDateDialog("Insira o endereço");
                var selectedCreatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está criando esse cliente?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var createdBy = employeeService.findById((int) selectedCreatedBy).getRole();

                customerService.save(
                        new Customer(
                                name,
                                phoneNumber,
                                birthDate,
                                cpf,
                                address,
                                createdBy
                        )
                );
            }

            case "update" -> {
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (customers.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um cliente antes de atualizar um cliente");
                    break;
                }

                var id = (int) JOptionPane.showInputDialog(
                        null,
                        "Que cliente você deseja alterar?",
                        "Alterar",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var customer = customerService.findById(id);

                var name = JOptionPane.showInputDialog("Insira um nome:", customer.getName());
                var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:", customer.getPhoneNumber());
                var birthDate = LocalDateField.showInputLocalDateDialog("Insira a data do nascimento:", customer.getBirthDate());
                var cpf = JOptionPane.showInputDialog("Insira o cpf:", customer.getCpf());
                var address = AddressForm.showInputLocalDateDialog("Insira o endereço", customer.getAddress());
                var selectedUpdatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está alterando esse cliente?",
                        "Alterado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var updatedBy = employeeService.findById((int) selectedUpdatedBy).getRole();

                var updatedCustomer = new Customer(
                        id,
                        name,
                        phoneNumber,
                        birthDate,
                        cpf,
                        address,
                        updatedBy
                );

                ObjectUtils.copyNonNullFields(updatedCustomer, customer);

                customerService.update(customer);
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                customerService.delete(customerService.findById(id));
            }

            case "addAllergy" -> {
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (customers.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um cliente antes de adicionar uma alergia a um cliente");
                    break;
                }

                var allergy = (Allergy) JOptionPane.showInputDialog(
                        null,
                        "Escolha uma alergia",
                        "Alergias",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Allergy.values(),
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cliente:",
                        "Cliente",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var customer = customerService.findById((int) selectedCustomer);

                customerService.addAllergy(customer, allergy);
            }

            case "deleteAllergy" -> {
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (customers.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um cliente antes de remover uma alergia a um cliente");
                    break;
                }

                var allergy = (Allergy) JOptionPane.showInputDialog(
                        null,
                        "Escolha uma alergia",
                        "Alergias",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Allergy.values(),
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cliente:",
                        "Cliente",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var customer = customerService.findById((int) selectedCustomer);

                customerService.deleteAllergy(customer, allergy);
            }
        }
    }
}