package com.aheadaviation.bagshop.orders.controllers;

import com.aheadaviation.bagshop.orders.config.OrdersConfigurationProperties;
import com.aheadaviation.bagshop.orders.entities.Address;
import com.aheadaviation.bagshop.orders.entities.CustomerOrder;
import com.aheadaviation.bagshop.orders.repositories.CustomerOrderRepository;
import com.aheadaviation.bagshop.orders.resources.NewOrderResource;
import com.aheadaviation.bagshop.orders.services.AsyncGetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@RepositoryRestController
public class OrdersController {
  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Autowired
  private OrdersConfigurationProperties config;

  @Autowired
  private AsyncGetService asyncGetService;

  @Autowired
  private CustomerOrderRepository customerOrderRepository;

  @Value(value = "${http.timeout:5}")
  private long timeout;

  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(path = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
  public
  @ResponseBody
  CustomerOrder newOrder(@RequestBody NewOrderResource item) {
    try {

      if (item.address == null || item.card == null || item.customer == null || item.items == null) {
        throw new InvalidOrderException("Invalid order request. Order requires a customer, address, card, and items.");
      }

      LOG.debug("Starting calls");
      Future<Resource<Address>> addressFuture = asyncGetService.getResource(item.address, new TypeReferences
          .ResourceType<Address>() {
      });
      LOG.debug("End of calls");

      LOG.debug("Received data: " + addressFuture.get(timeout, TimeUnit.SECONDS).getContent().toString());
      return new CustomerOrder();

    } catch (TimeoutException e) {
      throw new IllegalStateException("Unable to create order due to timeout from one of the services.", e);
    } catch (InterruptedException | IOException | ExecutionException e) {
      throw new IllegalStateException("Unable to create error due to unspecified IO error.", e);
    }
  }

  @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
  public class InvalidOrderException extends IllegalStateException {
    public InvalidOrderException(String s) {
      super(s);
    }
  }
}
