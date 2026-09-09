DELETE FROM public.customers;

ALTER SEQUENCE public.customer_id_sequence RESTART WITH 10000000001;

INSERT INTO public.customers (
    name,
    customer_type,
    status,
    email_address,
    mobile_number,
    address_line1,
    address_line2,
    city,
    state,
    postal_code,
    country
) VALUES
    (
        'Rahul Kumar Vishwakarma',
        'INDIVIDUAL',
        'PROSPECT',
        'rahul.customer1@example.com',
        '+919876543210',
        'Belapur',
        'Navi Mumbai',
        'Navi Mumbai',
        'Maharashtra',
        '400614',
        'India'
    ),
    (
        'Priya Sharma',
        'BUSINESS',
        'ACTIVE',
        'priya.business@example.com',
        '+919876543211',
        'Bandra',
        'West Mumbai',
        'Mumbai',
        'Maharashtra',
        '400051',
        'India'
    ),
    (
        'Amit Verma',
        'JOINT',
        'SUSPENDED',
        'amit.joint@example.com',
        '+919876543212',
        'Sector 62',
        'Noida',
        'Noida',
        'Uttar Pradesh',
        '201301',
        'India'
    ),
    (
        'Sneha Nair',
        'INDIVIDUAL',
        'BLOCKED',
        'sneha.individual@example.com',
        '+919876543213',
        'Koramangala',
        '5th Block',
        'Bengaluru',
        'Karnataka',
        '560095',
        'India'
    );
