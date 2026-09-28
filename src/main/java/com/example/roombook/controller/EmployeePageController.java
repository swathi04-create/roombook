package com.example.roombook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeePageController {

    @GetMapping("/employees")
    public String employeesPage() {

        return "employees";
    }


    @GetMapping("/add-employee")
    public String addEmployeePage() {

        return "add-employee";
    }
}