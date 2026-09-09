DELETE FROM public.accounts;

ALTER SEQUENCE public.account_id_sequence RESTART WITH 10000000001;
ALTER SEQUENCE public.account_number_sequence RESTART WITH 100000000001;

INSERT INTO public.accounts (
    customer_id,
    account_number,
    account_type,
    status,
    currency,
    balance,
    created_at,
    updated_at
) VALUES
    (
        10000000001,
        'ACC-100000000001',
        'SAVINGS',
        'ACTIVE',
        'INR',
        5000.00,
        NOW(),
        NOW()
    ),
    (
        10000000002,
        'ACC-100000000002',
        'CURRENT',
        'ACTIVE',
        'INR',
        12500.00,
        NOW(),
        NOW()
    ),
    (
        10000000003,
        'ACC-100000000003',
        'FIXED_DEPOSIT',
        'PENDING',
        'INR',
        250000.00,
        NOW(),
        NOW()
    ),
    (
        10000000004,
        'ACC-100000000004',
        'RECURRING_DEPOSIT',
        'DORMANT',
        'INR',
        18000.00,
        NOW(),
        NOW()
    );
