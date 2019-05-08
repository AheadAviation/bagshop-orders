package com.aheadaviation.bagshop.orders.repositories;

import com.aheadaviation.bagshop.orders.entities.CustomerOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "orders", itemResourceRel = "order")
public interface CustomerOrderRepository extends MongoRepository<CustomerOrder, String> {
  @RestResource(path = "customerId")
  List<CustomerOrder> findByCustomerId(@Param("custId") String id);
}
