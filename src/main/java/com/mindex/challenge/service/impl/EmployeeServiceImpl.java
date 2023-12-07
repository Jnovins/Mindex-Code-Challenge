package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;
import com.mindex.challenge.data.ReportingStructure;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure createReportingStructure(String id) {
        LOG.debug("Creating reporting structure for employee with id: [{}]", id);

        // Ensure that our requested ID has an existing user
        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return new ReportingStructure(employee, compileNumberOfReports(id));
    }

    /*
    Private helper method to calculate the number of reports that an employee has.
    Includes indirect and direct reports.
     */
    private int compileNumberOfReports(String id){
        Employee employee = employeeRepository.findByEmployeeId(id);
        List<Employee> directReports = employee.getDirectReports();

        if(directReports == null) return 0; // Termination case

        int numberOfReports = directReports.size(); // direct reports for employee

        for( Employee report : directReports) {
            numberOfReports += compileNumberOfReports(report.getEmployeeId()); // include indirect reports recursively
        }

        return numberOfReports;
    }
}
