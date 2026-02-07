-- Crear las bases de datos para cada microservicio
CREATE DATABASE sofkify_users;
CREATE DATABASE sofkify_products_bd;
CREATE DATABASE sofkify_cars_bd;

-- Dar permisos al usuario postgres
GRANT ALL PRIVILEGES ON DATABASE sofkify_users TO postgres;
GRANT ALL PRIVILEGES ON DATABASE sofkify_products_bd TO postgres;
GRANT ALL PRIVILEGES ON DATABASE sofkify_cars_bd TO postgres;
