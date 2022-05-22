package sm.tech.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sm.tech.model.CustomerDto
import sm.tech.ratelimit.RateLimit
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {

    @RateLimit(maxRatePerMinute = 5)
    @GetMapping("/{customerId}")
    fun getCustomer(@PathVariable customerId: Long, request: HttpServletRequest): ResponseEntity<CustomerDto> {

        return ResponseEntity.ok(CustomerDto(customerId, "John", "Doe"))
    }

    @GetMapping()
    fun searchCustomers(): List<CustomerDto> {

        return emptyList()
    }


}