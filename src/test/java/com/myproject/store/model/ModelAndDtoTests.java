package com.myproject.store.model;

import com.myproject.store.dto.JwtResponse;
import com.myproject.store.dto.LoginRequest;
import com.myproject.store.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ModelAndDtoTests {

    @Test
    public void testCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Test Desc");
        category.setItems(new HashSet<>());

        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
        assertEquals("Test Desc", category.getDescription());
        assertNotNull(category.getItems());

        Category category2 = new Category("Name", "Desc");
        assertEquals("Name", category2.getName());
    }

    @Test
    public void testItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Desc");
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("10.50"));
        
        Category cat = new Category();
        item.setCategory(cat);
        
        Supplier sup = new Supplier();
        item.setSupplier(sup);
        item.setStockMovements(new HashSet<>());
        
        item.prePersist();
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
        
        item.preUpdate();
        assertNotNull(item.getUpdatedAt());

        assertEquals(1L, item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Desc", item.getDescription());
        assertEquals(10, item.getQuantity());
        assertEquals(new BigDecimal("10.50"), item.getUnitPrice());
        assertEquals(cat, item.getCategory());
        assertEquals(sup, item.getSupplier());
        assertNotNull(item.getStockMovements());
    }

    @Test
    public void testRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");
        role.setUsers(new HashSet<>());

        assertEquals(1L, role.getId());
        assertEquals("ROLE_ADMIN", role.getName());
        assertNotNull(role.getUsers());
    }

    @Test
    public void testSupplier() {
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier Name");
        supplier.setContactPerson("Contact Name");
        supplier.setEmail("email@test.com");
        supplier.setPhone("1234567890");
        supplier.setAddress("Test Address");
        supplier.setItems(new HashSet<>());

        assertEquals(1L, supplier.getId());
        assertEquals("Supplier Name", supplier.getName());
        assertEquals("Contact Name", supplier.getContactPerson());
        assertEquals("email@test.com", supplier.getEmail());
        assertEquals("1234567890", supplier.getPhone());
        assertEquals("Test Address", supplier.getAddress());
        assertNotNull(supplier.getItems());
    }

    @Test
    public void testUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("pass");
        user.setName("Full Name");
        user.setEmail("email@test.com");
        user.setContact("1234567890");
        user.setEnabled(true);
        user.setRoles(new HashSet<>());

        assertEquals(1L, user.getId());
        assertEquals("user", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals("Full Name", user.getName());
        assertEquals("email@test.com", user.getEmail());
        assertEquals("1234567890", user.getContact());
        assertTrue(user.isEnabled());
        assertNotNull(user.getRoles());
    }

    @Test
    public void testStockMovement() {
        StockMovement sm = new StockMovement();
        Item item = new Item();
        sm.setItem(item);
        sm.setType(StockMovement.MovementType.IN);
        sm.setQuantity(5);
        sm.setReason("Remarks");

        User user = new User();
        sm.setPerformedBy(user);
        
        sm.prePersist();

        assertEquals(item, sm.getItem());
        assertEquals(StockMovement.MovementType.IN, sm.getType());
        assertEquals(5, sm.getQuantity());
        assertEquals("Remarks", sm.getReason());
        assertNotNull(sm.getCreatedAt());
        assertEquals(user, sm.getPerformedBy());
    }

    @Test
    public void testJwtResponse() {
        JwtResponse resp = new JwtResponse("token");
        assertEquals("token", resp.getToken());
        resp.setToken("newtoken");
        assertEquals("newtoken", resp.getToken());
    }

    @Test
    public void testLoginRequest() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user");
        req.setPassword("pass");

        assertEquals("user", req.getUsername());
        assertEquals("pass", req.getPassword());
    }

    @Test
    public void testUserRegistrationDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("user");
        dto.setPassword("pass");
        dto.setName("name");
        dto.setEmail("email");
        dto.setContact("1234567890");
        dto.setRoleKey("ADMIN");

        assertEquals("user", dto.getUsername());
        assertEquals("pass", dto.getPassword());
        assertEquals("name", dto.getName());
        assertEquals("email", dto.getEmail());
        assertEquals("1234567890", dto.getContact());
        assertEquals("ADMIN", dto.getRoleKey());
    }
}
