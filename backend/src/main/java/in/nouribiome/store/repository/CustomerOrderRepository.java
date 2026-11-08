package in.nouribiome.store.repository;

import in.nouribiome.store.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {
    Optional<CustomerOrder> findByRazorpayOrderId(String razorpayOrderId);
    List<CustomerOrder> findAllByOrderByCreatedAtDesc();
}