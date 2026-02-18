package com.chakray.usersapi.service;

import com.chakray.usersapi.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.chakray.usersapi.util.ValidatorUtil;
import com.chakray.usersapi.util.AESUtil;
import com.chakray.usersapi.util.DateUtil;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();

    public UserService() {

        User u1 = new User();
        u1.setId(UUID.randomUUID());
        u1.setName("user1");
        u1.setEmail("user1@mail.com");
        u1.setPhone("+1 55 555 555 55");
        u1.setTax_id("AARR990101XXX");
        u1.setPassword(AESUtil.encrypt("7c4a8d09ca3762af61e59520943dc26494f8941b"));
        u1.setCreated_at(DateUtil.getCurrentMadagascarTime());

        User u2 = new User();
        u2.setId(UUID.randomUUID());
        u2.setName("user2");
        u2.setEmail("user2@mail.com");
        u2.setPhone("+52 55 555 555 56");
        u2.setTax_id("BBRR990202YYY");
        u2.setPassword(AESUtil.encrypt("pass2"));
        u2.setCreated_at(DateUtil.getCurrentMadagascarTime());

        User u3 = new User();
        u3.setId(UUID.randomUUID());
        u3.setName("user3");
        u3.setEmail("user3@mail.com");
        u3.setPhone("+44 55 555 555 57");
        u3.setTax_id("CCRR990303ZZZ");
        u3.setPassword(AESUtil.encrypt("pass3"));
        u3.setCreated_at(DateUtil.getCurrentMadagascarTime());

        users.add(u1);
        users.add(u2);
        users.add(u3);
    }

    public List<User> getAllUsers() {
        return users;
    }

    public User createUser(User user) {
        // Validaciones
        if (!ValidatorUtil.isValidTaxId(user.getTax_id())) {
            throw new IllegalArgumentException("tax_id inválido");
        }
        if (!ValidatorUtil.isValidPhone(user.getPhone())) {
            throw new IllegalArgumentException("phone inválido");
        }
        // Revisar tax_id único
        for (User u : users) {
            if (u.getTax_id().equals(user.getTax_id())) {
                throw new IllegalArgumentException("tax_id ya existe");
            }
        }

        user.setId(UUID.randomUUID());
        user.setPassword(AESUtil.encrypt(user.getPassword()));
        user.setCreated_at(DateUtil.getCurrentMadagascarTime());
        users.add(user);

        // Remover password del objeto que se va a devolver
        User response = new User();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setTax_id(user.getTax_id());
        response.setCreated_at(user.getCreated_at());
        response.setAddresses(user.getAddresses());

        return response;
    }

    public User updateUser(UUID id, User updatedUser) {

        for (User u : users) {

            if (u.getId().equals(id)) {

                if (updatedUser.getName() != null)
                    u.setName(updatedUser.getName());

                if (updatedUser.getEmail() != null)
                    u.setEmail(updatedUser.getEmail());

                if (updatedUser.getPhone() != null)
                    u.setPhone(updatedUser.getPhone());

                return u;
            }
        }

        return null;
    }

    public boolean deleteUser(UUID id) {

        for (User u : users) {
            if (u.getId().equals(id)) {
                users.remove(u);
                return true;
            }
        }

        return false;
    }

    public List<User> getUsersSorted(String sortedBy) {

        List<User> sorted = new ArrayList<>(users);

        if (sortedBy == null || sortedBy.isEmpty())
            return sorted;

        switch (sortedBy) {

            case "name":
                sorted.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;

            case "email":
                sorted.sort((a, b) -> a.getEmail().compareToIgnoreCase(b.getEmail()));
                break;

            case "phone":
                sorted.sort((a, b) -> a.getPhone().compareToIgnoreCase(b.getPhone()));
                break;

            case "id":
                sorted.sort((a, b) -> a.getId().toString().compareTo(b.getId().toString()));
                break;
        }

        return sorted;
    }

    public User authenticate(String taxId, String password) {
        for (User u : users) {
            if (u.getTax_id().equals(taxId)) {
                String decrypted = AESUtil.decrypt(u.getPassword());
                if (decrypted.equals(password)) {
                    // Devuelve usuario sin password
                    User response = new User();
                    response.setId(u.getId());
                    response.setName(u.getName());
                    response.setEmail(u.getEmail());
                    response.setPhone(u.getPhone());
                    response.setTax_id(u.getTax_id());
                    response.setCreated_at(u.getCreated_at());
                    response.setAddresses(u.getAddresses());
                    return response;
                }
            }
        }
        return null;
    }

    public List<User> getUsersFiltered(String filter) {
        if (filter == null || filter.isEmpty())
            return new ArrayList<>(users);

        String[] parts = filter.split("\\+");
        if (parts.length != 3)
            return new ArrayList<>();

        String attr = parts[0];
        String op = parts[1];
        String value = parts[2];

        List<User> result = new ArrayList<>();
        for (User u : users) {
            String field = switch (attr) {
                case "name" -> u.getName();
                case "email" -> u.getEmail();
                case "phone" -> u.getPhone();
                case "tax_id" -> u.getTax_id();
                case "id" -> u.getId().toString();
                case "created_at" -> u.getCreated_at();
                default -> null;
            };
            if (field == null)
                continue;

            boolean match = switch (op) {
                case "co" -> field.contains(value);
                case "eq" -> field.equals(value);
                case "sw" -> field.startsWith(value);
                case "ew" -> field.endsWith(value);
                default -> false;
            };

            if (match)
                result.add(u);
        }
        return result;
    }

}
