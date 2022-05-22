package sm.tech.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sm.tech.model.CustomerDto
import sm.tech.ratelimit.RateLimit

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {

    @RateLimit(maxRatePerMinute = 5)
    @GetMapping("/{customerId}")
    fun getCustomer(@PathVariable customerId: Long): ResponseEntity<CustomerDto> {

        return ResponseEntity.ok(CustomerDto(customerId, "John", "Doe"))
    }

    @GetMapping()
    fun searchCustomers(): List<CustomerDto> {

        return emptyList()
    }




}