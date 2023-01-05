package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private static volatile Properties properties;

    private int loginTimeout;
    private PrintWriter logWriter;
    private final CustomConnector connector;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    static {
        properties = new Properties();
        try {
            properties.load(Files.newInputStream(Path.of("app.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
        connector = new CustomConnector();
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            instance = new CustomDataSource(properties.getProperty("driver"), properties.getProperty("url"), properties.getProperty("password"), properties.getProperty("name"));
        }
        return instance;
    }

    @Override
    public Connection getConnection() {
        return connector.getConnection(url);
    }

    @Override
    public Connection getConnection(String username, String password) {
        return connector.getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw  new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        } else {
            throw new SQLException("Cannot unwrap to " + iface.getName());
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass());
    }
}
