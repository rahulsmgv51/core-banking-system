package com.rahulsmgv.cbs.customer.application.port;

import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;

public interface CustomerIdGenerator {

    CustomerId nextCustomerId();
}
