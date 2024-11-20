package br.com.levva.order.repository;

import br.com.levva.order.entity.ProcessedOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedOrderRepository extends MongoRepository<ProcessedOrder, String> {
}
