package telran.employees;

import java.lang.reflect.Field;
import java.util.*;

import telran.view.InputOutput;
import telran.view.Item;
//public void addEmployee(Employee empl) ;
//public Employee getEmployee(long id) ;
//public Employee removeEmployee(long id) ;
//public int getDepartmentBudget(String department) ;
//public String[] getDepartments() ;
//public Manager[] getManagersWithMostFactor() ;

public class CompanyApplItems {
	static Company company;
	static HashSet<String> departments;

	public static List<Item> getCompanyItems(Company company, HashSet<String> departments) {
		CompanyApplItems.company = company;
		CompanyApplItems.departments = departments;
		Item[] items = { Item.of("add employee", CompanyApplItems::addEmployee),
				Item.of("display employee data", CompanyApplItems::getEmployee),
				Item.of("remove employee", CompanyApplItems::removeEmployee),
				Item.of("display department budget", CompanyApplItems::getDepartmentBudget),
				Item.of("display departments", CompanyApplItems::getDepartments),
				Item.of("display managers with most factor", CompanyApplItems::getManagersWithMostFactor), };
		return new ArrayList(List.of(items));

	}

	static void addEmployee(InputOutput io) {
		Employee empl = readEmployee(io);
		String type = io.readStringOptions("Enter employee type", "Wrong Employee Type",
				new HashSet<String>(List.of("WageEmployee", "Manager", "SalesPerson")));
		Employee result = switch (type) {
		case "WageEmployee" -> getWageEmployee(empl, io);
		case "Manager" -> getManager(empl, io);
		case "SalesPerson" -> getSalesPerson(empl, io);
		default -> null;
		};
		company.addEmployee(result);
		io.writeLine("Employee has been added");
	}

	private static Employee getSalesPerson(Employee empl, InputOutput io) {
		WageEmployee wageEmployee = (WageEmployee) getWageEmployee(empl, io);
		float percents = io.readNumberRange("Enter percents", "Wrong percents value", 0.5, 2).floatValue();
		long sales = io.readNumberRange("Enter sales", "Wrong sales value", 500, 50000).longValue();
		return new SalesPerson(empl.getId(), empl.getBasicSalary(), empl.getDepartment(), wageEmployee.getHours(),
				wageEmployee.getWage(), percents, sales);
	}

	private static Employee getManager(Employee empl, InputOutput io) {

		float factor = io.readNumberRange("Enter factor", "Wrong factor value", 1.5, 5).floatValue();
		return new Manager(empl.getId(), empl.getBasicSalary(), empl.getDepartment(), factor);
	}

	private static Employee getWageEmployee(Employee empl, InputOutput io) {

		int hours = io.readNumberRange("Enter working hours", "Wrong hours value", 10, 200).intValue();
		int wage = io.readNumberRange("Enter hour wage", "Wrong wage value", 100, 1000).intValue();
		;
		return new WageEmployee(empl.getId(), empl.getBasicSalary(), empl.getDepartment(), hours, wage);
	}

	private static Employee readEmployee(InputOutput io) {

		long id = io.readNumberRange("Enter id value", "Wrong id value", 1000, 10000).longValue();
		int basicSalary = io.readNumberRange("Enter basic salary", "Wrong basic salary", 2000, 20000).intValue();
		return new Employee(id, basicSalary, getDepartmentName(io));
	}

	private static String getDepartmentName(InputOutput io) {
		String department = io.readStringOptions("Enter department " + departments, "Wrong department", departments);
		return department;
	}

	static void getEmployee(InputOutput io) {
		if (checkCompanySize()) {
			long id = getEmployeeId(io);
			printEmployee(company.getEmployee(id), io);
		} else {
			io.writeLine("No employees registered");
		}
	}

	private static boolean checkCompanySize() {
		return company.iterator().hasNext();

	}

	private static long getEmployeeId(InputOutput io) {

		HashSet<String> ids = new HashSet<>();
		company.forEach(empl -> ids.add(Long.valueOf(empl.getId()).toString()));
		long id = Long.valueOf(io.readStringOptions("Enter id value" + ids, "Wrong id value", ids));
		return id;
	}

	private static void printEmployee(Employee empl, InputOutput io) {
		Class<?> cls = empl.getClass();
		io.writeLine(empl.getClass().getName());
		printFields(empl, cls, io);
		while ((cls = cls.getSuperclass()) != null) {
			printFields(empl, cls, io);
		}

	}

	private static void printFields(Employee empl, Class<?> cls, InputOutput io) {
		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			try {
				io.writeLine(f.getName() + ": " + f.get(empl));

			} catch (IllegalAccessException e) {
				io.writeLine(f.getName() + " is not available");
			}
		}
	}

	static void removeEmployee(InputOutput io) {
		if (checkCompanySize()) {
			long id = getEmployeeId(io);
			company.removeEmployee(id);
			io.writeLine("Employee has been successfully removed");
		} else {
			io.writeLine("No employees to remove");
		}
	}

	static void getDepartmentBudget(InputOutput io) {
		String name = getDepartmentName(io);
		int budget = company.getDepartmentBudget(name);
		io.writeLine("Budget of " + name + " department: " + budget);
	}

	static void getDepartments(InputOutput io) {
		String[] departments = company.getDepartments();
		if (departments.length != 0) {
			Arrays.stream(company.getDepartments()).forEach(d -> io.writeLine(d));
		} else {
			io.writeLine("No departments registered");
		}
	}

	static void getManagersWithMostFactor(InputOutput io) {
		Manager[] managers = company.getManagersWithMostFactor();
		if (managers.length != 0) {
			Arrays.stream(managers).forEach(m -> io.writeLine(m.getId()));
		} else {
			io.writeLine("No managers registered");
		}

	}
}
