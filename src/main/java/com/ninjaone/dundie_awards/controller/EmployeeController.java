package com.ninjaone.dundie_awards.controller;

// there are non used imports here
// to avoid that we could use a plugin or dependency like checkstyle since is common to forgot checking that
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
// we are missing the path here looks like that should be employees
// and we could remove it from @Get and @Post annotations to make concise
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private MessageBroker messageBroker;

    @Autowired
    private AwardsCache awardsCache;

    // get all employees
    @GetMapping("")
    @ResponseBody
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // create employee rest api
    @PostMapping("")
    @ResponseBody
    public Employee createEmployee(@RequestBody Employee employee) {
        if (employee.getDundieAwards() != null) {
            awardsCache.setTotalAwards(employee.getDundieAwards() + awardsCache.getTotalAwards());
        }
        return employeeRepository.save(employee);
    }

    // get employee by id rest api
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // update employee rest api
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {

        return employeeRepository.findById(id)
                .map(employee -> {
                    int existingTotal = employee.getDundieAwards() != null ? employee.getDundieAwards() : 0;
                    if (employeeDetails.getDundieAwards() != null){
                        awardsCache.setTotalAwards((employeeDetails.getDundieAwards() + awardsCache.getTotalAwards() - existingTotal));
                    }
                    employee.setFirstName(employeeDetails.getFirstName());
                    employee.setLastName(employeeDetails.getLastName());
                    employee.setDundieAwards(employeeDetails.getDundieAwards());
                    Employee updatedEmployee = employeeRepository.save(employee);
                    return ResponseEntity.ok(updatedEmployee);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // delete employee rest api
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    if (employee.getDundieAwards() != null && awardsCache.getTotalAwards() > 0){
                        awardsCache.setTotalAwards(awardsCache.getTotalAwards() - employee.getDundieAwards());
                    }
                    employeeRepository.delete(employee);
                    Map<String, Boolean> response = new HashMap<>();
                    response.put("deleted", Boolean.TRUE);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}