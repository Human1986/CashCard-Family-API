package example.cashcard.controllers;

import example.cashcard.CashCard;
import example.cashcard.repositories.CashCardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
//This tells Spring that this class is a Component of type RestController and capable of handling HTTP requests
@RequestMapping("/cashcards")  //This is a companion to @RestController that indicates which address requests must have to access this Controller.
class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    /**
     * This is data management.
     * Our Controller shouldn't be concerned with checking IDs or creating data.
     */
    @GetMapping("/{requestedId}")
    //@GetMapping marks a method as a handler method. GET requests that match cashcards/{requestedID} will be handled by this method.
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {

        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);  //We're calling CrudRepository.findById, which returns an Optional. This smart object might or might not contain the CashCard for which we're searching

        //This is how you determine if findById did or did not find the CashCard with the supplied id
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     The POST endpoint is similar to the GET endpoint in our CashCardController,
     but uses the @PostMapping annotation from Spring Web.
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {

        // it saves a new CashCard for us, and returns the saved object with a unique id provided by the database.
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}

