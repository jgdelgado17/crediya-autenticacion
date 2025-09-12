-- Creating the Role Table
CREATE TABLE public.roles (
    unique_id SERIAL PRIMARY KEY,
    names VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

-- Creating the Users Table
CREATE TABLE public.users (
    id_user SERIAL PRIMARY KEY,
    names VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    document_number VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    id_role INTEGER,
    base_salary DECIMAL(10, 2),
    passcode VARCHAR(255) NOT NULL,
    CONSTRAINT fk_id_role
        FOREIGN KEY (id_role)
        REFERENCES public.roles (unique_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
