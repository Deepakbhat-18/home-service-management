package com.homeservice.config;

import com.homeservice.model.Service;
import com.homeservice.model.User;
import com.homeservice.repository.ServiceRepository;
import com.homeservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**

 * Test accounts:
 *   admin@home.com    / admin123
 *   customer@home.com / customer123
 *   provider@home.com / provider123
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            createUser("admin@home.com",    "Admin User",     "HQ, City",              "9000000001", "admin",    "admin123");
            createUser("provider@home.com", "Rajesh Kumar",   "456 Provider Lane",     "9000000002", "provider", "provider123");
            createUser("provider2@home.com","Suresh Singh",   "789 Service Road",      "9000000003", "provider", "provider123");
            createUser("customer@home.com", "Priya Sharma",   "12 Customer Colony",    "9000000004", "customer", "customer123");
            System.out.println("✅ Users seeded");
        }

        if (serviceRepository.count() == 0) {
            createService("Electrical Repair",  "Fix wiring, switches, and electrical faults",  499,  "fas fa-bolt",        "Electrical");
            createService("Plumbing Service",   "Pipe repair, leaks, bathroom fixtures",         399,  "fas fa-tint",        "Plumbing");
            createService("Home Cleaning",      "Deep cleaning of home, kitchen and bathrooms",  599,  "fas fa-broom",       "Cleaning");
            createService("AC Service & Repair","Servicing, gas refill, and AC repair",          799,  "fas fa-snowflake",   "Appliance");
            createService("Painting Service",   "Interior and exterior painting work",           1499, "fas fa-paint-roller","Painting");
            createService("Carpentry Work",     "Furniture repair, installation, woodwork",      699,  "fas fa-hammer",      "Carpentry");
            createService("Pest Control",       "Cockroach, termite, and mosquito treatment",   899,  "fas fa-bug",         "Pest Control");
            createService("Appliance Repair",   "Washing machine, refrigerator, TV repair",     549,  "fas fa-tools",       "Appliance");
            System.out.println("✅ Services seeded");
        }
    }

    private void createUser(String email, String name, String address, String phone, String role, String pass) {
        User u = new User();
        u.setEmail(email); u.setFullname(name); u.setAddress(address);
        u.setPhone(phone); u.setRole(role);
        u.setPassword(passwordEncoder.encode(pass));
        userRepository.save(u);
    }

    private void createService(String name, String desc, int price, String icon, String category) {
        Service s = new Service();
        s.setName(name); s.setDescription(desc);
        s.setPrice(new BigDecimal(price));
        s.setIconClass(icon); s.setCategory(category);
        serviceRepository.save(s);
    }
}
