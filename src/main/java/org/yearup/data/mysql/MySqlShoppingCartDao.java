package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    private final ProductDao productDao;
    private final UserDao userDao;

    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao, UserDao userDao)
    {
        super(dataSource);
        this.productDao = productDao;
        this.userDao = userDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();

        String sql = "SELECT * FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quantity");
                BigDecimal discount = resultSet.getBigDecimal("discount");

                Product product = productDao.getById(productId);
                if (product != null)
                {
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(quantity);
                    item.setDiscountPercent(discount);

                    cart.getItems().put(productId, item);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return cart;
    }

    @Override
    public void addProduct(int userId, int productId)
    {
        String sql = """
                INSERT INTO shopping_cart (user_id, product_id, quantity, discount)
                VALUES (?, ?, 1, 0.0)
                ON DUPLICATE KEY UPDATE quantity = quantity + 1;
                """;

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity)
    {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }




    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
