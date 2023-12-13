package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
//This tells Spring that this class is a Component of type RestController and capable of handling HTTP requests
@RequestMapping("/cashcards")  //This is a companion to @RestController that indicates which address requests must have to access this Controller.
class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    /**
     * This is data management.
     * Our Controller shouldn't be concerned with checking IDs or creating data.
     *
     * @GetMapping marks a method as a handler method.
     * GET requests that match cashcards/{requestedID} will be handled by this method.
     */

    @GetMapping("/requestedId")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * The POST endpoint is similar to the GET endpoint in our CashCardController,
     * but uses the @PostMapping annotation from Spring Web.
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest,
                                                UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        //Now only the authenticated, authorized Principal is used to create a CashCard
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }


    /**
     * Once again we're using one of Spring Data's built-in implementations:
     * CrudRepository.findAll(). Our implementing Repository, CashCardRepository,
     * will automatically return all CashCard records from the database when findAll() is invoked.
     */


    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(              //PageRequest is a basic Java Bean implementation of Pageable
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    /**
     * The @PutMapping supports the PUT verb and supplies the target requestedId.
     * The @RequestBody contains the updated CashCard data.
     * Return an HTTP 204 NO_CONTENT response code for now, just to get started.
     */
    @PutMapping("/requestedId")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }
}

